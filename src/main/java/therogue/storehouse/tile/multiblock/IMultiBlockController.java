
package therogue.storehouse.tile.multiblock;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import therogue.storehouse.tile.multiblock.MultiBlockFormationHandler.IMultiBlockElement;

public interface IMultiBlockController extends IMultiBlockTile {
	
	public World getPositionWorld ();
	
	public BlockPos getPosition ();
	
	public IMultiBlockElement[][][] getStructure ();
	
	public void onBlockBroken (@Nullable BlockPos at);
	
	public boolean onMultiBlockActivatedAt (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side);
	
	public boolean hasCapability (Capability<?> capability, EnumFacing facing);
	
	public <T> T getCapability (Capability<T> capability, EnumFacing facing);
}