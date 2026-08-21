package mod.traister101.sns.common.items;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.client.models.*;
import mod.traister101.sns.common.attribute.SNSAttributes;
import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.config.entries.BootsConfig;
import mod.traister101.sns.util.SNSUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.*;
import java.util.*;
import java.util.function.Consumer;

public class HikingBootsItem extends ArmorItem {

	public static final String LAST_STEP_NBT_KEY = "lastStep";
	public static final String LAST_STEP_X_NBT_KEY = "x";
	public static final String LAST_STEP_Z_NBT_KEY = "z";
	public static final String STEPS_NBT_KEY = "steps";
	public static final String DISABLE_STEP_UP_NBT_KEY = "disableStepUp";

	public static final String PREVENT_SLOW_TOOLTIP = SacksNSuch.MODID + ".tooltip.hiking_boots.prevents_slow";
	public static final String STEP_UP_TOOLTIP = SacksNSuch.MODID + ".tooltip.hiking_boots.step_up";

	private final HikingBootProperties bootProperties;

	public HikingBootsItem(final Properties properties, final Holder<ArmorMaterial> armorMaterial, final HikingBootProperties bootProperties) {
		super(armorMaterial, Type.BOOTS, properties);
		this.bootProperties = bootProperties;
	}

	public static int getSteps(final ItemStack itemStack) {
		return itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt(STEPS_NBT_KEY);
	}

	public static void setSteps(final ItemStack itemStack, final int steps) {
		CustomData.update(DataComponents.CUSTOM_DATA, itemStack, tag -> tag.putInt(STEPS_NBT_KEY, steps));
	}

	public static boolean isStepUpEnabled(final ItemStack bootsStack) {
		if (!(bootsStack.getItem() instanceof HikingBootsItem)) return false;

		final var compoundTag = bootsStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

		if (!compoundTag.contains(DISABLE_STEP_UP_NBT_KEY, Tag.TAG_BYTE)) return true;

		return !compoundTag.getBoolean(DISABLE_STEP_UP_NBT_KEY);
	}

	public static void setStepUpEnabled(final ItemStack bootsStack, final boolean enabled) {
		CustomData.update(DataComponents.CUSTOM_DATA, bootsStack, tag -> tag.putBoolean(DISABLE_STEP_UP_NBT_KEY, !enabled));
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(final ItemStack itemStack) {
		final var builder = ItemAttributeModifiers.builder();
		for (final var entry : super.getDefaultAttributeModifiers(itemStack).modifiers()) {
			builder.add(entry.attribute(), entry.modifier(), entry.slot());
		}
		if (bootProperties.movementSpeed() != 0) builder.add(Attributes.MOVEMENT_SPEED,
				new AttributeModifier(SacksNSuch.location("hiking_boots_movement"), bootProperties.movementSpeed(), Operation.ADD_MULTIPLIED_TOTAL),
				EquipmentSlotGroup.FEET);
		if (bootProperties.fallPadding() != 0) builder.add(SNSAttributes.EXTRA_FALL_DISTANCE,
				new AttributeModifier(SacksNSuch.location("hiking_boots_fall_padding"), bootProperties.fallPadding(), Operation.ADD_VALUE),
				EquipmentSlotGroup.FEET);
		if (isStepUpEnabled(itemStack) && bootProperties.stepHeight() != 0) builder.add(Attributes.STEP_HEIGHT,
				new AttributeModifier(SacksNSuch.location("hiking_boots_step_height"), bootProperties.stepHeight(), Operation.ADD_VALUE),
				EquipmentSlotGroup.FEET);
		return builder.build();
	}

	@Override
	public void inventoryTick(final ItemStack itemStack, final Level level, final Entity entity, final int slotId, final boolean selected) {
		if (!(entity instanceof Player player) || player.getItemBySlot(EquipmentSlot.FEET) != itemStack) return;
		if (level.isClientSide) return;

		if (getSteps(itemStack) > bootProperties.stepsPerDamage()) {
			itemStack.hurtAndBreak(1, player, EquipmentSlot.FEET);
			setSteps(itemStack, 0);
		}

		final CompoundTag lastStep = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getCompound(LAST_STEP_NBT_KEY);
		final double lastX = lastStep.getDouble(LAST_STEP_X_NBT_KEY);
		final double lastZ = lastStep.getDouble(LAST_STEP_Z_NBT_KEY);
		if (player.onGround() && !player.isPassenger() && !player.isCreative()) {
			if (0 < bootProperties.stepsPerDamage() && (lastX != player.xOld || lastZ != player.zOld)) {
				setSteps(itemStack, getSteps(itemStack) + 1);
				lastStep.putDouble("x", player.xOld);
				lastStep.putDouble("z", player.zOld);
				CustomData.update(DataComponents.CUSTOM_DATA, itemStack, tag -> tag.put(LAST_STEP_NBT_KEY, lastStep));
			}
		}
	}

	@Nullable
	@Override
	public net.minecraft.resources.ResourceLocation getArmorTexture(final ItemStack itemStack, final Entity entity, final EquipmentSlot slot,
			final ArmorMaterial.Layer layer, final boolean innerModel) {
		return SacksNSuch.location("textures/models/armor/hiking_boots.png");
	}

	@Override
	public void appendHoverText(final ItemStack itemStack, final TooltipContext context, final List<Component> components,
			final TooltipFlag tooltipFlag) {
		components.add(Component.translatable(PREVENT_SLOW_TOOLTIP));
		components.add(Component.translatable(STEP_UP_TOOLTIP, SNSUtils.toggleTooltip(isStepUpEnabled(itemStack))).withStyle(ChatFormatting.GRAY));
		super.appendHoverText(itemStack, context, components, tooltipFlag);
	}

	@Override
	@SuppressWarnings("removal")
	public void initializeClient(final Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {

			private final EnumMap<BootModelType, HumanoidModel<?>> models = new EnumMap<>(BootModelType.class);

			@NotNull
			@Override
			public HumanoidModel<?> getHumanoidArmorModel(final LivingEntity livingEntity, final ItemStack itemStack,
					final EquipmentSlot equipmentSlot, final HumanoidModel<?> original) {
				return models.computeIfAbsent(SNSConfig.CLIENT.bootModelType.get(), bootModelType -> {
					final EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
					return switch (bootModelType) {
						case FANCY -> new FancyHikingBootsModel<>(entityModels.bakeLayer(FancyHikingBootsModel.LAYER_LOCATION));
						case NO_FLOOF -> new NoFloofHikingBootsModel<>(entityModels.bakeLayer(NoFloofHikingBootsModel.LAYER_LOCATION));
						case VANILLA -> new VanillaHikingBootsModel<>(entityModels.bakeLayer(VanillaHikingBootsModel.LAYER_LOCATION));
					};
				});
			}
		});
	}

	public enum BootModelType {
		FANCY,
		NO_FLOOF,
		VANILLA
	}

	public interface HikingBootProperties {

		static HikingBootProperties fromConfig(final BootsConfig config) {
			return new HikingBootProperties() {
				@Override
				public int stepsPerDamage() {
					return config.stepsPerDamage.get();
				}

				@Override
				public double movementSpeed() {
					return config.movementSpeed.get();
				}

				@Override
				public double stepHeight() {
					return config.stepHeight.get();
				}

				@Override
				public double fallPadding() {
					return config.fallPadding.get();
				}
			};
		}

		int stepsPerDamage();

		double movementSpeed();

		double stepHeight();

		double fallPadding();
	}
}
