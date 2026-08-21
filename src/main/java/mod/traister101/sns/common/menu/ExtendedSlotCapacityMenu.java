package mod.traister101.sns.common.menu;

import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Minimal extended-capacity menu support formerly supplied by ESC. */
public abstract class ExtendedSlotCapacityMenu extends AbstractContainerMenu {

	protected static final int MAIN_INVENTORY = Inventory.INVENTORY_SIZE - Inventory.getSelectionSize();
	public final int containerSlots;

	protected ExtendedSlotCapacityMenu(final MenuType<?> type, final int windowId, final int containerSlots) {
		super(type, windowId);
		this.containerSlots = containerSlots;
	}

	@Override
	public ItemStack quickMoveStack(final Player player, final int slotIndex) {
		final Slot slot = getSlot(slotIndex);
		if (!slot.hasItem()) return ItemStack.EMPTY;
		final ItemStack original = slot.getItem().copy();
		final ItemStack moved = slot.getItem();
		final boolean success = slotIndex < containerSlots
				? moveItemStackTo(moved, containerSlots, slots.size(), true)
				: moveItemStackTo(moved, 0, containerSlots, false);
		if (!success) return ItemStack.EMPTY;
		if (moved.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
		else slot.setChanged();
		slot.onTake(player, moved);
		return original;
	}

	@Override
	protected boolean moveItemStackTo(final ItemStack moved, final int start, final int end, final boolean reverse) {
		boolean changed = false;
		int index = reverse ? end - 1 : start;
		while (!moved.isEmpty() && (reverse ? index >= start : index < end)) {
			final Slot slot = getSlot(index);
			final ItemStack existing = slot.getItem();
			if (!existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, moved) && slot.mayPlace(moved)) {
				final int capacity = slot.getMaxStackSize(moved) - existing.getCount();
				if (capacity > 0) {
					final int count = Math.min(capacity, moved.getCount());
					existing.grow(count);
					moved.shrink(count);
					slot.setChanged();
					changed = true;
				}
			}
			index += reverse ? -1 : 1;
		}
		index = reverse ? end - 1 : start;
		while (!moved.isEmpty() && (reverse ? index >= start : index < end)) {
			final Slot slot = getSlot(index);
			if (!slot.hasItem() && slot.mayPlace(moved)) {
				final int count = Math.min(slot.getMaxStackSize(moved), moved.getCount());
				slot.setByPlayer(moved.split(count));
				changed = true;
			}
			index += reverse ? -1 : 1;
		}
		return changed;
	}
}
