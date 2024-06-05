package keystrokesmod.module.impl.movement;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Reflection;
import keystrokesmod.utility.Utils;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class LongJump extends Module {
    private final SliderSetting mode;
    private final SliderSetting horizonBoost;
    private final SliderSetting verticalMotion;
    private final ButtonSetting jumpAtEnd;
    private int ticks = 0;
    private boolean start;
    private boolean done;
    public static boolean stopModules;
    private boolean waitForDamage = false;

    public LongJump() {
        super("Long Jump", category.movement);
        this.registerSetting(mode = new SliderSetting("Mode", new String[]{"Fireball", "Fireball Auto"}, 0));
        this.registerSetting(horizonBoost = new SliderSetting("Horizon boost", 1.0, 0.5, 3.0, 0.01));
        this.registerSetting(verticalMotion = new SliderSetting("Vertical motion", 0.35, 0.01, 0.4, 0.01));
        this.registerSetting(jumpAtEnd = new ButtonSetting("Jump at end.", false));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPreMotion(PreMotionEvent event) {
        if (mode.getInput() == 1 && !waitForDamage) {
            event.setPitch(90);
        }
    }

    @SubscribeEvent
    public void onReceivePacket(@NotNull ReceivePacketEvent event) throws IllegalAccessException {
        if (event.getPacket() instanceof S12PacketEntityVelocity && Utils.nullCheck()) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
            if (packet.getEntityID() == mc.thePlayer.getEntityId() && ticks == 0) {
                Reflection.S12PacketEntityVelocityXMotion.set(packet, (int) Math.floor(packet.getMotionX() * horizonBoost.getInput()));
                Reflection.S12PacketEntityVelocityZMotion.set(packet, (int) Math.floor(packet.getMotionZ() * horizonBoost.getInput()));
                start = true;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPreUpdate(PreUpdateEvent event) {
        if (mode.getInput() == 1 && !waitForDamage) {
            int shouldSlot = getFireball();
            if (shouldSlot != mc.thePlayer.inventory.currentItem) {
                mc.thePlayer.inventory.currentItem = shouldSlot;
            } else {
                mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
                waitForDamage = true;
            }
        }

        if (mc.thePlayer.hurtTime >= 3) {
            start = true;
        }

        if (start) {
            ticks++;
        }

        if (ticks > 0 && ticks < 30) {
            mc.thePlayer.motionY = verticalMotion.getInput();
        } else if (ticks >= 30) {
            done = true;
            start = false;
        }

        if (mc.thePlayer.hurtTime == 0 && done) {
            if (jumpAtEnd.isToggled())
                mc.thePlayer.motionY = 0.4;
            disable();
        }
    }

    public void onDisable() {
        start = false;
        done = false;
        waitForDamage = false;
        ticks = 0;
        stopModules = false;
    }

    public void onEnable() {
        if (getFireball() == -1 && mode.getInput() == 1) {
            Utils.sendMessage("§cNo fireball found.");
            this.disable();
            return;
        }
        stopModules = true;
        if (mode.getInput() == 0)
            waitForDamage = true;
    }

    private int getFireball() {
        int n = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack getStackInSlot = mc.thePlayer.inventory.getStackInSlot(i);
            if (getStackInSlot != null && getStackInSlot.getItem() == Items.fire_charge) {
                n = i;
                break;
            }
        }
        return n;
    }
}
