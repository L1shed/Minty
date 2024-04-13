package keystrokesmod.utility;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;

public class PlayerData {
    public double a;
    public int b;
    public int c;
    public int d;
    public int e;
    public int f;
    public double g;
    public int h;
    public int i;
    public double j;
    public boolean k;
    public double l;

    public void update(EntityPlayer entityPlayer) {
        final int ticksExisted = entityPlayer.ticksExisted;
        this.l = entityPlayer.posX - entityPlayer.lastTickPosX;
        this.j = entityPlayer.posY - entityPlayer.lastTickPosY;
        this.g = entityPlayer.posZ - entityPlayer.lastTickPosZ;
        this.a = Math.max(Math.abs(this.l), Math.abs(this.g));
        if (this.a >= 0.07) {
            ++this.c;
            this.e = ticksExisted;
        }
        else {
            this.c = 0;
        }
        if (Math.abs(this.j) >= 0.1) {
            this.b = ticksExisted;
        }
        if (entityPlayer.isSneaking()) {
            this.f = ticksExisted;
        }
        if (entityPlayer.isSwingInProgress && entityPlayer.isBlocking()) {
            ++this.d;
        }
        else {
            this.d = 0;
        }
        if (entityPlayer.isSprinting() && entityPlayer.isUsingItem()) {
            ++this.i;
        }
        else {
            this.i = 0;
        }
        if (entityPlayer.rotationPitch >= 70.0f && entityPlayer.getHeldItem() != null && entityPlayer.getHeldItem().getItem() instanceof ItemBlock) {
            if (entityPlayer.swingProgressInt == 1) {
                if (!this.k && entityPlayer.isSneaking()) {
                    ++this.h;
                }
                else {
                    this.h = 0;
                }
            }
        }
        else {
            this.h = 0;
        }
    }

    public void updateSneak(final EntityPlayer entityPlayer) {
        this.k = entityPlayer.isSneaking();
    }
}
