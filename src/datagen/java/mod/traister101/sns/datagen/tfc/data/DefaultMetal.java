package mod.traister101.sns.datagen.tfc.data;

import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Metal;

import net.minecraft.world.level.material.Fluid;

public enum DefaultMetal implements MetalData {
	BISMUTH(0.14F, 270, Metal.BISMUTH),
	BISMUTH_BRONZE(0.35F, 985, Metal.BISMUTH_BRONZE),
	BLACK_BRONZE(0.35F, 1070, Metal.BLACK_BRONZE),
	BRONZE(0.35F, 950, Metal.BRONZE),
	BRASS(0.35F, 930, Metal.BRASS),
	COPPER(0.35F, 1080, Metal.COPPER),
	GOLD(0.6F, 1060, Metal.GOLD),
	NICKEL(0.48F, 1453, Metal.NICKEL),
	ROSE_GOLD(0.35F, 960, Metal.ROSE_GOLD),
	SILVER(0.48F, 961, Metal.SILVER),
	TIN(0.14F, 230, Metal.TIN),
	ZINC(0.21F, 420, Metal.ZINC),
	STERLING_SILVER(0.35F, 950, Metal.STERLING_SILVER),
	WROUGHT_IRON(0.35F, 1535, Metal.CAST_IRON),
	CAST_IRON(0.35F, 1535, Metal.CAST_IRON),
	PIG_IRON(0.35F, 1535, Metal.CAST_IRON),
	STEEL(0.35F, 1540, Metal.STEEL),
	BLACK_STEEL(0.35F, 1485, Metal.BLACK_STEEL),
	BLUE_STEEL(0.35F, 1540, Metal.BLUE_STEEL),
	RED_STEEL(0.35F, 1540, Metal.RED_STEEL),
	WEAK_STEEL(0.35F, 1540, Metal.WEAK_STEEL),
	WEAK_BLUE_STEEL(0.35F, 1540, Metal.WEAK_BLUE_STEEL),
	WEAK_RED_STEEL(0.35F, 1540, Metal.WEAK_RED_STEEL),
	HIGH_CARBON_STEEL(0.35F, 1540, Metal.PIG_IRON),
	HIGH_CARBON_BLACK_STEEL(0.35F, 1540, Metal.WEAK_STEEL),
	HIGH_CARBON_BLUE_STEEL(0.35F, 1540, Metal.WEAK_BLUE_STEEL),
	HIGH_CARBON_RED_STEEL(0.35F, 1540, Metal.WEAK_RED_STEEL),
	UNKNOWN_METAL(0.5F, 400, Metal.UNKNOWN);

	public final float meltTemp;
	private final Metal tfcMetal;
	private final float baseHeatCapacity;

	DefaultMetal(final float baseHeatCapacity, final float meltTemp, final Metal tfcMetal) {
		this.tfcMetal = tfcMetal;
		this.baseHeatCapacity = baseHeatCapacity;
		this.meltTemp = meltTemp;
	}

	@Override
	public float getMeltTemp() {
		return this.meltTemp;
	}

	@Override
	public float specificHeatCapacity() {
		return MetalData.specificHeatCapacity(baseHeatCapacity);
	}

	@Override
	public float ingotHeatCapacity() {
		return MetalData.ingotHeatCapacity(baseHeatCapacity);
	}

	@Override
	public int metalTier() {
		return tfcMetal.tier();
	}

	@Override
	public Fluid meltMetal() {
		return TFCFluids.METALS.get(tfcMetal).getSource();
	}
}
