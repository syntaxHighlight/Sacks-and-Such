package mod.traister101.sns.network;

import mod.traister101.sns.common.items.ContainerItem;
import mod.traister101.sns.util.NBTHelper;
import mod.traister101.sns.util.SNSUtils.ToggleType;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

public final class ServerboundTogglePacket implements CustomPacketPayload, SNSPacketHandler.ServerboundPayload {
	public static final Type<ServerboundTogglePacket> TYPE = new Type<>(mod.traister101.sns.SacksNSuch.location("toggle"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundTogglePacket> STREAM_CODEC =
			SNSPacketHandler.codec((packet, buffer) -> packet.encode(buffer), ServerboundTogglePacket::new);

	private final boolean toggle;
	private final ToggleType type;

	public ServerboundTogglePacket(final boolean toggle, final ToggleType type) {
		this.toggle = toggle;
		this.type = type;
	}

	ServerboundTogglePacket(final FriendlyByteBuf friendlyByteBuf) {
		toggle = friendlyByteBuf.readBoolean();
		type = ToggleType.byId(friendlyByteBuf.readInt());
	}

	void encode(final FriendlyByteBuf friendlyByteBuf) {
		friendlyByteBuf.writeBoolean(toggle);
		friendlyByteBuf.writeInt(type.ordinal());
	}

	public void handle(final ServerPlayer player) {
		if (player == null) return;

		final ItemStack mainHandItem = player.getMainHandItem();
		if ((mainHandItem.getItem() instanceof final ContainerItem containerItem) && type.supportsContainerType(containerItem.type)) {
			NBTHelper.toggle(mainHandItem, type, toggle);
		}
	}

	@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
