/*
 * This file is part of Storehouse. Copyright (c) 2016, TheRogue, All rights reserved.
 * 
 * Storehouse is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Storehouse is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Storehouse. If not, see <http://www.gnu.org/licenses/gpl>.
 */

package therogue.storehouse.block.machine;

import javax.annotation.Nonnull;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import therogue.storehouse.core.Storehouse;
import therogue.storehouse.reference.IDs;
import therogue.storehouse.tile.machine.TileThermalPress;

public class BlockThermalPress extends StorehouseBaseMachine {
	
	public BlockThermalPress (String name) {
		super(name);
		this.setDefaultState(this.getBlockState().getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}
	
	@Override
	public boolean isOpaqueCube (IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube (IBlockState state) {
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity (World worldIn, int meta) {
		TileThermalPress tile = new TileThermalPress();
		tile.setWorld(worldIn);
		return tile;
	}
	
	@Override
	public boolean onBlockActivated (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote)
		{
			player.openGui(Storehouse.instance, IDs.THERMALPRESSGUI, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}
	
	/**
	 * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only, LIQUID for vanilla liquids, INVISIBLE to skip all rendering
	 */
	@Override
	@Deprecated
	public EnumBlockRenderType getRenderType (IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	
	@SideOnly (Side.CLIENT)
	@Nonnull
	@Override
	public BlockRenderLayer getBlockLayer () {
		return BlockRenderLayer.TRANSLUCENT;
	}
	
	// --------------------Facing-------------------------------------------------
	/**
	 * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the IBlockstate
	 */
	public IBlockState getStateForPlacement (World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getAdjustedHorizontalFacing().getOpposite());
	}
	
	@Override
	public BlockStateContainer createBlockState () {
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}
	
	@Override
	public int getMetaFromState (IBlockState state) {
		return state.getValue(FACING).getIndex();
	}
	
	@Override
	public IBlockState getStateFromMeta (int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}
}
