package keystrokesmod.module.impl.player;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.ContainerUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemSkull;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoHeal extends Module {
    private final SliderSetting minHealth;
    private final SliderSetting healDelay;
    private final SliderSetting startDelay;
    private final SliderSetting switchBackDelay;
    private long lastHeal = -1;
    private long lastSwitchTo = -1;
    private long lastDoneUse = -1;
    private int lastSlot = -1;
    public AutoHeal() {
        super("AutoHeal", category.player);
        this.registerSetting(new DescriptionSetting("help you win Hypixel BUhc."));
        this.registerSetting(minHealth = new SliderSetting("Min health", 10, 0, 20, 1));
        this.registerSetting(healDelay = new SliderSetting("Heal delay", 500, 0, 1500, 1));
        this.registerSetting(startDelay = new SliderSetting("Start delay", 30, 0, 300, 1));
        this.registerSetting(switchBackDelay = new SliderSetting("Switch back delay", 40, 0, 300, 1));
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
       if (!Utils.nullCheck() || mc.thePlayer.isDead) return;
       if (System.currentTimeMillis() - lastHeal < healDelay.getInput()) return;

       if (mc.thePlayer.getHealth() <= minHealth.getInput()) {
           if (lastSwitchTo == -1) {
               int toSlot = ContainerUtils.getSlot(ItemSkull.class);
               if (toSlot == -1) return;

               lastSlot = mc.thePlayer.inventory.currentItem;
               mc.thePlayer.inventory.currentItem = toSlot;
               lastSwitchTo = System.currentTimeMillis();
           }
       }

       if (lastSwitchTo != -1) {
           if (lastDoneUse == -1) {
               if (System.currentTimeMillis() - lastSwitchTo < startDelay.getInput()) return;
               KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
               lastDoneUse = System.currentTimeMillis();
           } else {
               if (System.currentTimeMillis() - lastDoneUse < switchBackDelay.getInput()) return;
               KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
               mc.thePlayer.inventory.currentItem = lastSlot;

               lastSwitchTo = -1;
               lastDoneUse = -1;
               lastSlot = -1;
               lastHeal = System.currentTimeMillis();
           }
       }
    }
}
