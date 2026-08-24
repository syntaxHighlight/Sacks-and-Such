package mod.traister101.sns.mixins.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.*;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mod.traister101.sns.common.attribute.SNSAttributes;
import mod.traister101.sns.common.items.HorseshoesItem;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal {

	@Shadow
	protected SimpleContainer inventory;

	protected AbstractHorseMixin(final EntityType<? extends Animal> pEntityType, final Level pLevel) {
		super(pEntityType, pLevel);
	}

	/**
	 * @reason We want to damage our horseshoes when they are used
	 * @author Traister101
	 */
	@Inject(method = "tick", at = @At(value = "TAIL"))
	private void tickHorseshoe(final CallbackInfo ci) {
		final ItemStack itemStack = inventory.getItem(HorseshoesItem.getHorseshoesSlot(sns$self()));
		if (itemStack.getItem() instanceof HorseshoesItem horseshoes) horseshoes.horseshoeTick(itemStack, level(), sns$self());

		final ItemStack mirrored = getHorseshoes();
		// Our FEET slot mirrors the dedicated inventory slot, but nothing notifies us
		// when the stack is mutated in place (e.g. breaking from durability) or when
		// vanilla recreates the inventory. Deliberately not comparing components,
		// they change every step taken and don't affect the applied modifiers
		if (itemStack.getItem() != mirrored.getItem() || itemStack.getCount() != mirrored.getCount())
			setHorseshoeEquipment(itemStack);
	}

	/**
	 * @reason Equipping a chest recreates the inventory with more slots which leaves
	 *         horseshoes stranded in a storage slot while their dedicated slot sits
	 *         empty, move them back into the dedicated slot
	 * @author Traister101
	 */
	@Inject(method = "createInventory", at = @At(value = "TAIL"))
	private void migrateHorseshoes(final CallbackInfo ci) {
		final int horseshoesSlot = HorseshoesItem.getHorseshoesSlot(sns$self());
		if (!inventory.getItem(horseshoesSlot).isEmpty()) return;

		for (int i = 0; i < horseshoesSlot; i++) {
			if (!(inventory.getItem(i).getItem() instanceof HorseshoesItem)) continue;
			inventory.setItem(horseshoesSlot, inventory.getItem(i));
			inventory.setItem(i, ItemStack.EMPTY);
			break;
		}
	}

	/**
	 * @reason Mixins are lame and I need to store what the container has before it updates
	 * @author Traister101
	 */
	@Inject(method = "containerChanged", at = @At(value = "HEAD"))
	private void beforeContainerChanged(final Container pInvBasic, final CallbackInfo ci,
			@Share("beforeStack") final LocalRef<ItemStack> beforeStack) {
		beforeStack.set(getHorseshoes());
	}

	/**
	 * @reason Play an equip sound when horseshoes are put on
	 * @author Traister101
	 */
	@Inject(method = "containerChanged", at = @At("TAIL"))
	private void horseshoesEquipped(final Container pInvBasic, final CallbackInfo ci, @Share("beforeStack") final LocalRef<ItemStack> beforeStack) {
		final var afterStack = getHorseshoes();
		setHorseshoeEquipment(inventory.getItem(HorseshoesItem.getHorseshoesSlot(sns$self())));
		if (getHorseshoes().getItem() instanceof HorseshoesItem && !ItemStack.matches(beforeStack.get(), getHorseshoes())) {
			this.playSound(SoundEvents.HORSE_ARMOR, 0.5F, 1.0F);
		}
	}

	/**
	 * @reason We need to increase both the horse inventory and the client screen container by one for our horseshoe slot
	 * @author Traister101
	 */
	@ModifyReturnValue(method = "getInventorySize(I)I", at = @At(value = "RETURN"))
	private static int addHorseshoeSlot(final int original) {
		return original + 1;
	}

	/**
	 * @reason The {@link net.neoforged.neoforge.event.entity.living.LivingFallEvent} doesn't fire for horses...
	 * @author Traister101
	 */
	@ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private float modifyFallDistance(final float fallDistance) {
		final var attribute = getAttribute(SNSAttributes.EXTRA_FALL_DISTANCE);
		if (attribute == null) return fallDistance;
		return Math.max(0, fallDistance - (float) attribute.getValue());
	}

	/**
	 * @reason We need to save our horseshoes
	 * @author Traister101
	 */
	@Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
	private void saveHorseshoe(final CompoundTag compoundTag, CallbackInfo ci) {
		final ItemStack item = inventory.getItem(HorseshoesItem.getHorseshoesSlot(sns$self()));
		if (item.isEmpty()) return;
		compoundTag.put("HorseshoesItem", item.save(registryAccess()));
	}

	/**
	 * @reason We need to load our horseshoes
	 * @author Traister101
	 */
	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void loadHorseshoe(final CompoundTag compoundTag, final CallbackInfo ci) {
		if (!compoundTag.contains("HorseshoesItem", CompoundTag.TAG_COMPOUND)) return;

		final ItemStack itemStack = ItemStack.parseOptional(registryAccess(), compoundTag.getCompound("HorseshoesItem"));
		if (itemStack.is(holder -> holder.value() instanceof HorseshoesItem))
			inventory.setItem(HorseshoesItem.getHorseshoesSlot(sns$self()), itemStack);
		setHorseshoeEquipment(itemStack);
	}

	@Unique
	private AbstractHorse sns$self() {
		return (AbstractHorse) (Animal) this;
	}

	@Unique
	private ItemStack getHorseshoes() {
		return getItemBySlot(EquipmentSlot.FEET);
	}

	@Unique
	private void setHorseshoes(final ItemStack itemStack) {
		// Store a snapshot, sharing the instance with the inventory slot would let
		// in-place mutations (e.g. breaking from durability) silently desync our
		// equipment mirror from the actual slot contents
		this.setItemSlot(EquipmentSlot.FEET, itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.copy());
		this.setDropChance(EquipmentSlot.FEET, 0);
	}

	@Unique
	private void setHorseshoeEquipment(final ItemStack itemStack) {
		final var lastHorseshoes = getHorseshoes();
		// Container notifications fire for every slot in the inventory, bail out
		// when the horseshoes didn't actually change to avoid needless work
		if (ItemStack.matches(lastHorseshoes, itemStack)) return;

		setHorseshoes(itemStack);
		if (!level().isClientSide) {
			if (lastHorseshoes.getItem() instanceof HorseshoesItem horseshoes) {
				getAttributes().removeAttributeModifiers(horseshoes.getAttributeModifiers());
			}

			if (!(itemStack.getItem() instanceof final HorseshoesItem currentHorseshoes)) return;
			getAttributes().addTransientAttributeModifiers(currentHorseshoes.getAttributeModifiers());
		}
	}
}
