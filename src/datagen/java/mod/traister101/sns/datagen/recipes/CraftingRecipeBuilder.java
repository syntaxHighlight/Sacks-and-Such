package mod.traister101.sns.datagen.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.dries007.tfc.common.recipes.AdvancedShapedRecipe;
import net.dries007.tfc.common.recipes.AdvancedShapelessRecipe;
import net.dries007.tfc.common.recipes.outputs.DamageCraftingRemainderModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
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

/**
 * Small recipe builder used by this project's data generator. Recipes that
 * damage crafting tools are emitted as TFC advanced recipes with the 1.21
 * crafting-remainder modifier.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public abstract class CraftingRecipeBuilder<B extends CraftingRecipeBuilder<B>> implements RecipeBuilder {

	protected final String folderName;
	protected final CraftingBookCategory category;
	protected final Item result;
	protected final int count;
	protected final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
	private int criterionCount;
	@Nullable
	protected String group;
	protected boolean damageInputs;

	protected CraftingRecipeBuilder(final String folderName, final ItemLike result, final int count) {
		this.folderName = folderName;
		this.category = CraftingBookCategory.MISC;
		this.result = result.asItem();
		this.count = count;
	}

	public static ShapelessCraftingRecipeBuilder shapeless(final ItemLike result) {
		return shapeless("crafting", result, 1);
	}

	public static ShapelessCraftingRecipeBuilder shapeless(final ItemLike result, final int count) {
		return shapeless("crafting", result, count);
	}

	public static ShapelessCraftingRecipeBuilder shapeless(final String folderName, final ItemLike result) {
		return shapeless(folderName, result, 1);
	}

	public static ShapelessCraftingRecipeBuilder shapeless(final String folderName, final ItemLike result, final int count) {
		return new ShapelessCraftingRecipeBuilder(folderName, result, count);
	}

	public static ShapedCraftingRecipeBuilder shaped(final ItemLike result) {
		return shaped("crafting", result, 1);
	}

	public static ShapedCraftingRecipeBuilder shaped(final ItemLike result, final int count) {
		return shaped("crafting", result, count);
	}

	public static ShapedCraftingRecipeBuilder shaped(final String folderName, final ItemLike result) {
		return shaped(folderName, result, 1);
	}

	public static ShapedCraftingRecipeBuilder shaped(final String folderName, final ItemLike result, final int count) {
		return new ShapedCraftingRecipeBuilder(folderName, result, count);
	}

	@Override
	public B unlockedBy(final String criterionName, final Criterion<?> criterion) {
		advancement.addCriterion(criterionName, criterion);
		criterionCount++;
		return self();
	}

	@Override
	public B group(@Nullable final String groupName) {
		group = groupName;
		return self();
	}

	@Override
	public Item getResult() {
		return result;
	}

	@Override
	public void save(final RecipeOutput output, final ResourceLocation recipeId) {
		ensureValid(recipeId);
		final AdvancementHolder advancementHolder = advancement
				.parent(AdvancementSubProvider.createPlaceholder(ROOT_RECIPE_ADVANCEMENT.toString()))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId))
				.requirements(AdvancementRequirements.Strategy.OR)
				.build(recipeId.withPrefix("recipes/" + folderName + "/"));
		output.accept(recipeId, createRecipe(), advancementHolder);
	}

	@Override
	public void save(final RecipeOutput output) {
		save(output, RecipeBuilder.getDefaultRecipeId(result).withPrefix(folderName + "/"));
	}

	public B damageInputs() {
		damageInputs = true;
		return self();
	}

	protected void ensureValid(final ResourceLocation recipeId) {
		if (criterionCount == 0) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}

	protected ItemStackProvider resultProvider() {
		return ItemStackProvider.of(new ItemStack(result, count));
	}

	protected Optional<ItemStackProvider> damagedRemainder() {
		return Optional.of(ItemStackProvider.of(DamageCraftingRemainderModifier.INSTANCE));
	}

	protected abstract B self();

	protected abstract Recipe<?> createRecipe();

	public static final class ShapedCraftingRecipeBuilder extends CraftingRecipeBuilder<ShapedCraftingRecipeBuilder> {

		private final List<String> rows = Lists.newArrayList();
		private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
		private boolean showNotification = true;

		private ShapedCraftingRecipeBuilder(final String folderName, final ItemLike result, final int count) {
			super(folderName, result, count);
		}

		public ShapedCraftingRecipeBuilder define(final Character symbol, final TagKey<Item> tag) {
			return define(symbol, Ingredient.of(tag));
		}

		public ShapedCraftingRecipeBuilder define(final Character symbol, final ItemLike item) {
			return define(symbol, Ingredient.of(item));
		}

		public ShapedCraftingRecipeBuilder define(final Character symbol, final Ingredient ingredient) {
			if (symbol == ' ') throw new IllegalArgumentException("Whitespace is reserved");
			if (key.putIfAbsent(symbol, ingredient) != null) {
				throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined");
			}
			return this;
		}

		public ShapedCraftingRecipeBuilder pattern(final String pattern) {
			if (!rows.isEmpty() && pattern.length() != rows.getFirst().length()) {
				throw new IllegalArgumentException("Pattern must be the same width on every line");
			}
			rows.add(pattern);
			return this;
		}

		public ShapedCraftingRecipeBuilder pattern(final String... patterns) {
			Arrays.stream(patterns).forEach(this::pattern);
			return this;
		}

		public ShapedCraftingRecipeBuilder showNotification(final boolean value) {
			showNotification = value;
			return this;
		}

		@Override
		protected void ensureValid(final ResourceLocation recipeId) {
			super.ensureValid(recipeId);
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
		}

		@Override
		protected ShapedCraftingRecipeBuilder self() {
			return this;
		}

		@Override
		protected Recipe<?> createRecipe() {
			final ShapedRecipePattern pattern = ShapedRecipePattern.of(key, rows);
			if (damageInputs) {
				return new AdvancedShapedRecipe(pattern, showNotification, resultProvider(), damagedRemainder(), -1, -1);
			}
			return new ShapedRecipe(group == null ? "" : group, category, pattern, new ItemStack(result, count), showNotification);
		}
	}

	public static final class ShapelessCraftingRecipeBuilder extends CraftingRecipeBuilder<ShapelessCraftingRecipeBuilder> {

		private final NonNullList<Ingredient> ingredients = NonNullList.create();

		private ShapelessCraftingRecipeBuilder(final String folderName, final ItemLike result, final int count) {
			super(folderName, result, count);
		}

		public ShapelessCraftingRecipeBuilder requires(final TagKey<Item> tag) {
			return requires(Ingredient.of(tag));
		}

		public ShapelessCraftingRecipeBuilder requires(final ItemLike item) {
			return requires(Ingredient.of(item));
		}

		public ShapelessCraftingRecipeBuilder requires(final ItemLike item, final int quantity) {
			return requires(Ingredient.of(item), quantity);
		}

		public ShapelessCraftingRecipeBuilder requires(final Ingredient ingredient) {
			ingredients.add(ingredient);
			return this;
		}

		public ShapelessCraftingRecipeBuilder requires(final Ingredient ingredient, final int quantity) {
			for (int i = 0; i < quantity; i++) ingredients.add(ingredient);
			return this;
		}

		@Override
		protected void ensureValid(final ResourceLocation recipeId) {
			super.ensureValid(recipeId);
			if (ingredients.isEmpty()) throw new IllegalStateException("Recipe must have at least one ingredient: " + recipeId);
		}

		@Override
		protected ShapelessCraftingRecipeBuilder self() {
			return this;
		}

		@Override
		protected Recipe<?> createRecipe() {
			if (damageInputs) {
				return new AdvancedShapelessRecipe(ingredients, resultProvider(), damagedRemainder(), Optional.empty());
			}
			return new ShapelessRecipe(group == null ? "" : group, category, new ItemStack(result, count), ingredients);
		}
	}
}
