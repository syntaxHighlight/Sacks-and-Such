package mod.traister101.sns.datagen.recipes;

import net.dries007.tfc.common.recipes.AdvancedShapedRecipe;
import net.dries007.tfc.common.recipes.outputs.DamageCraftingRemainderModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/** Builder for TFC 1.21 advanced shaped crafting recipes. */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public final class AdvancedCraftingRecipeBuilder implements RecipeBuilder {

	private final String folderName;
	private final Item result;
	private final int count;
	private final List<String> rows = new ArrayList<>();
	private final Map<Character, Ingredient> key = new LinkedHashMap<>();
	private final List<ItemStackModifier> modifiers = new ArrayList<>();
	private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
	private int criterionCount;
	@Nullable
	private String group;
	private boolean showNotification = true;
	private boolean damageInputs;
	private int inputRow = -1;
	private int inputColumn = -1;

	private AdvancedCraftingRecipeBuilder(final String folderName, final ItemLike result, final int count) {
		this.folderName = folderName;
		this.result = result.asItem();
		this.count = count;
	}

	public static AdvancedCraftingRecipeBuilder shaped(final ItemLike result) {
		return shaped("crafting", result, 1);
	}

	public static AdvancedCraftingRecipeBuilder shaped(final ItemLike result, final int count) {
		return shaped("crafting", result, count);
	}

	public static AdvancedCraftingRecipeBuilder shaped(final String folderName, final ItemLike result) {
		return shaped(folderName, result, 1);
	}

	public static AdvancedCraftingRecipeBuilder shaped(final String folderName, final ItemLike result, final int count) {
		return new AdvancedCraftingRecipeBuilder(folderName, result, count);
	}

	public AdvancedCraftingRecipeBuilder define(final Character symbol, final TagKey<Item> tag) {
		return define(symbol, Ingredient.of(tag));
	}

	public AdvancedCraftingRecipeBuilder define(final Character symbol, final ItemLike item) {
		return define(symbol, Ingredient.of(item));
	}

	public AdvancedCraftingRecipeBuilder define(final Character symbol, final Ingredient ingredient) {
		if (symbol == ' ') throw new IllegalArgumentException("Whitespace is reserved");
		if (key.putIfAbsent(symbol, ingredient) != null) {
			throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined");
		}
		return this;
	}

	public AdvancedCraftingRecipeBuilder inputItem(final Character symbol, final ItemLike item, final int row, final int column) {
		return inputItem(symbol, Ingredient.of(item), row, column);
	}

	public AdvancedCraftingRecipeBuilder inputItem(final Character symbol, final Ingredient ingredient, final int row, final int column) {
		if (inputRow >= 0) throw new IllegalStateException("Primary input is already defined");
		inputRow = row;
		inputColumn = column;
		return define(symbol, ingredient);
	}

	public AdvancedCraftingRecipeBuilder pattern(final String pattern) {
		if (!rows.isEmpty() && pattern.length() != rows.getFirst().length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line");
		}
		rows.add(pattern);
		return this;
	}

	public AdvancedCraftingRecipeBuilder pattern(final String... patterns) {
		Arrays.stream(patterns).forEach(this::pattern);
		return this;
	}

	public AdvancedCraftingRecipeBuilder modifier(final ItemStackModifier modifier) {
		modifiers.add(modifier);
		return this;
	}

	public AdvancedCraftingRecipeBuilder damageInputs() {
		damageInputs = true;
		return this;
	}

	public AdvancedCraftingRecipeBuilder showNotification(final boolean value) {
		showNotification = value;
		return this;
	}

	@Override
	public AdvancedCraftingRecipeBuilder unlockedBy(final String criterionName, final Criterion<?> criterion) {
		advancement.addCriterion(criterionName, criterion);
		criterionCount++;
		return this;
	}

	@Override
	public AdvancedCraftingRecipeBuilder group(@Nullable final String groupName) {
		group = groupName;
		return this;
	}

	@Override
	public Item getResult() {
		return result;
	}

	@Override
	public void save(final RecipeOutput output) {
		save(output, RecipeBuilder.getDefaultRecipeId(result).withPrefix(folderName + "/"));
	}

	@Override
	public void save(final RecipeOutput output, final ResourceLocation recipeId) {
		ensureValid(recipeId);
		final List<net.dries007.tfc.common.recipes.outputs.ItemStackModifier> outputModifiers = modifiers.stream()
				.map(ItemStackModifier::value)
				.toList();
		final ItemStackProvider resultProvider = ItemStackProvider.of(new ItemStack(result, count), outputModifiers);
		final Optional<ItemStackProvider> remainder = damageInputs
				? Optional.of(ItemStackProvider.of(DamageCraftingRemainderModifier.INSTANCE))
				: Optional.empty();
		final AdvancedShapedRecipe recipe = new AdvancedShapedRecipe(
				ShapedRecipePattern.of(key, rows), showNotification, resultProvider, remainder, inputRow, inputColumn);
		final AdvancementHolder advancementHolder = advancement
				.parent(AdvancementSubProvider.createPlaceholder(ROOT_RECIPE_ADVANCEMENT.toString()))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId))
				.requirements(AdvancementRequirements.Strategy.OR)
				.build(recipeId.withPrefix("recipes/" + folderName + "/"));
		output.accept(recipeId, recipe, advancementHolder);
	}

	private void ensureValid(final ResourceLocation recipeId) {
		if (criterionCount == 0) throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		if (rows.isEmpty()) throw new IllegalStateException("No pattern is defined for " + recipeId);
		final Set<Character> unused = new HashSet<>(key.keySet());
		for (final String row : rows) {
			for (int i = 0; i < row.length(); i++) {
				final char symbol = row.charAt(i);
				if (symbol != ' ' && !key.containsKey(symbol)) {
					throw new IllegalStateException("Recipe " + recipeId + " uses undefined symbol '" + symbol + "'");
				}
				unused.remove(symbol);
			}
		}
		if (!unused.isEmpty()) throw new IllegalStateException("Unused symbols in " + recipeId + ": " + unused);
		if (inputRow >= rows.size() || inputColumn >= rows.get(inputRow).length()) {
			throw new IllegalStateException("Primary input is outside recipe pattern " + recipeId);
		}
	}
}
