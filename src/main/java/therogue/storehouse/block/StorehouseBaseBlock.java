/*
 * This file is part of Storehouse. Copyright (c) 2016, TheRogue, All rights reserved.
 * 
 * Storehouse is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Storehouse is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Storehouse. If not, see <http://www.gnu.org/licenses/gpl>.
 */

package therogue.storehouse.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import therogue.storehouse.client.render.blocks.BlockRender;
import therogue.storehouse.reference.General;
import therogue.storehouse.reference.Resources;


public class StorehouseBaseBlock extends Block implements IStorehouseBaseBlock
{
	public static final StorehouseBlockType TYPE = StorehouseBlockType.StorehouseBaseBlock;

	public StorehouseBaseBlock(String name)
	{
		this(name, Material.ROCK);
	}

	public StorehouseBaseBlock(String name, Material material)
	{
		super(material);
		this.setUnlocalizedName(name);
		this.setRegistryName(General.MOD_ID, name);
	}

	@Override
	public String getUnlocalizedName()
	{
		return String.format("tile.%s%s", Resources.RESOURCENAMEPREFIX, getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	private String getUnwrappedUnlocalizedName(String unlocalizedName)
	{
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	public String getName()
	{
		return getUnwrappedUnlocalizedName(super.getUnlocalizedName());
	}

	@SideOnly(Side.CLIENT)
	public void registertexture()
	{
		BlockRender.blockTexture(this);
	}

	public StorehouseBlockType getType()
	{
		return TYPE;
	}
}
