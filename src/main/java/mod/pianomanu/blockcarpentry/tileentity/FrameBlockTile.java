package mod.pianomanu.blockcarpentry.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static mod.pianomanu.blockcarpentry.setup.Registration.FRAMEBLOCK_TILE;

/**
 * BlockEntity for {@link mod.pianomanu.blockcarpentry.block.FrameBlock} and all sorts of frame blocks
 * Contains all information about the block and the mimicked block
 *
 * @author PianoManu
 * @version 1.0 08/15/21
 */
public class FrameBlockTile extends BlockEntity {
    public static final ModelProperty<BlockState> MIMIC = new ModelProperty<>();
    public static final ModelProperty<Integer> TEXTURE = new ModelProperty<>();
    public static final ModelProperty<Integer> DESIGN = new ModelProperty<>();
    public static final ModelProperty<Integer> DESIGN_TEXTURE = new ModelProperty<>();
    //currently only for doors and trapdoors
    public static final ModelProperty<Integer> GLASS_COLOR = new ModelProperty<>();
    public static final ModelProperty<Integer> OVERLAY = new ModelProperty<>();
    public static final ModelProperty<Integer> ROTATION = new ModelProperty<>();
    public static final ModelProperty<Boolean> NORTH_VISIBLE = new ModelProperty<>();
    public static final ModelProperty<Boolean> EAST_VISIBLE = new ModelProperty<>();
    public static final ModelProperty<Boolean> SOUTH_VISIBLE = new ModelProperty<>();
    public static final ModelProperty<Boolean> WEST_VISIBLE = new ModelProperty<>();
    public static final ModelProperty<Boolean> UP_VISIBLE = new ModelProperty<>();
    public static final ModelProperty<Boolean> DOWN_VISIBLE = new ModelProperty<>();

    public final int maxTextures = 8;
    public final int maxDesignTextures = 4;
    public final int maxDesigns = 4;

    private BlockState mimic;
    private Integer texture = 0;
    private Integer design = 0;
    private Integer designTexture = 0;
    private Integer glassColor = 0;
    private Integer overlay = 0;
    private Integer rotation = 0;

    private Boolean northVisible = true;
    private Boolean eastVisible = true;
    private Boolean southVisible = true;
    private Boolean westVisible = true;
    private Boolean upVisible = true;
    private Boolean downVisible = true;

    private static final Logger LOGGER = LogManager.getLogger();

    public FrameBlockTile(BlockPos pos, BlockState state) {
        super(FRAMEBLOCK_TILE.get(), pos, state);
    }

    private static Integer readInteger(CompoundTag tag) {
        if (!tag.contains("number", 8)) {
            return 0;
        } else {
            try {
                return Integer.parseInt(tag.getString("number"));
            } catch (NumberFormatException e) {
                LOGGER.error("Not a valid Number Format: " + tag.getString("number"));
                return 0;
            }
        }
    }

    public BlockState getMimic() {
        return this.mimic;
    }

    public Integer getDesign() {
        return this.design;
    }

    private static CompoundTag writeInteger(Integer tag) {
        CompoundTag compoundnbt = new CompoundTag();
        compoundnbt.putString("number", tag.toString());
        return compoundnbt;
    }

    public Integer getDesignTexture() {
        return this.designTexture;
    }

    public void setMimic(BlockState mimic) {
        this.mimic = mimic;
        setChanged();
        level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }

    public Integer getTexture() {
        return this.texture;
    }

    public void setDesign(Integer design) {
        this.design = design;
        setChanged();
        level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }

    public Integer getGlassColor() {
        return this.glassColor;
    }

    public void setDesignTexture(Integer designTexture) {
        this.designTexture = designTexture;
        setChanged();
        level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }

    public Integer getRotation() {
        return rotation;
    }

    public void setTexture(Integer texture) {
        this.texture = texture;
        setChanged();
        level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }

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
    }

    public void setGlassColor(Integer colorNumber) {
        this.glassColor = colorNumber;
        setChanged();
        level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }

    public Integer getOverlay() {
        return this.overlay;
    }

    public void setRotation(Integer rotation) {
        this.rotation = rotation;
        setChanged();
        level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }

    public void setOverlay(Integer overlay) {
        this.overlay = overlay;
        setChanged();
        level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 1, getUpdateTag());
    }

    //TODO
    /*@Override
    public void onDataPacket(NetworkManager net, SUpdateBlockEntityPacket pkt) {
        BlockState oldMimic = mimic;
        Integer oldTexture = texture;
        Integer oldDesign = design;
        Integer oldDesignTexture = designTexture;
        Integer oldGlassColor = glassColor;
        Integer oldOverlay = overlay;
        Integer oldRotation = rotation;
        CompoundTag tag = pkt.getNbtCompound();
        if (tag.contains("mimic")) {
            mimic = NbtUtils.readBlockState(tag.getCompound("mimic"));
            if (!Objects.equals(oldMimic, mimic)) {
                ModelDataManager.requestModelDataRefresh(this);
                level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
            }
        }
        if (tag.contains("texture")) {
            texture = readInteger(tag.getCompound("texture"));
            if (!Objects.equals(oldTexture, texture)) {
                ModelDataManager.requestModelDataRefresh(this);
                level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
            }
        }
        if (tag.contains("design")) {
            design = readInteger(tag.getCompound("design"));
            if (!Objects.equals(oldDesign, design)) {
                ModelDataManager.requestModelDataRefresh(this);
                level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
            }
        }
        if (tag.contains("design_texture")) {
            designTexture = readInteger(tag.getCompound("design_texture"));
            if (!Objects.equals(oldDesignTexture, designTexture)) {
                ModelDataManager.requestModelDataRefresh(this);
                level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
            }
        }
        if (tag.contains("glass_color")) {
            glassColor = readInteger(tag.getCompound("glass_color"));
            if (!Objects.equals(oldGlassColor, glassColor)) {
                ModelDataManager.requestModelDataRefresh(this);
                level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
            }
        }
        if (tag.contains("overlay")) {
            overlay = readInteger(tag.getCompound("overlay"));
            if (!Objects.equals(oldOverlay, overlay)) {
                ModelDataManager.requestModelDataRefresh(this);
                level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
            }
        }
        if (tag.contains("rotation")) {
            rotation = readInteger(tag.getCompound("rotation"));
            if (!Objects.equals(oldRotation, rotation)) {
                ModelDataManager.requestModelDataRefresh(this);
                level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
            }
        }
    }*/

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder()
                .withInitial(MIMIC, mimic)
                .withInitial(TEXTURE, texture)
                .withInitial(DESIGN, design)
                .withInitial(DESIGN_TEXTURE, designTexture)
                .withInitial(GLASS_COLOR, glassColor)
                .withInitial(OVERLAY, overlay)
                .withInitial(ROTATION, rotation)
                .withInitial(NORTH_VISIBLE, northVisible)
                .withInitial(EAST_VISIBLE, eastVisible)
                .withInitial(SOUTH_VISIBLE, southVisible)
                .withInitial(WEST_VISIBLE, westVisible)
                .withInitial(UP_VISIBLE, upVisible)
                .withInitial(DOWN_VISIBLE, downVisible)
                .build();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        if (mimic != null) {
            tag.put("mimic", NbtUtils.writeBlockState(mimic));
        }
        if (texture != null) {
            tag.put("texture", writeInteger(texture));
        }
        if (design != null) {
            tag.put("design", writeInteger(design));
        }
        if (designTexture != null) {
            tag.put("design_texture", writeInteger(designTexture));
        }
        if (glassColor != null) {
            tag.put("glass_color", writeInteger(glassColor));
        }
        if (overlay != null) {
            tag.put("overlay", writeInteger(overlay));
        }
        if (rotation != null) {
            tag.put("rotation", writeInteger(rotation));
        }
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("mimic")) {
            mimic = NbtUtils.readBlockState(tag.getCompound("mimic"));
        }
        if (tag.contains("texture")) {
            texture = readInteger(tag.getCompound("texture"));
        }
        if (tag.contains("design")) {
            design = readInteger(tag.getCompound("design"));
        }
        if (tag.contains("design_texture")) {
            designTexture = readInteger(tag.getCompound("design_texture"));
        }
        if (tag.contains("glass_color")) {
            glassColor = readInteger(tag.getCompound("glass_color"));
        }
        if (tag.contains("overlay")) {
            overlay = readInteger(tag.getCompound("overlay"));
        }
        if (tag.contains("rotation")) {
            rotation = readInteger(tag.getCompound("rotation"));
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        if (mimic != null) {
            tag.put("mimic", NbtUtils.writeBlockState(mimic));
        }
        if (texture != null) {
            tag.put("texture", writeInteger(texture));
        }
        if (design != null) {
            tag.put("design", writeInteger(design));
        }
        if (designTexture != null) {
            tag.put("design_texture", writeInteger(designTexture));
        }
        if (glassColor != null) {
            tag.put("glass_color", writeInteger(glassColor));
        }
        if (overlay != null) {
            tag.put("overlay", writeInteger(overlay));
        }
        if (rotation != null) {
            tag.put("rotation", writeInteger(rotation));
        }
        return super.save(tag);
    }

    public void clear() {
        this.setMimic(null);
        this.setDesign(0);
        this.setDesign(0);
        this.setDesign(0);
        this.setGlassColor(0);
        this.setOverlay(0);
        this.setRotation(0);
    }
}
//========SOLI DEO GLORIA========//