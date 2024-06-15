package keystrokesmod.module.impl.world;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RotationUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class WaterBucket extends Module {
    private final SliderSetting minDistance;
    private final ButtonSetting silentAim;
    private final ButtonSetting switchToItem;

    public WaterBucket() {
        super("Water bucket", category.world, 0);
        this.registerSetting(minDistance = new SliderSetting("Min distance", 3, 0, 20, 0.5));
        this.registerSetting(silentAim = new ButtonSetting("Silent aim", true));
        this.registerSetting(switchToItem = new ButtonSetting("Switch to item", true));
    }

    @SubscribeEvent
    public void onPreMotion(@NotNull PreMotionEvent e) {
        MovingObjectPosition rayCast = RotationUtils.rayCast(mc.playerController.getBlockReachDistance(), e.getYaw(), 90);
        if (inPosition() && rayCast != null && rayCast.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && holdWaterBucket(switchToItem.isToggled())) {
            if (silentAim.isToggled()) {
                e.setPitch(90);
            }
            else {
                mc.thePlayer.rotationPitch = 90;
            }
            sendPlace();
        }
    }

    private boolean inPosition() {
        return !mc.thePlayer.capabilities.isFlying && !mc.thePlayer.capabilities.isCreativeMode
                && !mc.thePlayer.onGround && !mc.thePlayer.isInWater() && mc.thePlayer.fallDistance >= minDistance.getInput();
    }

    private boolean holdWaterBucket(boolean setSlot) {
        if (this.containsWater(mc.thePlayer.getHeldItem())) {
            return true;
        } else {
            for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
                if (this.containsWater(mc.thePlayer.inventory.mainInventory[i]) && setSlot) {
                    mc.thePlayer.inventory.currentItem = i;
                    return true;
                }
            }

            return false;
        }
    }

    private boolean containsWater(ItemStack itemStack) {
        return itemStack != null && itemStack.getItem() == Items.water_bucket;
    }

    private void sendPlace() {
        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
    }
}
