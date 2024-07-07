package keystrokesmod.module.impl.combat;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.BlockUtils;
import keystrokesmod.utility.PacketUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLadder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class AutoPot extends Module {
    private final SliderSetting health;
    private final ButtonSetting randomRot;

    private int ticksSinceLastSplash, ticksSinceCanSplash, oldSlot;
    private boolean needSplash, switchBack;

    private final ArrayList<Integer> acceptedPotions = new ArrayList<>(Arrays.asList(6, 1, 5, 8, 14, 12, 10, 16));

    public AutoPot() {
        super("AutoPot", category.combat);
        this.registerSetting(new DescriptionSetting("Automatically throws potions."));
        this.registerSetting(health = new SliderSetting("Health", 10, 1, 20, 1));
        this.registerSetting(randomRot = new ButtonSetting("Randomized Rotations", true));
    }

    @Override
    public void onDisable() {
        needSplash = switchBack = false;
    }

    @SubscribeEvent
    public void onPreMotion(final PreMotionEvent event) {
        ticksSinceLastSplash++;

        if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava() || (BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY-1, mc.thePlayer.posZ )) instanceof BlockAir || BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY-1, mc.thePlayer.posZ )) instanceof BlockLadder))
            ticksSinceCanSplash = 0;
        else
            ticksSinceCanSplash++;

        if (switchBack) {
            PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            PacketUtils.sendPacket(new C09PacketHeldItemChange(oldSlot));
            switchBack = false;
            return;
        }

        if (ticksSinceCanSplash <= 1 || !mc.thePlayer.onGround)
            return;

        oldSlot = mc.thePlayer.inventory.currentItem;

        for (int i = 36; i < 45; ++i) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack != null && mc.currentScreen == null) {
                final Item item = itemStack.getItem();
                if (item instanceof ItemPotion) {
                    final ItemPotion p = (ItemPotion) item;
                    if (ItemPotion.isSplash(itemStack.getMetadata()) && p.getEffects(itemStack.getMetadata()) != null) {
                        final int potionID = p.getEffects(itemStack.getMetadata()).get(0).getPotionID();
                        boolean hasPotionIDActive = false;

                        for (final PotionEffect potion : mc.thePlayer.getActivePotionEffects()) {
                            if (potion.getPotionID() == potionID && potion.getDuration() > 0) {
                                hasPotionIDActive = true;
                                break;
                            }
                        }

                        if (acceptedPotions.contains(potionID) && !hasPotionIDActive && ticksSinceLastSplash > 20) {
                            final String effectName = p.getEffects(itemStack.getMetadata()).get(0).getEffectName();

                            if ((effectName.contains("regeneration") || effectName.contains("heal")) && mc.thePlayer.getHealth() > health.getInput()) {
                                continue;
                            } else {
                                event.setPitch(randomRot.isToggled() ? RandomUtils.nextFloat(85, 90) : 90);
                                if (!needSplash) {
                                    needSplash = true;
                                } else {
                                    PacketUtils.sendPacket(new C09PacketHeldItemChange(i - 36));
                                    PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(itemStack));
                                    switchBack = true;

                                    ticksSinceLastSplash = 0;
                                    needSplash = false;
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
    }
}
