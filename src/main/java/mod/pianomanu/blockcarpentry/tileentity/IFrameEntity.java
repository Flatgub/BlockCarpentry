package mod.pianomanu.blockcarpentry.tileentity;

import net.minecraft.block.BlockState;

//TODO: properly migrate generic FrameBlockTile tile methods to here
public interface IFrameEntity {
    void clear();
    void setMimic(BlockState mimic);
    int getTexture();
    void setTexture(int texture);
}
