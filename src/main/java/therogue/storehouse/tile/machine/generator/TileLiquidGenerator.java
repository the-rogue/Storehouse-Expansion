/*
 * This file is part of Storehouse. Copyright (c) 2016, TheRogue, All rights reserved.
 * 
 * Storehouse is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Storehouse is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Storehouse. If not, see <http://www.gnu.org/licenses/gpl>.
 */

package therogue.storehouse.tile.machine.generator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import therogue.storehouse.container.machine.ContainerLiquidGenerator;
import therogue.storehouse.init.ModBlocks;
import therogue.storehouse.inventory.InventoryManager;
import therogue.storehouse.network.GuiUpdateTEPacket;
import therogue.storehouse.reference.MachineStats;
import therogue.storehouse.tile.MachineTier;
import therogue.storehouse.util.EnergyUtils;
import therogue.storehouse.util.ItemUtils;

public class TileLiquidGenerator extends TileBaseGenerator {
	
	private int generatorburntime = 0;
	private int maxruntime = 0;
	protected FluidTank tank = new FluidTank(10000) {
		
		@Override
		public boolean canFillFluidType (FluidStack fluid) {
			if (fluid.getFluid().getTemperature(fluid) > 500) return canFill();
			return false;
		}
	};
	
	public TileLiquidGenerator (MachineTier tier) {
		super(ModBlocks.liquid_generator, tier, MachineStats.LIQUIDGENPERTICK, false);
		inventory = new InventoryManager(this, 4, new Integer[] { 0, 2 }, new Integer[] { 1, 3 }) {
			
			@Override
			public boolean isItemValidForSlotChecks (int index, ItemStack stack) {
				if ((index == 0 || index == 1) && stack.hasCapability(CapabilityEnergy.ENERGY, null)) return true;
				if ((index == 2 || index == 3) && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) return true;
				return false;
			}
		};
		tank.setTileEntity(this);
		tank.setCanDrain(false);
	}
	
	// -------------------------Customisable Generator Methods-------------------------------------------
	@Override
	public boolean isRunning () {
		return generatorburntime > 0;
	}
	
	@Override
	protected void doRunTick () {
		--generatorburntime;
	}
	
	@Override
	protected void tick () {
		this.sendEnergyToItems(0);
		if (EnergyUtils.isItemFull(inventory.getStackInSlot(0)))
		{
			inventory.pushItems(0, 1);
		}
		if (!inventory.getStackInSlot(2).isEmpty())
		{
			ItemStack tankItem = inventory.getStackInSlot(2);
			ItemStack outputSlot = inventory.getStackInSlot(3);
			ItemStack result = FluidUtil.tryEmptyContainer(tankItem, tank, tank.getCapacity() - tank.getFluidAmount(), null, false).result;
			if (ItemUtils.areStacksMergableWithLimit(inventory.getSlotLimit(3), result, outputSlot))
			{
				inventory.setStackInSlot(2, ItemStack.EMPTY);
				inventory.setStackInSlot(3, ItemUtils.mergeStacks(inventory.getSlotLimit(3), true, outputSlot, FluidUtil.tryEmptyContainer(tankItem, tank, tank.getCapacity() - tank.getFluidAmount(), null, true).result));
			}
		}
		if (!isRunning())
		{
			FluidStack burnable = tank.getFluid();
			if (burnable != null && burnable.amount >= 100)
			{
				int burntime = burnable.getFluid().getTemperature(new FluidStack(burnable.getFluid(), 100, burnable.tag)) / (18 * GeneratorUtils.getBaseModifier(tier) + 1);
				if (burntime * this.RFPerTick <= energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored())
				{
					generatorburntime += burntime;
					maxruntime = burntime;
					tank.drainInternal(100, true);
					this.markDirty();
				}
			}
		}
		if (generatorburntime == 0)
		{
			maxruntime = 0;
		}
	}
	
	@Override
	public int runtimeLeft () {
		return generatorburntime;
	}
	
	@Override
	public int maxruntime () {
		return maxruntime;
	}
	
	// -------------------------IInteractionObject-----------------------------------------------------------------
	@Override
	public Container createContainer (InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerLiquidGenerator(playerInventory, this);
	}
	
	@Override
	public String getGuiID () {
		return "storehouse:" + ModBlocks.liquid_generator.getName() + "." + tier.getName();
	}
	// -------------------------Standard TE methods-----------------------------------
	
	@Override
	public GuiUpdateTEPacket getGUIPacket () {
		GuiUpdateTEPacket packet = super.getGUIPacket();
		packet.getNbt().setInteger("BurnTime", generatorburntime);
		packet.getNbt().setInteger("MaxRuntime", maxruntime);
		tank.writeToNBT(packet.getNbt());
		return packet;
	}
	
	@Override
	public void processGUIPacket (GuiUpdateTEPacket packet) {
		super.processGUIPacket(packet);
		generatorburntime = packet.getNbt().getInteger("BurnTime");
		maxruntime = packet.getNbt().getInteger("MaxRuntime");
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
	
	// ------------------------PlaceHolder Classes------------------------------------------------------------------
	public static class TileLiquidGeneratorBasic extends TileLiquidGenerator {
		
		public TileLiquidGeneratorBasic () {
			super(MachineTier.basic);
		}
	}
	
	public static class TileLiquidGeneratorAdvanced extends TileLiquidGenerator {
		
		public TileLiquidGeneratorAdvanced () {
			super(MachineTier.advanced);
		}
	}
	
	public static class TileLiquidGeneratorInfused extends TileLiquidGenerator {
		
		public TileLiquidGeneratorInfused () {
			super(MachineTier.infused);
		}
	}
	
	public static class TileLiquidGeneratorEnder extends TileLiquidGenerator {
		
		public TileLiquidGeneratorEnder () {
			super(MachineTier.ender);
		}
	}
	
	public static class TileLiquidGeneratorUltimate extends TileLiquidGenerator {
		
		public TileLiquidGeneratorUltimate () {
			super(MachineTier.ultimate);
		}
	}
}