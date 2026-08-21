package mod.traister101.sns.common.attribute;

import mod.traister101.sns.SacksNSuch;

import net.minecraft.world.entity.ai.attributes.*;

import net.neoforged.neoforge.registries.*;

public final class SNSAttributes {

	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(net.minecraft.core.registries.Registries.ATTRIBUTE,
			SacksNSuch.MODID);

	public static final DeferredHolder<Attribute, Attribute> EXTRA_FALL_DISTANCE = ATTRIBUTES.register("extra_fall_distance",
			() -> new RangedAttribute(SacksNSuch.MODID + ".extra_fall_distance", 0, 0, 64).setSyncable(true));
}
