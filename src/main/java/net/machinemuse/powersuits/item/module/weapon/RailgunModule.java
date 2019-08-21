package net.machinemuse.powersuits.item.module.weapon;

import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.rightclick.IRightClickModule;
import net.machinemuse.numina.capabilities.module.rightclick.RightClickCapability;
import net.machinemuse.numina.capabilities.module.rightclick.RightClickModule;
import net.machinemuse.numina.capabilities.module.tickable.IModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTick;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.numina.heat.MuseHeatUtils;
import net.machinemuse.numina.nbt.MuseNBTUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
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
        IPowerModule moduleCap;
        IRightClickModule rightClickie;
        IModuleTick ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.WEAPON, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);
            this.moduleCap.addBasePropertyDouble(MPSConstants.RAILGUN_TOTAL_IMPULSE, 500, "Ns");
            this.moduleCap.addBasePropertyDouble(MPSConstants.RAILGUN_ENERGY_COST, 5000, "RF");
            this.moduleCap.addBasePropertyDouble(MPSConstants.RAILGUN_HEAT_EMISSION, 2, "");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.VOLTAGE, MPSConstants.RAILGUN_TOTAL_IMPULSE, 2500);
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.VOLTAGE, MPSConstants.RAILGUN_ENERGY_COST, 25000);
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.VOLTAGE, MPSConstants.RAILGUN_HEAT_EMISSION, 10);
            this.ticker = new Ticker(moduleCap);
            this.rightClickie = new RightClickie(module, moduleCap);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == RightClickCapability.RIGHT_CLICK)
                return RightClickCapability.RIGHT_CLICK.orEmpty(cap, LazyOptional.of(() -> rightClickie));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }

        class Ticker extends ModuleTick {
            IPowerModule moduleCap;
            public Ticker(IPowerModule moduleCapIn) {
                this.moduleCap = moduleCapIn;
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, @Nonnull ItemStack itemStackIn) {
                double timer = MuseNBTUtils.getModularItemDoubleOrZero(itemStackIn, MPSConstants.TIMER);
                if (timer > 0) MuseNBTUtils.setModularItemDoubleOrRemove(itemStackIn, MPSConstants.TIMER, timer - 1 > 0 ? timer - 1 : 0);
            }
        }
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
                double range = 64;
                double timer = MuseNBTUtils.getModularItemDoubleOrZero(itemStackIn, MPSConstants.TIMER);
                double energyConsumption = getEnergyUsage();
                if (ElectricItemUtils.getPlayerEnergy(playerIn) > energyConsumption && timer == 0) {
                    ElectricItemUtils.drainPlayerEnergy(playerIn, (int) energyConsumption);
                    MuseNBTUtils.setModularItemDoubleOrRemove(itemStackIn, MPSConstants.TIMER, 10);

                    MuseHeatUtils.heatPlayer(playerIn, moduleCap.applyPropertyModifiers(MPSConstants.RAILGUN_HEAT_EMISSION));
                    RayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);

                    if (raytraceresult != null) {
                        double damage = moduleCap.applyPropertyModifiers(MPSConstants.RAILGUN_TOTAL_IMPULSE) / 100.0;
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
                                drawParticleStreamTo(playerIn, worldIn, raytraceresult.getHitVec().x, raytraceresult.getHitVec().y, raytraceresult.getHitVec().z);
                                worldIn.playSound(playerIn, playerIn.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.5F, 0.4F / ((float) Math.random() * 0.4F + 0.8F));
                                DamageSource damageSource = DamageSource.causePlayerDamage(playerIn);
                                if (((EntityRayTraceResult)raytraceresult).getEntity().attackEntityFrom(damageSource, (int) damage)) {
                                    ((EntityRayTraceResult)raytraceresult).getEntity().addVelocity(lookVec.x * knockback, Math.abs(lookVec.y + 0.2f) * knockback, lookVec.z * knockback);
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
            return (int) Math.round(moduleCap.applyPropertyModifiers(MPSConstants.RAILGUN_ENERGY_COST));
        }
    }
}