package mod.traister101.sns.common.items;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.capability.*;
import mod.traister101.sns.common.menu.SNSMenus;
import mod.traister101.sns.util.*;
import mod.traister101.sns.util.ItemSlotData.HeldSlotData;
import net.dries007.tfc.common.component.food.FoodCapability;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import java.util.*;

public class LunchBoxItem extends ContainerItem {

	public static final String SELECTED_SLOT_TOOLTIP = SacksNSuch.MODID + ".tooltip.lunchbox.selected_slot";

	public LunchBoxItem(final Properties properties, final ContainerType type) {
		super(properties, type);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
		final ItemStack heldStack = player.getItemInHand(hand);

		if (!player.isShiftKeyDown()) {
			final FoodHolder foodHolder = heldStack.getCapability(SNSCapabilities.FOOD_HOLDER);
			if (foodHolder != null) {
				final ItemStack targetFood = foodHolder.getSelectedStack();
				if (targetFood.isEmpty()) return InteractionResultHolder.pass(heldStack);
				final FoodProperties targetFoodProperties = targetFood.getFoodProperties(player);
				if (targetFoodProperties != null && !player.getCooldowns().isOnCooldown(targetFood.getItem())
						&& player.canEat(targetFoodProperties.canAlwaysEat())) {
					player.startUsingItem(hand);
					return InteractionResultHolder.consume(heldStack);
				}
				return InteractionResultHolder.pass(heldStack);
			}
			return InteractionResultHolder.pass(heldStack);
		}

		if (!level.isClientSide) {
			if (player.isShiftKeyDown()) {
				SNSMenus.CONTAINER_ITEM_MENU_PROVIDER.openMenu(player, new HeldSlotData(hand));
				return InteractionResultHolder.consume(heldStack);
			}
		}

		return InteractionResultHolder.pass(heldStack);
	}

	@Override
	public void appendHoverText(final ItemStack itemStack, final TooltipContext context, final List<Component> tooltip, final TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			final FoodHolder foodHolder = itemStack.getCapability(SNSCapabilities.FOOD_HOLDER);
			tooltip.add(Component.translatable(SELECTED_SLOT_TOOLTIP,
					SNSUtils.intComponent((foodHolder == null ? 0 : foodHolder.getSelectedSlot()) + 1)
							.withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GRAY));
			super.appendHoverText(itemStack, context, tooltip, flagIn);
			return;
		}

		super.appendHoverText(itemStack, context, tooltip, flagIn);
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(final ItemStack itemStack) {
		final var handler = itemStack.getCapability(Capabilities.ItemHandler.ITEM);
		if (handler == null) return super.getTooltipImage(itemStack);
		return Optional.of(handler).flatMap(itemHandler -> {
			final int width, height;
			switch (itemHandler.getSlots()) {
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
					width = Math.min(9, itemHandler.getSlots());
					height = Math.ceilDiv(itemHandler.getSlots(), width);
				}
			}

			final FoodHolder foodHolder = itemStack.getCapability(SNSCapabilities.FOOD_HOLDER);
			return foodHolder == null ? Optional.empty() :
					Optional.of(LunchboxTooltip.getTooltipImage(itemHandler, width, height, foodHolder.getSelectedSlot()));
		}).orElse(super.getTooltipImage(itemStack));
	}

	@Override
	public ItemStack finishUsingItem(final ItemStack itemStack, final Level level, final LivingEntity livingEntity) {
		final FoodHolder foodHolder = itemStack.getCapability(SNSCapabilities.FOOD_HOLDER);
		return foodHolder == null ? itemStack : foodHolder.consumeSelected(itemStack, level, livingEntity);
	}

	@Override
	public UseAnim getUseAnimation(final ItemStack itemStack) {
		return UseAnim.EAT;
	}

	@Override
	public int getUseDuration(final ItemStack itemStack, final LivingEntity livingEntity) {
		final FoodHolder foodHolder = itemStack.getCapability(SNSCapabilities.FOOD_HOLDER);
		return foodHolder == null ? 32 : foodHolder.getSelectedStack().getUseDuration(livingEntity);
	}

	@Nullable
	@Override
	public FoodProperties getFoodProperties(final ItemStack itemStack, final @Nullable LivingEntity entity) {
		final FoodHolder foodHolder = itemStack.getCapability(SNSCapabilities.FOOD_HOLDER);
		return foodHolder == null ? null : foodHolder.getSelectedFoodProperties(entity);
	}

	@Getter
	public static class LunchboxHandler extends ContainerItemHandler implements FoodHolder {

		public static final String SELECTED_SLOT_KEY = "selectedSlot";
		private final ItemStack owner;

		public LunchboxHandler(final ContainerType type, final ItemStack owner) {
			super(type, owner);
			this.owner = owner;
		}

		@Override
		public int getSelectedSlot() {
			return Math.clamp(owner.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt(SELECTED_SLOT_KEY), 0,
					getSlots() - 1);
		}

		@Override
		public ItemStack insertItem(final int slotIndex, final ItemStack insertStack, final boolean simulate) {
			final ItemStack insert = FoodCapability.applyTrait(insertStack.copy(), LunchboxFoodTrait.LUNCHBOX);
			final ItemStack remainder = super.insertItem(slotIndex, insert, simulate);
			return FoodCapability.removeTrait(remainder, LunchboxFoodTrait.LUNCHBOX);
		}

		@Override
		public void setStackInSlot(final int slotIndex, final ItemStack itemStack) {
			super.setStackInSlot(slotIndex, FoodCapability.applyTrait(itemStack, LunchboxFoodTrait.LUNCHBOX));
		}

		@Override
		public ItemStack extractItem(final int slotIndex, final int amount, final boolean simulate) {
			final ItemStack extractItem = super.extractItem(slotIndex, amount, simulate);
			return FoodCapability.removeTrait(extractItem, LunchboxFoodTrait.LUNCHBOX);
		}

		@Override
		public ItemStack getSelectedStack() {
			return getStackInSlot(getSelectedSlot());
		}

		@Override
		public void cycleSelected(final CycleDirection cycleDirection) {
			int nextSelection = getSelectedSlot() + switch (cycleDirection) {
				case FORWARD -> 1;
				case BACKWARD -> -1;
			};

			{ // Wrap to within slot bounds
				if (nextSelection >= getSlots()) {
					nextSelection -= getSlots();
				}

				if (nextSelection < 0) {
					nextSelection += getSlots();
				}
			}

			final int selection = nextSelection;
			CustomData.update(DataComponents.CUSTOM_DATA, owner, tag -> tag.putInt(SELECTED_SLOT_KEY, selection));
		}

		@Override
		public ItemStack consumeSelected(final ItemStack itemStack, final Level level, final LivingEntity livingEntity) {
			// This appears to be the best way to handle TFCs way of handling Dynamic food like bowls
			final ItemStack selectedFood = extractItem(getSelectedSlot(), 1, false);
			if (selectedFood.isEmpty()) return itemStack;
			final var foodRemainder = EventHooks.onItemUseFinish(livingEntity, selectedFood.copy(),
					livingEntity.getUseItemRemainingTicks(), livingEntity.eat(level, selectedFood));

			if (!foodRemainder.isEmpty()) {
				if (livingEntity instanceof final Player player) {
					ItemHandlerHelper.giveItemToPlayer(player, foodRemainder);
				} else {
					if (!foodRemainder.isEmpty() && !level.isClientSide) {
						final ItemEntity itemEntity = new ItemEntity(level, livingEntity.getX(), livingEntity.getY() + 0.5, livingEntity.getZ(),
								foodRemainder);
						itemEntity.setPickUpDelay(40);
						itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0, 1, 0));

						level.addFreshEntity(itemEntity);
					}
				}
			}

			while (getSelectedStack().isEmpty() && getSelectedSlot() != 0) {
				cycleSelected(CycleDirection.BACKWARD);
			}
			return itemStack;
		}
	}
}
