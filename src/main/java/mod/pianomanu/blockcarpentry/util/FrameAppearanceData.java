package mod.pianomanu.blockcarpentry.util;

import net.minecraft.nbt.CompoundNBT;

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

    public <T> T getProperty(String name) {
        return (T) properties.get(name).getValue();
    }

    public <T> void setProperty(String name, T value) {
        AppearanceProperty<T> prop = (AppearanceProperty<T>) properties.get(name);
        prop.setValue(value);
    }

    public void reset() {
        for(Map.Entry<String, AppearanceProperty<?>> entry: properties.entrySet()) {
            entry.getValue().reset();
        }
    }

    public CompoundNBT toNBT() {
        CompoundNBT tag = new CompoundNBT();
        for(Map.Entry<String, AppearanceProperty<?>> entry: properties.entrySet()) {
            tag.put(entry.getKey(), entry.getValue().toNewNBT());
        }
        return tag;
    }

    public boolean fromNBT(CompoundNBT in) {
        boolean changed = false;
        for(Map.Entry<String, AppearanceProperty<?>> entry: properties.entrySet()) {
            AppearanceProperty<?> prop = entry.getValue();
            if(in.contains(entry.getKey())) {
                if(prop.fromNBT(in.getCompound(entry.getKey()))) {
                    changed = true;
                }
            }
        }
        return changed;
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
