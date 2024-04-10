package keystrokesmod.clickgui.components.impl;

import keystrokesmod.clickgui.components.Component;
import keystrokesmod.module.impl.client.Gui;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BindComponent extends Component {
    private boolean h;
    private boolean isBinding;
    private ModuleComponent moduleComponent;
    private int bind;
    private int x;
    private int y;

    public BindComponent(ModuleComponent moduleComponent, int bind) {
        this.moduleComponent = moduleComponent;
        this.x = moduleComponent.categoryComponent.getX() + moduleComponent.categoryComponent.gw();
        this.y = moduleComponent.categoryComponent.getY() + moduleComponent.o;
        this.bind = bind;
    }

    public void so(int n) {
        this.bind = n;
    }

    public void render() {
        GL11.glPushMatrix();
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        this.drawString(!this.moduleComponent.mod.canBeEnabled ? "This module cannot be bound." : this.isBinding ? "Press a key..." : "Bind" + ": " + Keyboard.getKeyName(this.moduleComponent.mod.getKeycode()));
        GL11.glPopMatrix();
    }

    public void drawScreen(int x, int y) {
        this.h = this.i(x, y);
        this.y = this.moduleComponent.categoryComponent.getY() + this.bind;
        this.x = this.moduleComponent.categoryComponent.getX();
    }

    public void onClick(int x, int y, int b) {
        if (this.i(x, y) && b == 0 && this.moduleComponent.po) {
            this.isBinding = !this.isBinding;
        }
    }

    public void keyTyped(char t, int keybind) {
        if (this.isBinding) {
            if (keybind == 11) {
                if (this.moduleComponent.mod instanceof Gui) {
                    this.moduleComponent.mod.setBind(54);
                } else {
                    this.moduleComponent.mod.setBind(0);
                }
            } else {
                this.moduleComponent.mod.setBind(keybind);
            }

            this.isBinding = false;
        }
    }

    public boolean i(int x, int y) {
        return x > this.x && x < this.x + this.moduleComponent.categoryComponent.gw() && y > this.y - 1 && y < this.y + 12;
    }

    public int gh() {
        return 16;
    }

    private void drawString(String s) {
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(s, (float) ((this.moduleComponent.categoryComponent.getX() + 4) * 2), (float) ((this.moduleComponent.categoryComponent.getY() + this.bind + 3) * 2), Color.HSBtoRGB((float) (System.currentTimeMillis() % 3750L) / 3750.0F, 0.8F, 0.8F));
    }
}
