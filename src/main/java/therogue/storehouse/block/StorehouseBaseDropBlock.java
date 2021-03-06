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

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class StorehouseBaseDropBlock extends StorehouseBaseBlock {
	
	private ItemStack drop;
	private int min_quantity;
	private int max_quantity;
	
	public StorehouseBaseDropBlock (String name, ItemStack drop) {
		this(name, drop, 1, 1);
	}
	
	public StorehouseBaseDropBlock (String name, ItemStack drop, int min_quantity, int max_quantity) {
		super(name);
		this.drop = drop;
		this.min_quantity = min_quantity;
		this.max_quantity = max_quantity;
		this.setHarvestLevel("pickaxe", 1);
	}
	
	@Override
	public Item getItemDropped (IBlockState blockstate, Random random, int fortune) {
		return this.drop.getItem();
	}
	
	@Override
	public int quantityDropped (IBlockState blockstate, int fortune, Random random) {
		if (this.min_quantity >= this.max_quantity) return this.min_quantity;
		return this.min_quantity + random.nextInt(this.max_quantity - this.min_quantity + fortune + 1);
	}
	
	/**
	 * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It returns the metadata of the dropped item based on the old metadata of the block.
	 */
	@Override
	public int damageDropped (IBlockState state) {
		return this.drop.getMetadata();
	}
}
