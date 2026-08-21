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

import net.neoforged.api.distmarker.*;

@OnlyIn(Dist.CLIENT)
public class FramePackModel extends Model {

	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(SacksNSuch.location("frame_pack"), "main");
	private final ModelPart pack;

	public FramePackModel(final ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.pack = root.getChild("pack");
	}

	@SuppressWarnings("unused")
	public static LayerDefinition createBodyLayer() {//@formatter:off
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition pack = partdefinition.addOrReplaceChild("pack", CubeListBuilder.create().texOffs(28, 0).addBox(-3.0F, -4.9F, -2.8F, 6.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 11).addBox(-4.5F, 0.1F, -1.8F, 9.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(22, 22).addBox(-3.5F, -5.9F, -1.8F, 7.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-4.5F, -0.15F, -1.55F, 9.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(0, 22).addBox(-3.5F, -6.15F, -1.55F, 7.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.9F, -5.2F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}//@formatter:on

	@Override
	public void renderToBuffer(final PoseStack poseStack, final VertexConsumer vertexConsumer, final int packedLight, final int packedOverlay,
			final int color) {
		pack.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}
