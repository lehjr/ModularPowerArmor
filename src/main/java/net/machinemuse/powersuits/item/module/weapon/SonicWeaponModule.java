//package net.machinemuse.powersuits.item.module.weapon;
//
//import net.machinemuse.numina.item.IModularItem;
//import net.machinemuse.numina.module.IRightClickModule;
//import net.machinemuse.powersuits.client.event.MuseIcon;
//import net.machinemuse.powersuits.powermodule.AbstractPowerModule;
//import net.machinemuse.powersuits.utils.MuseCommonStrings;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.ActionResult;
//import net.minecraft.util.ActionResultType;
//import net.minecraft.util.Direction;
//import net.minecraft.util.EnumHand;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//
//import java.util.List;
//
//public class SonicWeaponModule extends AbstractPowerModule implements IRightClickModule {
//
//    public static final String MODULE_SONIC_WEAPON = "Sonic Weapon";
//
//    public SonicWeaponModule() { {
//        super(validItems);
//    }
//
//    @Override
//    public String getCategory() {
//        return MuseCommonStrings.CATEGORY_WEAPON;
//    }
//
//    @Override
//    public String getDataName() {
//        return MODULE_SONIC_WEAPON;
//    }
//
//    @Override
//    public String getUnlocalizedName() { return "sonicWeapon";
//    }
//
//    @Override
//    public String getDescription() {
//        return "A high-amplitude, complex-frequency soundwave generator can have shattering or disorienting effects on foes and blocks alike.";
//    }
//
//    @Override
//    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, PlayerEntity playerIn, EnumHand hand) {
//        return null;
//    }
//
//    @Override
//    public ActionResultType onItemUse(ItemStack stack, PlayerEntity playerIn, World worldIn, BlockPos pos, EnumHand hand, Direction facing, float hitX, float hitY, float hitZ) {
//        return null;
//    }
//
//    @Override
//    public ActionResultType onItemUseFirst(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, EnumHand hand) {
//        return null;
//    }
//
//    @Override
//    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
//    }
//
//    @Override
//    public TextureAtlasSprite getIcon(ItemStack item) {
//        return MuseIcon.sonicWeapon;
//    }
//}