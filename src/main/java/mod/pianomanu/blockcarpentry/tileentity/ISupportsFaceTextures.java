package mod.pianomanu.blockcarpentry.tileentity;

import mod.pianomanu.blockcarpentry.util.AppearencePropertyCollection;
import mod.pianomanu.blockcarpentry.util.FrameAppearanceData;

import static mod.pianomanu.blockcarpentry.util.AppearencePropertyCollection.TEXTURE_PROPERTY;

public interface ISupportsFaceTextures {
    default int getFaceTexture() {
        return (int) getAppearanceData().getProperty(TEXTURE_PROPERTY);
    }

    default void setFaceTexture(int face) {
        getAppearanceData().setProperty(TEXTURE_PROPERTY, face);
    }

    default void nextFaceTexture() {
        int face = getFaceTexture();
        if(face + 1 >= AppearencePropertyCollection.MAX_FACE_TEXTURES) {
            setFaceTexture(0);
        }
        else {
            setFaceTexture(face+1);
        }
    }

    FrameAppearanceData getAppearanceData();
}
