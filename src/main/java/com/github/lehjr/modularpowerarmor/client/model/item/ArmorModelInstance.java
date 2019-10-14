package com.github.lehjr.modularpowerarmor.client.model.item;


/**
 * Author: MachineMuse (Claire Semple)
 * Created: 10:01 PM, 11/07/13
 */
public class ArmorModelInstance {
    private static HighPolyArmor instance = null;

    public static HighPolyArmor getInstance() {
        if (instance == null) {
//            if (ModCompatibility.isRenderPlayerAPILoaded()) {
//                try {
//                    MPALibLogger.logInfo("Attempting to load SmartMoving armor model.");
//                    instance = Class.forName("com.github.lehjr.modularpowerarmor.client.model.item.armor.SMovingArmorModel").asSubclass(ModelBiped.class).newInstance();
//                    MPALibLogger.logInfo("SmartMoving armor model loaded successfully!");
//                } catch (Exception e) {
//                    MPALibLogger.logInfo("Smart Moving armor model did not loadButton successfully. Either Smart Moving is not installed, or there was another problem.");
//                    instance = new HighPolyArmor();
//                }
//            }
//            else {
                instance = new HighPolyArmor();
//            }
        }
        return instance;
    }
}