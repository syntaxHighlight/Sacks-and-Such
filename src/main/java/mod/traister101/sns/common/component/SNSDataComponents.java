package mod.traister101.sns.common.component;

import mod.traister101.esc.common.component.ExtendedItemContainerContents;
import mod.traister101.sns.SacksNSuch;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

import net.neoforged.neoforge.registries.*;

public final class SNSDataComponents {

	public static final DeferredRegister.DataComponents DATA_COMPONENTS =
			DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SacksNSuch.MODID);
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ExtendedItemContainerContents>> CONTAINER_CONTENTS =
			DATA_COMPONENTS.registerComponentType("container_contents", builder -> builder
					.persistent(ExtendedItemContainerContents.CODEC)
					.networkSynchronized(ExtendedItemContainerContents.STREAM_CODEC)
					.cacheEncoding());

	private SNSDataComponents() {}
}
