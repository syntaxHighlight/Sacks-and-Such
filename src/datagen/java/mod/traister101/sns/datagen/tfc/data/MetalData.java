package mod.traister101.sns.datagen.tfc.data;

import net.minecraft.world.level.material.Fluid;

public interface MetalData {

	static float specificHeatCapacity(final float baseHeatCapacity) {
		return 300 / baseHeatCapacity / 100_000;
	}

	static float ingotHeatCapacity(final float baseHeatCapacity) {
		return 1 / baseHeatCapacity;
	}

	int metalTier();

	Fluid meltMetal();

	float getMeltTemp();

	float specificHeatCapacity();

	float ingotHeatCapacity();
}
