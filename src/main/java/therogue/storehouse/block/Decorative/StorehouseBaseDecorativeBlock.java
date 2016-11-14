/*
 * This file is part of Storehouse. Copyright (c) 2016, TheRogue, All rights reserved.
 * 
 * Storehouse is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Storehouse is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Storehouse. If not, see <http://www.gnu.org/licenses/gpl>.
 */

package therogue.storehouse.block.Decorative;

import net.minecraft.creativetab.CreativeTabs;
import therogue.storehouse.block.StorehouseBaseBlock;


public class StorehouseBaseDecorativeBlock extends StorehouseBaseBlock
{
	public StorehouseBaseDecorativeBlock(String name)
	{
		super(name);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}
}