package mod.traister101.sns.common.items;

import com.mojang.logging.LogUtils;
import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.SNSEntityTags;
import mod.traister101.sns.config.SNSConfig;
import net.dries007.tfc.common.component.size.*;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import org.slf4j.Logger;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;

import net.neoforged.neoforge.items.ItemHandlerHelper;

public class MobNetItem extends Item implements IItemSize {

	public static final String CAPTURED_MOB_KEY = "captured_mob";
	public static final String CANNOT_CAPTURE_PLAYERS = SacksNSuch.MODID + ".status.mob_net.cannot_capture_players";
	public static final String CANNOT_CAPTURE_GENERIC = SacksNSuch.MODID + ".status.mob_net.cannot_capture_generic";
	public static final String CANNOT_CAPTURE_SIZE = SacksNSuch.MODID + ".status.mob_net.cannot_capture_size";
	public static final String CANNOT_PLACE = SacksNSuch.MODID + ".status.mob_net.cannot_place";
	public static final String STACK_NAME = SacksNSuch.MODID + ".stack_name.mob_net.with_mob";
	private static final Logger LOGGER = LogUtils.getLogger();

	public MobNetItem(final Properties properties) {
		super(properties);
	}

	private static CompoundTag capturedMob(final ItemStack stack) {
		final CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		return tag.contains(CAPTURED_MOB_KEY) ? tag.getCompound(CAPTURED_MOB_KEY) : null;
	}

	private static void setCapturedMob(final ItemStack stack, final CompoundTag mobTag) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.put(CAPTURED_MOB_KEY, mobTag));
	}

	private static boolean hasCapturedMob(final ItemStack stack) {
		return capturedMob(stack) != null;
	}

	@Override
	public InteractionResult useOn(final UseOnContext context) {
		final Direction direction = context.getClickedFace();
		if (direction != Direction.UP) return InteractionResult.FAIL;

		final Level level = context.getLevel();
		final ItemStack heldStack = context.getItemInHand();
		final CompoundTag capturedMobTag = capturedMob(heldStack);
		if (capturedMobTag == null) return InteractionResult.FAIL;

		final Vec3 clickLocation = context.getClickLocation();
		final var optEntityType = EntityType.by(capturedMobTag);
		if (optEntityType.isEmpty()) return InteractionResult.FAIL;

		final EntityType<?> entityType = optEntityType.get();
		final AABB aabb = entityType.getDimensions().makeBoundingBox(clickLocation);

		final Player player = context.getPlayer();
		if (!level.noCollision(null, aabb) || !level.getEntities(null, aabb).isEmpty()) {
			if (player != null) player.displayClientMessage(Component.translatable(CANNOT_PLACE, entityType.getDescription()), true);
			return InteractionResult.FAIL;
		}

		final float rotation = (float) Mth.floor((Mth.wrapDegrees(context.getRotation() - 180) + 22.5) / 45) * 45;
		final Entity entity = entityType.create(level);

		if (entity == null) {
			LOGGER.warn("Cannot load {} from mob net.", entityType);
			return InteractionResult.FAIL;
		}

		entity.load(capturedMobTag);
		entity.moveTo(clickLocation.x, clickLocation.y, clickLocation.z, rotation, 0);
		level.addFreshEntity(entity);
		entity.gameEvent(GameEvent.ENTITY_PLACE, player);

		if (!level.isClientSide) {
			if (player != null) {
				CustomData.update(DataComponents.CUSTOM_DATA, heldStack, tag -> tag.remove(CAPTURED_MOB_KEY));
				if (player.getRandom().nextFloat() < 0.75) {
					heldStack.shrink(1);
				} else {
					player.setItemInHand(context.getHand(), heldStack);
				}
			}
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public InteractionResult interactLivingEntity(final ItemStack itemStack, final Player player, final LivingEntity livingEntity,
			final InteractionHand hand) {
		final EntityType<?> entityType = livingEntity.getType();
		if (entityType == EntityType.PLAYER) {
			player.displayClientMessage(Component.translatable(CANNOT_CAPTURE_PLAYERS), true);
			return InteractionResult.FAIL;
		}

		if (!entityType.is(SNSEntityTags.NETABLE_MOBS)) {
			player.displayClientMessage(Component.translatable(CANNOT_CAPTURE_GENERIC, entityType.getDescription()), true);
			return InteractionResult.FAIL;
		}

		{
			final float entitySize = livingEntity instanceof final TFCAnimalProperties animalProperties ?
					livingEntity.getBbWidth() * animalProperties.getAgeScale() :
					livingEntity.getBbWidth();

			if (entitySize >= SNSConfig.SERVER.maximumNetCaptureSize.get()) {
				player.displayClientMessage(Component.translatable(CANNOT_CAPTURE_SIZE, entityType.getDescription()), true);
				return InteractionResult.FAIL;
			}
		}

		final CompoundTag compoundTag = new CompoundTag();
		if (!livingEntity.save(compoundTag)) {
			LOGGER.warn("Failed to save {}", livingEntity.getType());
			return InteractionResult.FAIL;
		}

		if (itemStack.getCount() > 1) {
			final ItemStack split = itemStack.split(1);
			setCapturedMob(split, compoundTag);
			ItemHandlerHelper.giveItemToPlayer(player, split);
		} else {
			setCapturedMob(itemStack, compoundTag);
		}

		livingEntity.remove(RemovalReason.KILLED);
		return InteractionResult.sidedSuccess(player.level().isClientSide);
	}

	@Override
	public Component getName(final ItemStack itemStack) {
		final CompoundTag capturedMobTag = capturedMob(itemStack);
		if (capturedMobTag == null) return super.getName(itemStack);

		return EntityType.by(capturedMobTag)
				.<Component>map(entityType -> Component.translatable(STACK_NAME, super.getName(itemStack), entityType.getDescription()))
				.orElse(super.getName(itemStack));
	}

	@Override
	public Size getSize(final ItemStack itemStack) {
		return hasCapturedMob(itemStack) ? Size.HUGE : Size.LARGE;
	}

	@Override
	public Weight getWeight(final ItemStack itemStack) {
		return hasCapturedMob(itemStack) ? Weight.VERY_HEAVY : Weight.MEDIUM;
	}

	@Override
	public int getMaxStackSize(final ItemStack itemStack) {
		return hasCapturedMob(itemStack) ? 1 : 16;
	}
}
