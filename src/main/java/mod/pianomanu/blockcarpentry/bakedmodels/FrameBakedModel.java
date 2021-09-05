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
import net.minecraft.util.text.TranslationTextComponent;
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
 * @version 1.9 08/18/21
 */
public class FrameBakedModel implements IDynamicBakedModel {
    public static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "block/oak_planks");

    @SuppressWarnings("deprecation")
    private TextureAtlasSprite getTexture() {
        return Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(TEXTURE);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {

        BlockState mimic = extraData.getData(MIMIC_MODEL_PROPERTY);
        Integer design = extraData.getData(DESIGN_MODEL_PROPERTY);
        Integer desTex = extraData.getData(DESIGN_TEXTURE_MODEL_PROPERTY);
        if (side == null) {
            return Collections.emptyList();
        }
        if (mimic != null && !(mimic.getBlock() instanceof FrameBlock)) {
            ModelResourceLocation location = BlockModelShapes.getModelLocation(mimic);
            if (state != null) {
                IBakedModel model = Minecraft.getInstance().getModelManager().getModel(location);
                List<TextureAtlasSprite> textureList = TextureHelper.getTextureFromModel(model, extraData, rand);
                TextureAtlasSprite texture;
                Integer tex = extraData.getData(TEXTURE_MODEL_PROPERTY);
                if (textureList.size() <= tex) {
                    extraData.setData(TEXTURE_MODEL_PROPERTY, 0);
                    tex = 0;
                }
                if (textureList.size() == 0) {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.block_not_available"), true);
                    }
                    for (int i = 0; i < 6; i++) {
                        textureList.add(Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(new ResourceLocation("missing")));
                    }
                    //return Collections.emptyList();
                }
                texture = textureList.get(tex);
                SixSideSet vis = extraData.getData(SIDE_VISIBILITY_MODEL_PROPERTY);
                boolean renderNorth = vis.test(Direction.NORTH);
                boolean renderEast = vis.test(Direction.EAST);
                boolean renderSouth = vis.test(Direction.SOUTH);
                boolean renderWest = vis.test(Direction.WEST);
                boolean renderUp = vis.test(Direction.UP);
                boolean renderDown = vis.test(Direction.DOWN);
                int tintIndex = BlockAppearanceHelper.setTintIndex(mimic);
                List<BakedQuad> quads = new ArrayList<>(ModelHelper.createCuboid(0f, 1f, 0f, 1f, 0f, 1f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, renderUp, renderDown));
                int overlayIndex = extraData.getData(OVERLAY_MODEL_PROPERTY);
                if (overlayIndex != 0) {
                    //TODO fix overlay for transparent blocks - then also use transparent overlay
                    quads.addAll(ModelHelper.createOverlay(0f, 1f, 0f, 1f, 0f, 1f, overlayIndex, true, true, renderEast, renderWest, renderUp, renderDown, true));
                }
                return quads;
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
    @Nonnull
    public TextureAtlasSprite getParticleTexture() {
        return getTexture();
    }

    @Override
    @Nonnull
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }
}
