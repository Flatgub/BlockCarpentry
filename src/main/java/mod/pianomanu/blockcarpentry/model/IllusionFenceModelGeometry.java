package mod.pianomanu.blockcarpentry.model;

import com.mojang.datafixers.util.Pair;
import mod.pianomanu.blockcarpentry.bakedmodels.FrameBakedModel;
import mod.pianomanu.blockcarpentry.bakedmodels.IllusionFenceBakedModel;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class IllusionFenceModelGeometry implements IModelGeometry<IllusionFenceModelGeometry> {
    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
        return new IllusionFenceBakedModel();
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return Collections.singletonList(new RenderMaterial(TextureAtlas.LOCATION_BLOCKS, FrameBakedModel.TEXTURE));
    }
}
