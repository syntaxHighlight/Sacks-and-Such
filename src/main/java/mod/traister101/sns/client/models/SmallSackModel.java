package mod.traister101.sns.client.models;
// Made with Blockbench 4.10.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.*;
import mod.traister101.sns.SacksNSuch;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SmallSackModel extends Model {

	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(SacksNSuch.location("smallsackmodel"), "main");
	private final ModelPart sack;

	public SmallSackModel(final ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.sack = root.getChild("sack");
	}

	@SuppressWarnings("unused")
	public static LayerDefinition createBodyLayer() {//@formatter:off
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition sack = partdefinition.addOrReplaceChild("sack", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 4.0F, 0.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 8).addBox(-1.5F, 2.0F, 0.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(9, 8).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, -8.0F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}//@formatter:on

	public void setupAnim(final float limbSwing, final float limbSwingAmount) {
		sack.zRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
	}

	@Override
	public void renderToBuffer(final PoseStack poseStack, final VertexConsumer vertexConsumer, final int packedLight, final int packedOverlay,
			final int color) {
		sack.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}
