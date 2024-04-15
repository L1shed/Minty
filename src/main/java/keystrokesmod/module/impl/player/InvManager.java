package keystrokesmod.module.impl.player;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.block.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InvManager extends Module {
    private ButtonSetting autoArmor;
    private SliderSetting autoArmorDelay;
    private ButtonSetting autoSort;
    private SliderSetting sortDelay;
    private ButtonSetting stealChests;
    private ButtonSetting customChest;
    private ButtonSetting autoClose;
    private SliderSetting stealerDelay;
    private ButtonSetting inventoryCleaner;
    private ButtonSetting middleClickToClean;
    private SliderSetting cleanerDelay;
    private SliderSetting swordSlot;
    private SliderSetting blocksSlot;
    private SliderSetting goldenAppleSlot;
    private SliderSetting projectileSlot;
    private SliderSetting speedPotionSlot;
    private SliderSetting pearlSlot;
    private final String[] ignoreItems = {"stick", "string", "cake", "mushroom", "flint", "compass", "dyePowder", "feather", "shears", "anvil", "torch", "seeds", "leather", "skull", "record"};
    private int lastStole;
    private int lastSort;
    private int lastArmor;
    private int lastClean;

    public InvManager() {
        super("InvManager", category.player);
        this.registerSetting(autoArmor = new ButtonSetting("Auto armor", false));
        this.registerSetting(autoArmorDelay = new SliderSetting("Auto armor delay", 3.0, 1.0, 20.0, 1.0));
        this.registerSetting(autoSort = new ButtonSetting("Auto sort", false));
        this.registerSetting(sortDelay = new SliderSetting("Sort delay", 3.0, 1.0, 20.0, 1.0));
        this.registerSetting(stealChests = new ButtonSetting("Steal chests", false));
        this.registerSetting(customChest = new ButtonSetting("Custom chest", false));
        this.registerSetting(autoClose = new ButtonSetting("Close after stealing", false));
        this.registerSetting(stealerDelay = new SliderSetting("Stealer delay", 3.0, 1.0, 20.0, 1.0));
        this.registerSetting(inventoryCleaner = new ButtonSetting("Inventory cleaner", false));
        this.registerSetting(middleClickToClean = new ButtonSetting("Middle click to clean", false));
        this.registerSetting(cleanerDelay = new SliderSetting("Cleaner delay", 5.0, 1.0, 20.0, 1.0));
        this.registerSetting(swordSlot = new SliderSetting("Sword slot", 0.0, 0.0, 9.0, 1.0));
        this.registerSetting(blocksSlot = new SliderSetting("Blocks slot", 0.0, 0.0, 9.0, 1.0));
        this.registerSetting(goldenAppleSlot = new SliderSetting("Golden apple slot", 0.0, 0.0, 9.0, 1.0));
        this.registerSetting(projectileSlot = new SliderSetting("Projectile slot", 0.0, 0.0, 9.0, 1.0));
        this.registerSetting(speedPotionSlot = new SliderSetting("Speed potion slot", 0.0, 0.0, 9.0, 1.0));
        this.registerSetting(pearlSlot = new SliderSetting("Pearl slot", 0.0, 0.0, 9.0, 1.0));
    }

    public void onEnable() {
        resetDelay();
    }

    public void onUpdate() {
        if (Utils.inInventory()) {

        }
        else if (stealChests.isToggled() && mc.thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;
            if (chest == null || inventoryFull()) {
                autoClose();
                return;
            }
            String name = chest.getLowerChestInventory().getName();
            if (!customChest.isToggled() && !name.equals("Chest") && !name.equals("Ender Chest") && !name.equals("Large Chest")) {
                return;
            }
            boolean notEmpty = false;
            boolean stolen = false;
            int size = chest.getLowerChestInventory().getSizeInventory();
            for (int i = 0; i < size; i++) {
                ItemStack item = chest.getLowerChestInventory().getStackInSlot(i);
                if (item == null) {
                    continue;
                }
                if (Arrays.stream(ignoreItems).anyMatch(item.getUnlocalizedName()::contains)) {
                    continue;
                }
                IInventory inventory = chest.getLowerChestInventory();
                notEmpty = true;
                if (lastStole++ > stealerDelay.getInput()) {
                    if (item.getItem() instanceof ItemSword) {
                        if (getBestSword(inventory) != i) {
                            continue;
                        }
                        if (swordSlot.getInput() != 0) {
                            mc.playerController.windowClick(chest.windowId, i, (int) swordSlot.getInput() - 1, 2, mc.thePlayer);
                            lastStole = 0;
                            stolen = true;
                        }
                    }
                    else if (item.getItem() instanceof ItemBlock) {
                        if (!canBePlaced((ItemBlock) item.getItem())) {
                            continue;
                        }
                        mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                        lastStole = 0;
                        stolen = true;
                    }
                    else if (item.getItem() instanceof ItemAppleGold) {
                        mc.playerController.windowClick(chest.windowId, i, (int) (goldenAppleSlot.getInput() - 1), 1, mc.thePlayer);
                        lastStole = 0;
                        stolen = true;
                    }
                    else if (item.getItem() instanceof ItemSnowball || item.getItem() instanceof ItemEgg) {
                        mc.playerController.windowClick(chest.windowId, i, (int) (projectileSlot.getInput() - 1), 1, mc.thePlayer);
                        lastStole = 0;
                        stolen = true;
                    }
                    else if (item.getItem() == Items.potionitem ) {
                        if (!isSpeedPot(item)) {
                            mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                        }
                        else {
                            mc.playerController.windowClick(chest.windowId, i, (int) (speedPotionSlot.getInput() - 1), 1, mc.thePlayer);
                        }
                        lastStole = 0;
                        stolen = true;
                    }
                    else if (item.getItem() instanceof ItemEnderPearl) {
                        mc.playerController.windowClick(chest.windowId, i, (int) (pearlSlot.getInput() - 1), 1, mc.thePlayer);
                        lastStole = 0;
                        stolen = true;
                    }
                    else if (item.getItem() instanceof ItemArmor) {
                        if (getBestArmor(((ItemArmor) item.getItem()).armorType, inventory) != i) {
                            continue;
                        }
                        mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                        lastStole = 0;
                        stolen = true;
                    }
                    else {
                        mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                        lastStole = 0;
                        stolen = true;
                    }
                }
            }

            if (inventoryFull() || !notEmpty || !stolen) {
                autoClose();
            }
        }
        else {
            resetDelay();
        }
    }

    private int getProtection(final ItemStack itemStack) {
        return ((ItemArmor)itemStack.getItem()).damageReduceAmount + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { itemStack }, DamageSource.generic);
    }

    private void click(int slot, int mouseButton, boolean shiftClick) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, mouseButton, shiftClick ? 1 : 0, mc.thePlayer);
    }

    private void drop(int slot) {
        mc.playerController.windowClick(0, slot, 1, 4, mc.thePlayer);
    }

    private void swap(int slot, int hSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hSlot, 2, mc.thePlayer);
    }

    private void clickChest(int windowId, int slot, int mouseButton, boolean shiftClick) {
        mc.playerController.windowClick(windowId, slot, mouseButton, shiftClick ? 1 : 0, mc.thePlayer);
    }

    private boolean isSpeedPot(ItemStack item) {
        List<PotionEffect> list = ((ItemPotion) item.getItem()).getEffects(item);
        if (list == null) {
            return false;
        }
        for (PotionEffect effect : list) {
            if (effect.getEffectName().equals("potion.moveSpeed")) {
                return true;
            }
        }
        return false;
    }
    private boolean inventoryFull() {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getStack() == null) {
                return false;
            }
        }
        return true;
    }

    private void resetDelay() {
        lastStole = lastArmor = lastClean = lastSort = 0;
    }

    private void autoClose() {
        if (autoClose.isToggled()) {
            mc.thePlayer.closeScreen();
        }
    }

    public double getDamage(final ItemStack itemStack) {
        double getAmount = 0.0;
        for (final Map.Entry<String, AttributeModifier> entry : itemStack.getAttributeModifiers().entries()) {
            if (entry.getKey().equals("generic.attackDamage")) {
                getAmount = entry.getValue().getAmount();
                break;
            }
        }
        return getAmount + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25;
    }

    private int getBestSword(IInventory inventory) {
        int bestSword = -1;
        double lastDamage = 0;
        for (int i = 9; i < 45; i++) {
            ItemStack item = getItemStack(i);
            if (item == null || !(item.getItem() instanceof ItemSword)) {
                continue;
            }
            double damage = getDamage(item);
            if (damage > lastDamage) {
                lastDamage = damage;
                bestSword = i;
            }
        }
        if (inventory != null) {
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack item = inventory.getStackInSlot(i);
                if (item == null || !(item.getItem() instanceof ItemSword)) {
                    continue;
                }
                double damage = getDamage(item);
                if (damage > lastDamage) {
                    lastDamage = damage;
                    bestSword = i;
                }
            }
        }
        return bestSword;
    }

    private int getBestArmor(int armorType, IInventory inventory) {
        int bestArmor = -1;
        double lastProtection = 0;
        for (int i = 5; i < 45; i++) {
            ItemStack item = getItemStack(i);
            if (item == null || !(item.getItem() instanceof ItemArmor) || !(((ItemArmor) item.getItem()).armorType == armorType)) {
                continue;
            }
            double protection = getProtection(item);
            if (protection > lastProtection) {
                lastProtection = protection;
                bestArmor = i;
            }
        }
        if (inventory != null) {
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack item = inventory.getStackInSlot(i);
                if (item == null || !(item.getItem() instanceof ItemArmor) || !(((ItemArmor) item.getItem()).armorType == armorType)) {
                    continue;
                }
                double protection = getProtection(item);
                if (protection > lastProtection) {
                    lastProtection = protection;
                    bestArmor = i;
                }
            }
        }
        return bestArmor;
    }

    private int getBestPotion() {
        int amplifier = 0;
        int bestPotion = -1;
        for (int i = 9; i < 45; i++) {
            ItemStack item = getItemStack(i);
            if (item != null && item.getItem() instanceof ItemPotion) {
                List<PotionEffect> list = ((ItemPotion) item.getItem()).getEffects(item);
                if (list == null) {
                    continue;
                }
                for (PotionEffect effect : list) {
                    if (effect.getEffectName().equals("effect.speed") && effect.getAmplifier() > amplifier) {
                        bestPotion = i;
                        amplifier = effect.getAmplifier();
                    }
                }
            }
        }
        return bestPotion;
    }

    private int getBiggestStack(ItemStack itemStack) {
        int stack = 0;
        int biggestSlot = -1;
        for (int i = 9; i < 45; i++) {
            ItemStack item = getItemStack(i);
            if (item != null && item.getItem() instanceof ItemBlock && item.stackSize > stack) {
                if (!canBePlaced((ItemBlock) item.getItem())) {
                    continue;
                }
                stack = item.stackSize;
                biggestSlot = i;
            }
        }
        return biggestSlot;
    }

    private int getMostBlocks(IInventory inventory) {
        int stack = 0;
        int biggestSlot = -1;
        if (inventory != null) {
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack item = inventory.getStackInSlot(i);
                if (item != null && item.getItem() instanceof ItemBlock && item.stackSize > stack) {
                    if (!canBePlaced((ItemBlock) item.getItem())) {
                        continue;
                    }
                    stack = item.stackSize;
                    biggestSlot = i;
                }
            }
        }
        else {
            for (int i = 9; i < 45; i++) {
                ItemStack item = getItemStack(i);
                if (item != null && item.getItem() instanceof ItemBlock && item.stackSize > stack) {
                    if (!canBePlaced((ItemBlock) item.getItem())) {
                        continue;
                    }
                    stack = item.stackSize;
                    biggestSlot = i;
                }
            }
        }
        return biggestSlot;
    }

    private ItemStack getItemStack(int i) {
        Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
        if (slot == null) {
            return null;
        }
        ItemStack item = slot.getStack();
        if (item == null) {
            return null;
        }
        return item;
    }

    private boolean canBePlaced(ItemBlock itemBlock) {
        Block block = itemBlock.getBlock();
        if (block == null) {
            return false;
        }
        if (block instanceof BlockLiquid || block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockLadder || block instanceof BlockTorch || block instanceof BlockRedstoneTorch || block instanceof BlockFence || block instanceof BlockFenceGate || block instanceof BlockPane || block instanceof BlockStainedGlassPane || block instanceof BlockGravel || block instanceof BlockClay || block instanceof BlockSand || block instanceof BlockSoulSand) {
            return false;
        }
        return true;
    }
}
