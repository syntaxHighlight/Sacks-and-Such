package mod.traister101.sns.datagen.recipes;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** Fluent wrapper for TFC's codec-backed 1.21 heating recipe. */
public final class HeatingRecipe {

	private final ResourceLocation recipeId;
	private final Ingredient input;
	private final float temperature;
	@Nullable
	private final Item resultItem;
	private final int itemCount;
	@Nullable
	private final Fluid resultFluid;
	private final int fluidAmount;
	private final List<ItemStackModifier> modifiers = new ArrayList<>();

	private HeatingRecipe(final ResourceLocation recipeId, final Ingredient input, final float temperature, @Nullable final Item resultItem,
			final int itemCount, @Nullable final Fluid resultFluid, final int fluidAmount) {
		this.recipeId = recipeId.withPrefix("heating/");
		this.input = input;
		this.temperature = temperature;
		this.resultItem = resultItem;
		this.itemCount = itemCount;
		this.resultFluid = resultFluid;
		this.fluidAmount = fluidAmount;
	}

	public static HeatingRecipe destroy(final Item input, final float temperature) {
		return destroy(RecipeBuilder.getDefaultRecipeId(input), Ingredient.of(input), temperature);
	}

	public static HeatingRecipe destroy(final ResourceLocation recipeId, final Ingredient input, final float temperature) {
		return new HeatingRecipe(recipeId, input, temperature, null, 0, null, 0);
	}

	public static HeatingRecipe cook(final ResourceLocation recipeId, final Ingredient input, final float temperature, final Item result) {
		return cook(recipeId, input, temperature, result, 1);
	}

	public static HeatingRecipe cook(final ResourceLocation recipeId, final Ingredient input, final float temperature, final Item result,
			final int count) {
		return new HeatingRecipe(recipeId, input, temperature, result, count, null, 0);
	}

	public static HeatingRecipe melt(final Item input, final float temperature, final Fluid fluid, final int amount) {
		return melt(RecipeBuilder.getDefaultRecipeId(input), Ingredient.of(input), temperature, fluid, amount);
	}

	public static HeatingRecipe melt(final ResourceLocation recipeId, final Ingredient input, final float temperature, final Fluid fluid,
			final int amount) {
		return new HeatingRecipe(recipeId, input, temperature, null, 0, fluid, amount);
	}

	public HeatingRecipe modifier(final ItemStackModifier modifier) {
		if (resultItem == null) throw new IllegalStateException("This recipe doesn't have an item result");
		modifiers.add(modifier);
		return this;
	}

	public void save(final RecipeOutput output) {
		final ItemStackProvider itemOutput = resultItem == null
				? ItemStackProvider.empty()
				: ItemStackProvider.of(new ItemStack(resultItem, itemCount), modifiers.stream().map(ItemStackModifier::value).toList());
		final FluidStack fluidOutput = resultFluid == null ? FluidStack.EMPTY : new FluidStack(resultFluid, fluidAmount);
		output.accept(recipeId, new net.dries007.tfc.common.recipes.HeatingRecipe(input, itemOutput, fluidOutput, temperature, false), null);
	}
}
