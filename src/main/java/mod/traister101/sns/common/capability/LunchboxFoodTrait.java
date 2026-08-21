package mod.traister101.sns.common.capability;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.config.SNSConfig;
import net.dries007.tfc.common.component.food.*;

import net.neoforged.neoforge.registries.*;

public final class LunchboxFoodTrait {

	public static final String LUNCHBOX_LANG = SacksNSuch.MODID + ".tooltip.food_trait.lunchbox";
	public static final DeferredRegister<FoodTrait> TRAITS = DeferredRegister.create(FoodTraits.KEY, SacksNSuch.MODID);
	public static final DeferredHolder<FoodTrait, FoodTrait> LUNCHBOX = TRAITS.register("lunchbox",
			() -> new FoodTrait(() -> SNSConfig.SERVER.traitLunchboxModifier.get(), LUNCHBOX_LANG));

	public static void init() {
	}
}
