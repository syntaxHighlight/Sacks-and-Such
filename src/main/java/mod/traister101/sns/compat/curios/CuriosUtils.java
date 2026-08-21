package mod.traister101.sns.compat.curios;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.items.IItemHandler;

import java.util.Optional;

public final class CuriosUtils {

	public static Optional<IItemHandler> getEquippedCurios(final LivingEntity entity) {
		return CuriosApi.getCuriosInventory(entity).map(ICuriosItemHandler::getEquippedCurios);
	}

	public static ItemStack getStack(final LivingEntity entity, final String identifier, final int slotIndex) {
		return CuriosApi.getCuriosInventory(entity)
				.flatMap(handler -> handler.getStacksHandler(identifier))
				.map(ICurioStacksHandler::getStacks)
				.filter(stacks -> slotIndex < stacks.getSlots())
				.map(stacks -> stacks.getStackInSlot(slotIndex))
				.orElse(ItemStack.EMPTY);
	}
}
