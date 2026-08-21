package mod.traister101.sns;

import mod.traister101.sns.common.attribute.SNSAttributes;
import mod.traister101.sns.common.items.SNSItems;
import mod.traister101.sns.util.SNSUtils;
import mod.traister101.sns.util.handlers.PickupHandler;
import mod.traister101.sns.util.items.ItemSlot;

import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ProjectileWeaponItem;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.ArrowNockEvent;
import net.neoforged.bus.api.*;

import java.util.Objects;
import java.util.Optional;

public final class ForgeEventHandler {

	public static void init(final IEventBus eventBus) {
		eventBus.register(ForgeEventHandler.class);

		eventBus.addListener(PickupHandler::onPickupItem);
		// We want to handle this last to ensure we don't trample on anybody else
		eventBus.addListener(EventPriority.LOWEST, PickupHandler::onGroundBlockInteract);
	}

	@SubscribeEvent
	@SuppressWarnings("deprecation")
	public static void onProjectilePrepare(final ArrowNockEvent event) {
		if (!(event.getBow().getItem() instanceof final ProjectileWeaponItem projectileWeaponItem)) return;

		final var supportedProjectile = projectileWeaponItem.getAllSupportedProjectiles();

		final var maybeProjectileSlot = SNSUtils.curiosAndInventoryStream(event.getEntity())
				.flatMap(ItemSlot::stream)
				.filter(ItemSlot.contains(SNSItems.QUIVER.get()))
				.map(ItemSlot.extractCapability(Capabilities.ItemHandler.ITEM))
				.filter(Objects::nonNull)
				.map(quiverHandler -> SNSUtils.findFirstInHandler(quiverHandler, supportedProjectile))
				.flatMap(Optional::stream)
				.findFirst();

		maybeProjectileSlot.ifPresent(slot -> {
			event.setAction(InteractionResultHolder.consume(event.getBow()));
			event.getEntity().startUsingItem(event.getHand());
		});
	}

	@SubscribeEvent
	public static void onEntityFall(final LivingFallEvent event) {
		final var attribute = event.getEntity().getAttribute(SNSAttributes.EXTRA_FALL_DISTANCE);
		if (attribute == null) return;
		event.setDistance(Math.max(0, event.getDistance() - (float) attribute.getValue()));
	}
}
