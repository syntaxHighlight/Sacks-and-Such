package mod.traister101.sns.datagen.providers;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.SNSItemTags;
import mod.traister101.sns.common.items.*;
import mod.traister101.sns.datagen.recipes.*;
import mod.traister101.sns.datagen.recipes.CraftingRecipeBuilder;
import mod.traister101.sns.datagen.tfc.data.*;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.component.forge.ForgeRule;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.Metal.*;
import net.dries007.tfc.util.data.KnappingPattern;
import net.dries007.tfc.util.data.KnappingType;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;

import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BuiltInRecipes extends RecipeProvider {

	public BuiltInRecipes(final PackOutput packOutput, final CompletableFuture<HolderLookup.Provider> registries) {
		super(packOutput, registries);
	}

	private static void craftingItems(final RecipeOutput writer) {
		CraftingRecipeBuilder.shaped(SNSItems.REINFORCED_FIBER.get())
				.pattern("JJJ", "SSS", "JJJ")
				.define('J', TFCItems.JUTE_FIBER.get())
				.define('S', Tags.Items.STRINGS)
				.unlockedBy("has_jute", has(TFCItems.JUTE_FIBER.get()))
				.unlockedBy("has_string", has(Tags.Items.STRINGS))
				.save(writer);

		final TagKey<Item> steelRodsTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "rods/steel"));
		{
			CraftingRecipeBuilder.shaped(SNSItems.PACK_FRAME.get())
					.pattern("RRR", "R R", "RRR")
					.define('R', steelRodsTag)
					.unlockedBy("has_steel_rod", has(steelRodsTag))
					.save(writer);
			HeatingRecipe.melt(SNSItems.PACK_FRAME.get(), DefaultMetal.STEEL.meltTemp, DefaultMetal.STEEL.meltMetal(), 400).save(writer);
		}

		CraftingRecipeBuilder.shapeless(SNSItems.BOUND_LEATHER_STRIP.get())
				.damageInputs()
				.requires(SNSItems.LEATHER_STRIP.get())
				.requires(SNSItems.REINFORCED_FIBER.get())
				.requires(SNSItems.LEATHER_STRIP.get())
				.requires(TFCTags.Items.SEWING_NEEDLES)
				.primaryIngredient(TFCTags.Items.SEWING_NEEDLES)
				.unlockedBy("has_leather_strip", has(SNSItems.LEATHER_STRIP.get()))
				.unlockedBy("has_reinforced_fiber", has(SNSItems.REINFORCED_FIBER.get()))
				.unlockedBy("has_sewing_needle", has(TFCTags.Items.SEWING_NEEDLES))
				.save(writer);

		saveKnapping(writer, SNSItems.UNFINISHED_LEATHER_SACK.get(), 1, " XXX ", "XXXXX", "XXXXX", "XXXXX", " XXX ");
		saveKnapping(writer, SNSItems.LEATHER_STRIP.get(), 3, "X X X", "X X X", "X X X", "X X X", "X X X");
		saveLoom(writer, new SizedIngredient(Ingredient.of(SNSItems.REINFORCED_FIBER.get()), 16), SNSItems.REINFORCED_FABRIC.get(), 1, 16,
				ResourceLocation.fromNamespaceAndPath(SacksNSuch.MODID, "loom/reinforced_fabric"));

		saveAnvil(writer, Ingredient.of(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(ItemType.INGOT).get()),
				new ItemStack(SNSItems.BUCKLE.get()), Metal.WROUGHT_IRON.tier(),
				new ForgeRule[] {ForgeRule.PUNCH_LAST, ForgeRule.PUNCH_LAST, ForgeRule.PUNCH_LAST}, true,
				ResourceLocation.fromNamespaceAndPath(SacksNSuch.MODID, "iron_buckle"));
		HeatingRecipe.melt(SNSItems.BUCKLE.get(), DefaultMetal.WROUGHT_IRON.meltTemp, DefaultMetal.WROUGHT_IRON.meltMetal(), 100).save(writer);
		saveAnvil(writer, Ingredient.of(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(ItemType.INGOT).get()),
				new ItemStack(SNSItems.BUCKLE.get()), Metal.STEEL.tier(),
				new ForgeRule[] {ForgeRule.PUNCH_LAST, ForgeRule.PUNCH_LAST, ForgeRule.PUNCH_LAST}, true,
				ResourceLocation.fromNamespaceAndPath(SacksNSuch.MODID, "steel_buckle"));

		horseshoeRecipes(writer, SNSItems.STEEL_HORSESHOE.get(), steelRodsTag, DefaultMetal.STEEL);
		final TagKey<Item> blackSteelRods = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "rods/black_steel"));
		horseshoeRecipes(writer, SNSItems.BLACK_STEEL_HORSESHOE.get(), blackSteelRods, DefaultMetal.BLACK_STEEL);
		final TagKey<Item> blueSteelRods = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "rods/blue_steel"));
		horseshoeRecipes(writer, SNSItems.BLUE_STEEL_HORSESHOE.get(), blueSteelRods, DefaultMetal.BLUE_STEEL);
		final TagKey<Item> redSteelRods = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "rods/red_steel"));
		horseshoeRecipes(writer, SNSItems.RED_STEEL_HORSESHOE.get(), redSteelRods, DefaultMetal.RED_STEEL);
	}

	private static void horseshoeRecipes(final RecipeOutput writer, final Item horseshoe, final TagKey<Item> steelRodsTag,
			final MetalData metal) {
		saveAnvil(writer, Ingredient.of(steelRodsTag), new ItemStack(horseshoe), metal.metalTier(),
				new ForgeRule[] {ForgeRule.BEND_THIRD_LAST, ForgeRule.BEND_SECOND_LAST, ForgeRule.UPSET_LAST}, false, null);
		HeatingRecipe.melt(horseshoe, metal.getMeltTemp(), metal.meltMetal(), 50).save(writer);
	}

	private static void containerItems(final RecipeOutput writer) {
		CraftingRecipeBuilder.shaped(SNSItems.STRAW_BASKET.get())
				.damageInputs()
				.pattern("SSS", "T T", " TK")
				.define('S', TFCItems.STRAW.get())
				.define('T', TFCBlocks.THATCH.get())
				.define('K', TFCTags.Items.TOOLS_KNIFE)
				.unlockedBy("has_straw", has(TFCItems.STRAW.get()))
				.unlockedBy("has_thatch", has(TFCBlocks.THATCH.get()))
				.unlockedBy("has_knife", has(TFCTags.Items.TOOLS_KNIFE))
				.save(writer);

		CraftingRecipeBuilder.shaped(SNSItems.LEATHER_SACK.get())
				.damageInputs()
				.pattern("JJJ", "LUL", " LN")
				.define('J', TFCItems.JUTE_FIBER.get())
				.define('L', SNSItems.LEATHER_STRIP.get())
				.define('U', SNSItems.UNFINISHED_LEATHER_SACK.get())
				.define('N', TFCTags.Items.SEWING_NEEDLES)
				.unlockedBy("has_jute", has(TFCItems.JUTE_FIBER.get()))
				.unlockedBy("has_leather_strip", has(SNSItems.LEATHER_STRIP.get()))
				.unlockedBy("has_unfinished_sack", has(SNSItems.UNFINISHED_LEATHER_SACK.get()))
				.unlockedBy("has_sewing_needle", has(TFCTags.Items.SEWING_NEEDLES))
				.save(writer);

		CraftingRecipeBuilder.shaped(SNSItems.BURLAP_SACK.get())
				.damageInputs()
				.pattern("JJJ", "B B", " BN")
				.define('J', TFCItems.JUTE_FIBER.get())
				.define('B', TFCItems.BURLAP_CLOTH.get())
				.define('N', TFCTags.Items.SEWING_NEEDLES)
				.unlockedBy("has_jute", has(TFCItems.JUTE_FIBER.get()))
				.unlockedBy("has_burlap_cloth", has(TFCItems.BURLAP_CLOTH.get()))
				.unlockedBy("has_sewing_needle", has(TFCTags.Items.SEWING_NEEDLES))
				.save(writer);

		CraftingRecipeBuilder.shaped(SNSItems.SEED_POUCH.get())
				.damageInputs()
				.pattern("SSS", "WBW", " WN")
				.define('S', Tags.Items.STRINGS)
				.define('W', SNSItemTags.TFC_HIGH_QUALITY_CLOTH)
				.define('B', TFCItems.BURLAP_CLOTH.get())
				.define('N', TFCTags.Items.SEWING_NEEDLES)
				.unlockedBy("has_string", has(Tags.Items.STRINGS))
				.unlockedBy("has_wool_cloth", has(SNSItemTags.TFC_HIGH_QUALITY_CLOTH))
				.unlockedBy("has_burlap_cloth", has(TFCItems.BURLAP_CLOTH.get()))
				.unlockedBy("has_sewing_needle", has(TFCTags.Items.SEWING_NEEDLES))
				.save(writer);

		CraftingRecipeBuilder.shaped(SNSItems.ORE_SACK.get())
				.damageInputs()
				.pattern("RRR", "LBL", " LN")
				.define('R', SNSItems.REINFORCED_FIBER.get())
				.define('L', Tags.Items.LEATHERS)
				.define('B', TFCItems.BURLAP_CLOTH.get())
				.define('N', TFCTags.Items.SEWING_NEEDLES)
				.unlockedBy("has_reinforced_fiber", has(SNSItems.REINFORCED_FIBER.get()))
				.unlockedBy("has_leather", has(Tags.Items.LEATHERS))
				.unlockedBy("has_burlap_cloth", has(TFCItems.BURLAP_CLOTH.get()))
				.unlockedBy("has_sewing_needle", has(TFCTags.Items.SEWING_NEEDLES))
				.save(writer);

		CraftingRecipeBuilder.shaped(SNSItems.FRAME_PACK.get())
				.damageInputs()
				.pattern("LFL", "LPL", " FN")
				.define('P', SNSItems.PACK_FRAME.get())
				.define('F', SNSItems.REINFORCED_FABRIC.get())
				.define('L', SNSItems.BOUND_LEATHER_STRIP.get())
				.define('N', TFCTags.Items.SEWING_NEEDLES)
				.unlockedBy("has_pack_frame", has(SNSItems.PACK_FRAME.get()))
				.unlockedBy("has_reinforced_fabric", has(SNSItems.REINFORCED_FABRIC.get()))
				.unlockedBy("has_bound_leather_strip", has(SNSItems.BOUND_LEATHER_STRIP.get()))
				.unlockedBy("has_sewing_needle", has(TFCTags.Items.SEWING_NEEDLES))
				.save(writer);

		{
			final var wroughtIronRodsTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "rods/wrought_iron"));
			final var wroughtIronSheetsTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "sheets/wrought_iron"));
			CraftingRecipeBuilder.shaped(SNSItems.LUNCHBOX.get())
					.pattern("RLR", "SFS", " S ")
					.define('R', wroughtIronRodsTag)
					.define('L', SNSItems.BOUND_LEATHER_STRIP.get())
					.define('S', wroughtIronSheetsTag)
					.define('F', SNSItems.REINFORCED_FABRIC.get())
					.unlockedBy("has_wrought_iron_rods", has(wroughtIronRodsTag))
					.unlockedBy("has_bound_leather_strip", has(SNSItems.BOUND_LEATHER_STRIP.get()))
					.unlockedBy("has_wrought_iron_sheets", has(wroughtIronSheetsTag))
					.unlockedBy("has_reinforced_fabric", has(SNSItems.REINFORCED_FABRIC.get()))
					.save(writer);
		}

		saveKnapping(writer, SNSItems.QUIVER.get(), 1, " XXXX", "X XXX", "X XXX", "X XXX", " XXXX");
	}

	private static void horseshoesRecipes(final RecipeOutput writer, final HorseshoesItem horseshoes, final Item horseshoe,
			final MetalData metal) {
		CraftingRecipeBuilder.shapeless(horseshoes).requires(horseshoe, 4).unlockedBy("has_horseshoe", has(horseshoe)).save(writer);
		HeatingRecipe.melt(horseshoes, metal.getMeltTemp(), metal.meltMetal(), 200).save(writer);
	}

	private static void safetyToeHikingBoots(final HikingBootsItem hikingBootsItem, final TagKey<Item> metalSheetsTag,
			final RecipeOutput writer) {
		AdvancedCraftingRecipeBuilder.shaped(hikingBootsItem)
				.pattern("RWR", "LLL", "TBT")
				.define('R', SNSItems.REINFORCED_FIBER.get())
				.inputItem('W', SNSItems.BUCKLE.get(), 0, 1)
				.define('L', SNSItems.BOUND_LEATHER_STRIP.get())
				.define('T', metalSheetsTag)
				.define('B', Items.LEATHER_BOOTS)
				.modifier(ItemStackModifiers.COPY_FORGING_BONUS)
				.unlockedBy("has_reinforced_fiber", has(SNSItems.REINFORCED_FIBER.get()))
				.unlockedBy("has_buckle", has(SNSItems.BUCKLE.get()))
				.unlockedBy("has_bound_leather_strip", has(SNSItems.BOUND_LEATHER_STRIP.get()))
				.unlockedBy("has_double_steel_sheet", has(metalSheetsTag))
				.unlockedBy("has_leather_boots", has(Items.LEATHER_BOOTS))
				.save(writer);
	}

	@Override
	protected void buildRecipes(final RecipeOutput writer) {
		craftingItems(writer);
		containerItems(writer);

		CraftingRecipeBuilder.shaped(SNSItems.MOB_NET_ITEM.get())
				.pattern("R R", " R ", "R R")
				.define('R', SNSItems.REINFORCED_FIBER.get())
				.unlockedBy("has_reinforced_fiber", has(SNSItems.REINFORCED_FIBER.get()))
				.save(writer);

		AdvancedCraftingRecipeBuilder.shaped(SNSItems.HIKING_BOOTS.get())
				.pattern("RWR", "LLL", " B ")
				.define('R', SNSItems.REINFORCED_FIBER.get())
				.inputItem('W', SNSItems.BUCKLE.get(), 0, 1)
				.define('L', SNSItems.BOUND_LEATHER_STRIP.get())
				.define('B', Items.LEATHER_BOOTS)
				.modifier(ItemStackModifiers.COPY_FORGING_BONUS)
				.unlockedBy("has_reinforced_fiber", has(SNSItems.REINFORCED_FIBER.get()))
				.unlockedBy("has_buckle", has(SNSItems.BUCKLE.get()))
				.unlockedBy("has_bound_leather_strip", has(SNSItems.BOUND_LEATHER_STRIP.get()))
				.unlockedBy("has_leather_boots", has(Items.LEATHER_BOOTS))
				.save(writer);

		{
			final var steelSheets = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "sheets/steel"));
			safetyToeHikingBoots(SNSItems.STEEL_TOE_HIKING_BOOTS.get(), steelSheets, writer);
			final var blackSteelSheets = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "sheets/black_steel"));
			safetyToeHikingBoots(SNSItems.BLACK_STEEL_TOE_HIKING_BOOTS.get(), blackSteelSheets, writer);
			final var blueSteelSheets = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "sheets/blue_steel"));
			safetyToeHikingBoots(SNSItems.BLUE_STEEL_TOE_HIKING_BOOTS.get(), blueSteelSheets, writer);
			final var redSteelSheets = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "sheets/red_steel"));
			safetyToeHikingBoots(SNSItems.RED_STEEL_TOE_HIKING_BOOTS.get(), redSteelSheets, writer);
		}

		horseshoesRecipes(writer, SNSItems.STEEL_HORSESHOES.get(), SNSItems.STEEL_HORSESHOE.get(), DefaultMetal.STEEL);
		horseshoesRecipes(writer, SNSItems.BLACK_STEEL_HORSESHOES.get(), SNSItems.BLACK_STEEL_HORSESHOE.get(), DefaultMetal.BLACK_STEEL);
		horseshoesRecipes(writer, SNSItems.BLUE_STEEL_HORSESHOES.get(), SNSItems.BLUE_STEEL_HORSESHOE.get(), DefaultMetal.BLUE_STEEL);
		horseshoesRecipes(writer, SNSItems.RED_STEEL_HORSESHOES.get(), SNSItems.RED_STEEL_HORSESHOE.get(), DefaultMetal.RED_STEEL);
	}

	private static void saveKnapping(final RecipeOutput output, final Item result, final int count, final String... legacyPattern) {
		final String[] pattern = Arrays.stream(legacyPattern).map(row -> row.replace('X', '#')).toArray(String[]::new);
		final ResourceLocation id = itemId(result).withPrefix("leather_knapping/");
		final var recipe = new net.dries007.tfc.common.recipes.KnappingRecipe(
				KnappingType.MANAGER.getReference(ResourceLocation.fromNamespaceAndPath("tfc", "leather")),
				KnappingPattern.from(false, pattern), Optional.empty(), new ItemStack(result, count));
		output.accept(id, recipe, null);
	}

	private static void saveLoom(final RecipeOutput output, final SizedIngredient ingredient, final Item result, final int count, final int steps,
			final ResourceLocation texture) {
		final ResourceLocation id = itemId(result).withPrefix("loom/");
		output.accept(id, new net.dries007.tfc.common.recipes.LoomRecipe(ingredient, ItemStackProvider.of(result, count), steps, texture), null);
	}

	private static void saveAnvil(final RecipeOutput output, final Ingredient input, final ItemStack result, final int tier,
			final ForgeRule[] rules, final boolean applyForgingBonus, final ResourceLocation requestedId) {
		final ResourceLocation id = (requestedId == null ? itemId(result.getItem()) : requestedId).withPrefix("anvil/");
		output.accept(id, new net.dries007.tfc.common.recipes.AnvilRecipe(input, tier, List.of(rules), applyForgingBonus,
				ItemStackProvider.of(result)), null);
	}

	private static ResourceLocation itemId(final Item item) {
		return Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
	}
}
