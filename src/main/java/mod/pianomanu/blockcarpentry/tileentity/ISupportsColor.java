package mod.pianomanu.blockcarpentry.tileentity;

import mod.pianomanu.blockcarpentry.util.FrameAppearanceData;
import net.minecraft.item.DyeColor;

import static mod.pianomanu.blockcarpentry.util.AppearancePropertyCollection.COLOR_PROPERTY;

public interface ISupportsColor {
    default DyeColor getColor() {
        return (DyeColor) getAppearanceData().getProperty(COLOR_PROPERTY);
    }

    default void setColor(DyeColor col) {
        getAppearanceData().setProperty(COLOR_PROPERTY, col);
        notifySurroundings();
    }

    FrameAppearanceData getAppearanceData();
    void notifySurroundings();
}
