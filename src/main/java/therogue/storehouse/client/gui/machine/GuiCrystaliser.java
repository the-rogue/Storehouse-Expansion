/*
 * This file is part of Storehouse. Copyright (c) 2017, TheRogue, All rights reserved.
 * 
 * Storehouse is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Storehouse is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Storehouse. If not, see <http://www.gnu.org/licenses/gpl>.
 */

package therogue.storehouse.client.gui.machine;

import java.awt.Color;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import therogue.storehouse.client.gui.GuiBase;
import therogue.storehouse.client.gui.GuiHelper;
import therogue.storehouse.client.gui.Icons;
import therogue.storehouse.client.gui.element.ElementEnergyBar;
import therogue.storehouse.client.gui.element.ElementFadingProgressBar;
import therogue.storehouse.client.gui.element.ElementFluidTank;
import therogue.storehouse.client.gui.element.ProgressHandler;
import therogue.storehouse.container.ContainerBase;
import therogue.storehouse.crafting.MachineCraftingHandler.CapabilityCrafter;
import therogue.storehouse.crafting.MachineCraftingHandler.ICraftingManager;
import therogue.storehouse.tile.ModuleContext;
import therogue.storehouse.tile.machine.TileCrystaliser;

public class GuiCrystaliser extends GuiBase {
	
	public GuiCrystaliser (ContainerBase inventory, TileCrystaliser linked) {
		super(NORMAL_TEXTURE, inventory, linked);
		ICraftingManager<?> crafter = linked.getCapability(CapabilityCrafter.CraftingManager, null, ModuleContext.GUI);
		IEnergyStorage energy = linked.getCapability(CapabilityEnergy.ENERGY, null, ModuleContext.GUI);
		elements.add(new ProgressHandler( () -> energy.getEnergyStored(), () -> energy.getMaxEnergyStored(), new ElementEnergyBar(8, 8, Icons.EnergyBar.getLocation())));
		elements.add(new ElementFluidTank(Icons.FluidTank.getLocation(), 105, 12, linked.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null, ModuleContext.GUI)));
		elements.add(new ProgressHandler( () -> crafter.getTimeElapsed(), () -> crafter.getTotalCraftingTime(), new ElementFadingProgressBar(46, 18, 54, 54, new Color(15, 26, 95)) {
			
			@Override
			public void drawBottomLayer (GuiBase gui, int mouseX, int mouseY, float progress) {
				super.drawBottomLayer(gui, mouseX, mouseY, progress);
				if (progress != 0.0F)
				{
					GlStateManager.color((FluidRegistry.WATER.getColor() >> 16 & 255) / 255, (FluidRegistry.WATER.getColor() >> 8 & 255)
							/ 255, (FluidRegistry.WATER.getColor() & 255) / 255);
					GuiHelper.drawFluid(new FluidStack(FluidRegistry.WATER, 1000), x, y, width, height);
					GlStateManager.color(becomeColour.getRed() / 255.0F, becomeColour.getGreen() / 255.0F, becomeColour.getBlue() / 255.0F, progress);
					GuiHelper.drawFluid(new FluidStack(FluidRegistry.WATER, 1000), x, y, width, height);
				}
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GuiHelper.bindTexture(gui, Icons.CrystaliserProgressIcon.getLocation());
				gui.drawTexturedModalRect(x, y, width, height);
			}
		}));
		onConstructorFinishTEMP();
	}
}
