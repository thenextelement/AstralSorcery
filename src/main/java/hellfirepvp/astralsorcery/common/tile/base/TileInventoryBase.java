/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileInventoryBase
 * Created by HellFirePvP
 * Date: 27.03.2017 / 17:53
 */
public class TileInventoryBase extends TileEntityTick implements ItemInventory.Owner {

    protected int inventorySize;
    private ItemHandlerTile handle;
    private List<ForgeDirection> applicableSides;

    public TileInventoryBase() {
        this(0);
    }

    public TileInventoryBase(int inventorySize) {
        this(inventorySize, ForgeDirection.values());
    }

    public TileInventoryBase(int inventorySize, ForgeDirection... applicableSides) {
        this.inventorySize = inventorySize;
        this.handle = createNewItemHandler();
        this.applicableSides = Arrays.asList(applicableSides);
    }

    protected ItemHandlerTile createNewItemHandler() {
        return new ItemHandlerTile(this);
    }

    @Override
    protected void onFirstTick() {}

    public ItemHandlerTile getInventoryHandler() {
        return handle;
    }

    private boolean hasHandlerForSide(ForgeDirection facing) {
        return facing == null || applicableSides.contains(facing);
    }

    @Override
    public boolean canAccessFromSide(int side) {
        return hasHandlerForSide(ForgeDirection.getOrientation(side));
    }

    // @Override
    // public boolean hasCapability(Capability<?> capability, ForgeDirection facing) {
    // return hasHandlerForSide(facing) ? capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY :
    // super.hasCapability(capability, facing);
    // }
    //
    // @Override
    // public <T> T getCapability(Capability<T> capability, ForgeDirection facing) {
    // if(hasHandlerForSide(facing)) {
    // if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    // return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(handle);
    // }
    // }
    // return super.getCapability(capability, facing);
    // }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.handle = createNewItemHandler();
        this.handle.deserializeNBT(compound.getCompoundTag("inventory"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setTag("inventory", this.handle.serializeNBT());
    }

    public int getInventorySize() {
        return inventorySize;
    }

    @Override
    public void onInventoryChanged(int slotChanged) {}

    public static class ItemHandlerTileFiltered extends ItemHandlerTile {

        public ItemHandlerTileFiltered(TileInventoryBase inv) {
            super(inv);
        }

        @Override
        protected boolean acceptsStack(int slot, ItemStack stack) {
            return canInsertItem(slot, stack, getStackInSlot(slot));
        }

        public boolean canInsertItem(int slot, ItemStack toAdd, @javax.annotation.Nullable ItemStack existing) {
            return true;
        }
    }

    public static class ItemHandlerTile extends ItemInventory {

        public ItemHandlerTile(TileInventoryBase inv) {
            super(inv);
        }
    }
}
