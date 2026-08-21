package mod.traister101.sns.common.capability;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.items.*;

import net.neoforged.neoforge.capabilities.*;

public final class SNSCapabilities {

	public static final ItemCapability<FoodHolder, Void> FOOD_HOLDER =
			ItemCapability.createVoid(SacksNSuch.location("food_holder"), FoodHolder.class);
	public static final ItemCapability<ItemVoider, Void> ITEM_VOIDER =
			ItemCapability.createVoid(SacksNSuch.location("item_voider"), ItemVoider.class);
	public static final ItemCapability<DynamicWeight, Void> DYNAMIC_WEIGHT =
			ItemCapability.createVoid(SacksNSuch.location("dynamic_weight"), DynamicWeight.class);

	public static void register(final RegisterCapabilitiesEvent event) {
		registerContainer(event, SNSItems.STRAW_BASKET.get());
		registerContainer(event, SNSItems.LEATHER_SACK.get());
		registerContainer(event, SNSItems.BURLAP_SACK.get());
		registerContainer(event, SNSItems.ORE_SACK.get());
		registerContainer(event, SNSItems.SEED_POUCH.get());
		registerContainer(event, SNSItems.FRAME_PACK.get());
		registerContainer(event, SNSItems.LUNCHBOX.get());
		registerContainer(event, SNSItems.QUIVER.get());

		event.registerItem(FOOD_HOLDER, (stack, context) -> new LunchBoxItem.LunchboxHandler(DefaultContainers.LUNCHBOX, stack),
				SNSItems.LUNCHBOX.get());
	}

	private static void registerContainer(final RegisterCapabilitiesEvent event, final ContainerItem item) {
		event.registerItem(Capabilities.ItemHandler.ITEM, (stack, context) -> item.type.createItemHandler(stack), item);
		event.registerItem(ITEM_VOIDER, (stack, context) -> new SimpleItemVoider(stack), item);
		event.registerItem(DYNAMIC_WEIGHT,
				(stack, context) -> new SimpleDynamicCachedWeight(stack, SimpleDynamicCachedWeight::percentageWeight), item);
	}

	private SNSCapabilities() {}
}
