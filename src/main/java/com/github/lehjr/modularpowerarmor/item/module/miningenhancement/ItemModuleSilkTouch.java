package com.github.lehjr.modularpowerarmor.item.module.miningenhancement;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.enchantment.IEnchantmentModule;
import com.github.lehjr.mpalib.capabilities.module.miningenhancement.IMiningEnhancementModule;
import com.github.lehjr.mpalib.capabilities.module.miningenhancement.MiningEnhancement;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class ItemModuleSilkTouch extends AbstractPowerModule {
    public ItemModuleSilkTouch(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IMiningEnhancementModule miningEnhancement;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.miningEnhancement = new Enhancement(module, EnumModuleCategory.MINING_ENHANCEMENT, EnumModuleTarget.TOOLONLY, CommonConfigX.moduleConfig);
            this.miningEnhancement.addBasePropertyDouble(MPAConstants.SILK_TOUCH_ENERGY_CONSUMPTION, 2500, "RF");
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> miningEnhancement));
        }

        class Enhancement extends MiningEnhancement implements IEnchantmentModule {
            public Enhancement(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

            /**
             * Called before a block is broken.  Return true to prevent default block harvesting.
             *
             * Note: In SMP, this is called on both client and server sides!
             *
             * @param itemstack The current ItemStack
             * @param pos Block's position in world
             * @param player The Player that is wielding the item
             * @return True to prevent harvesting, false to continue as normal
             */
            @Override
            public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
                if (!player.world.isRemote) {
                    if (getEnergyUsage() > ElectricItemUtils.getPlayerEnergy(player))
                        removeEnchantment(itemstack);
                    else {
                        Block block = player.world.getBlockState(pos).getBlock();
                        // fixme!!

//                        if (block.canSilkHarvest(player.world, pos, player.world.getBlockState(pos), player)) {
//                            ElectricItemUtils.drainPlayerEnergy(player, getEnergyUsage());
//                        }
                    }
                }
                return false;
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(MPAConstants.SILK_TOUCH_ENERGY_CONSUMPTION);
            }

            @Override
            public Enchantment getEnchantment() {
                return Enchantments.SILK_TOUCH;
            }

            @Override
            public int getLevel(@Nonnull ItemStack itemStack) {
                return 1;
            }
        }
    }
}