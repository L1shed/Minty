package keystrokesmod.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Mouse;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    public static Method rightClickMouse;
    public static Field shaderResourceLocations;
    public static Field useShader;
    public static Field shaderIndex;
    public static Method loadShader;
    public static Field inGround;

    public static void getFields() {
        try {
            button = MouseEvent.class.getDeclaredField("button");
            buttonstate = MouseEvent.class.getDeclaredField("buttonstate");
            buttons = Mouse.class.getDeclaredField("buttons");

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

            shaderResourceLocations = ReflectionHelper.findField(EntityRenderer.class, "shaderResourceLocations", "field_147712_ad");
            if (shaderResourceLocations != null) {
                shaderResourceLocations.setAccessible(true);
            }

            useShader = ReflectionHelper.findField(EntityRenderer.class, "useShader");
            if (useShader != null) {
                useShader.setAccessible(true);
            }

            shaderIndex = ReflectionHelper.findField(EntityRenderer.class, "field_147713_ae", "shaderIndex");
            if (shaderIndex != null) {
                shaderIndex.setAccessible(true);
            }

            inGround = ReflectionHelper.findField(EntityArrow.class, "field_70254_i", "inGround");
            if (inGround != null) {
                inGround.setAccessible(true);
            }
        } catch (Exception var2) {
            System.out.println("There was an error, relaunch the game.");
            var2.printStackTrace();
        }
    }

    public static void getMethods() {
        try {
            rightClickMouse = Minecraft.getMinecraft().getClass().getDeclaredMethod("func_147121_ag");
        } catch (NoSuchMethodException var4) {
            try {
                rightClickMouse = Minecraft.getMinecraft().getClass().getDeclaredMethod("rightClickMouse");
            } catch (NoSuchMethodException var3) {
            }
        }

        if (rightClickMouse != null) {
            rightClickMouse.setAccessible(true);
        }

        loadShader = ReflectionHelper.findMethod(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, new String[]{"func_175069_a", "loadShader"}, ResourceLocation.class);

        if (loadShader != null) {
            loadShader.setAccessible(true);
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
                bf.put(t, (byte) (s ? 1 : 0));
            } catch (IllegalAccessException var4) {
            }
        }
    }

    public static void rightClick() {
        try {
            Reflection.rightClickMouse.invoke(Minecraft.getMinecraft());
        }
        catch (InvocationTargetException ex) {}
        catch (IllegalAccessException ex2) {}
    }
}
