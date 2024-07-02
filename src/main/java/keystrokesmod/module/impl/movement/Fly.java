package keystrokesmod.module.impl.movement;

import keystrokesmod.event.PrePlayerInput;
import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.module.setting.utils.ModeOnly;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.utility.PacketUtils;
import keystrokesmod.utility.render.RenderUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;

public class Fly extends Module {
    private final ModeSetting mode;
    public static SliderSetting horizontalSpeed;
    private final SliderSetting verticalSpeed;
    private final ButtonSetting showBPS;
    private final ButtonSetting stopMotion;
    private boolean d;

    private int offGroundTicks = 0;
    private boolean started, notUnder, clipped, teleport;

    public Fly() {
        super("Fly", category.movement);
        this.registerSetting(mode = new ModeSetting("Fly", new String[]{"Vanilla", "Fast", "Fast 2", "BlocksMC"}, 0));
        final ModeOnly canChangeSpeed = new ModeOnly(mode, 0, 1, 2);
        this.registerSetting(horizontalSpeed = new SliderSetting("Horizontal speed", 2.0, 1.0, 9.0, 0.1, canChangeSpeed));
        this.registerSetting(verticalSpeed = new SliderSetting("Vertical speed", 2.0, 1.0, 9.0, 0.1, canChangeSpeed));
        this.registerSetting(showBPS = new ButtonSetting("Show BPS", false));
        this.registerSetting(stopMotion = new ButtonSetting("Stop motion", false));
    }

    @Override
    public String getInfo() {
        return mode.getOptions()[(int) mode.getInput()];
    }

    public void onEnable() {
        this.d = mc.thePlayer.capabilities.isFlying;

        notUnder = false;
        started = false;
        clipped = false;
        teleport = false;

        if ((int) mode.getInput() == 3) {
            Utils.sendMessage("Start the fly under the block and walk forward.");
        }
    }

    @SubscribeEvent
    public void onReceivePacket(@NotNull ReceivePacketEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            if (teleport) {
                event.setCanceled(true);
                teleport = false;
                Utils.sendMessage("Teleported!");
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInput(PrePlayerInput event) {
        if ((int) mode.getInput() != 3) return;

        final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, 1, 0);

        if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty() || started) {
            switch (offGroundTicks) {
                case 0:
                    if (notUnder) {
                        if (clipped) {
                            started = true;
                            event.setSpeed(10);
                            mc.thePlayer.motionY = 0.42f;
                            notUnder = false;
                        }
                    }
                    break;

                case 1:
                    if (started) event.setSpeed(9.6);
                    break;

                default:
//                    if (mc.thePlayer.fallDistance > 0 && started) {
//                        mc.thePlayer.motionY += 2.5 / 100f;
//                    }
                    break;
            }
        } else {
            notUnder = true;

            if (clipped) return;

            clipped = true;

            PacketUtils.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
            PacketUtils.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
            PacketUtils.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));

            teleport = true;
        }

        MoveUtil.strafe();

        Utils.getTimer().timerSpeed = 0.4f;
    }

    public void onUpdate() {
        if (mc.thePlayer.onGround) {
            offGroundTicks = 0;
        } else {
            offGroundTicks++;
        }

        switch ((int) mode.getInput()) {
            case 0:
                mc.thePlayer.motionY = 0.0;
                mc.thePlayer.capabilities.setFlySpeed((float)(0.05000000074505806 * horizontalSpeed.getInput()));
                mc.thePlayer.capabilities.isFlying = true;
                break;
            case 1:
                mc.thePlayer.onGround = true;
                if (mc.currentScreen == null) {
                    if (Utils.jumpDown()) {
                        mc.thePlayer.motionY = 0.3 * verticalSpeed.getInput();
                    }
                    else if (Utils.jumpDown()) {
                        mc.thePlayer.motionY = -0.3 * verticalSpeed.getInput();
                    }
                    else {
                        mc.thePlayer.motionY = 0.0;
                    }
                }
                else {
                    mc.thePlayer.motionY = 0.0;
                }
                mc.thePlayer.capabilities.setFlySpeed(0.2f);
                mc.thePlayer.capabilities.isFlying = true;
                setSpeed(0.85 * horizontalSpeed.getInput());
                break;
            case 2:
                double nextDouble = RandomUtils.nextDouble(1.0E-7, 1.2E-7);
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    nextDouble = -nextDouble;
                }
                if (!mc.thePlayer.onGround) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + nextDouble, mc.thePlayer.posZ);
                }
                mc.thePlayer.motionY = 0.0;
                setSpeed(0.4 * horizontalSpeed.getInput());
                break;
        }

    }

    public void onDisable() {
        if (mc.thePlayer.capabilities.allowFlying) {
            mc.thePlayer.capabilities.isFlying = this.d;
        }
        else {
            mc.thePlayer.capabilities.isFlying = false;
        }
        this.d = false;
        switch ((int) mode.getInput()) {
            case 0:
            case 1: {
                mc.thePlayer.capabilities.setFlySpeed(0.05F);
                break;
            }
            case 2: {
                break;
            }
        }
        if (stopMotion.isToggled()) {
            mc.thePlayer.motionZ = 0;
            mc.thePlayer.motionY = 0;
            mc.thePlayer.motionX = 0;
        }
        if ((int) mode.getInput() == 3) {
            MoveUtil.stop();
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        RenderUtils.renderBPS(showBPS.isToggled(), e);
    }

    public static void setSpeed(final double n) {
        if (n == 0.0) {
            mc.thePlayer.motionZ = 0;
            mc.thePlayer.motionX = 0;
            return;
        }
        double n3 = mc.thePlayer.movementInput.moveForward;
        double n4 = mc.thePlayer.movementInput.moveStrafe;
        float rotationYaw = mc.thePlayer.rotationYaw;
        if (n3 == 0.0 && n4 == 0.0) {
            mc.thePlayer.motionZ = 0;
            mc.thePlayer.motionX = 0;
        }
        else {
            if (n3 != 0.0) {
                if (n4 > 0.0) {
                    rotationYaw += ((n3 > 0.0) ? -45 : 45);
                }
                else if (n4 < 0.0) {
                    rotationYaw += ((n3 > 0.0) ? 45 : -45);
                }
                n4 = 0.0;
                if (n3 > 0.0) {
                    n3 = 1.0;
                }
                else if (n3 < 0.0) {
                    n3 = -1.0;
                }
            }
            final double radians = Math.toRadians(rotationYaw + 90.0f);
            final double sin = Math.sin(radians);
            final double cos = Math.cos(radians);
            mc.thePlayer.motionX = n3 * n * cos + n4 * n * sin;
            mc.thePlayer.motionZ = n3 * n * sin - n4 * n * cos;
        }
    }
}
