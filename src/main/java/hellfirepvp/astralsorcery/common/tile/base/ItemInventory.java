/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * Real item storage for Astral Sorcery tiles - the 1.7 replacement for Forge's ItemStackHandler used in 1.10.
 * (After the port the handler was a stub with zero slots: altars, wells and pedestals could not hold items.)
 */
public class ItemInventory implements ISidedInventory {

    /** Implemented by the owning tile. */
    public interface Owner {

        int getInventorySize();

        void onInventoryChanged(int slot);

        void markForUpdate();

        /** Whether automation (hoppers etc.) may access the inventory from the given side (ForgeDirection ordinal). */
        boolean canAccessFromSide(int side);
    }

    private final Owner owner;
    private ItemStack[] stacks;

    public ItemInventory(Owner owner) {
        this.owner = owner;
        this.stacks = new ItemStack[Math.max(0, owner.getInventorySize())];
    }

    /** 1.10 ItemStackHandler naming; kept for existing call sites. */
    public void setStackInSlot(int slot, @Nullable ItemStack stack) {
        setInventorySlotContents(slot, stack);
    }

    /** Hook for filtered inventories: may this stack be placed into the slot? */
    protected boolean acceptsStack(int slot, @Nullable ItemStack stack) {
        return true;
    }

    protected void onContentsChanged(int slot) {
        owner.onInventoryChanged(slot);
        owner.markForUpdate();
    }

    public void clearInventory() {
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = null;
            onContentsChanged(i);
        }
    }

    @Override
    public int getSizeInventory() {
        return stacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot >= 0 && slot < stacks.length ? stacks[slot] : null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        ItemStack s = getStackInSlot(slot);
        if (s == null || count <= 0) return null;
        ItemStack out;
        if (s.stackSize <= count) {
            out = s;
            stacks[slot] = null;
        } else {
            out = s.splitStack(count);
            if (s.stackSize <= 0) stacks[slot] = null;
        }
        onContentsChanged(slot);
        return out;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null; // tile inventories keep their contents when a GUI closes
    }

    @Override
    public void setInventorySlotContents(int slot, @Nullable ItemStack stack) {
        if (slot < 0 || slot >= stacks.length) return;
        if (stack != null && !acceptsStack(slot, stack)) return;
        if (stack != null && stack.stackSize <= 0) stack = null;
        stacks[slot] = stack;
        onContentsChanged(slot);
    }

    @Override
    public String getInventoryName() {
        return "container.astralsorcery";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        owner.markForUpdate();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return acceptsStack(slot, stack);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        if (!owner.canAccessFromSide(side)) return new int[0];
        int[] slots = new int[stacks.length];
        for (int i = 0; i < slots.length; i++) slots[i] = i;
        return slots;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return owner.canAccessFromSide(side) && isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return owner.canAccessFromSide(side);
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound cmp = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < stacks.length; i++) {
            if (stacks[i] != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("Slot", i);
                stacks[i].writeToNBT(tag);
                list.appendTag(tag);
            }
        }
        cmp.setTag("Items", list);
        cmp.setInteger("Size", stacks.length);
        return cmp;
    }

    public void deserializeNBT(NBTTagCompound cmp) {
        stacks = new ItemStack[Math.max(0, owner.getInventorySize())];
        NBTTagList list = cmp.getTagList("Items", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int slot = tag.getInteger("Slot");
            if (slot >= 0 && slot < stacks.length) {
                stacks[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
    }
}
