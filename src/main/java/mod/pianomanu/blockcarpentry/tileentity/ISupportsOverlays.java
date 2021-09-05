package mod.pianomanu.blockcarpentry.tileentity;

import mod.pianomanu.blockcarpentry.util.FrameAppearanceData;
import static mod.pianomanu.blockcarpentry.util.AppearancePropertyCollection.OVERLAY_PROPERTY;

public interface ISupportsOverlays {
    default int getOverlay() {
        return (int) getAppearanceData().getProperty(OVERLAY_PROPERTY);
    }

    default void setOverlay(int overlay) {
        getAppearanceData().setProperty(OVERLAY_PROPERTY, overlay);
        notifySurroundings();
    }

    FrameAppearanceData getAppearanceData();
    void notifySurroundings();
}
