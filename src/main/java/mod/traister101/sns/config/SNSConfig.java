package mod.traister101.sns.config;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig.Type;

import java.util.function.Function;

public final class SNSConfig {

	public static final CommonConfig COMMON;
	public static final ClientConfig CLIENT;
	public static final ServerConfig SERVER;

	private static final ModConfigSpec COMMON_SPEC;
	private static final ModConfigSpec CLIENT_SPEC;
	private static final ModConfigSpec SERVER_SPEC;

	static {
		final var common = create(CommonConfig::new);
		COMMON = common.getKey();
		COMMON_SPEC = common.getRight();
		final var client = create(ClientConfig::new);
		CLIENT = client.getKey();
		CLIENT_SPEC = client.getRight();
		final var server = create(ServerConfig::new);
		SERVER = server.getKey();
		SERVER_SPEC = server.getRight();
	}

	public static void init(final ModContainer container) {
		container.registerConfig(Type.COMMON, COMMON_SPEC);
		container.registerConfig(Type.CLIENT, CLIENT_SPEC);
		container.registerConfig(Type.SERVER, SERVER_SPEC);
	}

	public static <T> T serverValue(final ConfigValue<T> value) {
		return SERVER_SPEC.isLoaded() ? value.get() : value.getDefault();
	}

	private static <Config> Pair<Config, ModConfigSpec> create(final Function<Builder, Config> configFactory) {
		return new ModConfigSpec.Builder().configure(configFactory);
	}
}
