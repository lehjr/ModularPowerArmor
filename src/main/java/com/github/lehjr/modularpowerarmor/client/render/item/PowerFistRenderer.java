package com.github.lehjr.modularpowerarmor.client.render.item;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.client.model.item.PowerFistModel2;
import com.github.lehjr.mpalib.util.math.Colour;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

/**
 * So far this looks like the only way to render a plasma ball with the fist, however, there's no way to pass the overrides here,
 * and no way to tell how big the charge for the plasma ball is... maybe add some arbitrary method to the module to fetch it? -_-
 *
 *
 */
public class PowerFistRenderer extends ItemStackTileEntityRenderer {
    PowerFistModel2 powerFist = new PowerFistModel2();

    @Override
    public void func_239207_a_/*render*/(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        IVertexBuilder builder = buffer.getBuffer(powerFist.getRenderType(MPAConstants.POWER_FIST_TEXTURE));
        matrixStack.push();

              /*
        matrixStack.scale(0.5F, 0.5F, 0.5F);




                        <transformationMatrix type="third_person_right_hand" translation="8, 8.01, 9" rotation="-15, 180, 0" scale="0.630, 0.630, 0.630"/>
                <transformationMatrix type="first_person_right_hand" translation="11.8, 8, 7" rotation="-16, -162, 0" scale="0.5, 0.5, 0.5"/>
                <transformationMatrix type="ground" translation="0, 5, 0" rotation="0,0,0" scale="0.630, 0.630, 0.630"/>


                            <modelTransforms>
                <transformationMatrix type="third_person_right_hand" translation="8, 8.01, 9" rotation="-15, 180, 0" scale="0.630, 0.630, 0.630"/>
                <transformationMatrix type="first_person_right_hand" translation="11.8, 8, 7" rotation="-16, -162, 0" scale="0.5, 0.5, 0.5"/>
                <transformationMatrix type="ground" translation="0, 5, 0" rotation="0,0,0" scale="0.630, 0.630, 0.630"/>
            </modelTransforms>
         */


        matrixStack.rotate(Vector3f.ZP.rotationDegrees(270F));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(90F));
        for (String partName : powerFist.partlMap.keySet()) {
            powerFist.renderPart(partName, matrixStack, builder, combinedLight, combinedOverlay, Colour.WHITE.r, Colour.WHITE.g, Colour.WHITE.b, Colour.WHITE.a);
        }
        matrixStack.pop();

    }
}