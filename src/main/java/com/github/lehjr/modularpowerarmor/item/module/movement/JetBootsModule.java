package com.github.lehjr.modularpowerarmor.item.module.movement;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.config.MPALibConfig;
import com.github.lehjr.mpalib.control.PlayerMovementInputWrapper;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IPlayerTickModule;
import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
import com.github.lehjr.modularpowerarmor.client.sound.SoundDictionary;
import com.github.lehjr.modularpowerarmor.client.event.MuseIcon;
import com.github.lehjr.modularpowerarmor.event.MovementManager;
import com.github.lehjr.modularpowerarmor.item.component.ItemComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

public class JetBootsModule extends AbstractPowerModule implements IToggleableModule, IPlayerTickModule {
    public JetBootsModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.ionThruster, 2));
        addBasePropertyDouble(ModuleConstants.JETBOOTS_ENERGY_CONSUMPTION, 0);
        addBasePropertyDouble(ModuleConstants.JETBOOTS_THRUST, 0);
        addTradeoffPropertyDouble(ModuleConstants.THRUST, ModuleConstants.JETBOOTS_ENERGY_CONSUMPTION, 750, "RF");
        addTradeoffPropertyDouble(ModuleConstants.THRUST, ModuleConstants.JETBOOTS_THRUST, 0.08);
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.MOVEMENT;
    }

    @Override
    public String getDataName() {
        return ModuleConstants.MODULE_JETBOOTS__DATANAME;
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
        if (player.isInWater())
            return;

        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        boolean hasFlightControl = ModuleManager.INSTANCE.itemHasActiveModule(helmet, ModuleConstants.MODULE_FLIGHT_CONTROL__DATANAME);
        double jetEnergy = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(item, ModuleConstants.JETBOOTS_ENERGY_CONSUMPTION);
        double thrust = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(item, ModuleConstants.JETBOOTS_THRUST);

        PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);
        // if player has enough energy to fly
        if (jetEnergy < ElectricItemUtils.getPlayerEnergy(player)) {
            if (hasFlightControl && thrust > 0) {
                thrust = MovementManager.thrust(player, thrust, true);
                if ((player.world.isRemote) && MPALibConfig.useSounds()) {
                    Musique.playerSound(player, SoundDictionary.SOUND_EVENT_JETBOOTS, SoundCategory.PLAYERS, (float) (thrust * 12.5), 1.0f, true);
                }
                ElectricItemUtils.drainPlayerEnergy(player, (int) (thrust * jetEnergy));
            } else if (playerInput.jumpKey && player.motionY < 0.5) {
                thrust = MovementManager.thrust(player, thrust, false);
                if ((player.world.isRemote) && MPALibConfig.useSounds()) {
                    Musique.playerSound(player, SoundDictionary.SOUND_EVENT_JETBOOTS, SoundCategory.PLAYERS, (float) (thrust * 12.5), 1.0f, true);
                }
                ElectricItemUtils.drainPlayerEnergy(player, (int) (thrust * jetEnergy));
            } else {
                if ((player.world.isRemote) && MPALibConfig.useSounds()) {
                    Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_JETBOOTS);
                }
            }
        } else {
            if (player.world.isRemote && MPALibConfig.useSounds()) {
                Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_JETBOOTS);
            }
        }
    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
        if (player.world.isRemote && MPALibConfig.useSounds()) {
            Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_JETBOOTS);
        }
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.jetBoots;
    }
}
