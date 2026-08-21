package mod.traister101.sns.client.screen;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.capability.SNSCapabilities;
import mod.traister101.sns.common.menu.ContainerItemMenu;
import mod.traister101.sns.network.*;
import mod.traister101.sns.util.ContainerType;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;

public class ContainerItemScreen extends AbstractContainerScreen<ContainerItemMenu> {

	private static final ResourceLocation TEXTURE = SacksNSuch.location("textures/gui/container.png");

	public ContainerItemScreen(final ContainerItemMenu menu, final Inventory inventory, final Component title) {
		super(menu, inventory, title);
	}

	@Override
	public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTick) {
		this.renderBackground(graphics, mouseX, mouseY, partialTick);
		super.render(graphics, mouseX, mouseY, partialTick);

		final var itemVoider = menu.getContainerStack().getCapability(SNSCapabilities.ITEM_VOIDER);
		if (itemVoider != null) {
			itemVoider.forEachVoidSlot(slotIndex -> {
				if (slotIndex < 0 || slotIndex >= menu.containerSlots) return;
				final Slot slot = menu.getSlot(slotIndex);

				final float minX = leftPos + slot.x - 1;
				final float minY = topPos + slot.y - 1;
				final float maxY = minY + 18;
				final float maxX = minX + 18;
				graphics.fill((int) minX, (int) minY, (int) maxX, (int) maxY, 0x668A2BE2);
			});
		}

		renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(final GuiGraphics graphics, final float partialTick, final int mouseX, final int mouseY) {
		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		drawSlots(graphics);
	}

	@Override
	protected void slotClicked(final Slot slot, final int slotIndex, final int mouseButton, final ClickType clickType) {
		if (!ContainerType.canDoItemVoiding(menu.getContainerStack())) {
			super.slotClicked(slot, slotIndex, mouseButton, clickType);
			return;
		}

		if (slotIndex >= menu.containerSlots || !Screen.hasControlDown()) {
			super.slotClicked(slot, slotIndex, mouseButton, clickType);
			return;
		}

		if ((mouseButton != 0 && mouseButton != 1)) {
			super.slotClicked(slot, slotIndex, mouseButton, clickType);
			return;
		}

		final var clickAction = mouseButton == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;
		if (clickAction == ClickAction.PRIMARY) {
			SNSPacketHandler.sendToServer(new ServerboundToggleSlotVoidingPacket(slotIndex));
			getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
			return;
		}

		super.slotClicked(slot, slotIndex, mouseButton, clickType);
	}

	/**
	 * Dynamically draws the slot texture to the screen where slots are located.
	 */
	private void drawSlots(final GuiGraphics graphics) {
		// TODO come up with good idea to restrict drawn slots? Don't want to require our own slot extension
		// Yes we draw every slot, even the player inventory ones we have baked into the texture
		// Despite this performance is not a concern. Drawing of the ItemStacks the slots contain is much more expensive
		for (final Slot slot : menu.slots) {
			final int x = leftPos + slot.x - 1;
			final int y = topPos + slot.y - 1;
			graphics.blit(TEXTURE, x, y, 176, 0, 18, 18);
		}
	}
}
