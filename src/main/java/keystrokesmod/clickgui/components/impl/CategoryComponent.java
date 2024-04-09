package keystrokesmod.clickgui.components.impl;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import keystrokesmod.clickgui.components.Component;
import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.utility.profile.Manager;
import keystrokesmod.utility.profile.Profile;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

public class CategoryComponent {
   public List<ModuleComponent> modules = new CopyOnWriteArrayList<>();
   public Module.category categoryName;
   private boolean categoryOpened;
   private int k;
   private int y;
   private int x;
   private int bh;
   private boolean id;
   public int xx;
   public int yy;
   public boolean n4m = false;
   public String pvp;
   public boolean pin = false;
   private boolean sorted;

   public CategoryComponent(Module.category category) {
      this.categoryName = category;
      this.k = 92;
      this.x = 5;
      this.y = 5;
      this.bh = 13;
      this.xx = 0;
      this.categoryOpened = false;
      this.id = false;
      int tY = this.bh + 3;

      for (Iterator var3 = Raven.getModuleManager().inCategory(this.categoryName).iterator(); var3.hasNext(); tY += 16) {
         Module mod = (Module)var3.next();
         ModuleComponent b = new ModuleComponent(mod, this, tY);
         this.modules.add(b);
      }
   }

   public List<ModuleComponent> getModules() {
      return this.modules;
   }

   public void reloadModules() {
      this.modules.clear();
      this.bh = 13;
      int tY = this.bh + 3;

      ModuleComponent manager = new ModuleComponent(new Manager(), this, tY);
      this.modules.add(manager);

      if (Raven.profileManager == null) {
         return;
      }

      for (Profile profile : Raven.profileManager.profiles) {
         tY += 16;
         ModuleComponent b = new ModuleComponent(profile.getModule(), this, tY);
         this.modules.add(b);
      }
   }
   public void x(int n) {
      this.x = n;
   }

   public void y(int y) {
      this.y = y;
   }

   public void d(boolean d) {
      this.id = d;
   }

   public boolean p() {
      return this.pin;
   }

   public void cv(boolean on) {
      this.pin = on;
   }

   public boolean fv() {
      return this.categoryOpened;
   }

   public void oo(boolean on) {
      this.categoryOpened = on;
   }

   public void rf(FontRenderer renderer) {
      this.k = 92;
      if (!this.modules.isEmpty() && this.categoryOpened) {
         int h = 0;

         Component c;
         for(Iterator var3 = this.modules.iterator(); var3.hasNext(); h += c.gh()) {
            c = (Component)var3.next();
         }

         net.minecraft.client.gui.Gui.drawRect(this.x - 2, this.y, this.x + this.k + 2, this.y + this.bh + h + 4, (new Color(0, 0, 0, 110)).getRGB());
      }

      ButtonComponent.d((float)(this.x - 2), (float)this.y, (float)(this.x + this.k + 2), (float)(this.y + this.bh + 3), -1);
      renderer.drawString(this.n4m ? this.pvp : this.categoryName.name(), (float)(this.x + 2), (float)(this.y + 4), new Color(220, 220, 220).getRGB(), false);
      if (!this.n4m) {
         GL11.glPushMatrix();
         renderer.drawString(this.categoryOpened ? "-" : "+", (float)(this.x + 80), (float)((double)this.y + 4.5D), this.categoryOpened ? new Color(250, 95, 85).getRGB() : new Color(135, 238, 144).getRGB(), false);
         GL11.glPopMatrix();
         if (this.categoryOpened && !this.modules.isEmpty()) {
            Iterator var5 = this.modules.iterator();

            while(var5.hasNext()) {
               Component c2 = (Component)var5.next();
               c2.render();
            }
         }

      }
   }

   public void render() {
      int o = this.bh + 3;

      Component c;
      for(Iterator var2 = this.modules.iterator(); var2.hasNext(); o += c.gh()) {
         c = (Component)var2.next();
         c.so(o);
      }

   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int gw() {
      return this.k;
   }

   public void up(int x, int y) {
      if (this.id) {
         this.x(x - this.xx);
         this.y(y - this.yy);
      }

   }

   public boolean i(int x, int y) {
      return x >= this.x + 92 - 13 && x <= this.x + this.k && (float)y >= (float)this.y + 2.0F && y <= this.y + this.bh + 1;
   }

   public boolean d(int x, int y) {
      return x >= this.x + 77 && x <= this.x + this.k - 6 && (float)y >= (float)this.y + 2.0F && y <= this.y + this.bh + 1;
   }

   public boolean v(int x, int y) {
      return x >= this.x && x <= this.x + this.k && y >= this.y && y <= this.y + this.bh;
   }
}
