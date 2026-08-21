package mod.traister101.sns.datagen.recipes;

/** Compatibility wrapper around TFC's codec-backed 1.21 output modifiers. */
@FunctionalInterface
public interface ItemStackModifier {

	net.dries007.tfc.common.recipes.outputs.ItemStackModifier value();

	static ItemStackModifier of(final net.dries007.tfc.common.recipes.outputs.ItemStackModifier modifier) {
		return () -> modifier;
	}
}
