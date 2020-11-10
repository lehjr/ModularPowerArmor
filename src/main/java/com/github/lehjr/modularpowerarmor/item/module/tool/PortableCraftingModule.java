//package com.github.lehjr.modularpowerarmor.item.module.tool;
//
//import com.github.lehjr.modularpowerarmor.config.MPASettings;
//import com.github.lehjr.modularpowerarmor.container.MPAWorkbenchContainerProvider;
//import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
//import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
//import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleTarget;
//import com.github.lehjr.mpalib.util.capabilities.module.powermodule.IConfig;
//import com.github.lehjr.mpalib.util.capabilities.module.powermodule.PowerModuleCapability;
//import com.github.lehjr.mpalib.util.capabilities.module.rightclick.IRightClickModule;
//import com.github.lehjr.mpalib.util.capabilities.module.rightclick.RightClickModule;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.ServerPlayerEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.util.ActionResult;
//import net.minecraft.util.Direction;
//import net.minecraft.util.Hand;
//import net.minecraft.world.World;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.capabilities.ICapabilityProvider;
//import net.minecraftforge.common.util.LazyOptional;
//import net.minecraftforge.fml.network.NetworkHooks;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//import java.util.concurrent.Callable;
//
//public class PortableCraftingModule extends AbstractPowerModule {
//    public PortableCraftingModule() {
//    }
//
//    @Nullable
//    @Override
//    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
//        return new CapProvider(stack);
//    }
//
//    public class CapProvider implements ICapabilityProvider {
//        ItemStack module;
//        IRightClickModule rightClick;
//
//        public CapProvider(@Nonnull ItemStack module) {
//            this.module = module;
//            this.rightClick = new RightClickie(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, MPASettings::getModuleConfig);
//        }
//
//        @Nonnull
//        @Override
//        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
//            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> rightClick));
//        }
//
//        class RightClickie extends RightClickModule {
//            public RightClickie(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, Callable<IConfig> config) {
//                super(module, category, target, config);
//            }
//
//            @Override
//            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, PlayerEntity playerIn, Hand hand) {
//                if (!worldIn.isRemote()) {
//                    NetworkHooks.openGui((ServerPlayerEntity) playerIn, new MPAWorkbenchContainerProvider(3), (buffer) -> buffer.writeInt(3));
//                }
//                return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
//            }
//        }
//    }
//}