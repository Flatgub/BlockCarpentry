package mod.pianomanu.blockcarpentry.bakedmodels;

import mod.pianomanu.blockcarpentry.block.FrameBlock;
import mod.pianomanu.blockcarpentry.tileentity.FrameBlockTile;
import mod.pianomanu.blockcarpentry.util.BlockAppearanceHelper;
import mod.pianomanu.blockcarpentry.util.ModelHelper;
import mod.pianomanu.blockcarpentry.util.TextureHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Contains all information for the block model
 * See {@link ModelHelper} for more information
 *
 * @author PianoManu
 * @version 1.3 05/01/21
 */
@SuppressWarnings("deprecation")
public class IllusionLadderBakedModel implements IDynamicBakedModel {
    public static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "block/oak_planks");

    private TextureAtlasSprite getTexture() {
        return Minecraft.getInstance().getAtlasSpriteGetter(TextureAtlas.LOCATION_BLOCKS).apply(TEXTURE);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        BlockState mimic = extraData.getData(FrameBlockTile.MIMIC);
        if (mimic != null && !(mimic.getBlock() instanceof FrameBlock)) {
            ModelResourceLocation location = BlockModelShapes.getModelLocation(mimic);
            if (location != null) {
                IBakedModel model = Minecraft.getInstance().getModelManager().getModel(location);
                model.getBakedModel().getQuads(mimic, side, rand, extraData);
                if (model != null) {
                    return getMimicQuads(state, side, rand, extraData, model);
                }
            }
        }

        return Collections.emptyList();
    }

    public List<BakedQuad> getMimicQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData, IBakedModel model) {
        if (side != null) {
            return Collections.emptyList();
        }
        BlockState mimic = extraData.getData(FrameBlockTile.MIMIC);
        if (mimic != null && state != null) {
            List<TextureAtlasSprite> textureList = TextureHelper.getTextureFromModel(model, extraData, rand);
            List<TextureAtlasSprite> designTextureList = new ArrayList<>();
            if (textureList.size() == 0) {
                if (Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.block_not_available"), true);
                }
                return Collections.emptyList();
            }
            designTextureList.add(textureList.get(0));
            designTextureList.addAll(TextureHelper.getMetalTextures());
            int tintIndex = BlockAppearanceHelper.setTintIndex(mimic);
            int rotation = extraData.getData(FrameBlockTile.ROTATION);
            int design = extraData.getData(FrameBlockTile.DESIGN);
            int desTex = extraData.getData(FrameBlockTile.DESIGN_TEXTURE);
            TextureAtlasSprite designTexture = designTextureList.get(desTex);
            List<BakedQuad> quads = new ArrayList<>();
            if (design == 5) { //do we use that? I don't really like it
                switch (state.getValue(LadderBlock.FACING)) {
                    case WEST:
                        return new ArrayList<>(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 0f, 1f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                    case SOUTH:
                        return new ArrayList<>(ModelHelper.createSixFaceCuboid(0f, 1f, 0f, 1f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                    case NORTH:
                        return new ArrayList<>(ModelHelper.createSixFaceCuboid(0f, 1f, 0f, 1f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                    case EAST:
                        return new ArrayList<>(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 0f, 1f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                }
            }
            if (design == 0 || design == 1 || design == 2) {
                switch (state.getValue(LadderBlock.FACING)) {
                    case WEST:
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 0f, 1f, 0f, 1 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 0f, 1f, 15 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        break;
                    case SOUTH:
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1 / 16f, 0f, 1f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(15 / 16f, 1f, 0f, 1f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        break;
                    case NORTH:
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1 / 16f, 0f, 1f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(15 / 16f, 1f, 0f, 1f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        break;
                    case EAST:
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 0f, 1f, 0f, 1 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 0f, 1f, 15 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        break;
                }
            }
            if (design == 0 || design == 1) {
                switch (state.getValue(LadderBlock.FACING)) {
                    case WEST:
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 2 / 16f, 3 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 6 / 16f, 7 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 10 / 16f, 11 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 14 / 16f, 15 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        break;
                    case SOUTH:
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 2 / 16f, 3 / 16f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 6 / 16f, 7 / 16f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 10 / 16f, 11 / 16f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 14 / 16f, 15 / 16f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        break;
                    case NORTH:
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 2 / 16f, 3 / 16f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 6 / 16f, 7 / 16f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 10 / 16f, 11 / 16f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 14 / 16f, 15 / 16f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        break;
                    case EAST:
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 2 / 16f, 3 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 6 / 16f, 7 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 10 / 16f, 11 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 14 / 16f, 15 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        break;
                }
            }
            if (design == 1) {
                switch (state.getValue(LadderBlock.FACING)) {
                    case WEST:
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 0 / 16f, 1 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 4 / 16f, 5 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 8 / 16f, 9 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 12 / 16f, 13 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        break;
                    case SOUTH:
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 0 / 16f, 1 / 16f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 4 / 16f, 5 / 16f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 8 / 16f, 9 / 16f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 12 / 16f, 13 / 16f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        break;
                    case NORTH:
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 0 / 16f, 1 / 16f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 4 / 16f, 5 / 16f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 8 / 16f, 9 / 16f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 12 / 16f, 13 / 16f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        break;
                    case EAST:
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 0 / 16f, 1 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 4 / 16f, 5 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 8 / 16f, 9 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 12 / 16f, 13 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        break;
                }
            }
            if (design == 2) {
                switch (state.getValue(LadderBlock.FACING)) {
                    case WEST:
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 1 / 16f, 3 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 5 / 16f, 7 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 9 / 16f, 11 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 13 / 16f, 15 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        break;
                    case SOUTH:
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 1 / 16f, 3 / 16f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 5 / 16f, 7 / 16f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 9 / 16f, 11 / 16f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 13 / 16f, 15 / 16f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        break;
                    case NORTH:
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 1 / 16f, 3 / 16f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 5 / 16f, 7 / 16f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 9 / 16f, 11 / 16f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(1 / 16f, 15 / 16f, 13 / 16f, 15 / 16f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        break;
                    case EAST:
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 1 / 16f, 3 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 5 / 16f, 7 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 9 / 16f, 11 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 13 / 16f, 15 / 16f, 1 / 16f, 15 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        break;
                }
            }
            if (design == 3) {
                switch (state.getValue(LadderBlock.FACING)) {
                    case WEST:
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 1 / 16f, 3 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 5 / 16f, 7 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 9 / 16f, 11 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 1f, 13 / 16f, 15 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));

                        quads.addAll(ModelHelper.createCuboid(14 / 16f, 1f, 0f, 1f, 2 / 16f, 4 / 16f, designTexture, tintIndex));
                        quads.addAll(ModelHelper.createCuboid(14 / 16f, 1f, 0f, 1f, 12 / 16f, 14 / 16f, designTexture, tintIndex));
                        break;
                    case SOUTH:
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 1 / 16f, 3 / 16f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 5 / 16f, 7 / 16f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 9 / 16f, 11 / 16f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 13 / 16f, 15 / 16f, 0f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));

                        quads.addAll(ModelHelper.createCuboid(2 / 16f, 4 / 16f, 0f, 1f, 0f, 2 / 16f, designTexture, tintIndex));
                        quads.addAll(ModelHelper.createCuboid(12 / 16f, 14 / 16f, 0f, 1f, 0f, 2 / 16f, designTexture, tintIndex));
                        break;
                    case NORTH:
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 1 / 16f, 3 / 16f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 5 / 16f, 7 / 16f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 9 / 16f, 11 / 16f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 13 / 16f, 15 / 16f, 13 / 16f, 1f, mimic, model, extraData, rand, tintIndex, rotation));

                        quads.addAll(ModelHelper.createCuboid(2 / 16f, 4 / 16f, 0f, 1f, 14 / 16f, 1f, designTexture, tintIndex));
                        quads.addAll(ModelHelper.createCuboid(12 / 16f, 14 / 16f, 0f, 1f, 14 / 16f, 1f, designTexture, tintIndex));
                        break;
                    case EAST:
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 1 / 16f, 3 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 5 / 16f, 7 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 9 / 16f, 11 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 3 / 16f, 13 / 16f, 15 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));

                        quads.addAll(ModelHelper.createCuboid(0f, 2 / 16f, 0f, 1f, 2 / 16f, 4 / 16f, designTexture, tintIndex));
                        quads.addAll(ModelHelper.createCuboid(0f, 2 / 16f, 0f, 1f, 12 / 16f, 14 / 16f, designTexture, tintIndex));
                        break;
                }
            }
            if (design == 4) {
                switch (state.getValue(LadderBlock.FACING)) {
                    case WEST:
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 14 / 16f, 1 / 16f, 3 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 14 / 16f, 5 / 16f, 7 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 14 / 16f, 9 / 16f, 11 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(13 / 16f, 14 / 16f, 13 / 16f, 15 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));

                        quads.addAll(ModelHelper.createCuboid(14 / 16f, 1f, 0f, 1f, 2 / 16f, 4 / 16f, designTexture, tintIndex));
                        quads.addAll(ModelHelper.createCuboid(14 / 16f, 1f, 0f, 1f, 12 / 16f, 14 / 16f, designTexture, tintIndex));
                        break;
                    case SOUTH:
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 1 / 16f, 3 / 16f, 2 / 16f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 5 / 16f, 7 / 16f, 2 / 16f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 9 / 16f, 11 / 16f, 2 / 16f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 13 / 16f, 15 / 16f, 2 / 16f, 3 / 16f, mimic, model, extraData, rand, tintIndex, rotation));

                        quads.addAll(ModelHelper.createCuboid(2 / 16f, 4 / 16f, 0f, 1f, 0f, 2 / 16f, designTexture, tintIndex));
                        quads.addAll(ModelHelper.createCuboid(12 / 16f, 14 / 16f, 0f, 1f, 0f, 2 / 16f, designTexture, tintIndex));
                        break;
                    case NORTH:
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 1 / 16f, 3 / 16f, 13 / 16f, 14 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 5 / 16f, 7 / 16f, 13 / 16f, 14 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 9 / 16f, 11 / 16f, 13 / 16f, 14 / 16f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 13 / 16f, 15 / 16f, 13 / 16f, 14 / 16f, mimic, model, extraData, rand, tintIndex, rotation));

                        quads.addAll(ModelHelper.createCuboid(2 / 16f, 4 / 16f, 0f, 1f, 14 / 16f, 1f, designTexture, tintIndex));
                        quads.addAll(ModelHelper.createCuboid(12 / 16f, 14 / 16f, 0f, 1f, 14 / 16f, 1f, designTexture, tintIndex));
                        break;
                    case EAST:
                        quads.addAll(ModelHelper.createSixFaceCuboid(2 / 16f, 3 / 16f, 1 / 16f, 3 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(2 / 16f, 3 / 16f, 5 / 16f, 7 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(2 / 16f, 3 / 16f, 9 / 16f, 11 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));
                        quads.addAll(ModelHelper.createSixFaceCuboid(2 / 16f, 3 / 16f, 13 / 16f, 15 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, rotation));

                        quads.addAll(ModelHelper.createCuboid(0f, 2 / 16f, 0f, 1f, 2 / 16f, 4 / 16f, designTexture, tintIndex));
                        quads.addAll(ModelHelper.createCuboid(0f, 2 / 16f, 0f, 1f, 12 / 16f, 14 / 16f, designTexture, tintIndex));
                        break;
                }
            }
            int overlayIndex = extraData.getData(FrameBlockTile.OVERLAY);
            if (overlayIndex != 0) {
                switch (state.getValue(LadderBlock.FACING)) {
                    case NORTH:
                        quads.addAll(ModelHelper.createOverlay(0f, 1f, 0f, 1f, 13 / 16f, 1f, overlayIndex));
                        break;
                    case WEST:
                        quads.addAll(ModelHelper.createOverlay(13 / 16f, 1f, 0f, 1f, 0f, 1f, overlayIndex));
                        break;
                    case EAST:
                        quads.addAll(ModelHelper.createOverlay(0f, 3 / 16f, 0f, 1f, 0f, 1f, overlayIndex));
                        break;
                    case SOUTH:
                        quads.addAll(ModelHelper.createOverlay(0f, 1f, 0f, 1f, 0f, 3 / 16f, overlayIndex));
                        break;
                }
            }
            return quads;
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
    public boolean func_230044_c_() {
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
//========SOLI DEO GLORIA========//