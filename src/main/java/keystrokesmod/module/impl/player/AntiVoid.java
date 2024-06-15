package keystrokesmod.module.impl.player;

import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static keystrokesmod.module.ModuleManager.*;

public class AntiVoid extends Module {

    private final SliderSetting distance;
    private final ButtonSetting toggleScaffold;
    private Vec3 position, motion;
    private boolean wasVoid, setBack;
    private int overVoidTicks;
    private boolean disabledForLongJump = false;

    public AntiVoid() {
        super("AntiVoid", category.player);
        this.registerSetting(new DescriptionSetting("Prevent you from falling into the void."));
        this.registerSetting(distance = new SliderSetting("Distance", 5, 0, 10, 1));
        this.registerSetting(toggleScaffold = new ButtonSetting("Toggle scaffold", false));
    }

    @Override
    public void onDisable() {
        blink.disable();
    }

    @SubscribeEvent
    public void onPreUpdate(PreUpdateEvent event) {
        if (mc.thePlayer.capabilities.allowFlying) return;
        if (mc.thePlayer.ticksExisted <= 50) return;

        if (disabledForLongJump && mc.thePlayer.onGround)
            disabledForLongJump = false;

        if (longJump.isEnabled())
            disabledForLongJump = true;
        if ((scaffold.isEnabled() && scaffold.totalBlocks() == 0) || fly.isEnabled() || motionModifier.isEnabled() || disabledForLongJump) {
            blink.disable();
            return;
        }

        boolean overVoid = !mc.thePlayer.onGround && !isBlockUnder();

        if (overVoid) {
            overVoidTicks++;
        } else if (mc.thePlayer.onGround) {
            overVoidTicks = 0;
        }

        if (overVoid && position != null && motion != null && overVoidTicks < 30 + distance.getInput() * 20) {
            if (!setBack) {
                wasVoid = true;

                blink.enable();

                if (mc.thePlayer.fallDistance > distance.getInput() || setBack) {
                    sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(position.xCoord, position.yCoord - 0.1 - Math.random(), position.zCoord, false));
                    if (this.toggleScaffold.isToggled()) {
                        scaffold.enable();
                    }

                    ((Blink) blink).blinkedPackets.clear();

                    mc.thePlayer.fallDistance = 0;

                    setBack = true;
                }
            } else {
                blink.disable();
            }
        } else {

            setBack = false;

            if (wasVoid) {
                blink.disable();
                wasVoid = false;
            }

            motion = new Vec3(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
            position = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        }
    }

    private boolean isBlockUnder() {
        for (int offset = 0; offset < (double) 30; offset += 2) {
            final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);

            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void sendNoEvent(final Packet<?> packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }
}