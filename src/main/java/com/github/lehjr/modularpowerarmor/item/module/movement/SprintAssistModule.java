package com.github.lehjr.modularpowerarmor.item.module.movement;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IPlayerTickModule;
import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
import com.github.lehjr.modularpowerarmor.client.event.MuseIcon;
import com.github.lehjr.modularpowerarmor.event.MovementManager;
import com.github.lehjr.modularpowerarmor.item.component.ItemComponent;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorLeggings;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

/**
 * Ported by leon on 10/18/16.
 */
public class SprintAssistModule extends AbstractPowerModule implements IToggleableModule, IPlayerTickModule {
    public SprintAssistModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.servoMotor, 4));

        addBasePropertyDouble(ModuleConstants.SPRINT_ENERGY_CONSUMPTION, 0, "RF");
        addTradeoffPropertyDouble(ModuleConstants.SPRINT_ASSIST, ModuleConstants.SPRINT_ENERGY_CONSUMPTION, 100);
        addBasePropertyDouble(ModuleConstants.SPRINT_SPEED_MULTIPLIER, .01, "%");
        addTradeoffPropertyDouble(ModuleConstants.SPRINT_ASSIST, ModuleConstants.SPRINT_SPEED_MULTIPLIER, 2.49);

        addBasePropertyDouble(ModuleConstants.SPRINT_ENERGY_CONSUMPTION, 0, "RF");
        addTradeoffPropertyDouble(ModuleConstants.COMPENSATION, ModuleConstants.SPRINT_ENERGY_CONSUMPTION, 20);
        addBasePropertyDouble(ModuleConstants.SPRINT_FOOD_COMPENSATION, 0, "%");
        addTradeoffPropertyDouble(ModuleConstants.COMPENSATION, ModuleConstants.SPRINT_FOOD_COMPENSATION, 1);

        addBasePropertyDouble(ModuleConstants.WALKING_ENERGY_CONSUMPTION, 0, "RF");
        addTradeoffPropertyDouble(ModuleConstants.WALKING_ASSISTANCE, ModuleConstants.WALKING_ENERGY_CONSUMPTION, 100);
        addBasePropertyDouble(ModuleConstants.WALKING_SPEED_MULTIPLIER, 0.01, "%");
        addTradeoffPropertyDouble(ModuleConstants.WALKING_ASSISTANCE, ModuleConstants.WALKING_SPEED_MULTIPLIER, 1.99);
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack itemStack) {
        if (player.capabilities.isFlying || player.isRiding() || player.isElytraFlying())
            onPlayerTickInactive(player, itemStack);

        ItemStack armorLeggings = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        // now you actually have to wear these to get the speed boost
        if (!armorLeggings.isEmpty() && armorLeggings.getItem() instanceof ItemPowerArmorLeggings) {
            double horzMovement = player.distanceWalkedModified - player.prevDistanceWalkedModified;
            double totalEnergy = ElectricItemUtils.getPlayerEnergy(player);
            if (horzMovement > 0) { // stop doing drain calculations when player hasn't moved
                if (player.isSprinting()) {
                    double exhaustion =  Math.round(horzMovement) * 0.1F;
                    double sprintCost = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, ModuleConstants.SPRINT_ENERGY_CONSUMPTION);
                    if (sprintCost < totalEnergy) {
                        double sprintMultiplier = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, ModuleConstants.SPRINT_SPEED_MULTIPLIER);
                        double exhaustionComp = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, ModuleConstants.SPRINT_FOOD_COMPENSATION);
                        ElectricItemUtils.drainPlayerEnergy(player, (int) (sprintCost * horzMovement * 5));
                        MovementManager.setMovementModifier(itemStack, sprintMultiplier, player);
                        player.getFoodStats().addExhaustion((float) (-1 * exhaustion * exhaustionComp));
                        player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
                    }
                } else {
                    double cost = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, ModuleConstants.WALKING_ENERGY_CONSUMPTION);
                    if (cost < totalEnergy) {
                        double walkMultiplier = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, ModuleConstants.WALKING_SPEED_MULTIPLIER);
                        ElectricItemUtils.drainPlayerEnergy(player, (int) (cost * horzMovement * 5));
                        MovementManager.setMovementModifier(itemStack, walkMultiplier, player);
                        player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
                    }
                }
            }
        } else
            onPlayerTickInactive(player, itemStack);
    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack itemStack) {
        MovementManager.setMovementModifier(itemStack, 0, player);
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.MOVEMENT;
    }

    @Override
    public String getDataName() {
        return ModuleConstants.MODULE_SPRINT_ASSIST__DATANAME;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.sprintAssist;
    }
}