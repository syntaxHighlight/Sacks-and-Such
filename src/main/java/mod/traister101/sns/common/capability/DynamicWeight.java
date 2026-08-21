package mod.traister101.sns.common.capability;

import net.dries007.tfc.common.component.size.Weight;

import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public interface DynamicWeight {

	static Function<ItemStack, Weight> weightFunction(final Weight fallback) {
		return itemStack -> {
			final DynamicWeight dynamicWeight = itemStack.getCapability(SNSCapabilities.DYNAMIC_WEIGHT);
			return dynamicWeight != null ? dynamicWeight.getWeight() : fallback;
		};
	}

	void invalidate();

	Weight getWeight();
}
