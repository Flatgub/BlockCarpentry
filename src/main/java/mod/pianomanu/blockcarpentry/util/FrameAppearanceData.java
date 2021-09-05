package mod.pianomanu.blockcarpentry.util;

import java.util.HashMap;
import java.util.Map;

public class FrameAppearanceData {
    private HashMap<String, AppearanceProperty<?>> properties;

    public FrameAppearanceData() {
        properties = AppearencePropertyCollection.newCollection().withDefaults().get();
    }

    public FrameAppearanceData(HashMap<String, AppearanceProperty<?>> collection) {
        properties = collection;
    }

    public boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

    public Integer getIntegerProperty(String name) {
        IntegerAppearanceProperty prop = (IntegerAppearanceProperty) properties.get(name);
        return prop.getValue();
    }

    public void setIntegerProperty(String name,Integer value) {
        IntegerAppearanceProperty prop = (IntegerAppearanceProperty) properties.get(name);
        prop.setValue(value);
    }

    public void clear() {
        for(Map.Entry<String, AppearanceProperty<?>> entry: properties.entrySet()) {
            entry.getValue().reset();
        }
    }

    //private AppearanceProperty<BlockState> mimicProperty = new AppearanceProperty<>();
    //private AppearanceProperty<Integer> designProperty = new AppearanceProperty<>(0);
    //public static final ModelProperty<BlockState> MIMIC = new ModelProperty<>();
    //public static final ModelProperty<Integer> TEXTURE = new ModelProperty<>();
    //public static final ModelProperty<Integer> DESIGN = new ModelProperty<>();
    //public static final ModelProperty<Integer> DESIGN_TEXTURE = new ModelProperty<>();
    //currently only for doors and trapdoors
    //public static final ModelProperty<Integer> GLASS_COLOR = new ModelProperty<>();
    //public static final ModelProperty<Integer> OVERLAY = new ModelProperty<>();
    //public static final ModelProperty<Integer> ROTATION = new ModelProperty<>();
    //public static final ModelProperty<Boolean> NORTH_VISIBLE = new ModelProperty<>();
    //public static final ModelProperty<Boolean> EAST_VISIBLE = new ModelProperty<>();
    //public static final ModelProperty<Boolean> SOUTH_VISIBLE = new ModelProperty<>();
    //public static final ModelProperty<Boolean> WEST_VISIBLE = new ModelProperty<>();
    //public static final ModelProperty<Boolean> UP_VISIBLE = new ModelProperty<>();
    //public static final ModelProperty<Boolean> DOWN_VISIBLE = new ModelProperty<>();

    //private BlockState mimic;
    //private Integer texture = 0;
    //private Integer design = 0;
    //private Integer designTexture = 0;
    //private Integer glassColor = 0;
    //private Integer overlay = 0;
    //private Integer rotation = 0;



}
