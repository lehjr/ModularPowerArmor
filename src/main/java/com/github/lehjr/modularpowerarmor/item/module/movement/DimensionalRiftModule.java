package com.github.lehjr.modularpowerarmor.item.module.movement;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.module.rightclick.RightClickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Created by Eximius88 on 2/3/14.
 */
public class DimensionalRiftModule extends AbstractPowerModule {
    public DimensionalRiftModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public static class CapProvider implements IPowerModuleCapabilityProvider {
        ItemStack module;
        IRightClickModule rightClick;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.rightClick = new RightClickie(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.TOOLONLY, MPAConfig.moduleConfig);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return (T) rightClick;
            }
            return null;
        }

        static class RightClickie extends RightClickModule {
            public RightClickie(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

//            final int theOverworld = 0;
//            final int theNether = -1;
//            final int theEnd = 1;
            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
                if (!playerIn.isRiding() && !playerIn.isBeingRidden() && playerIn.isNonBoss() && ((playerIn instanceof EntityPlayerMP))) {
                    EntityPlayerMP player = (EntityPlayerMP) playerIn;
                    BlockPos coords = playerIn.bedLocation != null ? playerIn.bedLocation : playerIn.world.getSpawnPoint();

                    while (!worldIn.isAirBlock(coords) && !worldIn.isAirBlock(coords.up())) {
                        coords = coords.up();
                    }

                    playerIn.changeDimension(0, new CommandTeleporter(coords));
                    int energyConsumption = getEnergyUsage();
                    int playerEnergy = ElectricItemUtils.getPlayerEnergy(playerIn);
                    if (playerEnergy >= energyConsumption) {
                        ElectricItemUtils.drainPlayerEnergy(player, getEnergyUsage());
                        HeatUtils.heatPlayer(player, applyPropertyModifiers(Constants.HEAT_GENERATION));
                        return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
                    }
                }
                return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(Constants.ENERGY_CONSUMPTION);
            }

            // Copied from Forge.
            private class CommandTeleporter implements ITeleporter {
                private final BlockPos targetPos;

                private CommandTeleporter(BlockPos targetPos)
                {
                    this.targetPos = targetPos;
                }

                @Override
                public void placeEntity(World world, Entity entity, float yaw)
                {
                    entity.moveToBlockPosAndAngles(targetPos, yaw, entity.rotationPitch);
                }
            }
        }
    }
}
