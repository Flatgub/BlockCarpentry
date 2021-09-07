package mod.pianomanu.blockcarpentry.tileentity;

import mod.pianomanu.blockcarpentry.util.*;
import net.minecraft.block.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static mod.pianomanu.blockcarpentry.setup.Registration.BED_FRAME_TILE;
import static mod.pianomanu.blockcarpentry.util.AppearancePropertyCollection.*;
import static mod.pianomanu.blockcarpentry.util.FrameAppearanceData.APPEARANCE_NBT_NAME;

/**
 * TileEntity for frame beds, you can customize both pillow and blanket
 *
 * @author PianoManu
 * @version 1.2 05/01/21
 */
public class BedFrameTile extends TileEntity implements IFrameEntity, ISupportsTexturedDesigns, ISupportsRotation, ISupportsFaceTextures {

    //custom appearence properties
    public static final String PILLOW_COLOR_PROPERTY = "pillow_color";
    public static final String BLANKET_COLOR_PROPERTY = "blanket_color";
    public static final ModelProperty<DyeColor> PILLOW_COLOR_MODEL_PROPERTY = new ModelProperty<>();
    public static final ModelProperty<DyeColor> BLANKET_COLOR_MODEL_PROPERTY = new ModelProperty<>();

    static {
        AppearancePropertyCollection.declareModelProperty(PILLOW_COLOR_PROPERTY, PILLOW_COLOR_MODEL_PROPERTY);
        AppearancePropertyCollection.declareModelProperty(BLANKET_COLOR_PROPERTY, BLANKET_COLOR_MODEL_PROPERTY);
    }

    public final int maxTextures = 6;
    public final int maxDesigns = 5;

    private final FrameAppearanceData appearanceData;

    public BedFrameTile() {
        super(BED_FRAME_TILE.get());

        appearanceData = new FrameAppearanceData(
                AppearancePropertyCollection.newCollection()
                        .withDefaults() //mimic, rotation, face texture, overlay, side visibility
                        .without(OVERLAY_PROPERTY) //beds don't support overlays
                        .with(DESIGN_PROPERTY, new IntegerAppearanceProperty(0))
                        .with(DESIGN_TEXTURE_PROPERTY, new IntegerAppearanceProperty(0))
                        .with(PILLOW_COLOR_PROPERTY, new DyeColorAppearanceProperty(DyeColor.WHITE))
                        .with(BLANKET_COLOR_PROPERTY, new DyeColorAppearanceProperty(DyeColor.WHITE))
                        .get());
    }


    @Override
    public FrameAppearanceData getAppearanceData() {
        return appearanceData;
    }

    @Override
    public void notifySurroundings() {
        markDirty();
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }

    @Override
    public void resetAppearance() {
        appearanceData.reset();
    }

    public DyeColor getPillowColor() {
        return appearanceData.getProperty(PILLOW_COLOR_PROPERTY);
    }

    public void setPillowColor(DyeColor pillowColor) {
        appearanceData.setProperty(PILLOW_COLOR_PROPERTY, pillowColor);
        notifySurroundings();

    }

    public DyeColor getBlanketColor() {
        return appearanceData.getProperty(BLANKET_COLOR_PROPERTY);
    }

    public void setBlanketColor(Integer blanketColor) {
        appearanceData.setProperty(PILLOW_COLOR_PROPERTY, blanketColor);
        notifySurroundings();
    }

    public int getMaxDesigns() {return this.maxDesigns;}

    @Override
    public int getMaxDesignTextures() { return maxTextures;}


    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        tag.put(FrameAppearanceData.APPEARANCE_NBT_NAME, appearanceData.toNBT());
        return tag;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT tag = pkt.getNbtCompound();
        if(tag.contains(FrameAppearanceData.APPEARANCE_NBT_NAME)) {
            boolean changed = appearanceData.fromNBT(tag.getCompound(FrameAppearanceData.APPEARANCE_NBT_NAME));
            if(changed) {
                notifySurroundings();
                ModelDataManager.requestModelDataRefresh(this);
            }
        }
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return appearanceData.toModelData();
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        if(tag.contains(FrameAppearanceData.APPEARANCE_NBT_NAME)) {
            appearanceData.fromNBT(tag.getCompound(FrameAppearanceData.APPEARANCE_NBT_NAME));
        }
        else {
            //this doesn't preserve pillow or blanket colour, but is better than nothing
            appearanceData.fromLegacyNBT(tag);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.put(APPEARANCE_NBT_NAME, appearanceData.toNBT());
        return super.write(tag);
    }

}
