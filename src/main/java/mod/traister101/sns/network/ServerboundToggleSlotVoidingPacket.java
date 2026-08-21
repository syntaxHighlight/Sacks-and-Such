package mod.traister101.sns.network;

import mod.traister101.sns.common.capability.SNSCapabilities;
import mod.traister101.sns.common.menu.ContainerItemMenu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import org.jetbrains.annotations.Nullable;

public class ServerboundToggleSlotVoidingPacket implements CustomPacketPayload, SNSPacketHandler.ServerboundPayload {
	public static final Type<ServerboundToggleSlotVoidingPacket> TYPE = new Type<>(mod.traister101.sns.SacksNSuch.location("toggle_voiding"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundToggleSlotVoidingPacket> STREAM_CODEC =
			SNSPacketHandler.codec((packet, buffer) -> packet.encode(buffer), ServerboundToggleSlotVoidingPacket::new);

	private final int slotIndex;

	public ServerboundToggleSlotVoidingPacket(final int slotIndex) {
		this.slotIndex = slotIndex;
	}

	ServerboundToggleSlotVoidingPacket(final FriendlyByteBuf friendlyByteBuf) {
		this.slotIndex = friendlyByteBuf.readVarInt();
	}

	void encode(final FriendlyByteBuf friendlyByteBuf) {
		friendlyByteBuf.writeVarInt(slotIndex);
	}

	public void handle(final ServerPlayer player) {
		if (player == null) return;

		if (!(player.containerMenu instanceof final ContainerItemMenu containerItemMenu)) return;

		if (slotIndex < 0 || slotIndex >= containerItemMenu.containerSlots) return;
		final var itemVoider = containerItemMenu.getContainerStack().getCapability(SNSCapabilities.ITEM_VOIDER);
		if (itemVoider != null) itemVoider.toggleVoidSlot(slotIndex);
	}

	@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
