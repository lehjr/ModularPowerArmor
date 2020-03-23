package com.github.lehjr.modularpowerarmor.item.module.weapon;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.toggleable.IToggleableModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
            this.ticker = new Ticker(module, EnumModuleCategory.WEAPON, EnumModuleTarget.TOOLONLY, CommonConfigX.moduleConfig);
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
            if (cap instanceof IToggleableModule) {
                // FIXME: not sure what to do here since this is one case where the toggle thing doesn't actually apply
            }
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> ticker));
        }

        class Ticker extends PlayerTickModule implements IRightClickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config, true);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, @Nonnull ItemStack itemStackIn) {
                double timer = NBTUtils.getModularItemDoubleOrZero(itemStackIn, MPAConstants.TIMER);
                if (timer > 0)
                    NBTUtils.setModularItemDoubleOrRemove(itemStackIn, MPAConstants.TIMER, timer - 1 > 0 ? timer - 1 : 0);
            }

            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, PlayerEntity playerIn, Hand hand) {
                if (hand == Hand.MAIN_HAND) {
                    double range = 64;
                    double timer = NBTUtils.getModularItemDoubleOrZero(itemStackIn, MPAConstants.TIMER);
                    double energyConsumption = getEnergyUsage();
                    if (ElectricItemUtils.getPlayerEnergy(playerIn) > energyConsumption && timer == 0) {
                        ElectricItemUtils.drainPlayerEnergy(playerIn, (int) energyConsumption);
                        NBTUtils.setModularItemDoubleOrRemove(itemStackIn, MPAConstants.TIMER, 10);

                        HeatUtils.heatPlayer(playerIn, applyPropertyModifiers(MPAConstants.RAILGUN_HEAT_EMISSION));
                        RayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY, range);

                        if (raytraceresult != null) {
                            double damage = applyPropertyModifiers(MPAConstants.RAILGUN_TOTAL_IMPULSE) / 100.0;
                            double knockback = damage / 20.0;
                            Vec3d lookVec = playerIn.getLookVec();

                            switch (raytraceresult.getType()) {
                                case MISS:
                                    worldIn.playSound(playerIn, playerIn.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 0.5F, 0.4F / ((float) Math.random() * 0.4F + 0.8F));
                                    break;

                                case BLOCK:
                                    drawParticleStreamTo(playerIn, worldIn, raytraceresult.getHitVec().x, raytraceresult.getHitVec().y, raytraceresult.getHitVec().z);
                                    worldIn.playSound(playerIn, playerIn.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.5F, 0.4F / ((float) Math.random() * 0.4F + 0.8F));
                                    break;

                                case ENTITY:
                                    Entity target = ((EntityRayTraceResult) raytraceresult).getEntity();
                                    drawParticleStreamTo(playerIn, worldIn, raytraceresult.getHitVec().x, raytraceresult.getHitVec().y, raytraceresult.getHitVec().z);
                                    worldIn.playSound(playerIn, playerIn.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.5F, 0.4F / ((float) Math.random() * 0.4F + 0.8F));
                                    DamageSource damageSource = DamageSource.causePlayerDamage(playerIn);
                                    if (((EntityRayTraceResult) raytraceresult).getEntity().attackEntityFrom(damageSource, (int) damage)) {
                                        ((EntityRayTraceResult) raytraceresult).getEntity().addVelocity(lookVec.x * knockback, Math.abs(lookVec.y + 0.2f) * knockback, lookVec.z * knockback);
                                    }
                                    break;
                            }
                            playerIn.addVelocity(-lookVec.x * knockback, Math.abs(-lookVec.y + 0.2f) * knockback, -lookVec.z * knockback);
                        }
                    }
                    playerIn.setActiveHand(hand);
                    return new ActionResult(ActionResultType.SUCCESS, itemStackIn);
                }
                return new ActionResult(ActionResultType.PASS, itemStackIn);
            }

            public void drawParticleStreamTo(PlayerEntity source, World world, double x, double y, double z) {
                Vec3d direction = source.getLookVec().normalize();
                double xoffset = 1.3f;
                double yoffset = -.2;
                double zoffset = 0.3f;
                Vec3d horzdir = direction.normalize();
                horzdir = new Vec3d(horzdir.x, 0, horzdir.z);
                horzdir = horzdir.normalize();
                double cx = source.posX + direction.x * xoffset - direction.y * horzdir.x * yoffset - horzdir.z * zoffset;
                double cy = source.posY + source.getEyeHeight() + direction.y * xoffset + (1 - Math.abs(direction.y)) * yoffset;
                double cz = source.posZ + direction.z * xoffset - direction.y * horzdir.z * yoffset + horzdir.x * zoffset;
                double dx = x - cx;
                double dy = y - cy;
                double dz = z - cz;
                double ratio = Math.sqrt(dx * dx + dy * dy + dz * dz);

                while (Math.abs(cx - x) > Math.abs(dx / ratio)) {
                    world.addParticle(ParticleTypes.MYCELIUM, cx, cy, cz, 0.0D, 0.0D, 0.0D);
                    cx += dx * 0.1 / ratio;
                    cy += dy * 0.1 / ratio;
                    cz += dz * 0.1 / ratio;
                }
            }

            @Override
            public int getEnergyUsage() {
                return (int) Math.round(applyPropertyModifiers(MPAConstants.RAILGUN_ENERGY_COST));
            }
        }
    }
}