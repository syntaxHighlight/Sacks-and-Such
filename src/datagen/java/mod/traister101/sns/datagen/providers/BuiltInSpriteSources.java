package mod.traister101.sns.datagen.providers;

import mod.traister101.sns.SacksNSuch;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.common.data.*;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BuiltInSpriteSources extends SpriteSourceProvider {

	public BuiltInSpriteSources(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider,
			final ExistingFileHelper fileHelper) {
		super(output, lookupProvider, SacksNSuch.MODID, fileHelper);
	}

	@Override
	protected void gather() {
		atlas(BLOCKS_ATLAS).addSource(new SingleFile(ResourceLocation.fromNamespaceAndPath(SacksNSuch.MODID, "loom/reinforced_fabric"), Optional.empty()));
	}
}
