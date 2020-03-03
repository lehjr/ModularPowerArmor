package com.github.lehjr.modularpowerarmor.client.model.item;

import com.github.lehjr.modularpowerarmor.client.render.item.armor.RenderPart;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
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
            ItemStack mainStack = entLive.getHeldItemMainhand();
            ItemStack offStack = entLive.getHeldItemOffhand();
            ArmPose armPose = this.getArmPose(entLive, mainStack, offStack, Hand.MAIN_HAND);
            ArmPose armPose1 = this.getArmPose(entLive, mainStack, offStack, Hand.OFF_HAND);
            if (entLive.getPrimaryHand() == HandSide.RIGHT) {
                this.rightArmPose = armPose;
                this.leftArmPose = armPose1;
            } else {
                this.rightArmPose = armPose1;
                this.leftArmPose = armPose;
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

    /**
     * From vanilla player renderer
     * @param player
     * @param mainhandStack
     * @param offhandStack
     * @param hand
     * @return
     */
    private ArmPose getArmPose(LivingEntity player, ItemStack mainhandStack, ItemStack offhandStack, Hand hand) {
        ArmPose armpose = ArmPose.EMPTY;
        ItemStack itemstack = hand == Hand.MAIN_HAND ? mainhandStack : offhandStack;
        if (!itemstack.isEmpty()) {
            armpose = ArmPose.ITEM;
            if (player.getItemInUseCount() > 0) {
                UseAction useaction = itemstack.getUseAction();
                if (useaction == UseAction.BLOCK) {
                    armpose = ArmPose.BLOCK;
                } else if (useaction == UseAction.BOW) {
                    armpose = ArmPose.BOW_AND_ARROW;
                } else if (useaction == UseAction.SPEAR) {
                    armpose = ArmPose.THROW_SPEAR;
                } else if (useaction == UseAction.CROSSBOW && hand == player.getActiveHand()) {
                    armpose = ArmPose.CROSSBOW_CHARGE;
                }
            } else {
                boolean flag3 = mainhandStack.getItem() == Items.CROSSBOW;
                boolean flag = CrossbowItem.isCharged(mainhandStack);
                boolean flag1 = offhandStack.getItem() == Items.CROSSBOW;
                boolean flag2 = CrossbowItem.isCharged(offhandStack);
                if (flag3 && flag) {
                    armpose = ArmPose.CROSSBOW_HOLD;
                }

                if (flag1 && flag2 && mainhandStack.getItem().getUseAction(mainhandStack) == UseAction.NONE) {
                    armpose = ArmPose.CROSSBOW_HOLD;
                }
            }
        }
        return armpose;
    }

    @Override
    protected HandSide func_217147_a(LivingEntity p_217147_1_) {
        return super.func_217147_a(p_217147_1_);
    }

    protected HandSide getOffHand(Entity entityIn) {
        return super.func_217147_a((LivingEntity) entityIn) == HandSide.LEFT ? HandSide.RIGHT : HandSide.LEFT;
    }

    protected HandSide getMainHand(Entity entityIn) {
        return  super.func_217147_a((LivingEntity) entityIn);
    }
}