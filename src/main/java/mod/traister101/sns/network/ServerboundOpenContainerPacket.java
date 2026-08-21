package mod.traister101.sns.network;

import mod.traister101.sns.common.menu.SNSMenus;
import mod.traister101.sns.common.items.ContainerItem;
import mod.traister101.sns.util.ItemSlotData;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class ServerboundOpenContainerPacket implements CustomPacketPayload, SNSPacketHandler.ServerboundPayload {
	public static final Type<ServerboundOpenContainerPacket> TYPE = new Type<>(mod.traister101.sns.SacksNSuch.location("open_container"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundOpenContainerPacket> STREAM_CODEC =
			SNSPacketHandler.codec((packet, buffer) -> packet.encode(buffer), ServerboundOpenContainerPacket::new);

	private final ItemSlotData slotData;

	ServerboundOpenContainerPacket(final FriendlyByteBuf friendlyByteBuf) {
		this.slotData = ItemSlotData.read(friendlyByteBuf);
	}

	void encode(final FriendlyByteBuf friendlyByteBuf) {
		slotData.write(friendlyByteBuf);
	}

	public void handle(final ServerPlayer player) {
		if (player == null) return;
		if (!(slotData.stack(player).getItem() instanceof ContainerItem)) return;

		SNSMenus.CONTAINER_ITEM_MENU_PROVIDER.openMenu(player, slotData);
	}

	@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
