package keystrokesmod.module.impl.world;

import java.util.HashMap;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.module.impl.player.Freecam;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiBot extends Module {
   private static final HashMap<EntityPlayer, Long> entities = new HashMap();
   private static ButtonSetting entitySpawnDelay;
   private static SliderSetting delay;

   public AntiBot() {
      super("AntiBot", Module.category.world, 0);
      this.registerSetting(entitySpawnDelay = new ButtonSetting("Entity spawn delay", false));
      this.registerSetting(delay = new SliderSetting("Delay", 7.0, 0.5, 15.0, 0.5));
   }

   @SubscribeEvent
   public void c(final EntityJoinWorldEvent entityJoinWorldEvent) {
      if (entitySpawnDelay.isToggled() && entityJoinWorldEvent.entity instanceof EntityPlayer && entityJoinWorldEvent.entity != mc.thePlayer) {
         entities.put((EntityPlayer)entityJoinWorldEvent.entity, System.currentTimeMillis());
      }
   }

   public void onUpdate() {
      if (entitySpawnDelay.isToggled() && !entities.isEmpty()) {
         entities.values().removeIf(n -> n < System.currentTimeMillis() - 7000L);
      }
   }

   public void onDisable() {
      entities.clear();
   }

   public static boolean isBot(Entity entity) {
      if (!ModuleManager.antiBot.isEnabled()) {
         return false;
      }
      if (Freecam.freeEntity != null && Freecam.freeEntity == entity) {
         return true;
      }
      if (!(entity instanceof EntityPlayer)) {
         return true;
      }
      final EntityPlayer entityPlayer = (EntityPlayer)entity;
      if (entitySpawnDelay.isToggled() && !entities.isEmpty() && entities.containsKey(entityPlayer)) {
         return true;
      }
      if (entityPlayer.isDead) {
         return true;
      }
      if (entityPlayer.getName().isEmpty()) {
         return true;
      }
      if (entityPlayer.getHealth() != 20.0f && entityPlayer.getName().startsWith("§c")) {
         return true;
      }
      if (entityPlayer.maxHurtTime == 0) {
         if (entityPlayer.getHealth() == 20.0f) {
            String unformattedText = entityPlayer.getDisplayName().getUnformattedText();
            if (unformattedText.length() == 10 && unformattedText.charAt(0) != '§') {
               return true;
            }
            if (unformattedText.length() == 12 && entityPlayer.isPlayerSleeping() && unformattedText.charAt(0) == '§') {
               return true;
            }
            if (unformattedText.length() >= 7 && unformattedText.charAt(2) == '[' && unformattedText.charAt(3) == 'N' && unformattedText.charAt(6) == ']') {
               return true;
            }
            if (entityPlayer.getName().contains(" ")) {
               return true;
            }
         }
         else if (entityPlayer.isInvisible()) {
            String unformattedText = entityPlayer.getDisplayName().getUnformattedText();
            if (unformattedText.length() >= 3 && unformattedText.charAt(0) == '§' && unformattedText.charAt(1) == 'c') {
               return true;
            }
         }
      }
      return false;
   }
}
