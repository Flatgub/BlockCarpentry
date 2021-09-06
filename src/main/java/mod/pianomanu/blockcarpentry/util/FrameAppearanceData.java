package mod.pianomanu.blockcarpentry.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

import java.util.HashMap;
import java.util.Map;

import static mod.pianomanu.blockcarpentry.util.AppearancePropertyCollection.*;

public class FrameAppearanceData {
    public static final String APPEARANCE_NBT_NAME = "appearance";
    private HashMap<String, AppearanceProperty<?>> properties;

    public FrameAppearanceData() {
        properties = AppearancePropertyCollection.newCollection().withDefaults().get();
    }

    public FrameAppearanceData(HashMap<String, AppearanceProperty<?>> collection) {
        properties = collection;
    }

    public boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String name) {
        return (T) properties.get(name).getValue();
    }

    @SuppressWarnings("unchecked")
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

    /**
     * Attempts to fill the properties of the FrameAppearanceData using the previous style of NBT structure
     * Although manually listing only a hand full of common properties here means that we may lose some of the
     * appearance data from previous saves, recovering the most important data is better than recovering none of it.
     * @param in CompoundNBT data created using a pianomanu style write() method
     */
    public void fromLegacyNBT(CompoundNBT in) {
        if (in.contains("mimic")) {
            setProperty(MIMIC_PROPERTY, NBTUtil.readBlockState(in.getCompound("mimic")));
        }
        if (in.contains("texture") && hasProperty(TEXTURE_PROPERTY)) {
            setProperty(TEXTURE_PROPERTY,in.getCompound("texture").getInt("number"));
        }
        if (in.contains("design") && hasProperty(DESIGN_PROPERTY)) {
            setProperty(DESIGN_PROPERTY, in.getCompound("design").getInt("number"));
        }
        if (in.contains("overlay") && hasProperty(OVERLAY_PROPERTY)) {
            setProperty(OVERLAY_PROPERTY, in.getCompound("overlay").getInt("number"));
        }
        if (in.contains("design_texture") && hasProperty(DESIGN_TEXTURE_PROPERTY)) {
            setProperty(DESIGN_TEXTURE_PROPERTY, in.getCompound("design_texture").getInt("number"));
        }
        if (in.contains("glass_color") && hasProperty(COLOR_PROPERTY)) {
            setProperty(COLOR_PROPERTY, in.getCompound("glass_color").getInt("number"));
        }
        if (in.contains("rotation") && hasProperty(ROTATION_PROPERTY)) {
            setProperty(ROTATION_PROPERTY, in.getCompound("rotation").getInt("number"));
        }
    }

    public IModelData toModelData() {
        ModelDataMap.Builder builder = new ModelDataMap.Builder();
        for(Map.Entry<String, AppearanceProperty<?>> property: properties.entrySet()) {
            String name = property.getKey();
            property.getValue().addToBuilder(builder, name);
        }
        return builder.build();
    }
}
