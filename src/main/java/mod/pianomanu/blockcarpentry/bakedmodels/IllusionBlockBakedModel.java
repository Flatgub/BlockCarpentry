package mod.pianomanu.blockcarpentry.bakedmodels;

import mod.pianomanu.blockcarpentry.block.FrameBlock;
import mod.pianomanu.blockcarpentry.util.BlockAppearanceHelper;
import mod.pianomanu.blockcarpentry.util.ModelHelper;
import mod.pianomanu.blockcarpentry.util.SixSideSet;
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
 * See {@link mod.pianomanu.blockcarpentry.util.ModelHelper} for more information
 *
 * @author PianoManu
 * @version 1.6 08/18/21
 */
public class IllusionBlockBakedModel implements IDynamicBakedModel {
    public static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "block/oak_planks");

    private TextureAtlasSprite getTexture() {
        return Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(TEXTURE);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {

        BlockState mimic = extraData.getData(MIMIC_MODEL_PROPERTY);
        if (mimic != null && !(mimic.getBlock() instanceof FrameBlock)) {
            ModelResourceLocation location = BlockModelShapes.getModelLocation(mimic);
            if (location != null) {
                IBakedModel model = Minecraft.getInstance().getModelManager().getModel(location);
                if (model != null) {
                    int tintIndex = BlockAppearanceHelper.setTintIndex(mimic);
                    SixSideSet vis = extraData.getData(SIDE_VISIBILITY_MODEL_PROPERTY);
                    boolean renderNorth = side == Direction.NORTH && vis.test(Direction.NORTH);
                    boolean renderEast = side == Direction.EAST && vis.test(Direction.EAST);
                    boolean renderSouth = side == Direction.SOUTH && vis.test(Direction.SOUTH);
                    boolean renderWest = side == Direction.WEST && vis.test(Direction.WEST);
                    boolean renderUp = side == Direction.UP && vis.test(Direction.UP);
                    boolean renderDown = side == Direction.DOWN && vis.test(Direction.DOWN);
                    int rotation = extraData.getData(ROTATION_MODEL_PROPERTY);
                    List<BakedQuad> quads = new ArrayList<>(ModelHelper.createSixFaceCuboid(0f, 1f, 0f, 1f, 0f, 1f, mimic, model, extraData, rand, tintIndex, renderNorth, renderSouth, renderEast, renderWest, renderUp, renderDown, rotation));
                    int overlayIndex = extraData.getData(OVERLAY_MODEL_PROPERTY);
                    if (overlayIndex != 0) {
                        //TODO fix overlay for transparent blocks - then also use transparent overlay
                        quads.addAll(ModelHelper.createOverlay(0f, 1f, 0f, 1f, 0f, 1f, overlayIndex, true, true, renderEast, renderWest, renderUp, renderDown, true));
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
