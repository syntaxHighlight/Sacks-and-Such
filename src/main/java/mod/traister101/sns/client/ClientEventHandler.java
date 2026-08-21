package mod.traister101.sns.client;

import mod.traister101.sns.client.models.*;
import mod.traister101.sns.client.screen.ContainerItemScreen;
import mod.traister101.sns.common.menu.SNSMenus;
import mod.traister101.sns.compat.curios.CuriosCompat;
import mod.traister101.sns.util.*;

import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ClientEventHandler {

	public static void init(final IEventBus modEventBus) {
		modEventBus.addListener(ClientEventHandler::onClientSetup);
		modEventBus.addListener(ClientEventHandler::onRegisterClientTooltip);
		modEventBus.addListener(ClientEventHandler::registerScreens);
		modEventBus.addListener(ClientEventHandler::registerKeyBindings);
		modEventBus.addListener(ClientEventHandler::registerLayers);
		modEventBus.addListener(SacksNSuchGuiOverlay::registerOverlays);
	}

	private static void onClientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			if (SNSUtils.isCuriosPresent()) CuriosCompat.clientSetup();
		});
	}

	private static void registerScreens(final RegisterMenuScreensEvent event) {
		event.register(SNSMenus.CONTAINER_ITEM_MENU.get(), ContainerItemScreen::new);
	}

	private static void onRegisterClientTooltip(final RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(LunchboxTooltip.class, ClientLunchboxTooltip::new);
	}

	private static void registerKeyBindings(final RegisterKeyMappingsEvent event) {
		event.register(SNSKeybinds.TOGGLE_PICKUP);
		event.register(SNSKeybinds.OPEN_ITEM_CONTAINER);
		event.register(SNSKeybinds.OPEN_HOVERED_ITEM_CONTAINER);
		event.register(SNSKeybinds.TOGGLE_STEP_UP);
	}

	private static void registerLayers(final RegisterLayerDefinitions event) {
		event.registerLayerDefinition(FramePackModel.LAYER_LOCATION, FramePackModel::createBodyLayer);
		event.registerLayerDefinition(SmallSackModel.LAYER_LOCATION, SmallSackModel::createBodyLayer);
		event.registerLayerDefinition(LargeSackModel.LAYER_LOCATION, LargeSackModel::createBodyLayer);
		event.registerLayerDefinition(FancyHikingBootsModel.LAYER_LOCATION, FancyHikingBootsModel::createBodyLayer);
		event.registerLayerDefinition(VanillaHikingBootsModel.LAYER_LOCATION, VanillaHikingBootsModel::createBodyLayer);
		event.registerLayerDefinition(NoFloofHikingBootsModel.LAYER_LOCATION, NoFloofHikingBootsModel::createBodyLayer);
	}
}
