package mod.traister101.sns.common.items;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.capability.*;
import mod.traister101.sns.common.menu.*;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.util.*;
import mod.traister101.sns.util.ItemSlotData.HeldSlotData;
import mod.traister101.sns.util.SNSUtils.ToggleType;
import mod.traister101.sns.util.items.ItemSlot;
import net.dries007.tfc.common.component.size.*;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.common.blocks.TooltipBlock;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.*;

public class ContainerItem extends Item implements IItemSize {

	public static final String CONTENTS_TAG = "contents";
	public static final String VOID_SLOTS_TAG = "void_slots";
	public static final String TYPE_NO_PICKUP = SacksNSuch.MODID + ".status.item_container.no_pickup";
	public static final String HOLD_SHIFT_TOOLTIP = SacksNSuch.MODID + ".tooltip.item_container.tooltip.shift";
	public static final String PICKUP_TOOLTIP = SacksNSuch.MODID + ".tooltip.item_container.tooltip.pickup";
	public static final String VOID_TOOLTIP = SacksNSuch.MODID + ".tooltip.item_container.tooltip.void";
	public static final String SLOT_COUNT_TOOLTIP = SacksNSuch.MODID + ".tooltip.item_container.slot_count";
	public static final String SLOT_CAPACITY_TOOLTIP = SacksNSuch.MODID + ".tooltip.item_container.slot_capacity";
	public static final String ALLOWED_SIZE_TOOLTIP = SacksNSuch.MODID + ".tooltip.item_container.allowed_size";
	public static final String INVENTORY_INTERACTION_TOOLTIP = SacksNSuch.MODID + ".tooltip.item_container.tooltip.inventory_interaction";

	public final ContainerType type;

	public ContainerItem(final Properties properties, final ContainerType type) {
		super(properties);
		this.type = type;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
		final ItemStack heldStack = player.getItemInHand(hand);

		if (level.isClientSide) {
			if (!player.isShiftKeyDown()) return InteractionResultHolder.success(heldStack);

			if (type.doesAutoPickup()) {
				final boolean flag = !NBTHelper.isAutoPickup(heldStack);
				SNSUtils.sendTogglePacket(ToggleType.PICKUP, flag);
				player.displayClientMessage(ToggleType.PICKUP.getTooltip(flag), true);
			} else {
				player.displayClientMessage(Component.translatable(TYPE_NO_PICKUP, this.getName(heldStack)), true);
			}

			return InteractionResultHolder.success(heldStack);
		}

		if (!player.isShiftKeyDown()) {
			SNSMenus.CONTAINER_ITEM_MENU_PROVIDER.openMenu(player, new HeldSlotData(hand));
			return InteractionResultHolder.consume(heldStack);
		}

		return InteractionResultHolder.consume(heldStack);
	}

	@Override
	public boolean overrideStackedOnOther(final ItemStack itemStack, final Slot slot, final ClickAction clickAction, final Player player) {
		if (!type.doesInventoryInteraction()) return false;
		if (clickAction != ClickAction.SECONDARY) return false;
		if (!SNSConfig.SERVER.enableContainerInventoryInteraction.get()) return false;

		final var handler = itemStack.getCapability(Capabilities.ItemHandler.ITEM);
		if (handler == null) return false;

		// Extract items into the slot
		if (!slot.hasItem()) {
			for (final var handlerSlot : ItemSlot.reverseIterable(handler)) {
				final var simulate = handlerSlot.extractItem(Integer.MAX_VALUE, true);
				if (simulate.isEmpty()) continue;

				final ItemStack extracted = handlerSlot.extractItem(Integer.MAX_VALUE, false);
				final ItemStack remainder = slot.safeInsert(extracted);

				if (!remainder.isEmpty()) {
					handlerSlot.insertItem(remainder, false);
					player.containerMenu.slotsChanged(slot.container);
					playRemoveOneSound(player);
					return true;
				}
			}
			return false;
		}

		final var slotStack = slot.getItem();
		// We have to simulate the insertion to account for crafting result slots
		final var simulate = ItemHandlerHelper.insertItemStacked(handler, slotStack, true);
		final var extracted = slot.safeTake(slotStack.getCount(), slotStack.getCount() - simulate.getCount(), player);
		if (extracted.isEmpty()) return false;

		ItemHandlerHelper.insertItemStacked(handler, extracted, false);
		player.containerMenu.slotsChanged(slot.container);
		playInsertSound(player);
		return true;
	}

	@Override
	public boolean overrideOtherStackedOnMe(final ItemStack itemStack, final ItemStack carriedStack, final Slot slot, final ClickAction clickAction,
			final Player player, final SlotAccess carriedSlot) {
		if (!type.doesInventoryInteraction()) return false;
		if (!slot.allowModification(player)) return false;
		if (clickAction != ClickAction.SECONDARY) return false;
		if (!SNSConfig.SERVER.enableContainerInventoryInteraction.get()) return false;

		final var handler = itemStack.getCapability(Capabilities.ItemHandler.ITEM);
		if (handler == null) return false;

		if (carriedStack.isEmpty()) {
			for (final var handlerSlot : ItemSlot.reverseIterable(handler)) {
				final var current = handlerSlot.getStack();
				if (current.isEmpty()) continue;

				carriedSlot.set(handlerSlot.extractItem(Integer.MAX_VALUE, false));
				player.containerMenu.slotsChanged(slot.container);
				playRemoveOneSound(player);
				return true;
			}
			return false;
		}

		final var remainder = ItemHandlerHelper.insertItemStacked(handler, carriedStack, false);
		if (remainder.getCount() == carriedStack.getCount()) return false;

		carriedSlot.set(remainder);
		player.containerMenu.slotsChanged(slot.container);
		playInsertSound(player);
		return true;
	}

	@Override
	public void appendHoverText(final ItemStack itemStack, final TooltipContext context, final List<Component> tooltip, final TooltipFlag flagIn) {
		if (!Screen.hasShiftDown()) {
			tooltip.add(Component.translatable(HOLD_SHIFT_TOOLTIP).withStyle(ChatFormatting.GRAY));
			return;
		}

		tooltip.add(Component.translatable(SLOT_COUNT_TOOLTIP, Component.literal(String.valueOf(type.slotCount())).withStyle(ChatFormatting.WHITE))
				.withStyle(ChatFormatting.GRAY));
		tooltip.add(
				Component.translatable(SLOT_CAPACITY_TOOLTIP, Component.literal(String.valueOf(type.slotCapacity())).withStyle(ChatFormatting.WHITE))
						.withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable(ALLOWED_SIZE_TOOLTIP, Helpers.translateEnum(type.allowedSize()).withStyle(ChatFormatting.WHITE))
				.withStyle(ChatFormatting.GRAY));

		if (type.doesAutoPickup()) {
			tooltip.add(
					Component.translatable(PICKUP_TOOLTIP, SNSUtils.toggleTooltip(NBTHelper.isAutoPickup(itemStack))).withStyle(ChatFormatting.GRAY));
		}

		if (type.doesVoiding()) {
			tooltip.add(Component.translatable(VOID_TOOLTIP,
							SNSUtils.toggleTooltip(isVoidingEnabled(itemStack)))
					.withStyle(ChatFormatting.GRAY));
		}

		if (type.doesInventoryInteraction()) {
			tooltip.add(Component.translatable(INVENTORY_INTERACTION_TOOLTIP, SNSUtils.toggleTooltip(true)).withStyle(ChatFormatting.GRAY));
		}
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(final ItemStack itemStack) {
		if (!SNSConfig.CLIENT.displayItemContentsAsImages.get()) return super.getTooltipImage(itemStack);

		final var handler = itemStack.getCapability(Capabilities.ItemHandler.ITEM);
		if (handler == null) return super.getTooltipImage(itemStack);
		final int width, height;
		final int slotCount = handler.getSlots();
			switch (slotCount) {
				case 1 -> width = height = 1;
				case 4 -> width = height = 2;
				case 8 -> {
					width = 4;
					height = 2;
				}
				case 18 -> {
					width = 9;
					height = 2;
				}
				default -> {
					width = Math.min(9, slotCount);
					height = Math.ceilDiv(slotCount, width);
				}
			}
		final List<ItemStack> contents = new ArrayList<>(slotCount);
		for (int slot = 0; slot < slotCount; slot++) contents.add(handler.getStackInSlot(slot));
		return TooltipBlock.buildInventoryTooltip(contents, width, height);
	}

	@Override
	public boolean isFoil(final ItemStack itemStack) {
		return SNSConfig.CLIENT.voidGlint.get() ?
				isVoidingEnabled(itemStack) :
				NBTHelper.isAutoPickup(itemStack);
	}

	private static boolean isVoidingEnabled(final ItemStack itemStack) {
		final ItemVoider itemVoider = itemStack.getCapability(SNSCapabilities.ITEM_VOIDER);
		return itemVoider != null && itemVoider.isVoidingEnabled();
	}

	private void playRemoveOneSound(final Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	private void playInsertSound(final Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	@Override
	public Size getSize(final ItemStack itemStack) {
		return type.size(itemStack);
	}

	@Override
	public Weight getWeight(final ItemStack itemStack) {
		return type.weight(itemStack);
	}

}
