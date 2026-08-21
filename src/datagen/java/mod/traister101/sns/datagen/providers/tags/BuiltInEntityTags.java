package mod.traister101.sns.datagen.providers.tags;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.SNSEntityTags;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;

import net.neoforged.neoforge.common.data.ExistingFileHelper;

import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

public class BuiltInEntityTags extends EntityTypeTagsProvider {

	public BuiltInEntityTags(final PackOutput output, final CompletableFuture<Provider> lookupProvider,
			@Nullable final ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, SacksNSuch.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(final Provider provider) {
		tag(SNSEntityTags.NETABLE_MOBS).addTag(SNSEntityTags.TFC_LIVESTOCK);
	}
}