package mod.traister101.sns.network;

import mod.traister101.sns.config.SNSConfig;
import mod.traister101.sns.util.handlers.PickBlockHandler;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

public final class ServerboundPickBlockPacket implements CustomPacketPayload, SNSPacketHandler.ServerboundPayload {
	public static final Type<ServerboundPickBlockPacket> TYPE = new Type<>(mod.traister101.sns.SacksNSuch.location("pick_block"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundPickBlockPacket> STREAM_CODEC =
			SNSPacketHandler.codec((packet, buffer) -> packet.encode(buffer), ServerboundPickBlockPacket::new);

	private final ItemStack stackToSelect;

	public ServerboundPickBlockPacket(final ItemStack stackToSelect) {
		this.stackToSelect = stackToSelect;
	}

	ServerboundPickBlockPacket(final RegistryFriendlyByteBuf friendlyByteBuf) {
		this.stackToSelect = ItemStack.STREAM_CODEC.decode(friendlyByteBuf);
	}

	void encode(final RegistryFriendlyByteBuf friendlyByteBuf) {
		ItemStack.STREAM_CODEC.encode(friendlyByteBuf, stackToSelect);
	}

	public void handle(final ServerPlayer player) {
		if (!SNSConfig.COMMON.doPickBlock.get()) return;

		if (player == null) return;

		PickBlockHandler.handlePickBlock(player, stackToSelect);
	}

	@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
