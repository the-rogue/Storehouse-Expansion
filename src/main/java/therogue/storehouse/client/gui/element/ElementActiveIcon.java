/*
 * This file is part of Storehouse. Copyright (c) 2016, TheRogue, All rights reserved.
 * 
 * Storehouse is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Storehouse is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Storehouse. If not, see <http://www.gnu.org/licenses/gpl>.
 */

package therogue.storehouse.client.gui.element;

import java.awt.image.BufferedImage;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import therogue.storehouse.client.gui.GuiBase;
import therogue.storehouse.util.GuiUtils;
import therogue.storehouse.util.TextureHelper;

public class ElementActiveIcon extends ElementBase
{
	public final ResourceLocation iconLocation;
	public final BufferedImage icon;
	public final int x;
	public final int y;
	public final IInventory stateChanger;
	public final int activeField;

	public ElementActiveIcon(GuiBase gui, int x, int y, ResourceLocation iconLocation, IInventory stateChanger, int activeField)
	{
		super(gui);
		this.iconLocation = iconLocation;
		this.icon = TextureHelper.getImageAt(iconLocation);
		this.x = x;
		this.y = y;
		this.stateChanger = stateChanger;
		this.activeField = activeField;
	}

	@Override
	public void drawElementForegroundLayer(int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		if (icon != null){
			if (stateChanger.getField(activeField) == 1) {
				GuiUtils.bindTexture(this, iconLocation);
				gui.drawTexturedModalRect(x, y, 0.0F, 0.0F, 0.5F, 1.0F, icon.getWidth()/2, icon.getHeight());//0.941176471F, 0.956862745F, 0.976470588F,
			} else {
				GuiUtils.bindTexture(this, iconLocation);
				gui.drawTintedTexturedModalRect(x, y, 0.5F, 0.0F, 1.0F, 1.0F, icon.getWidth()/2, icon.getHeight(), 255, 255, 255, 35);
			}
		}
	}

	@Override
	public void drawElementBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
	}

}