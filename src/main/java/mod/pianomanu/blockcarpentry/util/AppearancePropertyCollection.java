package mod.pianomanu.blockcarpentry.util;

import mod.pianomanu.blockcarpentry.BlockCarpentryMain;
import net.minecraft.block.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.ModelProperty;

import java.util.EnumSet;
import java.util.HashMap;

public class AppearancePropertyCollection {

    public static final int MAX_ROTATIONS = 6;
    public static final int MAX_FACE_TEXTURES = 8; //i dont know why? maybe this should be 6 as well

    public static final String MIMIC_PROPERTY = "mimic";
    public static final String TEXTURE_PROPERTY = "texture";
    public static final String ROTATION_PROPERTY = "rotation";
    public static final String OVERLAY_PROPERTY = "overlay";
    public static final String DESIGN_PROPERTY = "design";
    public static final String DESIGN_TEXTURE_PROPERTY = "design_texture";
    public static final String COLOR_PROPERTY = "color";
    public static final String SIDE_VISIBILITY_PROPERTY = "visible_sides";

    public static final HashMap<String, ModelProperty<?>> modelPropertyMapping = new HashMap<>();

    //default set (all blocks)
    public static final ModelProperty<BlockState> MIMIC_MODEL_PROPERTY = new ModelProperty<>();
    public static final ModelProperty<Integer> TEXTURE_MODEL_PROPERTY = new ModelProperty<>();
    public static final ModelProperty<Integer> ROTATION_MODEL_PROPERTY = new ModelProperty<>();
    public static final ModelProperty<Integer> OVERLAY_MODEL_PROPERTY = new ModelProperty<>();
    public static final ModelProperty<SixSideSet> SIDE_VISIBILITY_MODEL_PROPERTY = new ModelProperty<>();

    //commonly used (a large number of blocks)
    public static final ModelProperty<Integer> DESIGN_MODEL_PROPERTY = new ModelProperty<>(); //DEFAULT MAX 4
    public static final ModelProperty<Integer> DESIGN_TEXTURE_MODEL_PROPERTY = new ModelProperty<>(); //DEFAULT MAX 4
    public static final ModelProperty<DyeColor> COLOR_MODEL_PROPERTY = new ModelProperty<>();


    static {
        modelPropertyMapping.put(MIMIC_PROPERTY, MIMIC_MODEL_PROPERTY);
        modelPropertyMapping.put(TEXTURE_PROPERTY, TEXTURE_MODEL_PROPERTY);
        modelPropertyMapping.put(ROTATION_PROPERTY, ROTATION_MODEL_PROPERTY);
        modelPropertyMapping.put(OVERLAY_PROPERTY, OVERLAY_MODEL_PROPERTY);
        modelPropertyMapping.put(DESIGN_PROPERTY, DESIGN_MODEL_PROPERTY);
        modelPropertyMapping.put(DESIGN_TEXTURE_PROPERTY, DESIGN_TEXTURE_MODEL_PROPERTY);
        modelPropertyMapping.put(COLOR_PROPERTY, COLOR_MODEL_PROPERTY);
        modelPropertyMapping.put(SIDE_VISIBILITY_PROPERTY, SIDE_VISIBILITY_MODEL_PROPERTY);
    }

    public static void declareModelProperty(String name, ModelProperty<?> prop) {
        if(modelPropertyMapping.containsKey(name)) { BlockCarpentryMain.LOGGER.error("Tried to declare model property with conflicting name '"+name+"'");}
        else {
            modelPropertyMapping.put(name, prop);
        }

    }

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
            properties.put(SIDE_VISIBILITY_PROPERTY, new SideSetAppearanceProperty(new SixSideSet(EnumSet.allOf(Direction.class)))); //all sides visible by default
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
