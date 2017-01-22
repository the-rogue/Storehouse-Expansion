/*
 * This file is part of Storehouse. Copyright (c) 2016, TheRogue, All rights reserved.
 * 
 * Storehouse is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Storehouse is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Storehouse. If not, see <http://www.gnu.org/licenses/gpl>.
 */

package therogue.storehouse.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import therogue.storehouse.util.ItemUtils;

public class ContainerBase extends Container
{
	private IInventory teInv;
	

	public ContainerBase(IInventory playerInv, IInventory teInv)
	{
		this.teInv = teInv;
		
	    // Player Inventory, Slot 0-8, Slot IDs 0-8
	    for (int x = 0; x < 9; ++x) {
	        this.addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 142));
	    }
	    
	    // Player Inventory, Slot 9-35, Slot IDs 9-35
	    for (int y = 0; y < 3; ++y) {
	        for (int x = 0; x < 9; ++x) {
	            this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
	        }
	    }

	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		// TODO Auto-generated method stub
		return this.teInv.isUseableByPlayer(player);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot) {
	    ItemStack previous = null;
	    Slot slot = (Slot) this.inventorySlots.get(fromSlot);

	    if (slot != null && slot.getHasStack()) {
	        ItemStack current = slot.getStack();
	        previous = current.copy();

	        if (fromSlot >= 36) {
	            // From TE Inventory to Player Inventory (HotBar)
	            if (!this.mergeItemStack(current, 0, 36, true))
	                return null;
	        } else {
	            // From Player Inventory to TE Inventory
	            if (!this.mergeItemStack(current, 36, this.inventorySlots.size(), false))
	                return null;
	        }

	        if (current.stackSize == 0)
	            slot.putStack((ItemStack) null);
	        else
	            slot.onSlotChanged();

	        if (current.stackSize == previous.stackSize)
	            return null;
	        slot.onPickupFromSlot(playerIn, current);
	    }
	    return previous;
	}
	//Should check slot stack size limit, so copied code to fix this
    /**
     * Merges provided ItemStack with the first avaliable one in the container/player inventor between minIndex
     * (included) and maxIndex (excluded). Args : stack, minIndex, maxIndex, negativDirection. /!\ the Container
     * implementation do not check if the item is valid for the slot
     */
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection)
    {
        boolean flag = false;
        int i = reverseDirection ? endIndex - 1 : startIndex;

        if (stack.isStackable())
        {
            while (stack.stackSize > 0 && (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex))
            {
                Slot slot = (Slot)this.inventorySlots.get(i);
                ItemStack itemstack = slot.getStack();

                if (itemstack != null && ItemUtils.areItemStacksEqual(stack, itemstack))
                {
                    int j = itemstack.stackSize + stack.stackSize;
                    int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());

                    if (j <= maxSize)
                    {
                        stack.stackSize = 0;
                        itemstack.stackSize = j;
                        slot.onSlotChanged();
                        flag = true;
                    }
                    else if (itemstack.stackSize < maxSize)
                    {
                        stack.stackSize -= maxSize - itemstack.stackSize;
                        itemstack.stackSize = maxSize;
                        slot.onSlotChanged();
                        flag = true;
                    }
                }

                if (reverseDirection)
                {
                    --i;
                }
                else
                {
                    ++i;
                }
            }
        }

        if (stack.stackSize > 0)
        {
        	i = reverseDirection ? endIndex - 1 : startIndex;

            while (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex)
            {
                Slot slot1 = (Slot)this.inventorySlots.get(i);
                ItemStack itemstack1 = slot1.getStack();

                if (itemstack1 == null && slot1.isItemValid(stack)) // Forge: Make sure to respect isItemValid in the slot.
                {
                	int maxSize = Math.min(slot1.getSlotStackLimit(), stack.getMaxStackSize());
                	maxSize = Math.min(maxSize, stack.stackSize);
                	ItemStack newStack = stack.copy();
                	newStack.stackSize = maxSize;
                    slot1.putStack(newStack);
                    slot1.onSlotChanged();
                    stack.stackSize -= maxSize;
                    flag = true;
                    break;
                }

                if (reverseDirection)
                {
                    --i;
                }
                else
                {
                    ++i;
                }
            }
        }

        return flag;
    }

}
