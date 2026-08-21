package mod.traister101.sns.network;

import mod.traister101.sns.common.capability.FoodHolder.CycleDirection;
import mod.traister101.sns.common.capability.SNSCapabilities;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import org.jetbrains.annotations.Nullable;

public class ServerboundPacketCycleSlotPacket implements CustomPacketPayload, SNSPacketHandler.ServerboundPayload {
	public static final Type<ServerboundPacketCycleSlotPacket> TYPE = new Type<>(mod.traister101.sns.SacksNSuch.location("cycle_slot"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundPacketCycleSlotPacket> STREAM_CODEC =
			SNSPacketHandler.codec((packet, buffer) -> packet.encode(buffer), ServerboundPacketCycleSlotPacket::new);

	private final CycleDirection cycleDirection;

	public ServerboundPacketCycleSlotPacket(final CycleDirection cycleDirection) {
		this.cycleDirection = cycleDirection;
	}

	ServerboundPacketCycleSlotPacket(final FriendlyByteBuf friendlyByteBuf) {
		this.cycleDirection = friendlyByteBuf.readEnum(CycleDirection.class);
	}

	void encode(final FriendlyByteBuf friendlyByteBuf) {
		friendlyByteBuf.writeEnum(cycleDirection);
	}

	public void handle(final ServerPlayer player) {
		if (player == null) return;

		final var foodHolder = player.getMainHandItem().getCapability(SNSCapabilities.FOOD_HOLDER);
		if (foodHolder != null) foodHolder.cycleSelected(cycleDirection);
	}

	@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
