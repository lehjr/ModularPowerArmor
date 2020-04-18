//package com.github.lehjr.modularpowerarmor.client.model.item;
//
//import com.github.iunius118.tolaserblade.ToLaserBladeConfig;
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.UnmodifiableIterator;
//import net.minecraft.block.BlockState;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.model.BakedQuad;
//import net.minecraft.client.renderer.model.IBakedModel;
//import net.minecraft.client.renderer.model.ItemCameraTransforms;
//import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
//import net.minecraft.client.renderer.model.ItemOverrideList;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.Items;
//import net.minecraft.util.Direction;
//import net.minecraft.world.World;
//import net.minecraftforge.client.model.BasicState;
//import net.minecraftforge.client.model.ModelLoader;
//import net.minecraftforge.client.model.obj.OBJModel;
//import net.minecraftforge.client.model.obj.OBJModel.OBJBakedModel;
//import net.minecraftforge.common.model.IModelState;
//import net.minecraftforge.common.model.Models;
//import net.minecraftforge.common.model.TransformationMatrix;
//import org.apache.commons.lang3.tuple.Pair;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//import javax.vecmath.Matrix4f;
//import java.util.*;
//
//@SuppressWarnings("deprecation") // for ItemCameraTransforms
//public class LaserBladeModel implements IBakedModel {
//
//    public IBakedModel bakedOBJModel;
//    public final IBakedModel bakedJSONModel;
//
//    public ItemStack itemStack = ItemStack.EMPTY;
//    public World world;
//    public LivingEntity entity;
//
//    public TransformType cameraTransformType = TransformType.NONE;
//
//    public Map<String, List<BakedQuad>> mapQuads_0 = new HashMap<>();
//    public Map<String, List<BakedQuad>> mapQuads_1 = new HashMap<>();
//    public final String[] partNames = {"Hilt", "Hilt_bright", "Blade_core", "Blade_halo_1", "Blade_halo_2"};
//
//    public LaserBladeModel(IBakedModel bakedOBJModelIn, IBakedModel bakedJSONModelIn) {
//        this(bakedOBJModelIn, bakedJSONModelIn, false);
//    }
//
//    public LaserBladeModel(IBakedModel bakedOBJModelIn, IBakedModel bakedOBJModelSubIn, IBakedModel bakedJSONModelIn) {
//        this(bakedOBJModelIn, bakedOBJModelSubIn, bakedJSONModelIn, false);
//    }
//
//    public LaserBladeModel(IBakedModel bakedOBJModelIn, IBakedModel bakedJSONModelIn, boolean isInitialized) {
//        bakedOBJModel = bakedOBJModelIn;
//        bakedJSONModel = bakedJSONModelIn;
//
//        if (!isInitialized) {
//            // Separate Quads to each parts by OBJ Group.
//            for (String partName : partNames) {
//                mapQuads_0.put(partName, getQuadsByGroups(bakedOBJModelIn, ImmutableList.of(partName)));
//                mapQuads_1 = mapQuads_0;
//            }
//        }
//    }
//
//    public LaserBladeModel(IBakedModel bakedOBJModelIn, IBakedModel bakedOBJModelSubIn, IBakedModel bakedJSONModelIn, boolean isInitialized) {
//        bakedJSONModel = bakedJSONModelIn;
//
//        if (!isInitialized) {
//            // Separate Quads to each parts by OBJ Group.
//            for (String partName : partNames) {
//                mapQuads_0.put(partName, getQuadsByGroups(bakedOBJModelIn, ImmutableList.of(partName)));
//                mapQuads_1.put(partName, getQuadsByGroups(bakedOBJModelSubIn, ImmutableList.of(partName)));
//            }
//        }
//    }
//
//    public List<BakedQuad> getQuadsByGroups(IBakedModel bakedModelIn, final List<String> visibleGroups) {
//        List<BakedQuad> quads = null;
//
//        if (bakedModelIn instanceof OBJBakedModel) {
//            try {
//                OBJModel obj = ((OBJBakedModel) bakedModelIn).getModel();
//
//                // ModelState for handling visibility of each group.
//                IModelState modelState = part -> {
//                    if (part.isPresent()) {
//                        UnmodifiableIterator<String> parts = Models.getParts(part.get());
//
//                        if (parts.hasNext()) {
//                            String name = parts.next();
//
//                            if (!parts.hasNext() && visibleGroups.contains(name)) {
//                                // Return Absent for NOT invisible group.
//                                return Optional.empty();
//                            } else {
//                                // Return Present for invisible group.
//                                return Optional.of(TransformationMatrix.identity());
//                            }
//                        }
//                    }
//
//                    return Optional.empty();
//                };
//
//                // Bake model of visible groups.
//                IBakedModel bakedModel = obj.bake(null, ModelLoader.defaultTextureGetter(), new BasicState(modelState, false), DefaultVertexFormats.ITEM);
//
//                quads = bakedModel.getQuads(null, null, new Random());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (quads != null) {
//            return quads;
//        } else {
//            return Collections.emptyList();
//        }
//    }
//
//    public void handleItemState(ItemStack itemStackIn, World worldIn, LivingEntity entityLivingBaseIn) {
//        itemStack = itemStackIn;
//        world = worldIn;
//        entity = entityLivingBaseIn;
//    }
//
//    @Override
//    @Nonnull
//    public List<BakedQuad> getQuads(@Nullable BlockState blockStateIn, @Nullable Direction direction, Random randIn) {
//        if (direction == null) {
//            List<BakedQuad> quads =  bakedOBJModel.getQuads(null, null, randIn);
//
//            if (quads != null) {
//                return quads;
//            }
//        }
//
//        return Collections.emptyList();
//    }
//
//    public List<BakedQuad> getQuadsByName(String name) {
//        Map<String, List<BakedQuad>> mapQuads;
//
//        if (ToLaserBladeConfig.CLIENT.laserBladeRenderingMode.get() == 1) {
//            mapQuads = mapQuads_1;
//        } else {
//            mapQuads = mapQuads_0;
//        }
//
//        return mapQuads.getOrDefault(name, Collections.emptyList());
//    }
//
//    @Override
//    public boolean isAmbientOcclusion() {
//        return true;
//    }
//
//    @Override
//    public boolean isGui3d() {
//        return true;
//    }
//
//    @Override
//    public boolean isBuiltInRenderer() {
//        return true;
//    }
//
//    @Override
//    public TextureAtlasSprite getParticleTexture() {
//        return Minecraft.getInstance().getItemRenderer().getItemModelMesher().getParticleIcon(Items.IRON_INGOT);
//    }
//
//    @Override
//    public ItemCameraTransforms getItemCameraTransforms() {
//        return bakedJSONModel.getItemCameraTransforms();
//    }
//
//    @Override
//    public ItemOverrideList getOverrides() {
//        return new ItemOverrideList() {
//
//            @Override
//            public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, World world, LivingEntity entity) {
//                // Copy LaserBladeModel object and handle ItemStack.
//                if (originalModel instanceof LaserBladeModel) {
//                    LaserBladeModel model = (LaserBladeModel) originalModel;
//                    model.handleItemState(stack, world, entity);
//                    return model;
//                }
//
//                return originalModel;
//            }
//
//        };
//    }
//
//    @Override
//    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType transformTypeIn) {
//        Matrix4f matrix;
//
//        // Get transformation matrix from JSON item model.
//        matrix = bakedJSONModel.handlePerspective(transformTypeIn).getValue();
//
//        cameraTransformType = transformTypeIn;
//
//        if (ToLaserBladeConfig.CLIENT.isEnabledLaserBlade3DModel.get()) {
//            return Pair.of(this, matrix);
//        } else {
//            return Pair.of(this.bakedJSONModel, matrix);
//        }
//    }
//
//}