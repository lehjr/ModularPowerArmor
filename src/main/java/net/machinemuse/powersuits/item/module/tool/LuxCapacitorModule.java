package net.machinemuse.powersuits.item.module.tool;

import net.machinemuse.numina.capabilities.IConfig;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleTarget;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.capabilities.module.rightclick.IRightClickModule;
import net.machinemuse.numina.capabilities.module.rightclick.RightClickModule;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.numina.heat.MuseHeatUtils;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.entity.LuxCapacitorEntity;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
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

public class LuxCapacitorModule extends AbstractPowerModule {
    public LuxCapacitorModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IRightClickModule rightClick;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.rightClick = new RightClickie(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);
            this.rightClick.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 1000, "RF");
            this.rightClick.addTradeoffPropertyDouble(MPSConstants.RED, MPSConstants.RED_HUE, 1, "%");
            this.rightClick.addTradeoffPropertyDouble(MPSConstants.GREEN, MPSConstants.GREEN_HUE, 1, "%");
            this.rightClick.addTradeoffPropertyDouble(MPSConstants.BLUE, MPSConstants.BLUE_HUE, 1, "%");
            this.rightClick.addTradeoffPropertyDouble(MPSConstants.ALPHA, MPSConstants.OPACITY, 1, "%");
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> rightClick));
        }

        class RightClickie extends RightClickModule {
            public RightClickie(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, PlayerEntity playerIn, Hand hand) {
                playerIn.setActiveHand(hand);
                if (!worldIn.isRemote) {
                    double energyConsumption = getEnergyUsage();
                    MuseHeatUtils.heatPlayer(playerIn, energyConsumption / 500);
                    if (ElectricItemUtils.getPlayerEnergy(playerIn) > energyConsumption) {
                        ElectricItemUtils.drainPlayerEnergy(playerIn, (int) energyConsumption);

                        double red = applyPropertyModifiers(MPSConstants.RED_HUE);
                        double green = applyPropertyModifiers(MPSConstants.GREEN_HUE);
                        double blue = applyPropertyModifiers(MPSConstants.BLUE_HUE);
                        double alpha = applyPropertyModifiers(MPSConstants.OPACITY);

                        LuxCapacitorEntity luxCapacitor = new LuxCapacitorEntity(worldIn, playerIn, new Colour(red, green, blue, alpha));
                        worldIn.addEntity(luxCapacitor);
                    }
                    return ActionResult.newResult(ActionResultType.SUCCESS, itemStackIn);
                }
                return ActionResult.newResult(ActionResultType.PASS, itemStackIn);
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
            }
        }
    }
}