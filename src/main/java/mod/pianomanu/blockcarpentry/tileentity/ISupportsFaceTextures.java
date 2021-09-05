package mod.pianomanu.blockcarpentry.tileentity;

import mod.pianomanu.blockcarpentry.util.AppearancePropertyCollection;
import mod.pianomanu.blockcarpentry.util.FrameAppearanceData;

import static mod.pianomanu.blockcarpentry.util.AppearancePropertyCollection.TEXTURE_PROPERTY;

public interface ISupportsFaceTextures {
    default int getFaceTexture() {
        return (int) getAppearanceData().getProperty(TEXTURE_PROPERTY);
    }

    default void setFaceTexture(int face) {
        getAppearanceData().setProperty(TEXTURE_PROPERTY, face);
        notifySurroundings();
    }

    default void nextFaceTexture() {
        int face = getFaceTexture();
        if(face + 1 >= AppearancePropertyCollection.MAX_FACE_TEXTURES) {
            setFaceTexture(0);
        }
        else {
            setFaceTexture(face+1);
        }
    }

    FrameAppearanceData getAppearanceData();
    void notifySurroundings();
}
