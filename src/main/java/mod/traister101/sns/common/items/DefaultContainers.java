package mod.traister101.sns.common.items;

import mod.traister101.sns.common.SNSItemTags;
import mod.traister101.sns.common.capability.*;
import mod.traister101.sns.common.items.LunchBoxItem.LunchboxHandler;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.util.*;
import mod.traister101.sns.util.SimpleContainerType.ConstantSize;
import net.dries007.tfc.common.component.size.Weight;

import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public final class DefaultContainers {

	private static final Function<ItemStack, Weight> DYNAMIC_WEIGHT = DynamicWeight.weightFunction(Weight.VERY_HEAVY);

	public static final ContainerType STRAW_BASKET = SimpleContainerType.builder(SNSConfig.SERVER.strawBasket)
			.sizeFunction(ConstantSize.NORMAL)
			.weightFunction(DYNAMIC_WEIGHT)
			.preventedItems(SNSItemTags.PREVENTED_IN_STRAW_BASKET)
			.build();

	public static final ContainerType LEATHER_SACK = SimpleContainerType.builder(SNSConfig.SERVER.leatherSack)
			.sizeFunction(ConstantSize.NORMAL)
			.weightFunction(DYNAMIC_WEIGHT)
			.preventedItems(SNSItemTags.PREVENTED_IN_LEATHER_SACK)
			.build();

	public static final ContainerType BURLAP_SACK = SimpleContainerType.builder(SNSConfig.SERVER.burlapSack)
			.sizeFunction(ConstantSize.NORMAL)
			.weightFunction(DYNAMIC_WEIGHT)
			.preventedItems(SNSItemTags.PREVENTED_IN_BURLAP_SACK)
			.build();

	public static final ContainerType ORE_SACK = SimpleContainerType.builder(SNSConfig.SERVER.oreSack)
			.sizeFunction(ConstantSize.NORMAL)
			.weightFunction(DYNAMIC_WEIGHT)
			.preventedItems(SNSItemTags.PREVENTED_IN_ORE_SACK)
			.allowedItems(SNSItemTags.ALLOWED_IN_ORE_SACK)
			.build();

	public static final ContainerType SEED_POUCH = SimpleContainerType.builder(SNSConfig.SERVER.seedPouch)
			.sizeFunction(ConstantSize.NORMAL)
			.weightFunction(DYNAMIC_WEIGHT)
			.preventedItems(SNSItemTags.PREVENTED_IN_SEED_POUCH)
			.allowedItems(SNSItemTags.ALLOWED_IN_SEED_POUCH)
			.build();

	public static final ContainerType FRAME_PACK = SimpleContainerType.builder(SNSConfig.SERVER.framePack)
			.sizeFunction(ConstantSize.HUGE)
			.weightFunction(DYNAMIC_WEIGHT)
			.preventedItems(SNSItemTags.PREVENTED_IN_FRAME_PACK)
			.build();

	public static final ContainerType LUNCHBOX = SimpleContainerType.builder(SNSConfig.SERVER.lunchBox)
			.sizeFunction(ConstantSize.NORMAL)
			.weightFunction(DYNAMIC_WEIGHT)
			.preventedItems(SNSItemTags.PREVENTED_IN_LUNCHBOX)
			.allowedItems(SNSItemTags.LUNCHBOX_FOOD)
			.handlerFactory(LunchboxHandler::new)
			.build();

	public static final ContainerType QUIVER = SimpleContainerType.builder(SNSConfig.SERVER.quiver)
			.sizeFunction(ConstantSize.HUGE)
			.weightFunction(DYNAMIC_WEIGHT)
			.preventedItems(SNSItemTags.PREVENTED_IN_QUIVER)
			.allowedItems(SNSItemTags.ALLOWED_IN_QUIVER)
			.build();
}
