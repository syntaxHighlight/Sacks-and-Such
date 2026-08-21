package mod.traister101.sns.common.capability;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.*;
import java.util.function.IntConsumer;

public class SimpleItemVoider implements ItemVoider {

	private static final String VOID_SLOTS_KEY = "sns_void_slots";
	private final ItemStack owner;

	public SimpleItemVoider(final ItemStack owner) {
		this.owner = owner;
	}

	private BitSet voidSlots() {
		return BitSet.valueOf(owner.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getLongArray(VOID_SLOTS_KEY));
	}

	@Override
	public void forEachVoidSlot(final IntConsumer consumer) {
		voidSlots().stream().forEach(consumer);
	}

	@Override
	public boolean shouldSlotVoid(final int slotIndex) {
		return voidSlots().get(slotIndex);
	}

	@Override
	public boolean isVoidingEnabled() {
		return !voidSlots().isEmpty();
	}

	@Override
	public void toggleVoidSlot(final int slotIndex) {
		if (slotIndex < 0) throw new IllegalArgumentException("Slot index must be non-negative");
		final BitSet voidSlots = voidSlots();
		voidSlots.flip(slotIndex);
		CustomData.update(DataComponents.CUSTOM_DATA, owner, tag -> tag.putLongArray(VOID_SLOTS_KEY, voidSlots.toLongArray()));
	}
}
