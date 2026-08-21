package mod.traister101.sns;

import com.mojang.logging.LogUtils;
import mod.traister101.sns.client.*;
import mod.traister101.sns.common.SNSCreativeTab;
import mod.traister101.sns.common.attribute.SNSAttributes;
import mod.traister101.sns.common.capability.LunchboxFoodTrait;
import mod.traister101.sns.common.capability.SNSCapabilities;
import mod.traister101.sns.common.component.SNSDataComponents;
import mod.traister101.sns.common.items.SNSItems;
import mod.traister101.sns.common.menu.SNSMenus;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.network.SNSPacketHandler;
import org.slf4j.Logger;

import net.minecraft.resources.ResourceLocation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@SuppressWarnings("FieldMayBeFinal")
@Mod(SacksNSuch.MODID)
public final class SacksNSuch {

	public static final String MODID = "sns";
	public static final String NAME = "Sacks 'N Such";
	public static final Logger LOGGER = LogUtils.getLogger();

	public SacksNSuch(final IEventBus modBus, final ModContainer container, final Dist dist) {
		modBus.addListener(SacksNSuch::commonSetup);
		modBus.addListener(SacksNSuch::addEntityAttributes);
		modBus.addListener(SNSCapabilities::register);
		modBus.addListener(SNSPacketHandler::register);

		SNSItems.ITEMS.register(modBus);
		SNSDataComponents.DATA_COMPONENTS.register(modBus);
		SNSMenus.MENUS.register(modBus);
		SNSCreativeTab.CREATIVE_TABS.register(modBus);
		SNSAttributes.ATTRIBUTES.register(modBus);
		LunchboxFoodTrait.TRAITS.register(modBus);

		SNSConfig.init(container);
		SNSPacketHandler.init();
		ForgeEventHandler.init(NeoForge.EVENT_BUS);

		if (dist == Dist.CLIENT) {
			ClientEventHandler.init(modBus);
			ClientForgeEventHandler.init(NeoForge.EVENT_BUS);
		}
	}

	public static ResourceLocation location(final String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	private static void commonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(LunchboxFoodTrait::init);
	}

	private static void addEntityAttributes(final EntityAttributeModificationEvent event) {
		event.getTypes().forEach(entityType -> event.add(entityType, SNSAttributes.EXTRA_FALL_DISTANCE));
	}
}
