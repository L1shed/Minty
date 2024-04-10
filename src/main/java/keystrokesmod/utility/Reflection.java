package keystrokesmod.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Mouse;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class Reflection {
    public static Field button;
    public static Field buttonstate;
    public static Field buttons;
    public static Field leftClickCounter;
    public static Field jumpTicks;
    public static Field rightClickDelayTimerField;
    public static Field curBlockDamageMP;
    public static Field blockHitDelay;
    public static void getFields() {
       try {
          button = MouseEvent.class.getDeclaredField("button");
          buttonstate = MouseEvent.class.getDeclaredField("buttonstate");
          buttons = Mouse.class.getDeclaredField("buttons");
       } catch (Exception var2) {
       }

        leftClickCounter = ReflectionHelper.findField(Minecraft.class, "field_71429_W", "leftClickCounter");

        if (leftClickCounter != null) {
            leftClickCounter.setAccessible(true);
        }

        jumpTicks = ReflectionHelper.findField(EntityLivingBase.class, "field_70773_bE", "jumpTicks");

        if (jumpTicks != null) {
            jumpTicks.setAccessible(true);
        }

        rightClickDelayTimerField = ReflectionHelper.findField(Minecraft.class, "field_71467_ac", "rightClickDelayTimer");

        if (rightClickDelayTimerField != null) {
            rightClickDelayTimerField.setAccessible(true);
        }

        curBlockDamageMP = ReflectionHelper.findField(PlayerControllerMP.class, "field_78770_f", "curBlockDamageMP");
        if (curBlockDamageMP != null) {
            curBlockDamageMP.setAccessible(true);
        }

        blockHitDelay = ReflectionHelper.findField(PlayerControllerMP.class, "field_78781_i", "blockHitDelay");
        if (blockHitDelay != null) {
            blockHitDelay.setAccessible(true);
        }
    }

    public static void setButton(int t, boolean s) {
       if (button != null && buttonstate != null && buttons != null) {
          MouseEvent m = new MouseEvent();

          try {
             button.setAccessible(true);
             button.set(m, t);
             buttonstate.setAccessible(true);
             buttonstate.set(m, s);
             MinecraftForge.EVENT_BUS.post(m);
             buttons.setAccessible(true);
             ByteBuffer bf = (ByteBuffer) buttons.get(null);
             buttons.setAccessible(false);
             bf.put(t, (byte)(s ? 1 : 0));
          } catch (IllegalAccessException var4) {
          }
       }
    }
}
