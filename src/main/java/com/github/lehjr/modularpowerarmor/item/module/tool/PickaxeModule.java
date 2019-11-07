package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.blockbreaking.IBlockBreakingModule;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModule;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class PickaxeModule extends AbstractPowerModule {
    protected static final ItemStack emulatedTool = new ItemStack(Items.IRON_PICKAXE);
    public PickaxeModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements IPowerModuleCapabilityProvider {
        ItemStack module;
        IBlockBreakingModule blockBreaking;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.blockBreaking = new BlockBreaker(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, MPAConfig.moduleConfig);
            this.blockBreaking.addBasePropertyDouble(Constants.ENERGY_CONSUMPTION, 500, "RF");
            this.blockBreaking.addBasePropertyDouble(Constants.HARVEST_SPEED, 8, "x");
            this.blockBreaking.addTradeoffPropertyDouble(Constants.OVERCLOCK, Constants.ENERGY_CONSUMPTION, 9500);
            this.blockBreaking.addTradeoffPropertyDouble(Constants.OVERCLOCK, Constants.HARVEST_SPEED, 52);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return (T) blockBreaking;
            }
            return null;
        }

        class BlockBreaker extends PowerModule implements IBlockBreakingModule {
            public BlockBreaker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

            @Override
            public boolean onBlockDestroyed(ItemStack itemStack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving, int playerEnergy) {
                if (this.canHarvestBlock(itemStack, state, (EntityPlayer) entityLiving, pos, playerEnergy)) {
                    ElectricItemUtils.drainPlayerEnergy((EntityPlayer) entityLiving, getEnergyUsage());
                    return true;
                }
                return false;
            }

            @Override
            public ItemStack getEmulatedTool() {
                return new ItemStack(Items.IRON_PICKAXE);
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(Constants.ENERGY_CONSUMPTION);
            }

            @Override
            public void handleBreakSpeed(PlayerEvent.BreakSpeed event) {
                event.setNewSpeed((float) (event.getNewSpeed() * applyPropertyModifiers(Constants.HARVEST_SPEED)));
            }
        }
    }
}