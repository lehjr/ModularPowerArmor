package com.github.lehjr.modularpowerarmor.client.renderer;

import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import com.github.lehjr.modularpowerarmor.client.model.helper.LuxCapHelper;
import com.github.lehjr.modularpowerarmor.entity.LuxCapacitorEntity;
import com.github.lehjr.mpalib.client.model.helper.ModelHelper;
import com.github.lehjr.mpalib.client.render.entity.MPALibEntityRenderer;
import com.github.lehjr.mpalib.math.Colour;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.model.TransformationHelper;

import javax.annotation.Nullable;
import java.util.Random;

public class LuxCapacitorEntityRenderer extends MPALibEntityRenderer<LuxCapacitorEntity> {


    public LuxCapacitorEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    public ResourceLocation getEntityTexture(LuxCapacitorEntity luxCapacitorEntity) {
        return null;
    }

    private final Random random = new Random();

    ItemStack getStack(Colour color) {
        ItemStack stack = new ItemStack(MPAObjects.luxcapacitor_module);
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt("colour", color.getInt());
        return stack;
    }

    @Override
    public void render(LuxCapacitorEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        ItemStack itemstack = getStack(entityIn.color);
        int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getDamage();
        this.random.setSeed((long) i);
        IBakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(itemstack, entityIn.world, (LivingEntity) null);
        int time = (int) System.currentTimeMillis() % 360;
        matrixStackIn.rotate(TransformationHelper.quatFromXYZ(new Vector3f(0, time / 2, 0), true));
        matrixStackIn.scale(1.8F, 1.8F, 1.8F);

        boolean flag = ibakedmodel.isGui3d();
        matrixStackIn.push();
        Minecraft.getInstance().getItemRenderer().renderItem(itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
        matrixStackIn.pop();
        if (!flag) {
            matrixStackIn.translate(0.0, 0.0, 0.09375F);
        }
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }
}