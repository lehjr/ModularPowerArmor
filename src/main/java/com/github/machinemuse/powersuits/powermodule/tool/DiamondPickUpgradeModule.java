package com.github.machinemuse.powersuits.powermodule.tool;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.helper.ToolHelpers;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IBlockBreakingModule;
import com.github.lehjr.mpalib.legacy.module.IPowerModule;
import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.event.MuseIcon;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.item.component.ItemComponent;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;

import javax.annotation.Nonnull;

public class DiamondPickUpgradeModule extends PowerModuleBase implements IBlockBreakingModule, IToggleableModule {
    public static final ItemStack emulatedTool = new ItemStack(Items.DIAMOND_PICKAXE);

    public DiamondPickUpgradeModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.solenoid, 1));
        ModuleManager.INSTANCE.addInstallCost(getDataName(), new ItemStack(Items.DIAMOND, 3));
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.SPECIAL;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_DIAMOND_PICK_UPGRADE__DATANAME;
    }

    @Override
    public int getEnergyUsage(@Nonnull ItemStack itemStack) {
        return (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.PICKAXE_ENERGY_CONSUMPTION);
    }

    @Override
    public boolean canHarvestBlock(@Nonnull ItemStack stack, IBlockState state, EntityPlayer player, BlockPos pos, int playerEnergy) {
        if (!ModuleManager.INSTANCE.itemHasModule(stack, MPSModuleConstants.MODULE_PICKAXE__DATANAME))
            return false;

        IPowerModule pickaxeModule = ModuleManager.INSTANCE.getModule(MPSModuleConstants.MODULE_PICKAXE__DATANAME);
        return !((PickaxeModule) pickaxeModule).canHarvestBlock(stack, state, player, pos, playerEnergy) &&
                playerEnergy >= this.getEnergyUsage(stack) && ToolHelpers.isToolEffective(player.getEntityWorld(), pos, getEmulatedTool());
    }

    @Override
    public boolean onBlockDestroyed(ItemStack itemStack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving, int playerEnergy) {
        if (this.canHarvestBlock(itemStack, state, (EntityPlayer) entityLiving, pos, playerEnergy)) {
            ElectricItemUtils.drainPlayerEnergy((EntityPlayer) entityLiving, getEnergyUsage(itemStack));
            return true;
        }
        return false;
    }

    @Override
    public void handleBreakSpeed(BreakSpeed event) {
        event.setNewSpeed((float) (event.getNewSpeed() *
                ModuleManager.INSTANCE.getOrSetModularPropertyDouble(event.getEntityPlayer().inventory.getCurrentItem(), MPSModuleConstants.PICKAXE_HARVEST_SPEED)));
    }

    @Override
    public ItemStack getEmulatedTool() {
        return emulatedTool;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.diamondPickUpgrade;
    }
}