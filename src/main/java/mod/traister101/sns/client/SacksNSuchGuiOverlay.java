package mod.traister101.sns.client;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.capability.SNSCapabilities;
import mod.traister101.sns.common.items.*;
import mod.traister101.sns.util.SNSUtils;
import net.dries007.tfc.common.component.food.*;
import net.dries007.tfc.util.Helpers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.LayeredDraw.Layer;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.*;

public enum SacksNSuchGuiOverlay {

	LUNCHBOX_INFO("lunchbox_info", (guiGraphics, deltaTracker) -> {
		final Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.options.hideGui) return;
		final int screenWidth = guiGraphics.guiWidth();
		final int screenHeight = guiGraphics.guiHeight();
		final LocalPlayer player = minecraft.player;
		if (player == null) return;

		final ItemStack currentItem;
		{
			final ItemStack mainHandItem = player.getMainHandItem();
			if (!mainHandItem.is(SNSItems.LUNCHBOX.get())) {
				final ItemStack offhandItem = player.getOffhandItem();
				if (!offhandItem.is(SNSItems.LUNCHBOX.get())) return;
				currentItem = offhandItem;
			} else currentItem = mainHandItem;
		}

		final var lunchboxHandler = currentItem.getCapability(SNSCapabilities.FOOD_HOLDER);
		if (lunchboxHandler != null) {
			int nextLineOfText = renderComponent(minecraft.font, guiGraphics,
					Component.translatable(LunchBoxItem.SELECTED_SLOT_TOOLTIP, SNSUtils.intComponent(lunchboxHandler.getSelectedSlot() + 1)),
					screenWidth, screenHeight);

			final ItemStack selectedStack = lunchboxHandler.getSelectedStack();

			nextLineOfText = renderFormatedText(minecraft.font, guiGraphics, minecraft.font.split(selectedStack.getHoverName(), screenWidth / 4),
					screenWidth, nextLineOfText);

			final int itemStackX = screenWidth - 16;
			final int itemStackY = nextLineOfText - 16;
			guiGraphics.renderItem(selectedStack, itemStackX, itemStackY);
			guiGraphics.renderItemDecorations(minecraft.font, selectedStack, itemStackX, itemStackY);
			nextLineOfText -= 12;

			if (selectedStack.isEmpty()) return;

			if (!player.isShiftKeyDown()) return;

			final var food = FoodCapability.get(selectedStack);
			if (food == null) return;
			final var foodComponents = new ArrayList<Component>();
			{
				final var data = food.getData();

				foodComponents.add(Component.translatable("tfc.tooltip.nutrition").withStyle(ChatFormatting.GRAY));

				boolean hasData = false;
				if (!food.isRotten()) {
					{
						final float saturation = data.saturation();
						if (0 < saturation) {
							// This display makes it so 100% saturation means a full hunger bar worth of saturation.
							foodComponents.add(Component.translatable("tfc.tooltip.nutrition_saturation", String.format("%d", (int) (saturation * 5)))
									.withStyle(ChatFormatting.GRAY));
							hasData = true;
						}
					}
					{
						final int water = (int) data.water();
						if (0 < water) {
							foodComponents.add(
									Component.translatable("tfc.tooltip.nutrition_water", String.format("%d", water)).withStyle(ChatFormatting.GRAY));
							hasData = true;
						}
					}

					for (final var nutrient : Nutrient.VALUES) {
						final float value = data.nutrient(nutrient);
						if (0 >= value) continue;

						foodComponents.add(Component.literal(" - ")
								.append(Helpers.translateEnum(nutrient))
								.append(": " + String.format("%.1f", value))
								.withStyle(nutrient.getColor()));
						hasData = true;
					}
				}

				if (!hasData) {
					foodComponents.add(Component.translatable("tfc.tooltip.nutrition_none").withStyle(ChatFormatting.GRAY));
				}
			}
			renderComponents(minecraft.font, guiGraphics, foodComponents, screenWidth, nextLineOfText);
		}
	});

	private final String id;
	private final Layer overlay;

	SacksNSuchGuiOverlay(final String id, final Layer overlay) {
		this.id = id;
		this.overlay = overlay;
	}

	public static void registerOverlays(final RegisterGuiLayersEvent event) {
		event.registerAbove(VanillaGuiLayers.HOTBAR, SacksNSuch.location(LUNCHBOX_INFO.id()), LUNCHBOX_INFO.overlay);
	}

	/**
	 * @return Returns the position where the next line of text should reside on the Y axis
	 */
	private static int renderComponent(final Font font, final GuiGraphics guiGraphics, final Component component, final int xPos, final int startY) {
		return renderComponentsInternal(font, guiGraphics, List.of(ClientTooltipComponent.create(component.getVisualOrderText())), xPos, startY);
	}

	/**
	 * @return Returns the position where the next line of text should reside on the Y axis
	 */
	@SuppressWarnings("UnusedReturnValue")
	private static int renderComponents(final Font font, final GuiGraphics guiGraphics, final List<Component> components, final int xPos,
			final int startY) {
		return renderComponentsInternal(font, guiGraphics,
				components.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).toList(), xPos, startY);
	}

	/**
	 * @return Returns the position where the next line of text should reside on the Y axis
	 */
	private static int renderFormatedText(final Font font, final GuiGraphics guiGraphics, final List<FormattedCharSequence> text, final int xPos,
			final int startY) {
		return renderComponentsInternal(font, guiGraphics, text.stream().map(ClientTooltipComponent::create).toList(), xPos, startY);
	}

	/**
	 * @return Returns the position where the next line of text should reside on the Y axis
	 */
	private static int renderComponentsInternal(final Font font, final GuiGraphics guiGraphics,
			final List<ClientTooltipComponent> clientTooltipComponents, final int xPos, final int startY) {
		final int top = startY - clientTooltipComponents.stream().mapToInt(ClientTooltipComponent::getHeight).sum();
		int textHeight = top;
		for (final var component : clientTooltipComponents) {
			component.renderText(font, xPos - component.getWidth(font), textHeight, guiGraphics.pose().last().pose(), guiGraphics.bufferSource());
			textHeight += component.getHeight() + (textHeight == 0 ? 2 : 0);
		}
		return top;
	}

	public String id() {
		return id;
	}
}
