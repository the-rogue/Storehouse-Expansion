/*
 * This file is part of Storehouse. Copyright (c) 2017, TheRogue, All rights reserved.
 * 
 * Storehouse is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Storehouse is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Storehouse. If not, see <http://www.gnu.org/licenses/gpl>.
 */

package therogue.storehouse.tile.machine;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import therogue.storehouse.container.GuiItemCapability;
import therogue.storehouse.crafting.IMachineRecipe;
import therogue.storehouse.crafting.MachineCraftingHandler;
import therogue.storehouse.crafting.wrapper.IRecipeWrapper;
import therogue.storehouse.crafting.wrapper.ItemStackWrapper;
import therogue.storehouse.init.ModBlocks;
import therogue.storehouse.inventory.IInventoryItemHandler;
import therogue.storehouse.inventory.InventoryManager;
import therogue.storehouse.tile.ModuleContext;
import therogue.storehouse.tile.StorehouseBaseMachine;

public class TilePotionBrewer extends StorehouseBaseMachine implements ITickable {
	
	public static final int RFPerTick = 80;
	public static final int CRAFTING_TIME = 400;
	public static final List<Integer> OUTPUT_SLOTS = Lists.newArrayList(0, 1, 2);
	public static final List<Integer> BREWING_SLOTS = Lists.newArrayList(3, 4, 5);
	public static final List<Integer> BOTTLE_SLOTS = Lists.newArrayList(6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
	protected final MachineCraftingHandler<TilePotionBrewer>.CraftingManager theCrafter = MachineCraftingHandler.getHandler(TilePotionBrewer.class).newCrafter(this);
	private int injector = 0;
	// TODO ability to change injectors/configure, multiple lists of injectors, switchable via redstone, buttons in UI, bigger UI
	private final ArrayList<TilePotionInjector> injectors = new ArrayList<>();
	
	public TilePotionBrewer () {
		super(ModBlocks.potion_brewer);
		modules.add(theCrafter);
		this.setInventory(new InventoryManager(this, 18, BOTTLE_SLOTS.toArray(new Integer[0]), new Integer[] { 0, 1, 2 }) {
			
			@Override
			protected boolean isItemValidForSlotChecks (int index, ItemStack stack) {
				if (BOTTLE_SLOTS.contains(index))
				{
					for (IBrewingRecipe recipe : BrewingRecipeRegistry.getRecipes())
					{
						if (recipe.isInput(stack)) return true;
					}
					return false;
				}
				if (BREWING_SLOTS.contains(index) || OUTPUT_SLOTS.contains(index)) return true;
				return false;
			}
		});
	}
	
	public void addInjector (TilePotionInjector e) {
		if (injectors.size() < 10) injectors.add(e);
	}
	
	public void removeInjector (TilePotionInjector o) {
		if (injectors.contains(o)) injectors.remove(o);
	}
	
	@Override
	public void update () {
		if (injectors.size() == 0) return;
		IInventoryItemHandler internalView = this.getCapability(GuiItemCapability.CAP, null, ModuleContext.INTERNAL);
		if (injectors.size() <= injector)
		{
			int slots = BREWING_SLOTS.size();
			for (Integer i : OUTPUT_SLOTS)
			{
				if (internalView.getStackInSlot(i).isEmpty())
				{
					for (Integer j : BREWING_SLOTS)
					{
						if (!internalView.getStackInSlot(j).isEmpty())
						{
							internalView.insertItem(i, internalView.extractItem(j, -1, false), false);
							--slots;
							break;
						}
					}
				}
			}
			if (slots == 0) injector = 0;
		}
		if (injectors.size() <= injector) return;
		ItemStack nextIngredient = injectors.get(injector).getNext();
		for (Integer bottle_slot : BOTTLE_SLOTS)
		{
			if (BrewingRecipeRegistry.hasOutput(internalView.getStackInSlot(bottle_slot), nextIngredient))
			{
				for (Integer brewing_slot : BREWING_SLOTS)
				{
					if (internalView.getStackInSlot(brewing_slot).isEmpty())
					{
						internalView.insertItem(brewing_slot, internalView.extractItem(bottle_slot, -1, false), false);
					}
				}
			}
		}
	}
	
	// -------------------------Gui Methods----------------------------------------------------
	public ItemStack getNextIngredient () {
		if (injectors.size() == 0) return ItemStack.EMPTY;
		if (injectors.size() <= injector) return injectors.get(0).getNext().copy();
		return injectors.get(injector).getNext().copy();
	}
	
	public static enum PotionRecipe implements IMachineRecipe<TilePotionBrewer> {
		INSTANCE;
		
		public static void registerRecipes () {
			MachineCraftingHandler.register(TilePotionBrewer.class, INSTANCE);
		}
		
		@Override
		public int timeTaken (TilePotionBrewer machine) {
			return CRAFTING_TIME;
		}
		
		@Override
		public boolean itemValidForRecipe (TilePotionBrewer tile, int index, IRecipeWrapper stack) {
			if (!(stack instanceof ItemStackWrapper)) return false;
			for (IBrewingRecipe recipe : BrewingRecipeRegistry.getRecipes())
			{
				if (recipe.isInput(((ItemStackWrapper) stack).getStack())) return true;
			}
			return false;
		}
		
		@Override
		public boolean matches (TilePotionBrewer machine) {
			return BrewingRecipeRegistry.canBrew(machine.inventory.getInventory(), machine.getNextIngredient(), BREWING_SLOTS.stream().mapToInt(i -> i).toArray());
		}
		
		@Override
		public Result end (TilePotionBrewer machine) {
			ItemStack nextIngredient = machine.injectors.get(machine.injector).getNext();
			BrewingRecipeRegistry.brewPotions(machine.inventory.getInventory(), nextIngredient, BREWING_SLOTS.stream().mapToInt(i -> i).toArray());
			nextIngredient.shrink(1);
			++machine.injector;
			return Result.CONTINUE;
		}
	}
}
