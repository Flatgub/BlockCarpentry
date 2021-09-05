package mod.pianomanu.blockcarpentry.tileentity;

import mod.pianomanu.blockcarpentry.util.FrameAppearanceData;

import static mod.pianomanu.blockcarpentry.util.AppearencePropertyCollection.DESIGN_PROPERTY;
import static mod.pianomanu.blockcarpentry.util.AppearencePropertyCollection.DESIGN_TEXTURE_PROPERTY;

public interface ISupportsTexturedDesigns {
    int getMaxDesigns();
    int getMaxDesignTextures();

    default int getDesign() {
        return (int) getAppearanceData().getProperty(DESIGN_PROPERTY);
    }

    default void setDesign(int face) {
        getAppearanceData().setProperty(DESIGN_PROPERTY, face);
    }

    default void nextDesign() {
        int design = getDesign();
        if(design + 1 >= getMaxDesigns()) {
            setDesign(0);
        }
        else {
            setDesign(design+1);
        }
    }

    default int getDesignTexture() {
        return (int) getAppearanceData().getProperty(DESIGN_TEXTURE_PROPERTY);
    }

    default void setDesignTexture(int face) {
        getAppearanceData().setProperty(DESIGN_PROPERTY, face);
    }

    default void nextDesignTexture() {
        int tex = getDesignTexture();
        if(tex + 1 >= getMaxDesignTextures()) {
            setDesignTexture(0);
        }
        else {
            setDesignTexture(tex+1);
        }
    }

    FrameAppearanceData getAppearanceData();
}
