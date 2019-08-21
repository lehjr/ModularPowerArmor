package net.machinemuse.powersuits.item.module.weapon;


import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.rightclick.IRightClickModule;
import net.machinemuse.numina.capabilities.module.rightclick.RightClickCapability;
import net.machinemuse.numina.capabilities.module.rightclick.RightClickModule;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.numina.heat.MuseHeatUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by User: Andrew2448
 * 5:56 PM 6/14/13
 */
public class LightningModule extends AbstractPowerModule {
    public LightningModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPowerModule moduleCap;
        IRightClickModule rightClickie;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.WEAPON, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);
            this.moduleCap.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 4900000, "RF");
            this.moduleCap.addBasePropertyDouble(MPSConstants.HEAT_EMISSION, 100, "");
            this.rightClickie = new RightClickie(module, moduleCap);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == RightClickCapability.RIGHT_CLICK)
                return RightClickCapability.RIGHT_CLICK.orEmpty(cap, LazyOptional.of(() -> rightClickie));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }

        class RightClickie extends RightClickModule {
            ItemStack module;
            IPowerModule moduleCap;

            public RightClickie(@Nonnull ItemStack module, IPowerModule moduleCap) {
                this.module = module;
                this.moduleCap = moduleCap;
            }

            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, PlayerEntity playerIn, Hand hand) {
                if (hand == Hand.MAIN_HAND) {
                    try {
                        double range = 64;
                        int energyConsumption = getEnergyUsage();
                        if (energyConsumption < ElectricItemUtils.getPlayerEnergy(playerIn)) {
                            ElectricItemUtils.drainPlayerEnergy(playerIn, energyConsumption);
                            MuseHeatUtils.heatPlayer(playerIn, moduleCap.applyPropertyModifiers(MPSConstants.HEAT_EMISSION));

                            RayTraceResult raytraceResult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
                            worldIn.addEntity(new LightningBoltEntity(playerIn.world, raytraceResult.getHitVec().x, raytraceResult.getHitVec().y, raytraceResult.getHitVec().z, false));
                        }
                    } catch (Exception ignored) {
                        return ActionResult.newResult(ActionResultType.FAIL, itemStackIn);
                    }
                    return ActionResult.newResult(ActionResultType.SUCCESS, itemStackIn);
                }
                return ActionResult.newResult(ActionResultType.PASS, itemStackIn);
            }

            @Override
            public int getEnergyUsage() {
                return (int) Math.round(moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION));
            }
        }
    }
}