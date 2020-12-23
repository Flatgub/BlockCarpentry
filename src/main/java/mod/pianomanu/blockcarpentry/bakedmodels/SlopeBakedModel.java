package mod.pianomanu.blockcarpentry.bakedmodels;

import mod.pianomanu.blockcarpentry.block.FrameBlock;
import mod.pianomanu.blockcarpentry.tileentity.FrameBlockTile;
import mod.pianomanu.blockcarpentry.util.ModelHelper;
import mod.pianomanu.blockcarpentry.util.TextureHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
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
 * See {@link mod.pianomanu.blockcarpentry.util.ModelHelper} for more information
 *
 * @author PianoManu
 * @version 1.4 12/23/20
 */
public class SlopeBakedModel implements IDynamicBakedModel {
    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        //get block saved in frame tile
        BlockState mimic = extraData.getData(FrameBlockTile.MIMIC);
        if (mimic != null && !(mimic.getBlock() instanceof FrameBlock)) {
            ModelResourceLocation location = BlockModelShapes.getModelLocation(mimic);
            if (location != null) {
                IBakedModel model = Minecraft.getInstance().getModelManager().getModel(location);
                model.getBakedModel().getQuads(mimic, side, rand, extraData);
                if (model != null) {
                    //only if model (from block saved in tile entity) exists:
                    return getMimicQuads(state, side, rand, extraData, model);
                }
            }
        }
        return Collections.emptyList();
    }

    private static Vector3d v(double x, double y, double z) {
        return new Vector3d(x, y, z);
    }

    public List<BakedQuad> getMimicQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData, IBakedModel model) {
        List<BakedQuad> quads = new ArrayList<>();
        BlockState mimic = extraData.getData(FrameBlockTile.MIMIC);
        List<TextureAtlasSprite> texture = TextureHelper.getTextureFromModel(model, extraData, rand);
        int index = extraData.getData(FrameBlockTile.TEXTURE);
        if (index >= texture.size()) {
            index = 0;
        }
        if (texture.size() == 0) {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("We're sorry, but this block can't be displayed"), true);
            }
            return Collections.emptyList();
        }
        //TODO Remove when slopes are fixed
        /*if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("We're sorry, but Slopes do not work at the moment"), true);
        }*/
        int tintIndex = -1;
        if (mimic.getBlock() instanceof GrassBlock) {
            tintIndex = 1;
        }
        double w = 0.5;
        if (state.get(StairsBlock.HALF) == Half.TOP) {
            w = -0.5;
        }
        //Eight corners of the block
        Vector3d NWU = v(0, 0.5 + w, 0); //North-West-Up
        Vector3d NEU = v(1, 0.5 + w, 0); //...
        Vector3d NWD = v(0, 0.5 - w, 0);
        Vector3d NED = v(1, 0.5 - w, 0);
        Vector3d SWU = v(0, 0.5 + w, 1);
        Vector3d SEU = v(1, 0.5 + w, 1);
        Vector3d SWD = v(0, 0.5 - w, 1);
        Vector3d SED = v(1, 0.5 - w, 1); //South-East-Down
        //bottom face
        /*quads.add(ModelHelper.createQuad(SED, SWD, NWD, NED, texture.get(index), 0, 16, 0, 16, tintIndex));
        switch (state.get(StairsBlock.FACING)) {
            case NORTH:
                SWU = v(0,0.5-w,1);
                SEU = v(1,0.5-w,1);
                SWD = v(0,0.5+w,1);
                SED = v(1,0.5+w,1);
                //back face
                quads.add(ModelHelper.createQuad(NEU, NED, NWD, NWU, texture.get(index), 0, 16, 0, 16, tintIndex));

                quads.add(ModelHelper.createQuad(SWU, NWU, NWD, SWU, texture.get(index), 0, 16, 0, 16, tintIndex));
                quads.add(ModelHelper.createQuad(NEU, SEU, NED, NEU, texture.get(index), 0, 16, 0, 16, tintIndex));


                break;
            case WEST:
                NED = v(1,0.5+w,0);
                NEU = v(1,0.5-w,0);
                SEU = v(1,0.5-w,1);
                SED = v(1,0.5+w,1);
                //back face
                quads.add(ModelHelper.createQuad(NWU, NWD, SWD, SWU, texture.get(index), 0, 16, 0, 16, tintIndex));
                break;
            case SOUTH:
                NWU = v(0,0.5-w,0);
                NEU = v(1,0.5-w,0);
                NWD = v(0,0.5+w,0);
                NED = v(1,0.5+w,0);
                //back face
                quads.add(ModelHelper.createQuad(SWU, SWD, SED, SEU, texture.get(index), 0, 16, 0, 16,tintIndex));
                break;
            case EAST:
                SWU = v(0,0.5-w,1);
                SWD = v(0,0.5+w,1);
                NWU = v(0,0.5-w,0);
                NWD = v(0,0.5+w,0);
                //back face
                quads.add(ModelHelper.createQuad(SEU, SED, NED, NEU, texture.get(index), 0, 16, 0, 16, tintIndex));
                break;
        }
        //top face
        quads.add(ModelHelper.createQuad(SWU, SEU, NEU, NWU, texture.get(index), 0, 16, 0, 16, tintIndex));
        //quads.addAll(ModelHelper.createQuad(0,1,0,1,0,1, texture.get(index), tintIndex,state.get(StairsBlock.FACING), state.get(StairsBlock.HALF))); //TODO remove or fix
        */
        quads.addAll(createSlope(0, 1, 0, 1, 0, 1, texture.get(index), tintIndex, state.get(StairsBlock.FACING), state.get(StairsBlock.SHAPE), state.get(StairsBlock.HALF)));
        return quads;
    }

    public List<BakedQuad> createSlope(float xl, float xh, float yl, float yh, float zl, float zh, TextureAtlasSprite texture, int tintIndex, Direction direction, StairsShape shape, Half half) {
        List<BakedQuad> quads = new ArrayList<>();
        //Eight corners of the block
        Vector3d NWU = v(xl, yh, zl); //North-West-Up
        Vector3d SWU = v(xl, yh, zh); //...
        Vector3d NWD = v(xl, yl, zl);
        Vector3d SWD = v(xl, yl, zh);
        Vector3d NEU = v(xh, yh, zl);
        Vector3d SEU = v(xh, yh, zh);
        Vector3d NED = v(xh, yl, zl);
        Vector3d SED = v(xh, yl, zh); //South-East-Down
        if (xh - xl > 1 || yh - yl > 1 || zh - zl > 1) {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("An error occured with this block, please report to the mod author (PianoManu)"), true);
            }
            return quads;
        }
        if (xl < 0) {
            xl++;
            xh++;
        }
        if (xh > 1) {
            xh--;
            xl--;
        }
        if (yl < 0) {
            yl++;
            yh++;
        }
        if (yh > 1) {
            yh--;
            yl--;
        }
        if (zl < 0) {
            zl++;
            zh++;
        }
        if (zh > 1) {
            zh--;
            zl--;
        }
        if (half == Half.TOP) {
            Vector3d tmp = NWD;
            NWD = NWU;
            NWU = tmp;

            tmp = SWD;
            SWD = SWU;
            SWU = tmp;

            tmp = NED;
            NED= NEU;
            NEU = tmp;

            tmp = SED;
            SED = SEU;
            SEU = tmp;
        }
        //boolean invertFace = half == Half.BOTTOM;
        //bottom face
        quads.add(ModelHelper.createQuad(SWD, NWD, NED, SED, texture, 0, 16, 0, 16, tintIndex));
        switch (shape) {
            /*case STRAIGHT:
                switch (direction) {
                    case NORTH:
                        quads.add(ModelHelper.createQuad(NEU, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuad(NEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuadInverted(NWD, SWD, NWU, NWU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuad(NWU, SWD, SED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        break;
                    case WEST:
                        //back face
                        quads.add(ModelHelper.createQuad(NWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuad(NED, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuadInverted(SWD, SED, SWU, SWU, texture, 0, 16, 16, 0, tintIndex, invertFace));

                        //top face
                        quads.add(ModelHelper.createQuad(SWU, SED, NED, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        break;
                    case SOUTH:
                        quads.add(ModelHelper.createQuad(SWU, SWD, SED, SEU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuadInverted(NED, SEU, SED, NED, texture, 16, 0, 0, 16, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuad(SWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuad(NWD, SWU, SEU, NED, texture, 16, 0, 16, 0, tintIndex, invertFace));
                        break;
                    case EAST:
                        //back face
                        quads.add(ModelHelper.createQuad(SEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuadInverted(NED, NWD, NWD, NEU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuad(SED, SEU, SEU, SWD, texture, 16, 0, 16, 0, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuad(SWD, SEU, NEU, NWD, texture, 16, 0, 16, 0, tintIndex, invertFace));
                        break;
                }
                break;
            case INNER_RIGHT:
                switch (direction) {
                    case NORTH:
                        quads.add(ModelHelper.createQuad(NEU, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        //quads.add(ModelHelper.createQuad(NEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuadInverted(NWD, SWD, NWU, NWU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuad(NWU, SWD, SED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        //back face
                        quads.add(ModelHelper.createQuad(SEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        //quads.add(ModelHelper.createQuadInverted(NED, NWD, NWD, NEU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuad(SED, SEU, SEU, SWD, texture, 16, 0, 16, 0, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuad(SWD, SEU, NEU, NWD, texture, 16, 0, 16, 0, tintIndex, invertFace));
                        break;
                    case WEST:
                        //back face
                        quads.add(ModelHelper.createQuad(NWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        //quads.add(ModelHelper.createQuad(NED, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuadInverted(SWD, SED, SWU, SWU, texture, 0, 16, 16, 0, tintIndex, invertFace));

                        //top face
                        quads.add(ModelHelper.createQuad(SWU, SED, NED, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuad(NEU, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuad(NEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        //quads.add(ModelHelper.createQuadInverted(NWD, SWD, NWU, NWU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuad(NWU, SWD, SED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        break;
                    case SOUTH:
                        quads.add(ModelHelper.createQuad(SWU, SWD, SED, SEU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuadInverted(NED, SEU, SED, NED, texture, 16, 0, 0, 16, tintIndex, invertFace));
                        //quads.add(ModelHelper.createQuad(SWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuad(NWD, SWU, SEU, NED, texture, 16, 0, 16, 0, tintIndex, invertFace));

                        //back face
                        quads.add(ModelHelper.createQuad(NWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuad(NED, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        //quads.add(ModelHelper.createQuadInverted(SWD, SED, SWU, SWU, texture, 0, 16, 16, 0, tintIndex, invertFace));

                        //top face
                        quads.add(ModelHelper.createQuad(SWU, SED, NED, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        break;
                    case EAST:
                        //back face
                        quads.add(ModelHelper.createQuad(SEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuadInverted(NED, NWD, NWD, NEU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        //quads.add(ModelHelper.createQuad(SED, SEU, SEU, SWD, texture, 16, 0, 16, 0, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuad(SWD, SEU, NEU, NWD, texture, 16, 0, 16, 0, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuad(SWU, SWD, SED, SEU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        //quads.add(ModelHelper.createQuadInverted(NED, SEU, SED, NED, texture, 16, 0, 0, 16, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuad(SWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuad(NWD, SWU, SEU, NED, texture, 16, 0, 16, 0, tintIndex, invertFace));
                        break;
                }
                break;
            case INNER_LEFT:
                switch (direction) {
                    case EAST:
                        quads.add(ModelHelper.createQuad(NEU, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        //quads.add(ModelHelper.createQuad(NEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuadInverted(NWD, SWD, NWU, NWU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuad(NWU, SWD, SED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        //back face
                        quads.add(ModelHelper.createQuad(SEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        //quads.add(ModelHelper.createQuadInverted(NED, NWD, NWD, NEU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuad(SED, SEU, SEU, SWD, texture, 16, 0, 16, 0, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuad(SWD, SEU, NEU, NWD, texture, 16, 0, 16, 0, tintIndex, invertFace));
                        break;
                    case NORTH:
                        //back face
                        quads.add(ModelHelper.createQuad(NWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        //quads.add(ModelHelper.createQuad(NED, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuadInverted(SWD, SED, SWU, SWU, texture, 0, 16, 16, 0, tintIndex, invertFace));

                        //top face
                        quads.add(ModelHelper.createQuad(SWU, SED, NED, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuad(NEU, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuad(NEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        //quads.add(ModelHelper.createQuadInverted(NWD, SWD, NWU, NWU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuad(NWU, SWD, SED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        break;
                    case WEST:
                        quads.add(ModelHelper.createQuad(SWU, SWD, SED, SEU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuadInverted(NED, SEU, SED, NED, texture, 16, 0, 0, 16, tintIndex, invertFace));
                        //quads.add(ModelHelper.createQuad(SWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuad(NWD, SWU, SEU, NED, texture, 16, 0, 16, 0, tintIndex, invertFace));

                        //back face
                        quads.add(ModelHelper.createQuad(NWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuad(NED, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        //quads.add(ModelHelper.createQuadInverted(SWD, SED, SWU, SWU, texture, 0, 16, 16, 0, tintIndex, invertFace));

                        //top face
                        quads.add(ModelHelper.createQuad(SWU, SED, NED, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        break;
                    case SOUTH:
                        //back face
                        quads.add(ModelHelper.createQuad(SEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuadInverted(NED, NWD, NWD, NEU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        //quads.add(ModelHelper.createQuad(SED, SEU, SEU, SWD, texture, 16, 0, 16, 0, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuad(SWD, SEU, NEU, NWD, texture, 16, 0, 16, 0, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuad(SWU, SWD, SED, SEU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        //quads.add(ModelHelper.createQuadInverted(NED, SEU, SED, NED, texture, 16, 0, 0, 16, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuad(SWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuad(NWD, SWU, SEU, NED, texture, 16, 0, 16, 0, tintIndex, invertFace));
                        break;
                }
                break;
            case OUTER_RIGHT:
                switch (direction) {
                    case WEST:
                        quads.add(ModelHelper.createQuad(NED, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuad(NWU, SED, NED, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuadInverted(NWD, SWD, NWU, NWU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuadInverted(SED, NWU, SWD, SED, texture, 16, 0, 0, 16, tintIndex, invertFace));
                        break;
                    case SOUTH:
                        //back face
                        quads.add(ModelHelper.createQuad(SWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuad(SWU, NED, NWD, SWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuadInverted(SWD, SED, SWU, SWU, texture, 0, 16, 16, 0, tintIndex, invertFace));

                        //top face
                        quads.add(ModelHelper.createQuadInverted(NED, SWU, SED, NED, texture, 16, 0, 0, 16, tintIndex, invertFace));
                        break;
                    case EAST:
                        quads.add(ModelHelper.createQuad(SEU, SWD, SED, SEU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuadInverted(NED, SEU, SED, NED, texture, 16, 0, 0, 16, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuad(SEU, NWD, SWD, SEU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuadInverted(NED, NWD, SEU, SEU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        break;
                    case NORTH:
                        //back face
                        quads.add(ModelHelper.createQuad(NEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuadInverted(NED, NWD, NWD, NEU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuad(NEU, SWD, SED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuadInverted(NWD, SWD, NEU, NEU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        break;
                }
                break;
            case OUTER_LEFT:
                switch (direction) {
                    case NORTH:
                        quads.add(ModelHelper.createQuad(NED, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuad(NWU, SED, NED, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuadInverted(NWD, SWD, NWU, NWU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuadInverted(SED, NWU, SWD, SED, texture, 16, 0, 0, 16, tintIndex, invertFace));
                        break;
                    case WEST:
                        //back face
                        quads.add(ModelHelper.createQuad(SWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuad(SWU, NED, NWD, SWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuadInverted(SWD, SED, SWU, SWU, texture, 0, 16, 16, 0, tintIndex, invertFace));

                        //top face
                        quads.add(ModelHelper.createQuadInverted(NED, SWU, SED, NED, texture, 16, 0, 0, 16, tintIndex, invertFace));
                        break;
                    case SOUTH:
                        quads.add(ModelHelper.createQuad(SEU, SWD, SED, SEU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuadInverted(NED, SEU, SED, NED, texture, 16, 0, 0, 16, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuad(SEU, NWD, SWD, SEU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuadInverted(NED, NWD, SEU, SEU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        break;
                    case EAST:
                        //back face
                        quads.add(ModelHelper.createQuad(NEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));

                        quads.add(ModelHelper.createQuadInverted(NED, NWD, NWD, NEU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        quads.add(ModelHelper.createQuad(NEU, SWD, SED, NEU, texture, 0, 16, 0, 16, tintIndex, invertFace));
                        //top face
                        quads.add(ModelHelper.createQuadInverted(NWD, SWD, NEU, NEU, texture, 0, 16, 16, 0, tintIndex, invertFace));
                        break;
                }
                break;*/
            case STRAIGHT:
                switch (direction) {
                    case NORTH:
                        //back face
                        quads.add(ModelHelper.createQuad(NEU, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuad(NEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex));
                        quads.add(ModelHelper.createQuadInverted(NWD, SWD, NWU, NWU, texture, 0, 16, 16, 0, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(NWU, SWD, SED, NEU, texture, 0, 16, 0, 16, tintIndex));
                        break;
                    case EAST:
                        //back face
                        quads.add(ModelHelper.createQuad(SEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuadInverted(NED, NWD, NWD, NEU, texture, 0, 16, 16, 0, tintIndex));
                        quads.add(ModelHelper.createQuad(SED, SEU, SEU, SWD, texture, 16, 0, 16, 0, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(SWD, SEU, NEU, NWD, texture, 16, 0, 16, 0, tintIndex));
                        break;
                    case SOUTH:
                        //back face
                        quads.add(ModelHelper.createQuad(SWU, SWD, SED, SEU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuadInverted(NED, SEU, SED, NED, texture, 16, 0, 0, 16, tintIndex));
                        quads.add(ModelHelper.createQuad(SWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(NWD, SWU, SEU, NED, texture, 16, 0, 16, 0, tintIndex));
                        break;
                    case WEST:
                        //back face
                        quads.add(ModelHelper.createQuad(NWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuad(NED, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex));
                        quads.add(ModelHelper.createQuadInverted(SWD, SED, SWU, SWU, texture, 0, 16, 16, 0, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(SWU, SED, NED, NWU, texture, 0, 16, 0, 16, tintIndex));
                        break;
                }
                break;
            case OUTER_LEFT:
                break;
            case OUTER_RIGHT:
                break;
            case INNER_LEFT:
                switch (direction) {
                    case NORTH:
                        //NORTH PART
                        //back face
                        quads.add(ModelHelper.createQuad(NEU, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuad(NEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex));
                        quads.add(ModelHelper.createQuadInverted(NWD, SWD, NWU, NWU, texture, 0, 16, 16, 0, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(NWU, SWD, SED, NEU, texture, 0, 16, 0, 16, tintIndex));

                        //WEST PART
                        //back face
                        quads.add(ModelHelper.createQuad(NWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuad(NED, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex));
                        quads.add(ModelHelper.createQuadInverted(SWD, SED, SWU, SWU, texture, 0, 16, 16, 0, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(SWU, SED, NED, NWU, texture, 0, 16, 0, 16, tintIndex));
                        break;
                    case EAST:
                        //EAST PART
                        //back face
                        quads.add(ModelHelper.createQuad(SEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuadInverted(NED, NWD, NWD, NEU, texture, 0, 16, 16, 0, tintIndex));
                        quads.add(ModelHelper.createQuad(SED, SEU, SEU, SWD, texture, 16, 0, 16, 0, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(SWD, SEU, NEU, NWD, texture, 16, 0, 16, 0, tintIndex));

                        //NORTH PART
                        //back face
                        quads.add(ModelHelper.createQuad(NEU, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuad(NEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex));
                        quads.add(ModelHelper.createQuadInverted(NWD, SWD, NWU, NWU, texture, 0, 16, 16, 0, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(NWU, SWD, SED, NEU, texture, 0, 16, 0, 16, tintIndex));
                        break;
                    case SOUTH:
                        //SOUTH PART
                        //back face
                        quads.add(ModelHelper.createQuad(SWU, SWD, SED, SEU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuadInverted(NED, SEU, SED, NED, texture, 16, 0, 0, 16, tintIndex));
                        quads.add(ModelHelper.createQuad(SWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(NWD, SWU, SEU, NED, texture, 16, 0, 16, 0, tintIndex));

                        //EAST PART
                        //back face
                        quads.add(ModelHelper.createQuad(SEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuadInverted(NED, NWD, NWD, NEU, texture, 0, 16, 16, 0, tintIndex));
                        quads.add(ModelHelper.createQuad(SED, SEU, SEU, SWD, texture, 16, 0, 16, 0, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(SWD, SEU, NEU, NWD, texture, 16, 0, 16, 0, tintIndex));
                        break;
                    case WEST:
                        //WEST PART
                        //back face
                        quads.add(ModelHelper.createQuad(NWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuad(NED, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex));
                        quads.add(ModelHelper.createQuadInverted(SWD, SED, SWU, SWU, texture, 0, 16, 16, 0, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(SWU, SED, NED, NWU, texture, 0, 16, 0, 16, tintIndex));

                        //SOUTH PART
                        //back face
                        quads.add(ModelHelper.createQuad(SWU, SWD, SED, SEU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuadInverted(NED, SEU, SED, NED, texture, 16, 0, 0, 16, tintIndex));
                        quads.add(ModelHelper.createQuad(SWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(NWD, SWU, SEU, NED, texture, 16, 0, 16, 0, tintIndex));
                        break;
                }
                break;
            case INNER_RIGHT:
                switch (direction) {
                    case NORTH:
                        //NORTH PART
                        //back face
                        quads.add(ModelHelper.createQuad(NEU, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuad(NEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex));
                        quads.add(ModelHelper.createQuadInverted(NWD, SWD, NWU, NWU, texture, 0, 16, 16, 0, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(NWU, SWD, SED, NEU, texture, 0, 16, 0, 16, tintIndex));

                        //EAST PART
                        //back face
                        quads.add(ModelHelper.createQuad(SEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuadInverted(NED, NWD, NWD, NEU, texture, 0, 16, 16, 0, tintIndex));
                        quads.add(ModelHelper.createQuad(SED, SEU, SEU, SWD, texture, 16, 0, 16, 0, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(SWD, SEU, NEU, NWD, texture, 16, 0, 16, 0, tintIndex));
                        break;
                    case EAST:
                        //EAST PART
                        //back face
                        quads.add(ModelHelper.createQuad(SEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuadInverted(NED, NWD, NWD, NEU, texture, 0, 16, 16, 0, tintIndex));
                        quads.add(ModelHelper.createQuad(SED, SEU, SEU, SWD, texture, 16, 0, 16, 0, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(SWD, SEU, NEU, NWD, texture, 16, 0, 16, 0, tintIndex));

                        //SOUTH PART
                        //back face
                        quads.add(ModelHelper.createQuad(SWU, SWD, SED, SEU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuadInverted(NED, SEU, SED, NED, texture, 16, 0, 0, 16, tintIndex));
                        quads.add(ModelHelper.createQuad(SWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(NWD, SWU, SEU, NED, texture, 16, 0, 16, 0, tintIndex));
                        break;
                    case SOUTH:
                        //SOUTH PART
                        //back face
                        quads.add(ModelHelper.createQuad(SWU, SWD, SED, SEU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuadInverted(NED, SEU, SED, NED, texture, 16, 0, 0, 16, tintIndex));
                        quads.add(ModelHelper.createQuad(SWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(NWD, SWU, SEU, NED, texture, 16, 0, 16, 0, tintIndex));

                        //WEST PART
                        //back face
                        quads.add(ModelHelper.createQuad(NWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuad(NED, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex));
                        quads.add(ModelHelper.createQuadInverted(SWD, SED, SWU, SWU, texture, 0, 16, 16, 0, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(SWU, SED, NED, NWU, texture, 0, 16, 0, 16, tintIndex));
                        break;
                    case WEST:
                        //WEST PART
                        //back face
                        quads.add(ModelHelper.createQuad(NWU, NWD, SWD, SWU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuad(NED, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex));
                        quads.add(ModelHelper.createQuadInverted(SWD, SED, SWU, SWU, texture, 0, 16, 16, 0, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(SWU, SED, NED, NWU, texture, 0, 16, 0, 16, tintIndex));

                        //NORTH PART
                        //back face
                        quads.add(ModelHelper.createQuad(NEU, NED, NWD, NWU, texture, 0, 16, 0, 16, tintIndex));
                        //slanted faces
                        quads.add(ModelHelper.createQuad(NEU, SED, NED, NEU, texture, 0, 16, 0, 16, tintIndex));
                        quads.add(ModelHelper.createQuadInverted(NWD, SWD, NWU, NWU, texture, 0, 16, 16, 0, tintIndex));
                        //top face
                        quads.add(ModelHelper.createQuad(NWU, SWD, SED, NEU, texture, 0, 16, 0, 16, tintIndex));
                        break;
                }
                break;
        }
        //top face
        //quads.add(ModelHelper.createQuad(NEU, SEU, SWU, NWU, texture, 0, 16, 0, 16, tintIndex, invertFace));
        return quads;
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
        return Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(new ResourceLocation("minecraft", "block/oak_planks"));
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