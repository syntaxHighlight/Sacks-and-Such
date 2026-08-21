package mod.traister101.sns.util;

import mod.traister101.sns.common.items.ContainerItem;
import mod.traister101.sns.util.SNSUtils.ToggleType;

import net.minecraft.nbt.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class NBTHelper {

	public static void toggle(final ItemStack heldStack, final ToggleType toggleType, final boolean toggle) {
		CustomData.update(DataComponents.CUSTOM_DATA, heldStack, tag -> tag.putBoolean(toggleType.tag, toggle));
	}

	public static boolean isAutoPickup(final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ContainerItem)) {
			return false;
		}

		final CompoundTag compoundTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

		if (compoundTag.contains(ToggleType.PICKUP.tag, Tag.TAG_BYTE)) return compoundTag.getBoolean(ToggleType.PICKUP.tag);

		return true;
	}
}
