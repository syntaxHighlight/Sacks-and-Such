package mod.traister101.sns.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.BiConsumer;

public final class SNSPacketHandler {

	private static final String VERSION = "1";

	public static void sendToServer(final CustomPacketPayload message) {
		PacketDistributor.sendToServer(message);
	}

	public static void init() {
	}

	public static void register(final RegisterPayloadHandlersEvent event) {
		final var registrar = event.registrar(VERSION);
		registrar.playToServer(ServerboundPickBlockPacket.TYPE, ServerboundPickBlockPacket.STREAM_CODEC, SNSPacketHandler::handle);
		registrar.playToServer(ServerboundTogglePacket.TYPE, ServerboundTogglePacket.STREAM_CODEC, SNSPacketHandler::handle);
		registrar.playToServer(ServerboundPacketCycleSlotPacket.TYPE, ServerboundPacketCycleSlotPacket.STREAM_CODEC, SNSPacketHandler::handle);
		registrar.playToServer(ServerboundToggleSlotVoidingPacket.TYPE, ServerboundToggleSlotVoidingPacket.STREAM_CODEC, SNSPacketHandler::handle);
		registrar.playToServer(ServerboundOpenContainerPacket.TYPE, ServerboundOpenContainerPacket.STREAM_CODEC, SNSPacketHandler::handle);
		registrar.playToServer(ServerboundToggleBootsStepUp.TYPE, ServerboundToggleBootsStepUp.STREAM_CODEC, SNSPacketHandler::handle);
	}

	private static <T extends CustomPacketPayload & ServerboundPayload> void handle(final T payload, final IPayloadContext context) {
		context.enqueueWork(() -> payload.handle((ServerPlayer) context.player()));
	}

	static <T> StreamCodec<RegistryFriendlyByteBuf, T> codec(final BiConsumer<T, RegistryFriendlyByteBuf> encoder,
			final java.util.function.Function<RegistryFriendlyByteBuf, T> decoder) {
		return StreamCodec.of((buffer, value) -> encoder.accept(value, buffer), decoder::apply);
	}

	interface ServerboundPayload {
		void handle(ServerPlayer player);
	}
}
