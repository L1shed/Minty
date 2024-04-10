package keystrokesmod.module.impl.player;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoFall extends Module {
    private SliderSetting mode;
    private SliderSetting minFallDistance;
    private String[] modes = new String[]{"Spoof", "Extra"};

    public NoFall() {
        super("NoFall", Module.category.player, 0);
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(minFallDistance = new SliderSetting("Minimum fall distance", 3.0, 0.0, 8.0, 0.1));
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) {
        if ((double) mc.thePlayer.fallDistance > minFallDistance.getInput()) {
            switch ((int) mode.getInput()) {
                case 0:
                    e.setOnGround(true);
                    break;
                case 1:
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                    break;
            }
        }
    }

    @Override
    public String getInfo() {
        return modes[(int) mode.getInput()];
    }
}
