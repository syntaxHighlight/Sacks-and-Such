package mod.traister101.sns.common.items;

import com.google.common.collect.*;
import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.attribute.SNSAttributes;
import mod.traister101.sns.config.entries.HorseshoesConfig;
import mod.traister101.sns.util.SNSUtils;

import net.minecraft.*;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import java.util.*;

public class HorseshoesItem extends Item {

	public static final String LAST_STEP_NBT_KEY = "lastStep";
	public static final String LAST_STEP_X_NBT_KEY = "x";
	public static final String LAST_STEP_Z_NBT_KEY = "z";
	public static final String STEPS_NBT_KEY = "steps";

	public static final String HORSESHOE_MODIFIER_TOOLTIP = SacksNSuch.MODID + ".tooltip.horseshoe.modifier";

	private final HorseshoesProperties horseshoesProperties;
	@Getter(lazy = true)
	private final Multimap<Holder<Attribute>, AttributeModifier> attributeModifiers = Util.make(() -> {
		final var builder = ImmutableMultimap.<Holder<Attribute>, AttributeModifier>builder();
		builder.put(Attributes.MOVEMENT_SPEED,
				new AttributeModifier(SacksNSuch.location("horseshoe_movement"), horseshoesProperties.movementSpeed(), Operation.ADD_MULTIPLIED_TOTAL));
		builder.put(SNSAttributes.EXTRA_FALL_DISTANCE,
				new AttributeModifier(SacksNSuch.location("horseshoe_fall_distance"), horseshoesProperties.bonusFallDistance(), Operation.ADD_VALUE));
		builder.put(Attributes.STEP_HEIGHT,
				new AttributeModifier(SacksNSuch.location("horseshoe_step_height"), horseshoesProperties.bonusStepDistance(), Operation.ADD_VALUE));
		return builder.build();
	});

	public HorseshoesItem(final Properties properties, final HorseshoesProperties horseshoesProperties) {
		super(properties);
		this.horseshoesProperties = horseshoesProperties;
	}

	public static int getSteps(final ItemStack itemStack) {
		return itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt(STEPS_NBT_KEY);
	}

	public static void setSteps(final ItemStack itemStack, final int steps) {
		CustomData.update(DataComponents.CUSTOM_DATA, itemStack, tag -> tag.putInt(STEPS_NBT_KEY, steps));
	}

	public static int getHorseshoesSlot(final AbstractHorse horse) {
		return horse.getInventorySize() - 1;
	}

	public void horseshoeTick(final ItemStack itemStack, final Level level, final AbstractHorse horse) {
		if (level.isClientSide) return;

		if (getSteps(itemStack) > horseshoesProperties.stepsPerDamage()) {
			itemStack.hurtAndBreak(1, horse, EquipmentSlot.FEET);
			setSteps(itemStack, 0);
		}

		final CompoundTag lastStep = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getCompound(LAST_STEP_NBT_KEY);
		final double lastX = lastStep.getDouble(LAST_STEP_X_NBT_KEY);
		final double lastZ = lastStep.getDouble(LAST_STEP_Z_NBT_KEY);
		if (horse.onGround() && !horse.isPassenger()) {
			if (horseshoesProperties.stepsPerDamage() > 0 && (lastX != horse.xOld || lastZ != horse.zOld)) {
				setSteps(itemStack, getSteps(itemStack) + 1);
				lastStep.putDouble("x", horse.xOld);
				lastStep.putDouble("z", horse.zOld);
				CustomData.update(DataComponents.CUSTOM_DATA, itemStack, tag -> tag.put(LAST_STEP_NBT_KEY, lastStep));
			}
		}
	}

	@Override
	public void appendHoverText(final ItemStack itemStack, final TooltipContext context, final List<Component> tooltip,
			final TooltipFlag tooltipFlag) {
		final var modifiers = this.getAttributeModifiers();
		if (modifiers.isEmpty()) return;

		tooltip.add(Component.translatable(HORSESHOE_MODIFIER_TOOLTIP).withStyle(ChatFormatting.GRAY));

		SNSUtils.attributeTooltips(tooltip, modifiers);
	}

	public interface HorseshoesProperties {

		static HorseshoesProperties fromConfig(final HorseshoesConfig config) {
			return new HorseshoesProperties() {

				@Override
				public int stepsPerDamage() {
					return config.stepsPerDamage.get();
				}

				@Override
				public double movementSpeed() {
					return config.movementSpeed.get();
				}

				@Override
				public double bonusFallDistance() {
					return config.bonusFallDistance.get();
				}

				@Override
				public double bonusStepDistance() {
					return config.bonusStepDistance.get();
				}
			};
		}

		int stepsPerDamage();

		double movementSpeed();

		double bonusFallDistance();

		double bonusStepDistance();
	}
}
