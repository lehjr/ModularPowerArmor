/*
 * ModularPowersuits (Maintenance builds by lehjr)
 * Copyright (c) 2019 MachineMuse, Lehjr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.machinemuse.powersuits.item.component;

import com.github.lehjr.mpalib.string.StringUtils;
import com.github.machinemuse.powersuits.api.constants.MPSModConstants;
import com.github.machinemuse.powersuits.config.MPSConfig;
import com.github.machinemuse.powersuits.utils.MuseCommonStrings;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemComponent extends Item {
    public static Map<Integer, String> names = new HashMap<>();
    public static ItemStack wiring;
    public static ItemStack solenoid;
    public static ItemStack servoMotor;
    public static ItemStack gliderWing;
    public static ItemStack ionThruster;
    public static ItemStack parachute;
    public static ItemStack lvcapacitor;
    public static ItemStack mvcapacitor;
    public static ItemStack hvcapacitor;
    public static ItemStack evcapacitor;
    public static ItemStack ironPlating;
    public static ItemStack diamonddPlating;
    public static ItemStack fieldEmitter;
    public static ItemStack laserHologram;
    public static ItemStack carbonMyofiber;
    public static ItemStack controlCircuit;
    public static ItemStack myofiberGel;
    public static ItemStack artificialMuscle;
    public static ItemStack magnet;
    public static ItemStack solarPanel;
    public static ItemStack computerChip;
    public static ItemStack rubberHose;


    public ItemComponent(String regName) {
        this.maxStackSize = 64;

        this.setRegistryName(regName);
        this.setTranslationKey(new StringBuilder(MPSModConstants.MODID).append(".").append("powerArmorComponent").toString());
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(MPSConfig.INSTANCE.mpsCreativeTab);
        this.populate();
    }

    public ItemStack addComponent(int meta, String oredictName) {
        ItemStack stack = new ItemStack(this, 1, meta);
        names.put(meta, oredictName);
        return stack;
    }

    public void registerOres() {
        for (int meta : names.keySet()) {
            OreDictionary.registerOre(names.get(meta), new ItemStack(this, 1, meta));
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> currentTipList, ITooltipFlag flagIn) {
        if (MPSConfig.INSTANCE.doAdditionalInfo()) {
            String message = I18n.format("tooltip.powersuits.componentTooltip");
            message = StringUtils.wrapMultipleFormatTags(message, StringUtils.FormatCodes.Grey, StringUtils.FormatCodes.Italic);
            currentTipList.add(message);
            String description = I18n.format(getTranslationKey(stack) + ".desc");
            currentTipList.addAll(Arrays.asList(StringUtils.wordUtilsWrap(description, 30)));
        } else {
            currentTipList.add(MuseCommonStrings.additionalInfoInstructions());
        }
    }

    // changed this to static values for meta just in case we need to change or add things later
    public void populate() {
        // NOTE: Only add to end otherwise people's IDs will get screwed up n.n'
        wiring = addComponent(0, "componentWiring");
        solenoid = addComponent(1, "componentSolenoid");
        servoMotor = addComponent(2, "componentServo");
        gliderWing = addComponent(3, "componentGliderWing");
        ionThruster = addComponent(4, "componentIonThruster");
        lvcapacitor = addComponent(5, "componentLVCapacitor");
        mvcapacitor = addComponent(6, "componentMVCapacitor");
        hvcapacitor = addComponent(7, "componentHVCapacitor");
        evcapacitor = addComponent(8, "componentEVCapacitor");
        parachute = addComponent(9, "componentParachute");
        ironPlating = addComponent(10, "componentPlatingIron");
        diamonddPlating = addComponent(11, "componentPlatingDiamond");
        fieldEmitter = addComponent(12, "componentFieldEmitter");
        laserHologram = addComponent(13, "componentLaserEmitter");
        carbonMyofiber = addComponent(14, "componentCarbonMyofiber");
        controlCircuit = addComponent(15, "componentControlCircuit");
        myofiberGel = addComponent(16, "componentMyofiberGel");
        artificialMuscle = addComponent(17, "componentArtificialMuscle");
        solarPanel = addComponent(18, "componentSolarPanel");
        magnet = addComponent(19, "componentMagnet");
        computerChip = addComponent(20, "componentComputerChip");
        rubberHose = addComponent(21, "componentRubberHose");
    }

    @Override
    public String getTranslationKey(ItemStack itemStack) {
        String unlocalizedName = names.get(itemStack.getMetadata());
        if (unlocalizedName != null) {
            unlocalizedName = unlocalizedName.replaceAll("\\s", "");
        } else
            unlocalizedName = "";

        return this.getTranslationKey() + "." + unlocalizedName;
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye
     * returns 16 items). For creative tab.
     */
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (Integer meta : names.keySet()) {
                items.add(new ItemStack(this, 1, meta));
            }
        }
    }
}