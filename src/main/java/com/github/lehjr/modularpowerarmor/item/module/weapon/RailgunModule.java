package com.github.lehjr.modularpowerarmor.item.module.weapon;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.nbt.NBTUtils;
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
import java.util.concurrent.Callable;

public class RailgunModule extends AbstractPowerModule {
    public RailgunModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.WEAPON, EnumModuleTarget.TOOLONLY, MPASettings.getModuleConfig());
            this.ticker.addBasePropertyDouble(MPAConstants.RAILGUN_TOTAL_IMPULSE, 500, "Ns");
            this.ticker.addBasePropertyDouble(MPAConstants.RAILGUN_ENERGY_COST, 5000, "RF");
            this.ticker.addBasePropertyDouble(MPAConstants.RAILGUN_HEAT_EMISSION, 2, "");
            this.ticker.addTradeoffPropertyDouble(MPAConstants.VOLTAGE, MPAConstants.RAILGUN_TOTAL_IMPULSE, 2500);
            this.ticker.addTradeoffPropertyDouble(MPAConstants.VOLTAGE, MPAConstants.RAILGUN_ENERGY_COST, 25000);
            this.ticker.addTradeoffPropertyDouble(MPAConstants.VOLTAGE, MPAConstants.RAILGUN_HEAT_EMISSION, 10);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> ticker));
        }

        class Ticker extends PlayerTickModule implements IRightClickModule {

            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, Callable<IConfig> config) {
                super(module, category, target, config, true);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, @Nonnull ItemStack itemStackIn) {
                double timer = NBTUtils.getModularItemDoubleOrZero(itemStackIn, MPAConstants.TIMER);
                if (timer > 0) {
                    NBTUtils.setModularItemDoubleOrRemove(itemStackIn, MPAConstants.TIMER, timer - 1 > 0 ? timer - 1 : 0);
                }
            }

            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, PlayerEntity playerIn, Hand hand) {
                if (hand == Hand.MAIN_HAND) {

                    double timer = NBTUtils.getModularItemDoubleOrZero(itemStackIn, MPAConstants.TIMER);
                    double energyConsumption = getEnergyUsage();

                    ElectricItemUtils.givePlayerEnergy(playerIn, (int) energyConsumption);

                    // cooldown timer
                    if (ElectricItemUtils.getPlayerEnergy(playerIn) > energyConsumption && timer == 0) {
System.out.println("fixme!!!");
////                        ElectricItemUtils.drainPlayerEnergy(playerIn, (int) energyConsumption);// fixme disabled during development
//                        ElectricItemUtils.givePlayerEnergy(playerIn, (int) energyConsumption);
//
//                        NBTUtils.setModularItemDoubleOrRemove(itemStackIn, MPAConstants.TIMER, 10);
//                        HeatUtils.heatPlayer(playerIn, applyPropertyModifiers(MPAConstants.RAILGUN_HEAT_EMISSION));
//                        float velocity = (float) applyPropertyModifiers(MPAConstants.RAILGUN_TOTAL_IMPULSE);
//
//                        BoltEntity bolt = new BoltEntity(worldIn, playerIn, velocity);
////
////                        float inaccuracy = 1;
////                        Vec3d lookVec = playerIn.getLookVec().normalize();
////                        bolt.shoot(lookVec.getX(), lookVec.getY(), lookVec.getZ(), velocity, inaccuracy);
//                        worldIn.addEntity(bolt);
                    }
                    return new ActionResult(ActionResultType.SUCCESS, itemStackIn);
                }
                return new ActionResult(ActionResultType.FAIL, itemStackIn);
            }


            @Override
            public int getEnergyUsage() {
                return (int) Math.round(applyPropertyModifiers(MPAConstants.RAILGUN_ENERGY_COST));
            }
        }
    }
}