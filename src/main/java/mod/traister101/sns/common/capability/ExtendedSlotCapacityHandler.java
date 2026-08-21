package mod.traister101.sns.common.capability;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.items.ComponentItemHandler;

/** Item-backed handler whose slots may exceed the item's normal maximum stack size. */
public class ExtendedSlotCapacityHandler extends ComponentItemHandler {

	private final int slotStackLimit;

	public ExtendedSlotCapacityHandler(final ItemStack owner, final int slotCount, final int slotStackLimit) {
		super(owner, DataComponents.CONTAINER, slotCount);
		if (slotStackLimit < 1) throw new IllegalArgumentException("Slot capacity must be positive");
		this.slotStackLimit = slotStackLimit;
	}

	@Override
	public int getSlotLimit(final int slotIndex) {
		validateSlotIndex(slotIndex);
		return slotStackLimit;
	}

	protected int getStackLimit(final int slotIndex, final ItemStack stack) {
		if (stack.getMaxStackSize() == 1 || stack.isDamageableItem()) return stack.getMaxStackSize();
		return getSlotLimit(slotIndex);
	}

	@Override
	public ItemStack insertItem(final int slotIndex, final ItemStack insertStack, final boolean simulate) {
		validateSlotIndex(slotIndex);
		if (insertStack.isEmpty()) return ItemStack.EMPTY;
		if (!isItemValid(slotIndex, insertStack)) return insertStack;

		final var contents = getContents();
		final ItemStack existing = getStackFromContents(contents, slotIndex);
		int available = getStackLimit(slotIndex, insertStack);
		if (!existing.isEmpty()) {
			if (!ItemStack.isSameItemSameComponents(insertStack, existing)) return insertStack;
			available -= existing.getCount();
		}
		if (available <= 0) return insertStack;

		final int inserted = Math.min(available, insertStack.getCount());
		if (!simulate) updateContents(contents, insertStack.copyWithCount(existing.getCount() + inserted), slotIndex);
		return inserted == insertStack.getCount() ? ItemStack.EMPTY : insertStack.copyWithCount(insertStack.getCount() - inserted);
	}
}
