package keystrokesmod.utility;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

import static keystrokesmod.Raven.mc;

public class ContainerUtils {
    public static  <T extends Item> int getSlot(Class<T> item, Predicate<T> predicate) {
        int slot = -1;
        int highestStack = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack != null && item.isInstance(itemStack.getItem()) && predicate.test(item.cast(itemStack.getItem())) && itemStack.stackSize > 0) {
                if (mc.thePlayer.inventory.mainInventory[i].stackSize > highestStack) {
                    highestStack = mc.thePlayer.inventory.mainInventory[i].stackSize;
                    slot = i;
                }
            }
        }
        return slot;
    }

    public static  <T extends Item> int getSlot(Class<T> item) {
        return getSlot(item, t -> true);
    }
}
