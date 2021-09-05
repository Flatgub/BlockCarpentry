package mod.pianomanu.blockcarpentry.tileentity;

import mod.pianomanu.blockcarpentry.util.FrameAppearanceData;

import static mod.pianomanu.blockcarpentry.util.AppearancePropertyCollection.DESIGN_PROPERTY;

public interface ISupportsDesigns {
    int getMaxDesigns();

    default int getDesign() {
        return (int) getAppearanceData().getProperty(DESIGN_PROPERTY);
    }

    default void setDesign(int face) {
        getAppearanceData().setProperty(DESIGN_PROPERTY, face);
        notifySurroundings();
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

    FrameAppearanceData getAppearanceData();
    void notifySurroundings();
}
