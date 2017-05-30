/*
 * This file is part of Storehouse. Copyright (c) 2016, TheRogue, All rights reserved.
 * 
 * Storehouse is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Storehouse is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Storehouse. If not, see <http://www.gnu.org/licenses/gpl>.
 */

package therogue.storehouse.tile.machine;

import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.Sets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RangedWrapper;
import therogue.storehouse.container.machine.ContainerThermalPress;
import therogue.storehouse.crafting.ICrafter;
import therogue.storehouse.init.ModBlocks;
import therogue.storehouse.inventory.InventoryManager;
import therogue.storehouse.network.GuiClientUpdatePacket;
import therogue.storehouse.network.GuiUpdateTEPacket;
import therogue.storehouse.network.StorehousePacketHandler;
import therogue.storehouse.reference.MachineStats;
import therogue.storehouse.tile.IClientPacketReciever;
import therogue.storehouse.tile.MachineTier;
import therogue.storehouse.tile.StorehouseBaseMachine;
import therogue.storehouse.tile.machine.MachineCraftingHandler.CraftingManager;
import therogue.storehouse.util.GeneralUtils;

public class TileThermalPress extends StorehouseBaseMachine implements IClientPacketReciever, ICrafter {
	
	public static final int RFPerTick = MachineStats.THERMALPRESSPERTICK;
	private Mode mode = Mode.PRESS;
	private CraftingManager theCrafter = MachineCraftingHandler.getHandler(this.getClass()).newCrafter(this);
	
	public TileThermalPress () {
		super(ModBlocks.thermal_press, MachineTier.advanced);
		inventory = new InventoryManager(this, 8, new Integer[] { 1, 2, 3, 4, 5 }, new Integer[] { 0 }) {
			
			protected boolean isItemValidForSlotChecks (int index, ItemStack stack) {
				return theCrafter.checkItemValidForSlot(index - 1, stack);
			}
		};
	}
	
	// -------------------------ITickable-----------------------------------------------------------------
	@Override
	public void update () {
		if (GeneralUtils.isServerSide(world))
		{
			theCrafter.updateCraftingStatus();
		}
	}
	
	// -------------------------Tile Specific Utility Methods-------------------------------------------
	private void modeUpdate (int mode) {
		Mode m = GeneralUtils.getEnumFromNumber(Mode.class, mode);
		this.mode = m != null ? m : this.mode;
		this.onInventoryChange();
	}
	
	// -------------------------ICrafter Methods-----------------------------------
	@Override
	public Set<Integer> getOrderMattersSlots () {
		return mode.orderMattersSlots;
	}
	
	@Override
	public IItemHandlerModifiable getCraftingInventory () {
		return new RangedWrapper(getInventory(), 1, mode.craftingSlots + 1);
	}
	
	@Override
	public IItemHandlerModifiable getOutputInventory () {
		return new RangedWrapper(getInventory(), 0, 1);
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
	
	// ------------------IClientPacketReciever Methods-------------------------------------------------------
	@Override
	public void processGUIPacket (GuiClientUpdatePacket message) {
		NBTTagCompound nbt = message.getNbt();
		switch (nbt.getInteger("type")) {
			case 0:
				modeUpdate(message.getNbt().getInteger("mode"));
				break;
		}
	}
	
	// -------------------------Inventory Methods-----------------------------------
	@Override
	public void onInventoryChange () {
		super.onInventoryChange();
		theCrafter.checkRecipes();
	}
	
	// -------------------------Container/Gui Methods----------------------------------------------------
	@Override
	public int getField (int id) {
		switch (id) {
			case 4:
				return mode.ordinal();
			default:
				return super.getField(id);
		}
	}
	
	@Override
	public void setField (int id, int value) {
		switch (id) {
			case 4:
				modeUpdate(value);
				NBTTagCompound sendtag = new NBTTagCompound();
				sendtag.setInteger("type", 0);
				sendtag.setInteger("mode", this.mode.ordinal());
				StorehousePacketHandler.INSTANCE.sendToServer(new GuiClientUpdatePacket(this.getPos(), sendtag));
			default:
				super.setField(id, value);
		}
	}
	
	@Override
	public int getFieldCount () {
		return super.getFieldCount() + 1;
	}
	
	// -------------------------IInteractionObject-----------------------------------------------------------------
	@Override
	public Container createContainer (InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerThermalPress(playerInventory, this);
	}
	
	@Override
	public String getGuiID () {
		return "storehouse:" + ModBlocks.thermal_press.getName();
	}
	
	// -------------------------Standard TE methods-----------------------------------
	@Override
	public GuiUpdateTEPacket getGUIPacket () {
		GuiUpdateTEPacket packet = super.getGUIPacket();
		packet.getNbt().setInteger("TPMode", mode.ordinal());
		return packet;
	}
	
	@Override
	public void processGUIPacket (GuiUpdateTEPacket packet) {
		super.processGUIPacket(packet);
		modeUpdate(packet.getNbt().getInteger("TPMode"));
	}
	
	@Override
	public NBTTagCompound writeToNBT (NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("TPMode", mode.ordinal());
		return nbt;
	}
	
	@Override
	public void readFromNBT (NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		modeUpdate(nbt.getInteger("TPMode"));
	}
	
	// ------------------Thermal Press Mode Enum-------------------------------------------------------
	public static enum Mode
	{
		PRESS (3, new Predicate<ICrafter>() {
			
			@Override
			public boolean test (ICrafter machine) {
				if (machine instanceof TileThermalPress) { return ((TileThermalPress) machine).mode == Mode.PRESS; }
				return false;
			}
		}),
		JOIN (4, new Predicate<ICrafter>() {
			
			@Override
			public boolean test (ICrafter machine) {
				if (machine instanceof TileThermalPress) { return ((TileThermalPress) machine).mode == Mode.JOIN; }
				return false;
			}
		}),
		STAMP (2, new Predicate<ICrafter>() {
			
			@Override
			public boolean test (ICrafter machine) {
				if (machine instanceof TileThermalPress) { return ((TileThermalPress) machine).mode == Mode.STAMP; }
				return false;
			}
		}),
		HIGH_PRESSURE (5, new Predicate<ICrafter>() {
			
			@Override
			public boolean test (ICrafter machine) {
				if (machine instanceof TileThermalPress) { return ((TileThermalPress) machine).mode == Mode.HIGH_PRESSURE; }
				return false;
			}
		});
		
		public final int craftingSlots;
		public final Set<Integer> orderMattersSlots = Sets.newHashSet(0);
		public final Predicate<ICrafter> modeTest;
		
		private Mode (int craftingSlots, Predicate<ICrafter> modeTest) {
			this.craftingSlots = craftingSlots;
			this.modeTest = modeTest;
		}
	}
}
