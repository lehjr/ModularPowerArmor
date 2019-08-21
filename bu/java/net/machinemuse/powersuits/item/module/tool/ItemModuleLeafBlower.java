package net.machinemuse.powersuits.item.module.tool;

import net.machinemuse.numina.module.EnumModuleCategory;
import net.machinemuse.numina.module.EnumModuleTarget;
import net.machinemuse.numina.module.IRightClickModule;
import net.machinemuse.powersuits.item.module.ItemAbstractPowerModule;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by User: Andrew2448
 * 7:13 PM 4/21/13
 */
public class ItemModuleLeafBlower extends ItemAbstractPowerModule implements IRightClickModule {
    public ItemModuleLeafBlower(String regName) {
        super(regName, EnumModuleTarget.TOOLONLY, EnumModuleCategory.CATEGORY_TOOL);
//        ModuleManager.INSTANCE.addInstallCost(getDataName(), new ItemStack(Items.IRON_INGOT, 3));
//        ModuleManager.INSTANCE.addInstallCost(getDataName(), MuseItemUtils.copyAndResize(ItemComponent.solenoid, 1));
//        addBasePropertyDouble(MPSModuleConstants.ENERGY_CONSUMPTION, 500, "RF");
//        addTradeoffPropertyDouble(MPSModuleConstants.LEAF_BLOWER_RADIUS, MPSModuleConstants.ENERGY_CONSUMPTION, 9500);
//        addBasePropertyDouble(MPSModuleConstants.LEAF_BLOWER_RADIUS, 1, "m");
//        addTradeoffPropertyDouble(MPSModuleConstants.LEAF_BLOWER_RADIUS, MPSModuleConstants.LEAF_BLOWER_RADIUS, 15);
    }

    @Override
    public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
//        int radius = (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStackIn, MPSModuleConstants.LEAF_BLOWER_RADIUS);
//        if (useBlower(radius, itemStackIn, playerIn, worldIn, playerIn.getPosition()))
//            return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);

        return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack itemStack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.PASS;
    }

    private boolean useBlower(int radius, ItemStack itemStack, EntityPlayer player, World world, BlockPos pos) {
//        int totalEnergyDrain = 0;
//        BlockPos newPos;
//        for (int i = pos.getX() - radius; i < pos.getX() + radius; i++) {
//            for (int j = pos.getY() - radius; j < pos.getY() + radius; j++) {
//                for (int k = pos.getZ() - radius; k < pos.getZ() + radius; k++) {
//                    newPos = new BlockPos(i, j, k);
//                    if (ToolHelpers.blockCheckAndHarvest(player, world, newPos)) {
//                        totalEnergyDrain += getEnergyUsage(itemStack);
//                    }
//                }
//            }
//        }
//        ElectricItemUtils.drainPlayerEnergy(player, totalEnergyDrain);
        return true;
    }

    @Override
    public int getEnergyUsage(@Nonnull ItemStack itemStack) {
        return 0;
//        return (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.ENERGY_CONSUMPTION);
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        return EnumActionResult.PASS;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {

    }
}