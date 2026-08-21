package mod.traister101.sns.network;

import mod.traister101.sns.common.items.HikingBootsItem;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

public final class ServerboundToggleBootsStepUp implements CustomPacketPayload, SNSPacketHandler.ServerboundPayload {
	public static final Type<ServerboundToggleBootsStepUp> TYPE = new Type<>(mod.traister101.sns.SacksNSuch.location("toggle_boots_step_up"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundToggleBootsStepUp> STREAM_CODEC =
			SNSPacketHandler.codec((packet, buffer) -> packet.encode(buffer), ServerboundToggleBootsStepUp::new);

	public ServerboundToggleBootsStepUp() {}

	ServerboundToggleBootsStepUp(final FriendlyByteBuf friendlyByteBuf) {}

	void encode(final FriendlyByteBuf friendlyByteBuf) {}

	public void handle(final ServerPlayer player) {
		if (player == null) return;

		final var bootsStack = player.getItemBySlot(EquipmentSlot.FEET);
		if (!(bootsStack.getItem() instanceof HikingBootsItem)) return;
		// Just take the boots off to ensure things update correctly
		player.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
		final var stepUpEnabled = HikingBootsItem.isStepUpEnabled(bootsStack);
		HikingBootsItem.setStepUpEnabled(bootsStack, !stepUpEnabled);
		player.setItemSlot(EquipmentSlot.FEET, bootsStack);
	}

	@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
