package mod.traister101.sns.client;

import com.mojang.blaze3d.platform.InputConstants;
import mod.traister101.sns.common.capability.FoodHolder.CycleDirection;
import mod.traister101.sns.common.capability.SNSCapabilities;
import mod.traister101.sns.common.items.*;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.network.*;
import mod.traister101.sns.util.*;
import mod.traister101.sns.util.ItemSlotData.*;
import mod.traister101.sns.util.SNSUtils.ToggleType;

import net.minecraft.network.chat.*;
import net.minecraft.world.entity.EquipmentSlot;
import top.theillusivec4.curios.api.*;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.client.event.InputEvent.*;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.bus.api.IEventBus;

import java.util.Optional;

public final class ClientForgeEventHandler {

	public static void init(final IEventBus eventBus) {
		eventBus.addListener(ClientForgeEventHandler::onKeyPress);
		eventBus.addListener(ClientForgeEventHandler::onScreenKeyPress);
		eventBus.addListener(ClientForgeEventHandler::onMouseScroll);
	}

	public static void onKeyPress(final Key event) {
		// Sanity check
		final var minecraft = Minecraft.getInstance();
		final var player = minecraft.player;
		if (player == null) return;

		if (SNSKeybinds.OPEN_ITEM_CONTAINER.consumeClick()) {
			ItemSlotData slotData = null;
			if (SNSUtils.isCuriosPresent()) {
				slotData = CuriosApi.getCuriosInventory(player).map(ICuriosItemHandler::getCurios).flatMap(curios -> {
					for (final var identifier : SNSConfig.CLIENT.openItemContainerCuriosPriorities.get()) {
						final var handler = curios.get(identifier);
						if (handler == null) continue;
						final var itemHandler = handler.getStacks();
						return SNSUtils.findFirstInHandler(itemHandler, itemStack -> itemStack.getItem() instanceof ContainerItem)
								.map(itemHandlerSlot -> new CuriosSlotData(identifier, itemHandlerSlot.slotIndex()));
					}
					return Optional.empty();
				}).orElse(null);
				if (slotData == null) {
					final var maybeSlotResult = CuriosApi.getCuriosInventory(player)
							.flatMap(
									curiosItemHandler -> curiosItemHandler.findFirstCurio(itemStack -> itemStack.getItem() instanceof ContainerItem));
					if (maybeSlotResult.isPresent()) {
						final var slotResult = maybeSlotResult.get();
						final ItemStack itemStack = slotResult.stack();

						final var itemHandler = itemStack.getCapability(Capabilities.ItemHandler.ITEM);

						if (itemHandler != null) {
							final SlotContext slotContext = slotResult.slotContext();
							slotData = new CuriosSlotData(slotContext.identifier(), slotContext.index());
						}
					}
				}
			}

			final Inventory inventory = player.getInventory();
			if (slotData == null) for (int slotIndex = inventory.items.size() - 1; slotIndex >= 0; slotIndex--) {
				final ItemStack itemStack = inventory.items.get(slotIndex);

				if (!(itemStack.getItem() instanceof ContainerItem)) continue;

				if (Inventory.isHotbarSlot(slotIndex)) {
					inventory.selected = slotIndex;
					player.connection.send(new ServerboundSetCarriedItemPacket(slotIndex));
					slotData = new HeldSlotData(InteractionHand.MAIN_HAND);
					break;
				}

				slotData = new InventorySlotData(slotIndex);
				break;
			}

			if (slotData != null) {
				SNSPacketHandler.sendToServer(new ServerboundOpenContainerPacket(slotData));
			}
		}

		if (SNSKeybinds.TOGGLE_PICKUP.isDown()) {
			final ItemStack heldStack = player.getMainHandItem();
			final boolean flag = !NBTHelper.isAutoPickup(heldStack);
			SNSUtils.sendTogglePacket(ToggleType.PICKUP, flag);
			player.displayClientMessage(ToggleType.PICKUP.getTooltip(flag), true);
		}

		if (SNSKeybinds.TOGGLE_STEP_UP.isDown()) {
			final ItemStack wornBoots = player.getItemBySlot(EquipmentSlot.FEET);
			final boolean flag = !HikingBootsItem.isStepUpEnabled(wornBoots);
			player.displayClientMessage(Component.translatable(HikingBootsItem.STEP_UP_TOOLTIP, SNSUtils.toggleTooltip(flag)), true);
			SNSPacketHandler.sendToServer(new ServerboundToggleBootsStepUp());
		}
	}

	private static void onScreenKeyPress(final ScreenEvent.KeyPressed.Pre event) {
		if (SNSKeybinds.OPEN_HOVERED_ITEM_CONTAINER.isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode()))) {
			if (!(event.getScreen() instanceof final AbstractContainerScreen<?> containerScreen)) return;

			final var slotUnderMouse = containerScreen.getSlotUnderMouse();
			if (slotUnderMouse != null) {
				final var index = slotUnderMouse.getContainerSlot();
				final var minecraft = Minecraft.getInstance();
				final var player = minecraft.player;
				if (player == null) return;
				if (!(slotUnderMouse.getItem().getItem() instanceof ContainerItem)) return;

				ItemSlotData slotData = null;
				if (SNSUtils.isCuriosPresent()) {
					final var result = CuriosApi.getCuriosInventory(player)
							.flatMap(handler -> handler.findFirstCurio(stack -> stack == slotUnderMouse.getItem())).orElse(null);
					if (result != null) {
						final SlotContext context = result.slotContext();
						slotData = new CuriosSlotData(context.identifier(), context.index());
					}
				}

				final var inventory = player.getInventory();
				if (slotData == null && slotUnderMouse.container == inventory) {
					if (Inventory.isHotbarSlot(index)) {
						inventory.selected = index;
						player.connection.send(new ServerboundSetCarriedItemPacket(index));
						slotData = new HeldSlotData(InteractionHand.MAIN_HAND);
					} else slotData = new InventorySlotData(index);
				}

				if (slotData != null) {
					SNSPacketHandler.sendToServer(new ServerboundOpenContainerPacket(slotData));
				}
			}
		}
	}

	private static void onMouseScroll(final MouseScrollingEvent event) {
		// Sanity checks
		final var player = Minecraft.getInstance().player;
		if (player == null) return;

		final ItemStack mainHandStack = player.getMainHandItem();

		if (!mainHandStack.is(SNSItems.LUNCHBOX.get())) return;

		if (!player.isShiftKeyDown()) return;

		final double scrollDelta = event.getScrollDeltaY();

		final boolean scrollForwards = scrollDelta < 0;
		final boolean scrollBackwards = scrollDelta > 0;

		final var foodHolder = mainHandStack.getCapability(SNSCapabilities.FOOD_HOLDER);
		if (foodHolder == null) return;

		if (scrollForwards) {
			foodHolder.cycleSelected(CycleDirection.FORWARD);
			SNSPacketHandler.sendToServer(new ServerboundPacketCycleSlotPacket(CycleDirection.FORWARD));
		} else if (scrollBackwards) {
			foodHolder.cycleSelected(CycleDirection.BACKWARD);
			SNSPacketHandler.sendToServer(new ServerboundPacketCycleSlotPacket(CycleDirection.BACKWARD));
		}
		event.setCanceled(true);
	}
}
