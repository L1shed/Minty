package keystrokesmod.utility;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;

public class BlockUtils {
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean canInteract(final Block block) {
        return block instanceof BlockContainer || block == Blocks.crafting_table;
    }

    public static boolean isSamePos(BlockPos blockPos, BlockPos blockPos2) {
        return blockPos == blockPos2 || (blockPos.getX() == blockPos2.getX() && blockPos.getY() == blockPos2.getY() && blockPos.getZ() == blockPos2.getZ());
    }

    public static float getBlockHardness(final Block block, final ItemStack itemStack, final boolean b) {
        final float getBlockHardness = block.getBlockHardness(mc.theWorld, null);
        if (getBlockHardness < 0.0f) {
            return 0.0f;
        }
        return (block.getMaterial().isToolNotRequired() || (itemStack != null && itemStack.canHarvestBlock(block))) ? (getToolDigEfficiency(itemStack, block, b) / getBlockHardness / 30.0f) : (getToolDigEfficiency(itemStack, block, b) / getBlockHardness / 100.0f);
    }

    public static float getToolDigEfficiency(final ItemStack itemStack, final Block block, final boolean b) {
        float n = (itemStack == null) ? 1.0f : itemStack.getItem().getStrVsBlock(itemStack, block);
        if (n > 1.0f) {
            final int getEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
            if (getEnchantmentLevel > 0 && itemStack != null) {
                n += getEnchantmentLevel * getEnchantmentLevel + 1;
            }
        }
        if (mc.thePlayer.isPotionActive(Potion.digSpeed)) {
            n *= 1.0f + (mc.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2f;
        }
        if (!b) {
            if (mc.thePlayer.isPotionActive(Potion.digSlowdown)) {
                float n2;
                switch (mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
                    case 0: {
                        n2 = 0.3f;
                        break;
                    }
                    case 1: {
                        n2 = 0.09f;
                        break;
                    }
                    case 2: {
                        n2 = 0.0027f;
                        break;
                    }
                    default: {
                        n2 = 8.1E-4f;
                        break;
                    }
                }
                n *= n2;
            }
            if (mc.thePlayer.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(mc.thePlayer)) {
                n /= 5.0f;
            }
            if (!mc.thePlayer.onGround) {
                n /= 5.0f;
            }
        }
        return n;
    }

    public static Block getBlock(BlockPos blockPos) {
        return getBlockState(blockPos).getBlock();
    }

    public static IBlockState getBlockState(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos);
    }
}
