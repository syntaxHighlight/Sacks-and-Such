package mod.traister101.sns.common.capability;

import mod.traister101.esc.common.capability.ExtendedComponentItemHandler;
import mod.traister101.esc.common.component.ExtendedItemContainerContents;
import mod.traister101.sns.common.component.SNSDataComponents;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.util.ContainerType;
import net.dries007.tfc.common.component.size.*;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.ItemStack;

public class ContainerItemHandler extends ExtendedComponentItemHandler {

	public final ContainerType type;
	private final ItemStack owner;

	@SuppressWarnings("this-escape")
	public ContainerItemHandler(final ContainerType type, final ItemStack owner) {
		super(owner, SNSDataComponents.CONTAINER_CONTENTS.get(), type.slotCount(), type.slotCapacity());
		this.type = type;
		this.owner = owner;
		migrateVanillaContents();
	}

	@Override
	public ItemStack insertItem(final int slotIndex, final ItemStack insertStack, final boolean simulate) {
		final ItemStack remainder = super.insertItem(slotIndex, insertStack, simulate);

		if (remainder.isEmpty()) return ItemStack.EMPTY;

		if (!SNSConfig.SERVER.doVoiding.get()) return remainder;

		if (!type.doesVoiding()) return remainder;

		final ItemVoider itemVoider = owner.getCapability(SNSCapabilities.ITEM_VOIDER);
		if (itemVoider != null && itemVoider.shouldSlotVoid(slotIndex)) {
			if (ItemStack.isSameItemSameComponents(insertStack, getStackInSlot(slotIndex))) return ItemStack.EMPTY;
		}

		return remainder;
	}

	@Override
	public boolean isItemValid(final int slotIndex, final ItemStack itemStack) {
		// Client menu initialization writes empty stacks when clearing synchronized slots.
		if (itemStack.isEmpty()) return true;

		if (itemStack.is(type.preventedItems())) return false;

		return type.allowedItems().map(itemStack::is).orElse(true) && fitsInSlot(itemStack);
	}

	@Override
	protected void onContentsChanged(final int slotIndex) {
		final DynamicWeight dynamicWeight = owner.getCapability(SNSCapabilities.DYNAMIC_WEIGHT);
		if (dynamicWeight != null) dynamicWeight.invalidate();
		super.onContentsChanged(slotIndex);
	}

	private void migrateVanillaContents() {
		if (owner.has(SNSDataComponents.CONTAINER_CONTENTS.get())) return;
		final ItemContainerContents vanillaContents = owner.get(DataComponents.CONTAINER);
		if (vanillaContents == null) return;

		final NonNullList<ItemStack> migrated = NonNullList.withSize(Math.max(vanillaContents.getSlots(), getSlots()), ItemStack.EMPTY);
		vanillaContents.copyInto(migrated);
		owner.set(SNSDataComponents.CONTAINER_CONTENTS.get(), ExtendedItemContainerContents.fromItems(migrated));
		for (int slot = 0; slot < stacks.size(); slot++) stacks.set(slot, migrated.get(slot).copy());
		owner.remove(DataComponents.CONTAINER);
	}

	/**
	 * @param itemStack The {@link ItemStack} to check
	 *
	 * @return If the provided {@link ItemStack} will fit inside our slots
	 */
	protected final boolean fitsInSlot(final ItemStack itemStack) {
		final IItemSize stackSize = ItemSizeManager.get(itemStack);
		final Size size = stackSize.getSize(itemStack);
		// Larger than the slot size
		return size.isEqualOrSmallerThan(type.allowedSize());
	}
}
