package com.github.lehjr.modularpowerarmor.client.model.item;

import com.github.lehjr.modularpowerarmor.client.render.item.armor.RenderPart;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 9:24 PM, 11/07/13
 * <p>
 * Ported to Java by lehjr on 11/7/16.
 * <p>
 * FIXME: IMPORTANT!!!!: Note that SmartMoving will mess up the rendering here and the armor's yaw will not change with the player's yaw but will be fine with it not installed.
 */
@OnlyIn(Dist.CLIENT)
public class HighPolyArmor extends BipedModel {
    public CompoundNBT renderSpec = null;
    public EquipmentSlotType visibleSection = EquipmentSlotType.HEAD;

    public HighPolyArmor() {
        super(0);
        init();
    }

    public CompoundNBT getRenderSpec() {
        return this.renderSpec;
    }

    public void setRenderSpec(CompoundNBT nbt) {
        renderSpec = nbt;
    }

    public EquipmentSlotType getVisibleSection() {
        return this.visibleSection;
    }

    public void setVisibleSection(EquipmentSlotType equipmentSlot) {
        this.visibleSection = equipmentSlot;
    }

    public HighPolyArmor(float p_i1149_1_, float p_i1149_2_, int p_i1149_3_, int p_i1149_4_) {
        super(p_i1149_1_, p_i1149_2_, p_i1149_3_, p_i1149_4_);
    }

//    @Override
//    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
////        super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//        if (this.isChild) {
//            matrixStackIn.push();
//            if (this.field_228221_a_) {
//                float f = 1.5F / this.field_228224_g_;
//                matrixStackIn.scale(f, f, f);
//            }
//
//            matrixStackIn.translate(0.0D,
//                    (double)(this.field_228222_b_ / 16.0F),
//                    (double)(this.field_228223_f_ / 16.0F));
//
//
//            this.getHeadParts().forEach((part) -> {
//                part.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//            });
//            matrixStackIn.pop();
//            matrixStackIn.push();
//            float f1 = 1.0F / this.field_228225_h_;
//            matrixStackIn.scale(f1, f1, f1);
//            matrixStackIn.translate(0.0D,
//                    (double)(this.field_228226_i_ / 16.0F),
//                    0.0D);
//
//            this.getBodyParts().forEach((part) -> {
//                part.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//            });
//            matrixStackIn.pop();
//        } else {
//            this.getHeadParts().forEach((part) -> {
//                part.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//            });
//            this.getBodyParts().forEach((part) -> {
//                part.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//            });
//        }
//    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }


//
//    /**
//     * Sets the models various rotation angles then renders the model.
//     */
//        @Override
//    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
//
//
//
//        prep(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
//        this.bipedBody.rotateAngleY = entityIn.rotationYaw;
//        setRotationAngles(entityIn, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, limbSwing);
//        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
//        post(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
//    }

    public void clearAndAddChildWithInitialOffsets(ModelRenderer mr, float xo, float yo, float zo) {
        mr.cubeList.clear();
        RenderPart rp = new RenderPart(this, mr);
        mr.addChild(rp);
        setInitialOffsets(rp, xo, yo, zo);
    }

//    public BipedModel(Function<ResourceLocation, RenderType> renderTypeIn, float modelSizeIn, float yOffsetIn, int textureWidthIn, int textureHeightIn) {
//        super(renderTypeIn, true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F);
//        this.textureWidth = textureWidthIn;
//        this.textureHeight = textureHeightIn;
//        this.bipedHead = new ModelRenderer(this, 0, 0);
//        this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSizeIn);
//        this.bipedHead.setRotationPoint(0.0F, 0.0F + yOffsetIn, 0.0F);
//        this.bipedHeadwear = new ModelRenderer(this, 32, 0);
//        this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSizeIn + 0.5F);
//        this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + yOffsetIn, 0.0F);
//        this.bipedBody = new ModelRenderer(this, 16, 16);
//        this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, modelSizeIn);
//        this.bipedBody.setRotationPoint(0.0F, 0.0F + yOffsetIn, 0.0F);
//        this.bipedRightArm = new ModelRenderer(this, 40, 16);
//        this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn);
//        this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + yOffsetIn, 0.0F);
//        this.bipedLeftArm = new ModelRenderer(this, 40, 16);
//        this.bipedLeftArm.mirror = true;
//        this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn);
//        this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + yOffsetIn, 0.0F);
//        this.bipedRightLeg = new ModelRenderer(this, 0, 16);
//        this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn);
//        this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + yOffsetIn, 0.0F);
//        this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
//        this.bipedLeftLeg.mirror = true;
//        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn);
//        this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F + yOffsetIn, 0.0F);
//    }


    public void init() {
        clearAndAddChildWithInitialOffsets(bipedHead, 0.0F, 0.0F, 0.0F);
        clearAndAddChildWithInitialOffsets(bipedBody, 0.0F, 0.0F, 0.0F);
        clearAndAddChildWithInitialOffsets(bipedRightArm, 5, 2.0F, 0.0F);
        clearAndAddChildWithInitialOffsets(bipedLeftArm, -5, 2.0F, 0.0F);
        clearAndAddChildWithInitialOffsets(bipedRightLeg, 2, 12.0F, 0.0F);
        clearAndAddChildWithInitialOffsets(bipedLeftLeg, -2, 12.0F, 0.0F);
        bipedHeadwear.cubeList.clear();
    }
//
    public void setInitialOffsets(ModelRenderer r, float x, float y, float z) {
//        r.offsetX = x;
//        r.offsetY = y;
//        r.offsetZ = z;
    }
//
//    public void prep(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
//        try {
//            LivingEntity entLive = (LivingEntity) entityIn;
//            ItemStack mainStack = entLive.getHeldItemMainhand();
//            ItemStack offStack = entLive.getHeldItemOffhand();
//            ArmPose armPose = this.getArmPose(entLive, mainStack, offStack, Hand.MAIN_HAND);
//            ArmPose armPose1 = this.getArmPose(entLive, mainStack, offStack, Hand.OFF_HAND);
//            if (entLive.getPrimaryHand() == HandSide.RIGHT) {
//                this.rightArmPose = armPose;
//                this.leftArmPose = armPose1;
//            } else {
//                this.rightArmPose = armPose1;
//                this.leftArmPose = armPose;
//            }
//        } catch (Exception ignored) {
//        }
//
//        bipedHead.showModel = true;
//        bipedBody.showModel = true;
//        bipedRightArm.showModel = true;
//        bipedLeftArm.showModel = true;
//        bipedRightLeg.showModel = true;
//        bipedLeftLeg.showModel = true;
//    }

//    public void post(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
////        leftArmPose = ArmPose.EMPTY;
////        rightArmPose = ArmPose.EMPTY;
////        isSneak = false;
//    }
//
//    /**
//     * From vanilla player renderer
//     * @param player
//     * @param mainhandStack
//     * @param offhandStack
//     * @param hand
//     * @return
//     */
//    private ArmPose getArmPose(LivingEntity player, ItemStack mainhandStack, ItemStack offhandStack, Hand hand) {
//        ArmPose armpose = ArmPose.EMPTY;
//        ItemStack itemstack = hand == Hand.MAIN_HAND ? mainhandStack : offhandStack;
//        if (!itemstack.isEmpty()) {
//            armpose = ArmPose.ITEM;
//            if (player.getItemInUseCount() > 0) {
//                UseAction useaction = itemstack.getUseAction();
//                if (useaction == UseAction.BLOCK) {
//                    armpose = ArmPose.BLOCK;
//                } else if (useaction == UseAction.BOW) {
//                    armpose = ArmPose.BOW_AND_ARROW;
//                } else if (useaction == UseAction.SPEAR) {
//                    armpose = ArmPose.THROW_SPEAR;
//                } else if (useaction == UseAction.CROSSBOW && hand == player.getActiveHand()) {
//                    armpose = ArmPose.CROSSBOW_CHARGE;
//                }
//            } else {
//                boolean flag3 = mainhandStack.getItem() == Items.CROSSBOW;
//                boolean flag = CrossbowItem.isCharged(mainhandStack);
//                boolean flag1 = offhandStack.getItem() == Items.CROSSBOW;
//                boolean flag2 = CrossbowItem.isCharged(offhandStack);
//                if (flag3 && flag) {
//                    armpose = ArmPose.CROSSBOW_HOLD;
//                }
//
//                if (flag1 && flag2 && mainhandStack.getItem().getUseAction(mainhandStack) == UseAction.NONE) {
//                    armpose = ArmPose.CROSSBOW_HOLD;
//                }
//            }
//        }
//        return armpose;
//    }
//
//
//
//    protected HandSide getOffHand(LivingEntity entityIn) {
//        return super.getMainHand(entityIn) == HandSide.LEFT ? HandSide.RIGHT : HandSide.LEFT;
//    }
//    @Override
//    protected HandSide getMainHand(LivingEntity entityIn) {
//        return  super.getMainHand(entityIn);
//    }
}