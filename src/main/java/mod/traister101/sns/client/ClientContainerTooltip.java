package mod.traister101.sns.client;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.util.ContainerTooltip;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import lombok.AllArgsConstructor;

/**
 * Renders container item contents as a slot grid with a border that strictly follows the grid dimensions.
 *
 * @apiNote The {@link #TEXTURE_LOCATION} texture is a copy of TFC's {@code device_image_tooltip.png}
 * (EUPL-1.2, https://github.com/TerraFirmaCraft/TerraFirmaCraft) to match TFC's visual style.
 */
@AllArgsConstructor
public class ClientContainerTooltip implements ClientTooltipComponent {

	private static final ResourceLocation TEXTURE_LOCATION = SacksNSuch.location("textures/gui/container_tooltip.png");

	private final ContainerTooltip tooltip;

	@Override
	public int getHeight() {
		return tooltip.height() * 20 + 2 + 4;
	}

	@Override
	public int getWidth(final Font font) {
		return tooltip.width() * 18 + 2;
	}

	@Override
	public void renderImage(final Font font, final int x, final int y, final GuiGraphics graphics) {
		final int width = tooltip.width();
		final int height = tooltip.height();

		for (var row = 0; row < height; row++) {
			for (var column = 0; column < width; column++) {
				final int slotX = x + column * 18 + 1;
				final int slotY = y + row * 20 + 1;
				final int slotIndex = row * width + column;
				renderSlot(graphics, font, slotX, slotY, slotIndex, tooltip.items().get(slotIndex));
			}
		}

		drawBorder(graphics, x, y, width, height);
	}

	private static void renderSlot(final GuiGraphics graphics, final Font font, final int x, final int y, final int slotIndex,
			final ItemStack itemStack) {
		blit(graphics, x, y, 0, 0, 18, 20);
		graphics.renderItem(itemStack, x + 1, y + 1, slotIndex);
		graphics.renderItemDecorations(font, itemStack, x + 1, y + 1);
	}

	private static void drawBorder(final GuiGraphics graphics, final int x, final int y, final int slotWidth, final int slotHeight) {
		blit(graphics, x, y, 0, 20, 1, 1);
		blit(graphics, x + slotWidth * 18 + 1, y, 0, 20, 1, 1);

		for (int column = 0; column < slotWidth; column++) {
			blit(graphics, x + 1 + column * 18, y, 0, 20, 18, 1);
			blit(graphics, x + 1 + column * 18, y + slotHeight * 20, 0, 60, 18, 1);
		}

		for (int row = 0; row < slotHeight; row++) {
			blit(graphics, x, y + row * 20 + 1, 0, 18, 1, 20);
			blit(graphics, x + slotWidth * 18 + 1, y + row * 20 + 1, 0, 18, 1, 20);
		}

		blit(graphics, x, y + slotHeight * 20, 0, 60, 1, 1);
		blit(graphics, x + slotWidth * 18 + 1, y + slotHeight * 20, 0, 60, 1, 1);
	}

	private static void blit(final GuiGraphics graphics, final int x, final int y, final int u, final int v, final int width, final int height) {
		graphics.blit(TEXTURE_LOCATION, x, y, u, v, width, height, 128, 128);
	}
}
