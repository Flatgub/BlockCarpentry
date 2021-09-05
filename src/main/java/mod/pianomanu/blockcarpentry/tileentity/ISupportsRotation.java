package mod.pianomanu.blockcarpentry.tileentity;

import mod.pianomanu.blockcarpentry.util.AppearencePropertyCollection;
import mod.pianomanu.blockcarpentry.util.FrameAppearanceData;
import static mod.pianomanu.blockcarpentry.util.AppearencePropertyCollection.ROTATION_PROPERTY;

public interface ISupportsRotation {
    default int getRotation() {
        return (int) getAppearanceData().getProperty(ROTATION_PROPERTY);
    }

    default void setRotation(int rotation) {
        getAppearanceData().setProperty(ROTATION_PROPERTY, rotation);
    }

    default void nextRotation() {
        int rot = getRotation();
        if(rot + 1 >= AppearencePropertyCollection.MAX_ROTATIONS) {
            setRotation(0);
        }
        else {
            setRotation(rot+1);
        }
    }

    FrameAppearanceData getAppearanceData();
}
