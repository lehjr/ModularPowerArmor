package com.github.lehjr.modularpowerarmor.item.module.special;

import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.util.capabilities.module.toggleable.IToggleableModule;
import com.github.lehjr.mpalib.util.capabilities.module.toggleable.ToggleableModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by User: Andrew2448
 * 11:12 PM 6/11/13
 */
public class ClockModule extends AbstractPowerModule {
   public ClockModule(String regName) {
        super(regName);

       this.addPropertyOverride(new ResourceLocation("time"), new IItemPropertyGetter() {
           @OnlyIn(Dist.CLIENT)
           private double rotation;
           @OnlyIn(Dist.CLIENT)
           private double rota;
           @OnlyIn(Dist.CLIENT)
           private long lastUpdateTick;

           @OnlyIn(Dist.CLIENT)
           public float call(ItemStack p_call_1_, @Nullable World p_call_2_, @Nullable LivingEntity p_call_3_) {
               boolean flag = p_call_3_ != null;
               Entity entity = (Entity)(flag ? p_call_3_ : p_call_1_.getItemFrame());
               if (p_call_2_ == null && entity != null) {
                   p_call_2_ = entity.world;
               }

               if (p_call_2_ == null) {
                   return 0.0F;
               } else {
                   double d0;
                   if (p_call_2_.dimension.isSurfaceWorld()) {
                       d0 = (double)p_call_2_.getCelestialAngle(1.0F);
                   } else {
                       d0 = Math.random();
                   }

                   d0 = this.wobble(p_call_2_, d0);
                   return (float)d0;
               }
           }

           @OnlyIn(Dist.CLIENT)
           private double wobble(World p_185087_1_, double p_185087_2_) {
               if (p_185087_1_.getGameTime() != this.lastUpdateTick) {
                   this.lastUpdateTick = p_185087_1_.getGameTime();
                   double d0 = p_185087_2_ - this.rotation;
                   d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
                   this.rota += d0 * 0.1D;
                   this.rota *= 0.9D;
                   this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
               }

               return this.rotation;
           }
       });
   }
    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IToggleableModule moduleToggle;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleToggle = new ToggleableModule(module, EnumModuleCategory.SPECIAL, EnumModuleTarget.HEADONLY, MPASettings.getModuleConfig(), true);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap instanceof IToggleableModule) {
                ((IToggleableModule) cap).updateFromNBT();
            }
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(()-> moduleToggle));
        }
    }
}