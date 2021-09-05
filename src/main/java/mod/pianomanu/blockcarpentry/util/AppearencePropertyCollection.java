package mod.pianomanu.blockcarpentry.util;

import net.minecraft.block.BlockState;

import java.util.HashMap;

public class AppearencePropertyCollection {

    public static final String MIMIC_PROPERTY = "mimic";
    public static final String TEXTURE_PROPERTY = "texture";
    public static final String ROTATION_PROPERTY = "rotation";

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
            properties.put(MIMIC_PROPERTY, new AppearanceProperty<BlockState>(null));
            properties.put(TEXTURE_PROPERTY, new IntegerAppearanceProperty(0));
            properties.put(ROTATION_PROPERTY, new IntegerAppearanceProperty(0));
            return this;
        }

        public unfinishedPropertyCollection with(String name, AppearanceProperty<?> prop) {
            properties.put(name, prop);
            return this;
        }

        public HashMap<String, AppearanceProperty<?>> get() {
            return properties;
        }
    }
}
