package keystrokesmod.module.impl.movement.fly;

import keystrokesmod.event.*;
import keystrokesmod.module.impl.movement.Fly;
import keystrokesmod.module.impl.other.SlotHandler;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.module.setting.impl.SubMode;
import keystrokesmod.utility.ContainerUtils;
import keystrokesmod.utility.MoveUtil;
import net.minecraft.item.ItemBow;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class TestFly extends SubMode<Fly> {
    private int ticksSinceVelocity = 999;
    private float yaw;

    private final SliderSetting speed;
    private final SliderSetting motionY;

    public TestFly(String name, @NotNull Fly parent) {
        super(name, parent);
        this.registerSetting(speed = new SliderSetting("Speed", 1.0, 1.0, 2.0, 0.01));
        this.registerSetting(motionY = new SliderSetting("MotionY", 0.42, 0.2, 1, 0.01));
    }

    @SubscribeEvent
    public void onPreMotionEvent(@NotNull PreMotionEvent event) {
        event.setPitch(-89);
        event.setYaw(yaw);
    }

    @Override
    public void onEnable() throws Exception {
        yaw = mc.thePlayer.rotationYaw;
    }

    @Override
    public void onUpdate() {
        SlotHandler.setCurrentSlot(ContainerUtils.getSlot(ItemBow.class));
        ticksSinceVelocity++;
    }

    @SubscribeEvent
    public void onPreVelocity(@NotNull PreVelocityEvent event) {
        ticksSinceVelocity = 0;
        final float yaw = (float) MoveUtil.direction();
        event.setMotionX((int) (-MathHelper.sin(yaw) * event.getMotionX() * speed.getInput()));
        event.setMotionY((int) (motionY.getInput() * 8000.0));
        event.setMotionZ((int) (MathHelper.cos(yaw) * event.getMotionZ() * speed.getInput()));
        this.yaw = mc.thePlayer.rotationYaw;
    }

    @SubscribeEvent
    public void onMove(@NotNull MoveEvent event) {
        if (ticksSinceVelocity >= 6) {
            event.setCanceled(true);
        }
    }
}
