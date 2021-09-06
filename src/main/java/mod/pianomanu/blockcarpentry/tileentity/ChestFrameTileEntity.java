package mod.pianomanu.blockcarpentry.tileentity;

import mod.pianomanu.blockcarpentry.block.ChestFrameBlock;
import mod.pianomanu.blockcarpentry.container.ChestFrameContainer;
import mod.pianomanu.blockcarpentry.setup.Registration;
import mod.pianomanu.blockcarpentry.util.AppearancePropertyCollection;
import mod.pianomanu.blockcarpentry.util.DyeColorAppearanceProperty;
import mod.pianomanu.blockcarpentry.util.FrameAppearanceData;
import mod.pianomanu.blockcarpentry.util.IntegerAppearanceProperty;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static mod.pianomanu.blockcarpentry.util.AppearancePropertyCollection.*;
import static mod.pianomanu.blockcarpentry.util.FrameAppearanceData.APPEARANCE_NBT_NAME;

/**
 * TileEntity for {@link mod.pianomanu.blockcarpentry.block.ChestFrameBlock} and all sorts of frame/illusion chest blocks
 * Contains all information about the block and the mimicked block, as well as the inventory size and stored items
 * @author PianoManu
 * @version 1.2 05/01/21
 */
public class ChestFrameTileEntity extends ChestTileEntity implements IFrameEntity, ISupportsFaceTextures, ISupportsRotation, ISupportsTexturedDesigns {

    //======================================= FRAME STUFF =======================================//

    public final int maxDesignTextures = 4;
    public final int maxDesigns = 4;

    private final FrameAppearanceData appearanceData;

    public ChestFrameTileEntity(TileEntityType<?> typeIn) {
        super(typeIn);

        appearanceData = new FrameAppearanceData(
                AppearancePropertyCollection.newCollection()
                        .withDefaults() //mimic, rotation, face texture, overlay, side visibility
                        .without(OVERLAY_PROPERTY) //chests don't support overlays
                        .with(DESIGN_PROPERTY, new IntegerAppearanceProperty(0))
                        .with(DESIGN_TEXTURE_PROPERTY, new IntegerAppearanceProperty(0))
                        .with(COLOR_PROPERTY, new DyeColorAppearanceProperty(null)) //null is no colour
                        .get());
    }

    public ChestFrameTileEntity() {
        this(Registration.CHEST_FRAME_TILE.get());
    }

    @Override
    public void notifySurroundings() {
        markDirty();
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put(APPEARANCE_NBT_NAME, appearanceData.toNBT());
        super.write(compound);
        if (!this.checkLootAndWrite(compound)) {
            ItemStackHelper.saveAllItems(compound, this.chestContents);
        }
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        //FRAME BEGIN
        if(compound.contains(APPEARANCE_NBT_NAME)) {
            appearanceData.fromNBT(compound.getCompound(APPEARANCE_NBT_NAME));
        }
        else {
            appearanceData.fromLegacyNBT(compound);
        }
        //FRAME END
        this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (!this.checkLootAndRead(compound)) {
            ItemStackHelper.loadAllItems(compound, this.chestContents);
        }
    }

    public int getMaxDesigns() {return this.maxDesigns;}

    public int getMaxDesignTextures() {return this.maxDesignTextures; }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        tag.put(APPEARANCE_NBT_NAME, appearanceData.toNBT());
        return tag;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
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
    public void resetAppearance() {
        appearanceData.reset();
    }

    @Override
    public FrameAppearanceData getAppearanceData() {
        return appearanceData;
    }

    @Override
    public void remove() {
        super.remove();
        if(itemHandler != null) {
            itemHandler.invalidate();
        }
    }

    //======================================= CHEST STUFF =======================================//

    //<editor-fold desc="ChestTileEntity related methods and fields">
    private NonNullList<ItemStack> chestContents = NonNullList.withSize(27, ItemStack.EMPTY);
    /**
     * The number of players currently using this chest
     */
    protected int numPlayersUsing;
    private IItemHandlerModifiable items = createHandler();
    private LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> items);

    public static void swapContents(ChestFrameTileEntity te, ChestFrameTileEntity otherTe) {
        NonNullList<ItemStack> list = te.getItems();
        te.setItems(otherTe.getItems());
        otherTe.setItems(list);
    }

    @Override
    public int getSizeInventory() {
        return 27;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.frame_chest");
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.chestContents;
    }

    @Override
    public void setItems(NonNullList<ItemStack> itemsIn) {
        this.chestContents = itemsIn;
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new ChestFrameContainer(id, player, this);
    }

    private void playSound(SoundEvent sound) {
        double dx = (double) this.pos.getX() + 0.5D;
        double dy = (double) this.pos.getY() + 0.5D;
        double dz = (double) this.pos.getZ() + 0.5D;
        this.world.playSound((PlayerEntity) null, dx, dy, dz, sound, SoundCategory.BLOCKS, 0.5f,
                this.world.rand.nextFloat() * 0.1f + 0.9f);
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            this.numPlayersUsing = type;
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }

            ++this.numPlayersUsing;
            this.onOpenOrClose();
        }
    }

    public static int getPlayersUsing(IBlockReader reader, BlockPos pos) {
        BlockState blockstate = reader.getBlockState(pos);
        if (blockstate.hasTileEntity()) {
            TileEntity tileentity = reader.getTileEntity(pos);
            if (tileentity instanceof ChestFrameTileEntity) {
                return ((ChestFrameTileEntity) tileentity).numPlayersUsing;
            }
        }
        return 0;
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.numPlayersUsing;
            this.onOpenOrClose();
        }
    }

    protected void onOpenOrClose() {
        Block block = this.getBlockState().getBlock();
        if (block instanceof ChestFrameBlock) {
            this.world.addBlockEvent(this.pos, block, 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, block);
        }
    }

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        if (this.itemHandler != null) {
            this.itemHandler.invalidate();
            this.itemHandler = null;
        }
    }

    private IItemHandlerModifiable createHandler() {
        return new InvWrapper(this);
    }
    //</editor-fold>
}
