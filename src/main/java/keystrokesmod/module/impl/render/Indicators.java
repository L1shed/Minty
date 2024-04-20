package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Reflection;
import keystrokesmod.utility.Utils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Iterator;

public class Indicators extends Module {
    private ButtonSetting renderArrows;
    private ButtonSetting renderPearls;
    private ButtonSetting renderFireballs;
    private ButtonSetting renderPlayers;
    private SliderSetting radius;
    private ButtonSetting renderItem;
    private ButtonSetting threatsOnly;
    private HashSet<Entity> threats = new HashSet<>();

    public Indicators() {
        super("Indicators", category.render);
        this.registerSetting(renderArrows = new ButtonSetting("Render arrows", true));
        this.registerSetting(renderPearls = new ButtonSetting("Render ender pearls", true));
        this.registerSetting(renderFireballs = new ButtonSetting("Render fireballs", true));
        this.registerSetting(renderPlayers = new ButtonSetting("Render players", true));
        this.registerSetting(radius = new SliderSetting("Circle radius", 50, 5, 250, 2));
        this.registerSetting(renderItem = new ButtonSetting("Render item", true));
        this.registerSetting(threatsOnly = new ButtonSetting("Render only threats", true));
    }

    public void onDisable() {
        this.threats.clear();
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        if (mc.currentScreen != null || !Utils.nullCheck()) {
            return;
        }
        if (threats.isEmpty()) {
            return;
        }
        Iterator<Entity> iterator = threats.iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            try {
                if (entity instanceof EntityArrow && Reflection.inGround.getBoolean(entity)) {
                    iterator.remove();
                    continue;
                }
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
                continue;
            }
            float rotationDegree = Utils.getYaw(entity) - mc.thePlayer.rotationYaw;
            ItemStack entityItem;
            if (entity instanceof EntityEnderPearl) {
                entityItem = new ItemStack(Items.ender_pearl);
            } else if (entity instanceof EntityArrow) {
                entityItem = new ItemStack(Items.arrow);
            } else {
                entityItem = new ItemStack(Items.fire_charge);
            }
            ScaledResolution sr = new ScaledResolution(mc);
            float x = sr.getScaledWidth( )/ 2;
            float y = sr.getScaledHeight() / 2;
            GL11.glPushMatrix();
            GL11.glTranslatef(x, y, 0.0f);
            GL11.glRotatef(rotationDegree, 0.0f, 0.0f, 1.0f);
            mc.getRenderItem().renderItemIntoGUI(entityItem, 0, 0);
            GL11.glPopMatrix();
        }
    }


    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent e) {
        if (!Utils.nullCheck()) {
            return;
        }
        if (e.entity == mc.thePlayer) {
            this.threats.clear();
        } else if (canRender(e.entity) && (mc.thePlayer.getDistanceSqToEntity(e.entity) > 16.0 || !threatsOnly.isToggled())) {
            this.threats.add(e.entity);
        }
    }

    private boolean canRender(Entity entity) {
        try {
            if (entity instanceof EntityArrow && !Reflection.inGround.getBoolean(entity) && renderArrows.isToggled()) {
                return true;
            }
            else if (entity instanceof EntityFireball && renderFireballs.isToggled()) {
                return true;
            }
            else if (entity instanceof EntityEnderPearl && renderPearls.isToggled()) {
                return true;
            }
            else if (entity instanceof EntityPlayer && renderPlayers.isToggled()) {
                return true;
            }
        } catch (IllegalAccessException e) {
            Utils.sendMessage("&cIssue checking entity.");
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
