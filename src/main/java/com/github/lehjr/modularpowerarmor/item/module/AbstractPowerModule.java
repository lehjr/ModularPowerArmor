package com.github.lehjr.modularpowerarmor.item.module;

import com.github.lehjr.modularpowerarmor.event.RegisterStuff;
import com.github.lehjr.modularpowerarmor.utils.AdditionalInfo;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractPowerModule extends Item {

    public AbstractPowerModule(String regName) {
        setRegistryName(regName);
        this.setTranslationKey(new StringBuilder("module.").append(getRegistryName().getNamespace()).append(".").append(getRegistryName().getPath()).toString());
        setCreativeTab(RegisterStuff.creativeTab);
        setMaxDamage(-1);
        setMaxStackSize(1);
        setNoRepair();
    }

    // copied from vanilla item and added a range
    public RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids, double range) {
        float pitch = playerIn.rotationPitch;
        float yaw = playerIn.rotationYaw;
        double d0 = playerIn.posX;
        double d1 = playerIn.posY + (double)playerIn.getEyeHeight();
        double d2 = playerIn.posZ;
        Vec3d vec3d = new Vec3d(d0, d1, d2);
        float f2 = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f3 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-pitch * 0.017453292F);
        float f5 = MathHelper.sin(-pitch * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3d vec3d1 = vec3d.add((double)f6 * range, (double)f5 * range, (double)f7 * range);
        return worldIn.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
    }

    @Nullable
    @Override
    public abstract  ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt);

    @Override
    public void addInformation(ItemStack itemStack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (worldIn != null) {
            AdditionalInfo.addInformation(itemStack, worldIn, tooltip, flagIn);
        }
    }
}