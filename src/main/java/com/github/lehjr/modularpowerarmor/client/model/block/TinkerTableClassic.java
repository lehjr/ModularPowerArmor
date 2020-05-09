package com.github.lehjr.modularpowerarmor.client.model.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public class TinkerTableClassic extends Model {
    ModelRenderer cube;
    ModelRenderer screen3;
    ModelRenderer screen2;
    ModelRenderer screen1;
    ModelRenderer middletable;
    ModelRenderer uppertable;
    ModelRenderer particles;
    ModelRenderer footbase;
    ModelRenderer foot1;
    ModelRenderer fatfoot2;
    ModelRenderer fatfoot1;
    ModelRenderer backsupport;
    ModelRenderer tank3;
    ModelRenderer tank2;
    ModelRenderer tank1;
    ModelRenderer wireshort4;
    ModelRenderer wireshort3;
    ModelRenderer wireshort2;
    ModelRenderer wireshort1;
    ModelRenderer wirelong1;
    
    public TinkerTableClassic(Function<ResourceLocation, RenderType> renderTypeIn) {
        super(renderTypeIn);
        textureWidth = 112;
        textureHeight = 70;

        
        cube = new ModelRenderer(this, 96, 20);
        cube.addBox(-2F, -2F, -2F, 4, 4, 4);
        cube.setTextureSize(112, 70);
        cube.mirror = true;
        
        
        screen3 = new ModelRenderer(this, 1, 1);
        screen3.addBox(0F, 0F, 0F, 11, 0, 14);
        screen3.setRotationPoint(-9.666667F, 3.466667F, -7F);
        screen3.setTextureSize(112, 70);
        screen3.mirror = true;
        
        
        screen2 = new ModelRenderer(this, 0, 32);
        screen2.addBox(0F, 0F, 0F, 8, 0, 11);
        screen2.setRotationPoint(2F, 4.966667F, -6F);
        screen2.setTextureSize(112, 70);
        screen2.mirror = true;
        
        
        screen1 = new ModelRenderer(this, 3, 20);
        screen1.addBox(0F, 0F, 0F, 14, 0, 7);
        screen1.setRotationPoint(-6F, 2.466667F, 3F);
        screen1.setTextureSize(112, 70);
        screen1.mirror = true;
        setRotation(screen1, 0F, 0F, 0F);    
        
        
        middletable = new ModelRenderer(this, 40, 49);
        middletable.addBox(-5F, 1F, -5F, 17, 3, 18);
        middletable.setRotationPoint(-4F, 10F, -4F);
        middletable.setTextureSize(112, 70);
        middletable.mirror = true;
        setRotation(middletable, 0F, 0F, 0F);
        
        
        uppertable = new ModelRenderer(this, 56, 28);
        uppertable.addBox(0F, 0F, 0F, 12, 5, 16);
        uppertable.setRotationPoint(-8F, 10F, -8F);
        uppertable.setTextureSize(112, 70);
        uppertable.mirror = true;
        setRotation(uppertable, 0F, 0F, 0F);
        
        
        particles = new ModelRenderer(this, 90, 0);
        particles.addBox(0F, 0F, 0F, 6, 7, 5);
        particles.setRotationPoint(-3F, 15F, -3F);
        particles.setTextureSize(112, 70);
        particles.mirror = true;
        setRotation(particles, 0F, 0F, 0F);
        
        
        footbase = new ModelRenderer(this, 0, 54);
        footbase.addBox(-5F, 8F, -5F, 12, 2, 11);
        footbase.setRotationPoint(-1F, 14F, -1F);
        footbase.setTextureSize(112, 70);
        footbase.mirror = true;
        setRotation(footbase, 0F, 0F, 0F);
        
        
        foot1 = new ModelRenderer(this, 82, 13);
        foot1.addBox(-5F, 8F, -5F, 4, 3, 3);
        foot1.setRotationPoint(-2F, 13F, 3F);
        foot1.setTextureSize(112, 70);
        foot1.mirror = true;
        setRotation(foot1, 0F, 0F, 0F);
        
        
        fatfoot2 = new ModelRenderer(this, 96, 13);
        fatfoot2.addBox(-5F, 8F, -5F, 4, 3, 4);
        fatfoot2.setRotationPoint(3F, 13F, 1F);
        fatfoot2.setTextureSize(112, 70);
        fatfoot2.mirror = true;
        setRotation(fatfoot2, 0F, 1.570796F, 0F);
        
        
        fatfoot1 = new ModelRenderer(this, 96, 13);
        fatfoot1.addBox(-5F, 8F, -5F, 4, 3, 4);
        fatfoot1.setRotationPoint(3F, 13F, -8F);
        fatfoot1.setTextureSize(112, 70);
        fatfoot1.mirror = true;
        setRotation(fatfoot1, 0F, 1.570796F, 0F);
        
        
        backsupport = new ModelRenderer(this, 38, 34);
        backsupport.addBox(0F, 0F, -2F, 2, 8, 7);
        backsupport.setRotationPoint(3F, 14F, -2F);
        backsupport.setTextureSize(112, 70);
        backsupport.mirror = true;
        setRotation(backsupport, 0F, 0F, 0F);
        
        
        tank3 = new ModelRenderer(this, 51, 18);
        tank3.addBox(0F, 0F, 0F, 3, 5, 3);
        tank3.setRotationPoint(6F, 10F, 3F);
        tank3.setTextureSize(112, 70);
        tank3.mirror = true;
        setRotation(tank3, 0F, 0F, 0F);
        
        
        tank2 = new ModelRenderer(this, 51, 18);
        tank2.addBox(0F, 0F, 0F, 3, 5, 3);
        tank2.setRotationPoint(6F, 10F, -2F);
        tank2.setTextureSize(112, 70);
        tank2.mirror = true;
        setRotation(tank2, 0F, 0F, 0F);
        
        
        tank1 = new ModelRenderer(this, 51, 18);
        tank1.addBox(0F, 0F, 0F, 3, 5, 3);
        tank1.setRotationPoint(6F, 10F, -7F);
        tank1.setTextureSize(112, 70);
        tank1.mirror = true;
        setRotation(tank1, 0F, 0F, 0F);
        
        
        wireshort4 = new ModelRenderer(this, 71, 15);
        wireshort4.addBox(0F, 0F, 5F, 1, 2, 1);
        wireshort4.setRotationPoint(7F, 15F, -1F);
        wireshort4.setTextureSize(112, 70);
        wireshort4.mirror = true;
        setRotation(wireshort4, 0F, 0F, 0F);
        
        
        wireshort3 = new ModelRenderer(this, 71, 15);
        wireshort3.addBox(0F, 0F, 0F, 1, 2, 1);
        wireshort3.setRotationPoint(7F, 15F, -6F);
        wireshort3.setTextureSize(112, 70);
        wireshort3.mirror = true;
        setRotation(wireshort3, 0F, 0F, 0F);
        
        
        wireshort2 = new ModelRenderer(this, 69, 13);
        wireshort2.addBox(0F, 0F, 0F, 2, 1, 1);
        wireshort2.setRotationPoint(5F, 17F, -1F);
        wireshort2.setTextureSize(112, 70);
        wireshort2.mirror = true;
        setRotation(wireshort2, 0F, 0F, 0F);
        
        
        wireshort1 = new ModelRenderer(this, 71, 15);
        wireshort1.addBox(0F, 0F, 0F, 1, 2, 1);
        wireshort1.setRotationPoint(7F, 15F, -1F);
        wireshort1.setTextureSize(112, 70);
        wireshort1.mirror = true;
        setRotation(wireshort1, 0F, 0F, 0F);
        
        
        wirelong1 = new ModelRenderer(this, 77, 1);
        wirelong1.addBox(0F, 0F, 0F, 1, 1, 11);
        wirelong1.setRotationPoint(7F, 17F, -6F);
        wirelong1.setTextureSize(112, 70);
        wirelong1.mirror = true;
        setRotation(wirelong1, 0F, 0F, 0F);
    }
    
    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        float f = 0.0625f;
//        RenderState.blendingOn();
        int timestep = (int) ((System.currentTimeMillis()) % 10000);
        float angle = (float) (timestep * Math.PI / 5000.0);
        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.XP.rotation(180.0f));
        matrixStackIn.translate(0.5f, -1.5f, -0.5f);
        middletable.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        uppertable.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        footbase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        foot1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        fatfoot2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        fatfoot1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        backsupport.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        tank3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        tank2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        tank1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        wireshort4.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        wireshort3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        wireshort2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        wireshort1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        wirelong1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);


//        RenderState.glowOn(); // fixme
        matrixStackIn.pop();
        matrixStackIn.push();
        matrixStackIn.translate(0.5f, 1.05f, 0.5f);
        matrixStackIn.translate(0, 0.02f * Math.sin(angle * 3), 0);
        // GLRotate uses degrees instead of radians for some reason grr
        matrixStackIn.rotate(Vector3f.YP.rotation(angle * 57.2957795131F));
        matrixStackIn.rotate(Vector3f.XP.rotation(45F));
        // arctangent of 0.5.
        matrixStackIn.rotate(new Vector3f(0, 1F, 1F).rotation(35.2643897F));
        float cubeScale = f * 0.5f;
         matrixStackIn.scale(cubeScale, cubeScale, cubeScale);
        cube.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, 1, 1, 1, 0.8F);
        matrixStackIn.pop();


        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.XP.rotation(180));
        matrixStackIn.translate(0.5f, -1.5f, -0.5f);
        matrixStackIn.scale(f, f, f);
        screen3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        screen2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        screen1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        particles.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pop();
        // glow off ... how?
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}