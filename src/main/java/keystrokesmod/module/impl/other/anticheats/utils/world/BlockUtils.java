package keystrokesmod.module.impl.other.anticheats.utils.world;

import net.minecraft.block.state.IBlockState;
import org.jetbrains.annotations.NotNull;

public class BlockUtils {
    public static boolean isFullBlock(@NotNull IBlockState blockState) {
        return blockState.getBlock().isFullBlock();
    }

}
