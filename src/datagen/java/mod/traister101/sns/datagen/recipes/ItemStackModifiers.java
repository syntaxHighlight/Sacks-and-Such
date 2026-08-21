package mod.traister101.sns.datagen.recipes;

import net.dries007.tfc.common.recipes.outputs.*;
import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("unused")
public final class ItemStackModifiers {

	public static final ItemStackModifier COPY_INPUT = ItemStackModifier.of(CopyInputModifier.INSTANCE);
	public static final ItemStackModifier COPY_FOOD = ItemStackModifier.of(CopyFoodModifier.INSTANCE);
	public static final ItemStackModifier COPY_OLDEST_FOOD = ItemStackModifier.of(CopyOldestFoodModifier.INSTANCE);
	public static final ItemStackModifier COPY_HEAT = ItemStackModifier.of(CopyHeatModifier.INSTANCE);
	public static final ItemStackModifier COPY_FORGING_BONUS = ItemStackModifier.of(CopyForgingBonusModifier.INSTANCE);
	public static final ItemStackModifier RESET_FOOD = ItemStackModifier.of(ResetFoodModifier.INSTANCE);
	public static final ItemStackModifier EMPTY_BOWL = ItemStackModifier.of(EmptyBowlModifier.INSTANCE);
	public static final ItemStackModifier ADD_BAIT_TO_ROD = ItemStackModifier.of(AddBaitToRodModifier.INSTANCE);
	public static final ItemStackModifier ADD_GLASS = ItemStackModifier.of(AddGlassModifier.INSTANCE);
	public static final ItemStackModifier ADD_POWDER = ItemStackModifier.of(AddPowderModifier.INSTANCE);
}
