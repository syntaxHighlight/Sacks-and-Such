package mod.traister101.sns.mixins.common.feature.quiver;

import com.llamalad7.mixinextras.expression.*;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import mod.traister101.sns.util.SNSUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.event.entity.living.LivingGetProjectileEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;

@Mixin(BowItem.class)
@SuppressWarnings("deprecation")
public class BowItemMixin {

	@Definition(id = "getProjectile", method = "Lnet/minecraft/world/entity/player/Player;getProjectile(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;")
	@Definition(id = "isEmpty", method = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z")
	@Expression("?.getProjectile(?).isEmpty()")
	@ModifyExpressionValue(method = "use", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean findProjectileInQuiver(final boolean projectileMissing, final @Local(argsOnly = true) Player player,
			final @Local(argsOnly = true) InteractionHand hand) {
		if (!projectileMissing) return false;

		final var bow = player.getItemInHand(hand);
		if (!(bow.getItem() instanceof final ProjectileWeaponItem projectileWeaponItem)) return true;

		return SNSUtils.findFirstSlotInQuiver(player, projectileWeaponItem.getAllSupportedProjectiles(bow)).isEmpty();
	}

	/**
	 * @reason The Forge {@link LivingGetProjectileEvent} doesn't work for us because it's fired twice in the
	 * process of drawing back the bow. First it's fired in {@link BowItem#use(Level, Player, InteractionHand)} (will not modify the stack) and then
	 * second it's fired in {@link BowItem#releaseUsing(ItemStack, Level, LivingEntity, int)} (will modify the stack) with no reasonable way to switch
	 * out the stack outside of handling the whole arrow shooting process via {@link ArrowLooseEvent}.
	 * @author Traister101
	 */
	@Definition(id = "getProjectile", method = "Lnet/minecraft/world/entity/player/Player;getProjectile(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;")
	@Expression("?.getProjectile(?)")
	@ModifyExpressionValue(method = "releaseUsing", at = @At("MIXINEXTRAS:EXPRESSION"))
	private ItemStack extractProjectileFromQuiver(final ItemStack originalProjectile, final ItemStack bow, final @Local Player player) {
		// Already found a projectile
		if (!originalProjectile.isEmpty()) return originalProjectile;

		if (!(bow.getItem() instanceof final ProjectileWeaponItem projectileWeaponItem)) return originalProjectile;

		return SNSUtils.extractProjectileFromQuiver(player, projectileWeaponItem.getAllSupportedProjectiles(bow)).orElse(originalProjectile);
	}
}
