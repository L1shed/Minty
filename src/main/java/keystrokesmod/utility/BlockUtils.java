package keystrokesmod.utility;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class BlockUtils {
    public static boolean isInteractable(final Block block) {
        return block instanceof BlockContainer || block == Blocks.crafting_table;
    }

    public static boolean isSamePos(BlockPos blockPos, BlockPos blockPos2) {
        return blockPos == blockPos2 || (blockPos.getX() == blockPos2.getX() && blockPos.getY() == blockPos2.getY() && blockPos.getZ() == blockPos2.getZ());
    }
}
