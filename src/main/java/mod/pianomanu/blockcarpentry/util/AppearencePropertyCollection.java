package mod.pianomanu.blockcarpentry.util;

import mod.pianomanu.blockcarpentry.BlockCarpentryMain;
import net.minecraft.block.BlockState;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

import java.util.HashMap;

public class AppearencePropertyCollection {

    public static final String MIMIC_PROPERTY = "mimic";
    public static final String TEXTURE_PROPERTY = "texture";
    public static final String ROTATION_PROPERTY = "rotation";
    public static final String OVERLAY_PROPERTY = "overlay";
    public static final String DESIGN_PROPERTY = "design";
    public static final String DESIGN_TEXTURE_PROPERTY = "design_texture";

    public static final HashMap<String, ModelProperty<?>> modelPropertyMapping = new HashMap<>();

    //default set (all blocks)
    public static final ModelProperty<BlockState> MIMIC_MODEL_PROPERTY = new ModelProperty<>();
    public static final ModelProperty<Integer> TEXTURE_MODEL_PROPERTY = new ModelProperty<>();
    public static final ModelProperty<Integer> ROTATION_MODEL_PROPERTY = new ModelProperty<>();

    //commonly used (a large number of blocks)
    public static final ModelProperty<Integer> OVERLAY_MODEL_PROPERTY = new ModelProperty<>();
    public static final ModelProperty<Integer> DESIGN_MODEL_PROPERTY = new ModelProperty<>();
    public static final ModelProperty<Integer> DESIGN_TEXTURE_MODEL_PROPERTY = new ModelProperty<>();
    //public static final ModelProperty<Integer> GLASS_COLOR = new ModelProperty<>();

    static {
        modelPropertyMapping.put(MIMIC_PROPERTY, MIMIC_MODEL_PROPERTY);
        modelPropertyMapping.put(TEXTURE_PROPERTY, TEXTURE_MODEL_PROPERTY);
        modelPropertyMapping.put(ROTATION_PROPERTY, ROTATION_MODEL_PROPERTY);
        modelPropertyMapping.put(OVERLAY_PROPERTY, OVERLAY_MODEL_PROPERTY);
        modelPropertyMapping.put(DESIGN_PROPERTY, DESIGN_MODEL_PROPERTY);
        modelPropertyMapping.put(DESIGN_TEXTURE_PROPERTY, DESIGN_TEXTURE_MODEL_PROPERTY);
    }

    //public static final ModelProperty<Boolean> NORTH_VISIBLE = new ModelProperty<>();
    //public static final ModelProperty<Boolean> EAST_VISIBLE = new ModelProperty<>();
    //public static final ModelProperty<Boolean> SOUTH_VISIBLE = new ModelProperty<>();
    //public static final ModelProperty<Boolean> WEST_VISIBLE = new ModelProperty<>();
    //public static final ModelProperty<Boolean> UP_VISIBLE = new ModelProperty<>();
    //public static final ModelProperty<Boolean> DOWN_VISIBLE = new ModelProperty<>();

    public static unfinishedPropertyCollection newCollection() {
        return new unfinishedPropertyCollection();
    }

    public static unfinishedPropertyCollection extend(HashMap<String, AppearanceProperty<?>> from) {
        return new unfinishedPropertyCollection(from);
    }

    public static class unfinishedPropertyCollection {
        private final HashMap<String, AppearanceProperty<?>> properties;

        public unfinishedPropertyCollection() {
            properties = new HashMap<>();
        }

        public unfinishedPropertyCollection(HashMap<String, AppearanceProperty<?>> from) {
            properties = new HashMap<>(from);
        }

        public unfinishedPropertyCollection withDefaults() {
            properties.put(MIMIC_PROPERTY, new BlockstateAppearanceProperty(null));
            properties.put(TEXTURE_PROPERTY, new IntegerAppearanceProperty(0));
            properties.put(ROTATION_PROPERTY, new IntegerAppearanceProperty(0));
            properties.put(OVERLAY_PROPERTY, new IntegerAppearanceProperty(0));
            return this;
        }

        public unfinishedPropertyCollection with(String name, AppearanceProperty<?> prop) {
            properties.put(name, prop);
            return this;
        }

        public unfinishedPropertyCollection without(String name) {
            properties.remove(name);
            return this;
        }


        public HashMap<String, AppearanceProperty<?>> get() {
            return properties;
        }
    }
}
