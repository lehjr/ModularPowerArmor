package com.github.machinemuse.powersuits.powermodule.vision;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IPlayerTickModule;
import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.event.MuseIcon;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.item.component.ItemComponent;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class NightVisionModule extends PowerModuleBase implements IPlayerTickModule, IToggleableModule {
    static final int powerDrain = 50;
    private static final Potion nightvision = MobEffects.NIGHT_VISION;

    public NightVisionModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.laserHologram, 1));
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.controlCircuit, 1));
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.VISION;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_NIGHT_VISION__DATANAME;
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
        if (player.world.isRemote)
            return;

        double totalEnergy = ElectricItemUtils.getPlayerEnergy(player);
        PotionEffect nightVisionEffect = player.isPotionActive(nightvision) ? player.getActivePotionEffect(nightvision) : null;

        if (totalEnergy > powerDrain) {
            if (nightVisionEffect == null || nightVisionEffect.getDuration() < 250 && nightVisionEffect.getAmplifier() == -3) {
                player.addPotionEffect(new PotionEffect(nightvision, 500, -3, false, false));
                ElectricItemUtils.drainPlayerEnergy(player, powerDrain);
            }
        } else
            onPlayerTickInactive(player, item);
    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
        PotionEffect nightVisionEffect = null;
        if (player.isPotionActive(nightvision)) {
            nightVisionEffect = player.getActivePotionEffect(nightvision);
            if (nightVisionEffect.getAmplifier() == -3) {
                player.removePotionEffect(nightvision);
            }
        }
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.nightVision;
    }
}