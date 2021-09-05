package mod.pianomanu.blockcarpentry.tileentity;

import mod.pianomanu.blockcarpentry.util.FrameAppearanceData;
import mod.pianomanu.blockcarpentry.util.SixSideSet;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

import static mod.pianomanu.blockcarpentry.util.AppearancePropertyCollection.MIMIC_PROPERTY;
import static mod.pianomanu.blockcarpentry.util.AppearancePropertyCollection.SIDE_VISIBILITY_PROPERTY;

//TODO: properly migrate generic FrameBlockTile tile methods to here
public interface IFrameEntity {
    default <T> T getAppearanceProperty(String name) {
        return getAppearanceData().getProperty(name);
    }

    default <T> void setAppearanceProperty(String name, T value) {
        getAppearanceData().setProperty(name, value);
        notifySurroundings();
    }

    default void setMimic(BlockState mimic) {
        setAppearanceProperty(MIMIC_PROPERTY, mimic);
    };

    default BlockState getMimic() {
        return getAppearanceProperty(MIMIC_PROPERTY);
    };

    default void setVisibleSide(Direction side, boolean isVisible) {
        SixSideSet set = getAppearanceData().getProperty(SIDE_VISIBILITY_PROPERTY);
        set.set(side, isVisible);
    }

    FrameAppearanceData getAppearanceData();
    void notifySurroundings();
    void resetAppearance();
}
