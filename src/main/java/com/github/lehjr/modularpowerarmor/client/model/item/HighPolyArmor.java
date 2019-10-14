package com.github.lehjr.modularpowerarmor.client.model.item;

import com.github.lehjr.modularpowerarmor.client.render.item.armor.RenderPart;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
    public void render(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        prep(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.bipedBody.rotateAngleY = entityIn.rotationYaw;
        setRotationAngles(entityIn, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, limbSwing);
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        post(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    public void clearAndAddChildWithInitialOffsets(RendererModel mr, float xo, float yo, float zo) {
        System.out.println("doing something here");

        mr.cubeList.clear();
        RenderPart rp = new RenderPart(this, mr);
        mr.addChild(rp);
        setInitialOffsets(rp, xo, yo, zo);
    }

    public void init() {
        clearAndAddChildWithInitialOffsets(bipedHead, 0.0F, 0.0F, 0.0F);
        clearAndAddChildWithInitialOffsets(bipedBody, 0.0F, 0.0F, 0.0F);
        clearAndAddChildWithInitialOffsets(bipedRightArm, 5, 2.0F, 0.0F);
        clearAndAddChildWithInitialOffsets(bipedLeftArm, -5, 2.0F, 0.0F);
        clearAndAddChildWithInitialOffsets(bipedRightLeg, 2, 12.0F, 0.0F);
        clearAndAddChildWithInitialOffsets(bipedLeftLeg, -2, 12.0F, 0.0F);
        bipedHeadwear.cubeList.clear();
    }

    public void setInitialOffsets(RendererModel r, float x, float y, float z) {
        r.offsetX = x;
        r.offsetY = y;
        r.offsetZ = z;
    }

    public void prep(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        try {
            LivingEntity entLive = (LivingEntity) entityIn;
            ItemStack stack = entLive.getActiveItemStack();

            // set pose for main hand, whichever hand that is
            if (entLive.getHeldItemMainhand().isEmpty()) {
                if (getMainHand(entLive) == HandSide.RIGHT)
                    this.rightArmPose = ArmPose.EMPTY;
                else
                    this.leftArmPose = ArmPose.EMPTY;
            } else {
                if (getMainHand(entLive) == HandSide.RIGHT)
                    this.rightArmPose = ArmPose.ITEM;
                else
                    this.leftArmPose = ArmPose.ITEM;
            }

            // the "offhand" is the other hand
            if (entLive.getHeldItemOffhand().isEmpty()) {
                if (getMainHand(entLive) == HandSide.RIGHT)
                    this.rightArmPose = ArmPose.EMPTY;
                else
                    this.leftArmPose = ArmPose.EMPTY;
            } else {
                if (getMainHand(entLive) == HandSide.RIGHT)
                    this.rightArmPose = ArmPose.ITEM;
                else
                    this.leftArmPose = ArmPose.ITEM;
            }

            isSneak = entLive.isSneaking();
            PlayerEntity entPlayer = (PlayerEntity) entLive;
            if ((!stack.isEmpty()) && (entPlayer.getItemInUseCount() > 0)) {
                UseAction UseAction = stack.getUseAction();
                if (UseAction == UseAction.BLOCK) {
                    if (getMainHand(entLive) == HandSide.LEFT)
                        this.leftArmPose = ArmPose.BLOCK;
                    else
                        this.rightArmPose = ArmPose.BLOCK;
                } else if (UseAction == UseAction.BOW) {
                    if (getMainHand(entLive) == HandSide.LEFT)
                        this.leftArmPose = ArmPose.BOW_AND_ARROW;
                    else
                        this.rightArmPose = ArmPose.BOW_AND_ARROW;
                }
            }
        } catch (Exception ignored) {
        }

        bipedHead.isHidden = false;
        bipedBody.isHidden = false;
        bipedRightArm.isHidden = false;
        bipedLeftArm.isHidden = false;
        bipedRightLeg.isHidden = false;
        bipedLeftLeg.isHidden = false;

        bipedHead.showModel = true;
        bipedBody.showModel = true;
        bipedRightArm.showModel = true;
        bipedLeftArm.showModel = true;
        bipedRightLeg.showModel = true;
        bipedLeftLeg.showModel = true;
    }

    public void post(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
//        leftArmPose = ArmPose.EMPTY;
//        rightArmPose = ArmPose.EMPTY;
//        isSneak = false;
    }

    

    @Override
    protected HandSide func_217147_a(LivingEntity p_217147_1_) {
        return super.func_217147_a(p_217147_1_);
    }
//
//    @Override
    protected HandSide getMainHand(Entity entityIn) {
        return  super.func_217147_a((LivingEntity) entityIn);
    }
}