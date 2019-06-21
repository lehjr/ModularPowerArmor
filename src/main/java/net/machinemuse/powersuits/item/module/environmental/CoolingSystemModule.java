package net.machinemuse.powersuits.item.module.environmental;

import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Eximius88 on 1/17/14.
 */
public class CoolingSystemModule extends AbstractPowerModule {
    public CoolingSystemModule(String regName) {
        super(regName);
    }






    addTradeoffProperty("Power", COOLING_BONUS, 7, "%");
    addTradeoffProperty("Power", ENERGY, 16, "J/t");






    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
        if (MuseItemUtils.getWaterLevel(item) > ModuleManager.computeModularProperty(item, WATER_TANK_SIZE)) {
            MuseItemUtils.setWaterLevel(item, ModuleManager.computeModularProperty(item, WATER_TANK_SIZE));
        }

        // Fill tank if player is in water
        Block block = player.worldObj.getBlockState(player.getPosition()).getBlock();
        if (((block == Blocks.WATER) || block == Blocks.FLOWING_WATER) && MuseItemUtils.getWaterLevel(item) < ModuleManager.computeModularProperty(item, WATER_TANK_SIZE)) {
            MuseItemUtils.setWaterLevel(item, MuseItemUtils.getWaterLevel(item) + 1);
        }

        // Fill tank if raining
        int xCoord = MathHelper.floor_double(player.posX);
        int zCoord = MathHelper.floor_double(player.posZ);
        boolean isRaining = (player.worldObj.getBiomeForCoordsBody(player.getPosition()).getRainfall() > 0) && (player.worldObj.isRaining() || player.worldObj.isThundering());
        if (isRaining && player.worldObj.canBlockSeeSky(player.getPosition().add(0,1,0))
                && (player.worldObj.getTotalWorldTime() % 5) == 0 && MuseItemUtils.getWaterLevel(item) < ModuleManager.computeModularProperty(item, WATER_TANK_SIZE)) {
            MuseItemUtils.setWaterLevel(item, MuseItemUtils.getWaterLevel(item) + 1);
        }

        // Apply cooling
        double currentHeat = MuseHeatUtils.getPlayerHeat(player);
        double maxHeat = MuseHeatUtils.getMaxHeat(player);
        if ((currentHeat / maxHeat) >= ModuleManager.computeModularProperty(item, ACTIVATION_PERCENT) && MuseItemUtils.getWaterLevel(item) > 0) {
            MuseHeatUtils.coolPlayer(player, 1);
            MuseItemUtils.setWaterLevel(item, MuseItemUtils.getWaterLevel(item) - 1);
            for (int i = 0; i < 4; i++) {
                player.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, player.posX, player.posY + 0.5, player.posZ, 0.0D, 0.0D, 0.0D);
            }
        }



             public NitrogenCoolingSystem(List<IModularItem> validItems) {
            super(validItems);
            //addInstallCost(new ItemStack(Item.netherStar, 1));
            addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.liquidNitrogen, 1));
            addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.rubberHose, 2));
            addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.controlCircuit, 1));
            addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.computerChip, 2));
            addTradeoffProperty("Power", COOLING_BONUS, 7, "%");
            addTradeoffProperty("Power", ENERGY, 16, "J/t");
        }

        @Override
        public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
            double heatBefore = MuseHeatUtils.getPlayerHeat(player);
            MuseHeatUtils.coolPlayer(player, 0.210 * ModuleManager.computeModularProperty(item, COOLING_BONUS));
            double cooling = heatBefore - MuseHeatUtils.getPlayerHeat(player);
            ElectricItemUtils.drainPlayerEnergy(player, cooling * ModuleManager.computeModularProperty(item, ENERGY));
        }


//
//        addTradeoffPropertyDouble(MPSModuleConstants.ADVANCED_COOLING_POWER, MPSModuleConstants.COOLING_BONUS, 7, "%");
//        addTradeoffPropertyDouble(MPSModuleConstants.ADVANCED_COOLING_POWER, MPSModuleConstants.ADVANCED_COOLING_SYSTEM_ENERGY_CONSUMPTION, 160, "RF/t");


    @Override
    public double getCoolingFactor() {
        return 2.1;
    }

    @Override
    public double getCoolingBonus(@Nonnull ItemStack itemStack) {
        return 0;
//        return moduleCap.applyPropertyModifiers(MPSModuleConstants.COOLING_BONUS);
    }

    @Override
    public double getEnergyConsumption(@Nonnull ItemStack itemStack) {
        return 0;
//        return moduleCap.applyPropertyModifiers(MPSModuleConstants.ADVANCED_COOLING_SYSTEM_ENERGY_CONSUMPTION);
    }
}