package mod.pianomanu.blockcarpentry.tileentity;

import mod.pianomanu.blockcarpentry.setup.Registration;
import mod.pianomanu.blockcarpentry.util.FrameAppearanceData;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FrameBlockTileEntity  extends TileEntity implements IFrameEntity, ISupportsRotation, ISupportsFaceTextures, ISupportsOverlays{
    private final FrameAppearanceData appearanceData;

    public FrameBlockTileEntity() {
        super(Registration.FRAMEBLOCK_TILE.get());

        appearanceData = new FrameAppearanceData(); //with defaults (mimic, rotation, texture and overlay)
    }

    public void notifySurroundings() {
        markDirty();
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }

    public boolean hasProperty(String name) {
        return appearanceData.hasProperty(name);
    }

    public <T> T getAppearanceProperty(String name) {
        return appearanceData.getProperty(name);
    }

    public <T> void setAppearanceProperty(String name, T value) {
        appearanceData.setProperty(name, value);
        notifySurroundings();
    }

    private static final String APPEARANCE_NBT_NAME = "appearance";

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        tag.put(APPEARANCE_NBT_NAME, appearanceData.toNBT());
        return tag;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT tag = pkt.getNbtCompound();
        if(tag.contains(APPEARANCE_NBT_NAME)) {
            boolean changed = appearanceData.fromNBT(tag.getCompound(APPEARANCE_NBT_NAME));
            if(changed) {
                notifySurroundings();
                ModelDataManager.requestModelDataRefresh(this);
            }
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        if(tag.contains(APPEARANCE_NBT_NAME)) {
            appearanceData.fromNBT(tag.getCompound(APPEARANCE_NBT_NAME));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.put(APPEARANCE_NBT_NAME, appearanceData.toNBT());
        return super.write(tag);
    }

    public void resetAppearance() {
        appearanceData.reset();
        notifySurroundings();
    }

    @Override
    public FrameAppearanceData getAppearanceData() {
        return appearanceData;
    }

    /* THESE COME FROM THE ORIGINAL FRAMEBLOCKTILE
    public void setVisibileSides(Direction dir, boolean isVisible) {
        switch (dir) {
            case DOWN:
                downVisible = isVisible;
                break;
            case UP:
                upVisible = isVisible;
                break;
            case NORTH:
                northVisible = isVisible;
                break;
            case WEST:
                westVisible = isVisible;
                break;
            case SOUTH:
                southVisible = isVisible;
                break;
            case EAST:
                eastVisible = isVisible;
                break;
            default:
                break;
        }
    }

    public List<Direction> getVisibleSides() {
        List<Direction> dir = new ArrayList<>();
        if (northVisible)
            dir.add(Direction.NORTH);
        if (eastVisible)
            dir.add(Direction.EAST);
        if (southVisible)
            dir.add(Direction.SOUTH);
        if (westVisible)
            dir.add(Direction.WEST);
        if (upVisible)
            dir.add(Direction.UP);
        if (downVisible)
            dir.add(Direction.DOWN);
        return dir;
    }*/
}
