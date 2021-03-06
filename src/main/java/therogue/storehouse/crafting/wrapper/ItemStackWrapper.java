/*
 * This file is part of Storehouse. Copyright (c) 2017, TheRogue, All rights reserved.
 * 
 * Storehouse is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Storehouse is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Storehouse. If not, see <http://www.gnu.org/licenses/gpl>.
 */

package therogue.storehouse.crafting.wrapper;

import net.minecraft.item.ItemStack;
import therogue.storehouse.crafting.wrapper.IRecipeComponent.IItemComponent;
import therogue.storehouse.inventory.ItemStackUtils;

public class ItemStackWrapper implements IRecipeWrapper {
	
	private ItemStack stack;
	
	public ItemStackWrapper (ItemStack stack) {
		this.stack = stack;
	}
	
	public ItemStack getStack () {
		return stack.copy();
	}
	
	@Override
	public boolean isUnUsed () {
		return stack.isEmpty();
	}
	
	@Override
	public boolean mergable (IRecipeWrapper component, int limit) {
		if (!(component instanceof ItemStackWrapper)) return false;
		return ItemStackUtils.areStacksMergableWithLimit(limit, ((ItemStackWrapper) component).getStack(), stack);
	}
	
	@Override
	public ItemStackWrapper merge (IRecipeWrapper component, int limit) {
		if (!mergable(component, limit)) return this;
		stack = ItemStackUtils.mergeStacks(limit, true, stack, ((ItemStackWrapper) component).getStack());
		return this;
	}
	
	@Override
	public boolean canAddComponent (IRecipeComponent component, int limit) {
		if (!(component instanceof IItemComponent)) return false;
		return ItemStackUtils.areStacksMergableWithLimit(limit, ((IItemComponent) component).getInput(), stack);
	}
	
	@Override
	public ItemStackWrapper addComponent (IRecipeComponent component, int limit) {
		if (!canAddComponent(component, limit)) return this;
		return new ItemStackWrapper(ItemStackUtils.mergeStacks(limit, true, stack, ((IItemComponent) component).getInput()));
	}
	
	@Override
	public void increaseSize (int i) {
		stack.grow(i);
	}
	
	@Override
	public void setSize (int i) {
		stack.setCount(i);
	}
	
	@Override
	public int getSize () {
		return stack.getCount();
	}
	
	@Override
	public ItemStackWrapper copy () {
		return new ItemStackWrapper(stack.copy());
	}
}
