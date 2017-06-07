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

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import therogue.storehouse.container.machine.ContainerCrystaliser;
import therogue.storehouse.crafting.ICrafter;
import therogue.storehouse.crafting.MachineCraftingHandler;
import therogue.storehouse.crafting.MachineCraftingHandler.CraftingManager;
import therogue.storehouse.crafting.inventory.IRecipeInventory;
import therogue.storehouse.crafting.inventory.RangedItemInventory;
import therogue.storehouse.crafting.wrapper.ItemStackWrapper;
import therogue.storehouse.init.ModBlocks;
import therogue.storehouse.inventory.InventoryManager;
import therogue.storehouse.network.GuiUpdateTEPacket;
import therogue.storehouse.reference.IDs;
import therogue.storehouse.reference.MachineStats;
import therogue.storehouse.tile.MachineTier;
import therogue.storehouse.tile.StorehouseBaseMachine;
import therogue.storehouse.util.GeneralUtils;

public class TileCrystaliser extends StorehouseBaseMachine implements ICrafter {
	
	public static final int RFPerTick = MachineStats.CRYSTALISERPERTICK;
	private CraftingManager theCrafter = MachineCraftingHandler.getHandler(this.getClass()).newCrafter(this);
	protected FluidTank tank = new FluidTank(10000) {
		
		@Override
		public boolean canFillFluidType (FluidStack fluid) {
			if (fluid.getFluid() == FluidRegistry.WATER) return canFill();
			return false;
		}
	};
	
	public TileCrystaliser () {
		super(ModBlocks.crystaliser, MachineTier.basic);
		inventory = new InventoryManager(this, 2, new Integer[] { 1 }, new Integer[] { 0 }) {
			
			@Override
			protected boolean isItemValidForSlotChecks (int index, ItemStack stack) {
				if (!this.getStackInSlot(0).isEmpty()) return false;
				return theCrafter.checkItemValidForSlot(index - 1, new ItemStackWrapper(stack));
			}
			
			@Override
			public int getSlotLimit (int slot) {
				return 1;
			}
		};
		tank.setTileEntity(this);
		tank.setCanDrain(false);
	}
	
	@Override
	public void update () {
		if (GeneralUtils.isServerSide(world))
		{
			theCrafter.updateCraftingStatus();
		}
	}
	
	// -------------------------ICrafter Methods-----------------------------------
	@Override
	public Set<Integer> getOrderMattersSlots () {
		return Sets.newHashSet(0);
	}
	
	@Override
	public IRecipeInventory getCraftingInventory () {
		return new RangedItemInventory(getInventory(), 1, 2);
	}
	
	@Override
	public IRecipeInventory getOutputInventory () {
		return new RangedItemInventory(getInventory(), 0, 1);
	}
	
	@Override
	public boolean isRunning () {
		return energyStorage.getEnergyStored() >= RFPerTick;
	}
	
	@Override
	public void doRunTick () {
		if (isRunning())
		{
			energyStorage.modifyEnergyStored(-RFPerTick);
		}
	}
	
	// -------------------------Inventory Methods-----------------------------------
	@Override
	public void onInventoryChange () {
		super.onInventoryChange();
		theCrafter.checkRecipes();
	}
	
	// -------------------------IInteractionObject-----------------------------------------------------------------
	@Override
	public Container createContainer (InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerCrystaliser(playerInventory, this);
	}
	
	@Override
	public String getGuiID () {
		return IDs.RESOURCENAMEPREFIX + ModBlocks.crystaliser.getName();
	}
	// -------------------------Standard TE methods-----------------------------------
	
	@Override
	public GuiUpdateTEPacket getGUIPacket () {
		GuiUpdateTEPacket packet = super.getGUIPacket();
		tank.writeToNBT(packet.getNbt());
		return packet;
	}
	
	@Override
	public void processGUIPacket (GuiUpdateTEPacket packet) {
		super.processGUIPacket(packet);
		tank.readFromNBT(packet.getNbt());
	}
	
	@Override
	public NBTTagCompound writeToNBT (NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		tank.writeToNBT(nbt);
		return nbt;
	}
	
	@Override
	public void readFromNBT (NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		tank.readFromNBT(nbt);
	}
	
	@Override
	public boolean hasCapability (Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return true;
		return super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings ("unchecked")
	@Override
	public <T> T getCapability (Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return (T) tank;
		return super.getCapability(capability, facing);
	}
}
