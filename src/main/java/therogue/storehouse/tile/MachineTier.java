/*
 * This file is part of Storehouse. Copyright (c) 2016, TheRogue, All rights reserved.
 * 
 * Storehouse is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Storehouse is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Storehouse. If not, see <http://www.gnu.org/licenses/gpl>.
 */

package therogue.storehouse.tile;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import therogue.storehouse.Storehouse;

public enum MachineTier implements IStringSerializable {
	basic (Storehouse.RESOURCENAMEPREFIX + "textures/gui/basic.png"),
	advanced (Storehouse.RESOURCENAMEPREFIX + "textures/gui/advanced.png"),
	ender (Storehouse.RESOURCENAMEPREFIX + "textures/gui/ender.png");
	
	public final ResourceLocation guiLocation;
	
	private MachineTier (String guiLocation) {
		this.guiLocation = new ResourceLocation(guiLocation);
	}
	
	@Override
	public String getName () {
		return this.name();
	}
	
	@Override
	public String toString () {
		return getName();
	}
}
