package mod.pianomanu.blockcarpentry.tileentity;

import mcp.MethodsReturnNonnullByDefault;
import mod.pianomanu.blockcarpentry.util.FrameAppearanceData;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class FrameBlockTileEntity  extends TileEntity{
    private FrameAppearanceData appearanceData;

    public FrameBlockTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn); //replace this with a reference to registration type

        appearanceData = new FrameAppearanceData(); //with defaults (mimic, rotation, texture and overlay)
    }

    protected void notifySurrounding() {
        markDirty();
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }

    protected boolean hasProperty(String name) {
        return appearanceData.hasProperty(name);
    }

    protected <T> T getAppearanceProperty(String name) {
        return appearanceData.getProperty(name);
    }

    protected <T> void setAppearanceProperty(String name, T value) {
        appearanceData.setProperty(name, value);
        notifySurrounding();
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
                notifySurrounding();
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
    }
}
