package mod.pianomanu.blockcarpentry.tileentity;

import mod.pianomanu.blockcarpentry.util.FrameAppearanceData;

import static mod.pianomanu.blockcarpentry.util.AppearancePropertyCollection.DESIGN_PROPERTY;
import static mod.pianomanu.blockcarpentry.util.AppearancePropertyCollection.DESIGN_TEXTURE_PROPERTY;

public interface ISupportsTexturedDesigns extends ISupportsDesigns {
    int getMaxDesignTextures();

    default int getDesignTexture() {
        return (int) getAppearanceData().getProperty(DESIGN_TEXTURE_PROPERTY);
    }

    default void setDesignTexture(int face) {
        getAppearanceData().setProperty(DESIGN_PROPERTY, face);
        notifySurroundings();
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
    void notifySurroundings();
}
