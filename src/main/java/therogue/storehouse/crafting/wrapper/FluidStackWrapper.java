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

import net.minecraftforge.fluids.FluidStack;
import therogue.storehouse.crafting.wrapper.IRecipeComponent.IFluidComponent;
import therogue.storehouse.fluid.FluidUtils;

public class FluidStackWrapper implements IRecipeWrapper {
	
	private FluidStack stack;
	
	public FluidStackWrapper () {
		this.stack = null;
	}
	
	public FluidStackWrapper (FluidStack stack) {
		this.stack = stack;
	}
	
	public FluidStack getStack () {
		return stack != null ? stack.copy() : null;
	}
	
	@Override
	public boolean isUnUsed () {
		return stack == null || stack != null && stack.amount <= 0;
	}
	
	@Override
	public boolean mergable (IRecipeWrapper component, int limit) {
		if (!(component instanceof FluidStackWrapper)) return false;
		return FluidUtils.areStacksMergableWithLimit(limit, stack, ((FluidStackWrapper) component).getStack());
	}
	
	@Override
	public FluidStackWrapper merge (IRecipeWrapper component, int limit) {
		if (!mergable(component, limit)) return this;
		stack = FluidUtils.mergeStacks(limit, true, stack, ((FluidStackWrapper) component).getStack());
		return this;
	}
	
	@Override
	public boolean canAddComponent (IRecipeComponent component, int limit) {
		if (!(component instanceof IFluidComponent)) return false;
		return FluidUtils.areStacksMergableWithLimit(limit, stack, ((IFluidComponent) component).getComponent());
	}
	
	@Override
	public FluidStackWrapper addComponent (IRecipeComponent component, int limit) {
		if (!canAddComponent(component, limit)) return this;
		FluidUtils.mergeStacks(limit, true, stack, ((IFluidComponent) component).getComponent());
		return this;
	}
	
	@Override
	public void increaseSize (int i) {
		if (stack != null) stack.amount += i;
	}
	
	@Override
	public void setSize (int i) {
		if (stack != null) stack.amount = i;
	}
	
	@Override
	public int getSize () {
		if (stack != null) return stack.amount;
		return 0;
	}
	
	@Override
	public FluidStackWrapper copy () {
		return new FluidStackWrapper(getStack().copy());
	}
}
