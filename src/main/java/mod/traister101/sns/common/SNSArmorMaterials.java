package mod.traister101.sns.common;

import net.dries007.tfc.util.Metal;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;

public final class SNSArmorMaterials {
	public static final Holder<ArmorMaterial> HIKING_BOOTS = ArmorMaterials.LEATHER;
	public static final Holder<ArmorMaterial> STEEL_TOE_HIKING_BOOTS = Metal.STEEL.armorMaterial();
	public static final Holder<ArmorMaterial> BLACK_STEEL_TOE_HIKING_BOOTS = Metal.BLACK_STEEL.armorMaterial();
	public static final Holder<ArmorMaterial> BLUE_STEEL_TOE_HIKING_BOOTS = Metal.BLUE_STEEL.armorMaterial();
	public static final Holder<ArmorMaterial> RED_STEEL_TOE_HIKING_BOOTS = Metal.RED_STEEL.armorMaterial();

	private SNSArmorMaterials() {
	}
}
