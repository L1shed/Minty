package keystrokesmod.module.impl.movement;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.utility.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Step extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", new String[]{"HypixelTest"}, 0);
    private final SliderSetting delay = new SliderSetting("Delay", 1000, 0, 5000, 250, "ms");
    private final SliderSetting timer = new SliderSetting("Timer", 1, 0.1, 2, 0.1, "x");

    private int offGroundTicks = -1;
    private boolean stepping = false;
    private long lastStep = -1;

    public Step() {
        super("Step", category.movement);
        this.registerSetting(mode, delay);
    }

    @Override
    public void onDisable() {
        offGroundTicks = -1;
        stepping = false;
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent event) {
        final long time = System.currentTimeMillis();
        if (mc.thePlayer.onGround && mc.thePlayer.isCollidedHorizontally && MoveUtil.isMoving() && time - lastStep >= delay.getInput()) {
            stepping = true;
            lastStep = time;
        }
    }

    @SubscribeEvent
    public void onPreUpdate(PreUpdateEvent event) {
        if (mc.thePlayer.onGround) {
            offGroundTicks = 0;
        } else if (offGroundTicks != -1) {
            offGroundTicks++;
        }

        if (stepping) {
            if (timer.getInput() != 1) {
                Utils.getTimer().timerSpeed = (float) timer.getInput();
            }
            switch (offGroundTicks) {
                case 0:
                    mc.thePlayer.motionY = 0.4196;
                    break;
                case 3:
                case 4:
                    mc.thePlayer.motionY = 0;
                    break;
                case 5:
                    stepping = false;
                    if (timer.getInput() != 1) {
                        Utils.resetTimer();
                    }
                    break;
            }
        }
    }

    @Override
    public String getInfo() {
        return mode.getOptions()[(int) mode.getInput()];
    }
}
