package net.machinemuse.powersuits.item.module.tool;

import net.machinemuse.numina.capabilities.module.blockbreaking.BlockBreaking;
import net.machinemuse.numina.capabilities.module.blockbreaking.BlockBreakingCapability;
import net.machinemuse.numina.capabilities.module.blockbreaking.IBlockBreakingModule;
import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.rightclick.IRightClickModule;
import net.machinemuse.numina.capabilities.module.rightclick.RightClickModule;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// FIXME IShearable ?? pretty much a dead thing now?
public class ShearsModule extends AbstractPowerModule {
    private static final ItemStack emulatedTool = new ItemStack(Items.SHEARS);
    static final ArrayList<Material> materials =
            new ArrayList<Material>() {{
                add(Material.PLANTS);
                add(Material.OCEAN_PLANT);
                add(Material.TALL_PLANTS);
                add(Material.SEA_GRASS);
                add(Material.WEB);
                add(Material.WOOL);
                add(Material.LEAVES);

            }};

    public ShearsModule(String regName) {
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
        IBlockBreakingModule blockBreaking;
        IRightClickModule rightClick;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);

            this.moduleCap.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 1000, "RF");
            this.moduleCap.addBasePropertyDouble(MPSConstants.HARVEST_SPEED, 8, "x");

            this.blockBreaking = new BlockBreaker();

            this.rightClick = new RightClickie();
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == BlockBreakingCapability.BLOCK_BREAKING)
                return BlockBreakingCapability.BLOCK_BREAKING.orEmpty(cap, LazyOptional.of(() -> blockBreaking));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }

        class BlockBreaker extends BlockBreaking {
            @Override
            public boolean onBlockDestroyed(ItemStack itemStack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving, int playerEnergy) {
                if (entityLiving.world.isRemote()) {
                    return false;
                }
                Block block = state.getBlock();

                if (block instanceof IShearable && ElectricItemUtils.getPlayerEnergy(((PlayerEntity) entityLiving)) > getEnergyUsage()) {
                    IShearable target = (IShearable) block;
                    if (target.isShearable(itemStack, entityLiving.world, pos)) {
                        List<ItemStack> drops = target.onSheared(itemStack, entityLiving.world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemStack));
                        Random rand = new Random();

                        for (ItemStack stack : drops) {
                            float f = 0.7F;
                            double d = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                            double d1 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                            double d2 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                            ItemEntity entityitem = new ItemEntity(entityLiving.world, (double) pos.getX() + d, (double) pos.getY() + d1, (double) pos.getZ() + d2, stack);
                            entityitem.setDefaultPickupDelay(); // this is 10
                            entityitem.world.addEntity(entityitem);
                        }
                        ElectricItemUtils.drainPlayerEnergy((PlayerEntity) entityLiving, getEnergyUsage());
                        ((PlayerEntity) (entityLiving)).addStat(block.getRegistryName());
                        return true;
                    }
                }
                return false;
            }

            @Override
            public ItemStack getEmulatedTool() {
                return emulatedTool;
            }

            @Override
            public int getEnergyUsage() {
                return (int) moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
            }

            @Override
            public void handleBreakSpeed(PlayerEvent.BreakSpeed event) {
                event.setNewSpeed((float) (event.getNewSpeed() * moduleCap.applyPropertyModifiers(MPSConstants.HARVEST_SPEED)));
            }
        }

        class RightClickie extends RightClickModule {

            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, PlayerEntity playerIn, Hand hand) {
                if (playerIn.world.isRemote) {
                    return ActionResult.newResult(ActionResultType.PASS, itemStackIn);
                }

                RayTraceResult rayTraceResult = rayTrace(playerIn.world, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);

                if (rayTraceResult != null && rayTraceResult instanceof EntityRayTraceResult
                        && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof IShearable) {
                    IShearable target = (IShearable) ((EntityRayTraceResult) rayTraceResult).getEntity();
                    Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();
                    if (target.isShearable(itemStackIn, entity.world, new BlockPos(entity))) {
                        List<ItemStack> drops = target.onSheared(itemStackIn, entity.world, new BlockPos(entity), 0);
                        Random rand = new Random();
                        for (ItemStack drop : drops) {
                            ItemEntity ent = entity.entityDropItem(drop, 1.0F);
                            Vec3d motion = ent.getMotion();
                            ent.setMotion(
                                    motion.x + (rand.nextFloat() - rand.nextFloat()) * 0.1F,
                                    motion.y + rand.nextFloat() * 0.05F,
                                    motion.z + (rand.nextFloat() - rand.nextFloat()) * 0.1F);
                        }
                        ElectricItemUtils.drainPlayerEnergy(playerIn, getEnergyUsage());
                        return ActionResult.newResult(ActionResultType.SUCCESS, itemStackIn);
                    }
                }
                return ActionResult.newResult(ActionResultType.PASS, itemStackIn);
            }

            @Override
            public int getEnergyUsage() {
                return (int) moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
            }
        }
    }
}