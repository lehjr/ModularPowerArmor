package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.config.CommonConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.blockbreaking.IBlockBreakingModule;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.module.rightclick.RightClickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ShearsModule extends AbstractPowerModule {
    private static final ItemStack emulatedTool = new ItemStack(Items.SHEARS);

    public ShearsModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IRightClickModule rightClick;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.rightClick = new BlockBreaker(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);
            this.rightClick.addBasePropertyDouble(Constants.ENERGY_CONSUMPTION, 1000, "RF");
            this.rightClick.addBasePropertyDouble(Constants.HARVEST_SPEED, 8, "x");
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == PowerModuleCapability.POWER_MODULE;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return (T) rightClick;
            }
            return null;
        }

        class BlockBreaker extends RightClickModule implements IBlockBreakingModule {
            public BlockBreaker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

            @Override
            public boolean onBlockDestroyed(ItemStack itemStack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving, int playerEnergy) {
                if (entityLiving.world.isRemote) {
                    return false;
                }
                Block block = state.getBlock();

                if (block instanceof IShearable && ElectricItemUtils.getPlayerEnergy(((EntityPlayer) entityLiving)) > getEnergyUsage()) {
                    IShearable target = (IShearable) block;
                    if (target.isShearable(itemStack, entityLiving.world, pos)) {
                        List<ItemStack> drops = target.onSheared(itemStack, entityLiving.world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemStack));
                        Random rand = new Random();

                        for (ItemStack stack : drops) {
                            float f = 0.7F;
                            double d = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                            double d1 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                            double d2 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                            EntityItem entityitem = new EntityItem(entityLiving.world, (double) pos.getX() + d, (double) pos.getY() + d1, (double) pos.getZ() + d2, stack);
                            entityitem.setDefaultPickupDelay(); // this is 10
                            entityitem.world.spawnEntity(entityitem);
                        }
                        ElectricItemUtils.drainPlayerEnergy((EntityPlayer) entityLiving, getEnergyUsage());
                        ((EntityPlayer) (entityLiving)).addStat(StatList.getBlockStats(block), 1);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public ItemStack getEmulatedTool() {
                return new ItemStack(Items.SHEARS);
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(Constants.ENERGY_CONSUMPTION);
            }

            @Override
            public void handleBreakSpeed(PlayerEvent.BreakSpeed event) {
                event.setNewSpeed((float) (event.getNewSpeed() * applyPropertyModifiers(Constants.HARVEST_SPEED)));
            }

            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
                if (playerIn.world.isRemote) {
                    return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
                }
                RayTraceResult rayTraceResult = rayTrace(playerIn.world, playerIn, false);

                if (rayTraceResult != null && rayTraceResult.entityHit instanceof IShearable) {
                    IShearable target = (IShearable) rayTraceResult.entityHit;
                    Entity entity = rayTraceResult.entityHit;
                    if (target.isShearable(itemStackIn, entity.world, new BlockPos(entity))) {
                        List<ItemStack> drops = target.onSheared(itemStackIn, entity.world, new BlockPos(entity),
                                EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByLocation("fortune"), itemStackIn));
                        Random rand = new Random();
                        for (ItemStack drop : drops) {
                            EntityItem ent = entity.entityDropItem(drop, 1.0F);
                            ent.motionY += rand.nextFloat() * 0.05F;
                            ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                            ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                        }
                        ElectricItemUtils.drainPlayerEnergy(playerIn, getEnergyUsage());
                        return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
                    }
                }
                return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
            }
        }
    }
}