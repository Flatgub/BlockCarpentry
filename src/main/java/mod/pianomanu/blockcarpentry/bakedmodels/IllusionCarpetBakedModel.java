package mod.pianomanu.blockcarpentry.bakedmodels;

import mod.pianomanu.blockcarpentry.block.FrameBlock;
import mod.pianomanu.blockcarpentry.util.BlockAppearanceHelper;
import mod.pianomanu.blockcarpentry.util.ModelHelper;
import mod.pianomanu.blockcarpentry.util.SixSideSet;
import mod.pianomanu.blockcarpentry.util.TextureHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import static mod.pianomanu.blockcarpentry.util.AppearancePropertyCollection.*;
/**
 * Contains all information for the block model
 * See {@link ModelHelper} for more information
 *
 * @author PianoManu
 * @version 1.1 08/20/21
 */
public class IllusionCarpetBakedModel implements IDynamicBakedModel {
    public static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "block/oak_planks");

    private TextureAtlasSprite getTexture() {
        return Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(TEXTURE);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (side != null) {
            return Collections.emptyList();
        }
        BlockState mimic = extraData.getData(MIMIC_MODEL_PROPERTY);
        Integer design = extraData.getData(DESIGN_MODEL_PROPERTY);
        if (mimic != null && !(mimic.getBlock() instanceof FrameBlock)) {
            ModelResourceLocation location = BlockModelShapes.getModelLocation(mimic);
            if (location != null) {
                IBakedModel model = Minecraft.getInstance().getModelManager().getModel(location);
                if (model != null) {
                    TextureAtlasSprite glass = TextureHelper.getGlassTextures().get(extraData.getData(COLOR_MODEL_PROPERTY).getId());
                    int woolInt = extraData.getData(COLOR_MODEL_PROPERTY).getId() - 1; // why is this -1?
                    if (woolInt < 0)
                        woolInt = 0;
                    TextureAtlasSprite wool = TextureHelper.getWoolTextures().get(woolInt);
                    int tintIndex = BlockAppearanceHelper.setTintIndex(mimic);
                    SixSideSet vis = extraData.getData(SIDE_VISIBILITY_MODEL_PROPERTY);
                    boolean renderNorth = vis.test(Direction.NORTH);
                    boolean renderEast = vis.test(Direction.EAST);
                    boolean renderSouth = vis.test(Direction.SOUTH);
                    boolean renderWest = vis.test(Direction.WEST);
                    int rotation = extraData.getData(ROTATION_MODEL_PROPERTY);
                    List<BakedQuad> quads = new ArrayList<>();
                    if (design == 0) {
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 0f, 1 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                    } else if (design == 1) {
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1 / 16f, 0f, 1 / 16f, 0f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 1, 0f, 1 / 16f, 0f, 1 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(15 / 16f, 1f, 0f, 1 / 16f, 1 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 15 / 16f, 0f, 1 / 16f, 15 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));

                        quads.addAll(ModelHelper.createCuboid(1 / 16f, 15 / 16f, 0f, 1 / 16f, 1 / 16f, 15 / 16f, glass, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));
                    } else if (design == 2) {
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 2 / 16f, 0f, 1 / 16f, 0f, 14 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(2 / 16f, 1, 0f, 1 / 16f, 0f, 2 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(14 / 16f, 1f, 0f, 1 / 16f, 2 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 14 / 16f, 0f, 1 / 16f, 14 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));

                        quads.addAll(ModelHelper.createCuboid(2 / 16f, 14 / 16f, 0f, 1 / 16f, 2 / 16f, 14 / 16f, glass, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));
                    } else if (design == 3) {
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1 / 16f, 0f, 1 / 16f, 0f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 1, 0f, 1 / 16f, 0f, 1 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(15 / 16f, 1f, 0f, 1 / 16f, 1 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 15 / 16f, 0f, 1 / 16f, 15 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));

                        quads.addAll(ModelHelper.createCuboid(1 / 16f, 15 / 16f, 0f, 1 / 16f, 1 / 16f, 15 / 16f, wool, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));
                    } else if (design == 4) {
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 2 / 16f, 0f, 1 / 16f, 0f, 14 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(2 / 16f, 1, 0f, 1 / 16f, 0f, 2 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(14 / 16f, 1f, 0f, 1 / 16f, 2 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 14 / 16f, 0f, 1 / 16f, 14 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));

                        quads.addAll(ModelHelper.createCuboid(2 / 16f, 14 / 16f, 0f, 1 / 16f, 2 / 16f, 14 / 16f, wool, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));
                    }
                    int overlayIndex = extraData.getData(OVERLAY_MODEL_PROPERTY);
                    if (overlayIndex != 0) {
                        quads.addAll(ModelHelper.createOverlay(0f, 1f, 0f, 1 / 16f, 0f, 1f, overlayIndex, true, true, true, true, true, true, false));
                    }
                    return quads;
                }
            }
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return getTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }
}
