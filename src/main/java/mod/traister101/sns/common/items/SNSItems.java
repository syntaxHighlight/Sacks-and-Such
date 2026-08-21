package mod.traister101.sns.common.items;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.SNSArmorMaterials;
import mod.traister101.sns.common.items.HikingBootsItem.HikingBootProperties;
import mod.traister101.sns.common.items.HorseshoesItem.HorseshoesProperties;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.config.entries.HorseshoesConfig;
import mod.traister101.sns.util.ContainerType;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.RegistryMetal;

import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.core.registries.Registries;

import net.neoforged.neoforge.registries.*;

import java.util.function.Function;

@SuppressWarnings("unused")
public final class SNSItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, SacksNSuch.MODID);

	// Crafting items
	public static final DeferredHolder<Item, Item> UNFINISHED_LEATHER_SACK = registerSimple("unfinished_leather_sack");
	public static final DeferredHolder<Item, Item> REINFORCED_FIBER = registerSimple("reinforced_fiber");
	public static final DeferredHolder<Item, Item> REINFORCED_FABRIC = registerSimple("reinforced_fabric");
	public static final DeferredHolder<Item, Item> PACK_FRAME = registerSimple("pack_frame", new Properties().rarity(Rarity.UNCOMMON));
	public static final DeferredHolder<Item, Item> LEATHER_STRIP = registerSimple("leather_strip");
	public static final DeferredHolder<Item, Item> BOUND_LEATHER_STRIP = registerSimple("bound_leather_strip");
	public static final DeferredHolder<Item, Item> BUCKLE = registerSimple("buckle");
	public static final DeferredHolder<Item, Item> STEEL_HORSESHOE = registerHorseshoe(Metal.STEEL);
	public static final DeferredHolder<Item, Item> BLACK_STEEL_HORSESHOE = registerHorseshoe(Metal.BLACK_STEEL);
	public static final DeferredHolder<Item, Item> BLUE_STEEL_HORSESHOE = registerHorseshoe(Metal.BLUE_STEEL);
	public static final DeferredHolder<Item, Item> RED_STEEL_HORSESHOE = registerHorseshoe(Metal.RED_STEEL);

	// Container Items
	public static final DeferredHolder<Item, ContainerItem> STRAW_BASKET = registerContainerItem("straw_basket", DefaultContainers.STRAW_BASKET);
	public static final DeferredHolder<Item, ContainerItem> LEATHER_SACK = registerContainerItem("leather_sack", DefaultContainers.LEATHER_SACK);
	public static final DeferredHolder<Item, ContainerItem> BURLAP_SACK = registerContainerItem("burlap_sack", DefaultContainers.BURLAP_SACK);
	public static final DeferredHolder<Item, ContainerItem> ORE_SACK = registerContainerItem("ore_sack", DefaultContainers.ORE_SACK);
	public static final DeferredHolder<Item, ContainerItem> SEED_POUCH = registerContainerItem("seed_pouch", DefaultContainers.SEED_POUCH);
	public static final DeferredHolder<Item, ContainerItem> FRAME_PACK = registerContainerItem("frame_pack", DefaultContainers.FRAME_PACK,
			new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	public static final DeferredHolder<Item, LunchBoxItem> LUNCHBOX = register("lunchbox",
			properties -> new LunchBoxItem(properties, DefaultContainers.LUNCHBOX));
	public static final DeferredHolder<Item, ContainerItem> QUIVER = registerContainerItem("quiver", DefaultContainers.QUIVER);

	public static final DeferredHolder<Item, MobNetItem> MOB_NET_ITEM = register("mob_net", MobNetItem::new);

	public static final DeferredHolder<Item, HikingBootsItem> HIKING_BOOTS = register("hiking_boots",
			properties -> new HikingBootsItem(properties, SNSArmorMaterials.HIKING_BOOTS,
					HikingBootProperties.fromConfig(SNSConfig.SERVER.hikingBoots)), new Properties().stacksTo(1));

	public static final DeferredHolder<Item, HikingBootsItem> STEEL_TOE_HIKING_BOOTS = register("steel_toe_hiking_boots",
			properties -> new HikingBootsItem(properties, SNSArmorMaterials.STEEL_TOE_HIKING_BOOTS,
					HikingBootProperties.fromConfig(SNSConfig.SERVER.steelToeHikingBoots)), new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

	public static final DeferredHolder<Item, HikingBootsItem> BLACK_STEEL_TOE_HIKING_BOOTS = register("black_steel_toe_hiking_boots",
			properties -> new HikingBootsItem(properties, SNSArmorMaterials.BLACK_STEEL_TOE_HIKING_BOOTS,
					HikingBootProperties.fromConfig(SNSConfig.SERVER.blackSteelToeHikingBoots)), new Properties().stacksTo(1).rarity(Rarity.RARE));

	public static final DeferredHolder<Item, HikingBootsItem> BLUE_STEEL_TOE_HIKING_BOOTS = register("blue_steel_toe_hiking_boots",
			properties -> new HikingBootsItem(properties, SNSArmorMaterials.BLUE_STEEL_TOE_HIKING_BOOTS,
					HikingBootProperties.fromConfig(SNSConfig.SERVER.blueSteelToeHikingBoots)), new Properties().stacksTo(1).rarity(Rarity.EPIC));

	public static final DeferredHolder<Item, HikingBootsItem> RED_STEEL_TOE_HIKING_BOOTS = register("red_steel_toe_hiking_boots",
			properties -> new HikingBootsItem(properties, SNSArmorMaterials.RED_STEEL_TOE_HIKING_BOOTS,
					HikingBootProperties.fromConfig(SNSConfig.SERVER.redSteelToeHikingBoots)), new Properties().stacksTo(1).rarity(Rarity.EPIC));

	public static final DeferredHolder<Item, HorseshoesItem> STEEL_HORSESHOES = registerHorseShoes(Metal.STEEL, SNSConfig.SERVER.steelHorseshoes);

	public static final DeferredHolder<Item, HorseshoesItem> BLACK_STEEL_HORSESHOES = registerHorseShoes(Metal.BLACK_STEEL,
			SNSConfig.SERVER.blackSteelHorseshoes);

	public static final DeferredHolder<Item, HorseshoesItem> BLUE_STEEL_HORSESHOES = registerHorseShoes(Metal.BLUE_STEEL,
			SNSConfig.SERVER.blueSteelHorseshoes);

	public static final DeferredHolder<Item, HorseshoesItem> RED_STEEL_HORSESHOES = registerHorseShoes(Metal.RED_STEEL,
			SNSConfig.SERVER.redSteelHorseshoes);

	private static DeferredHolder<Item, Item> registerHorseshoe(final RegistryMetal metal) {
		return registerSimple("metal/horseshoe/" + metal.getSerializedName(), new Properties().rarity(metal.rarity()));
	}

	private static DeferredHolder<Item, HorseshoesItem> registerHorseShoes(final RegistryMetal metal, final HorseshoesConfig horseshoesConfig) {
		return register("metal/horseshoes/" + metal.getSerializedName(),
				properties -> new HorseshoesItem(properties, HorseshoesProperties.fromConfig(horseshoesConfig)),
				new Properties().durability(metal.toolTier().getUses()).rarity(metal.rarity()));
	}

	private static DeferredHolder<Item, ContainerItem> registerContainerItem(final String name, final ContainerType containerType) {
		return registerContainerItem(name, containerType, new Properties().stacksTo(1));
	}

	private static DeferredHolder<Item, ContainerItem> registerContainerItem(final String name, final ContainerType containerType,
			final Properties properties) {
		return register(name, prop -> new ContainerItem(prop, containerType), properties);
	}

	private static DeferredHolder<Item, Item> registerSimple(final String name) {
		return register(name, Item::new);
	}

	private static DeferredHolder<Item, Item> registerSimple(final String name, final Properties properties) {
		return register(name, Item::new, properties);
	}

	private static <I extends Item> DeferredHolder<Item, I> register(final String name, final Function<Properties, ? extends I> itemFactory) {
		return register(name, itemFactory, new Properties());
	}

	private static <I extends Item> DeferredHolder<Item, I> register(final String name, final Function<Properties, ? extends I> itemFactory,
			final Properties properties) {
		return ITEMS.register(name, () -> itemFactory.apply(properties));
	}
}
