package net.machinemuse.powersuits.item.module.weapon;

import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.rightclick.IRightClickModule;
import net.machinemuse.numina.capabilities.module.rightclick.RightClickCapability;
import net.machinemuse.numina.capabilities.module.rightclick.RightClickModule;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.entity.EntitySpinningBlade;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BladeLauncherModule extends AbstractPowerModule {
    public BladeLauncherModule(String regName) {
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
            this.moduleCap = new PowerModule(module, EnumModuleCategory.CATEGORY_WEAPON, EnumModuleTarget.TOOLONLY, MPSConfig.INSTANCE);
            this.moduleCap.addBasePropertyDouble(MPSConstants.BLADE_ENERGY, 5000, "RF");
            this.moduleCap.addBasePropertyDouble(MPSConstants.BLADE_DAMAGE, 6, "pt");
            this.rightClickie = new RightClickie(module, moduleCap);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == RightClickCapability.RIGHT_CLICK)
                return RightClickCapability.RIGHT_CLICK.orEmpty(cap, LazyOptional.of(()-> rightClickie));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(()-> moduleCap));
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
                    if (ElectricItemUtils.getPlayerEnergy(playerIn) > moduleCap.applyPropertyModifiers(MPSConstants.BLADE_ENERGY)) {
                        playerIn.setActiveHand(hand);
                        return new ActionResult(ActionResultType.SUCCESS, itemStackIn);
                    }
                }
                return new ActionResult(ActionResultType.PASS, itemStackIn);
            }

            @Override
            public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
                if (!worldIn.isRemote) {
                    int energyConsumption = getEnergyUsage(module);
                    if (ElectricItemUtils.getPlayerEnergy((PlayerEntity) entityLiving) > energyConsumption) {
                        ElectricItemUtils.drainPlayerEnergy((PlayerEntity) entityLiving, energyConsumption);
                        EntitySpinningBlade blade = new EntitySpinningBlade(worldIn, entityLiving);
                        worldIn.addEntity(blade);
                    }
                }
            }

            @Override
            public int getEnergyUsage() {
                return (int) Math.round(moduleCap.applyPropertyModifiers(MPSConstants.BLADE_ENERGY));
            }
        }
    }
}