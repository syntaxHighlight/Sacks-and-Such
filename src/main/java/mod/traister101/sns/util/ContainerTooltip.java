package mod.traister101.sns.util;

import mod.traister101.sns.util.items.*;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.items.IItemHandler;

import org.jetbrains.annotations.Range;
import java.util.*;

public record ContainerTooltip(List<ItemStack> items, int width, int height) implements TooltipComponent {

	public static Optional<TooltipComponent> getTooltipImage(final IItemHandler handler, @Range(from = 0, to = Integer.MAX_VALUE) final int width,
			@Range(from = 0, to = Integer.MAX_VALUE) final int height) {
		final var stacks = ItemSlot.stream(handler).map(ItemHandlerSlot::getStack).toList();
		if (stacks.stream().allMatch(ItemStack::isEmpty)) {
			return Optional.empty();
		}

		assert width * height == stacks.size() : "Width: " + width + " * Height: " + height + " must be the same as the amount of slots: " + (stacks.size());

		return Optional.of(new ContainerTooltip(stacks, width, height));
	}
}
