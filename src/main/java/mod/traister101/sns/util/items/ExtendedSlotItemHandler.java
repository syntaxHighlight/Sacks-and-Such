package mod.traister101.sns.util.items;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.*;

/** SlotItemHandler variant that exposes the handler's extended capacity. */
public final class ExtendedSlotItemHandler extends SlotItemHandler {

	private final int slotIndex;

	public ExtendedSlotItemHandler(final IItemHandler handler, final int slotIndex, final int x, final int y) {
		super(handler, slotIndex, x, y);
		this.slotIndex = slotIndex;
	}

	@Override
	public int getMaxStackSize(final ItemStack stack) {
		return getItemHandler().getSlotLimit(slotIndex);
	}
}
