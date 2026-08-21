package mod.traister101.sns.common;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.items.MobNetItem;
import net.dries007.tfc.common.TFCTags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SNSEntityTags {

	/**
	 * TFCs livestock entity tag
	 */
	public static final TagKey<EntityType<?>> TFC_LIVESTOCK = TFCTags.Entities.FARM_ANIMALS;

	/**
	 * Mobs which can be captured with our {@link MobNetItem}
	 */
	public static final TagKey<EntityType<?>> NETABLE_MOBS = create("netable_mobs");

	@SuppressWarnings("SameParameterValue")
	private static TagKey<EntityType<?>> create(final String name) {
		return TagKey.create(Registries.ENTITY_TYPE, SacksNSuch.location(name));
	}
}
