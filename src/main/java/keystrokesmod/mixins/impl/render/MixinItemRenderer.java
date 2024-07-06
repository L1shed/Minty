package keystrokesmod.mixins.impl.render;


import keystrokesmod.event.RenderItemEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @Shadow private float prevEquippedProgress;

    @Shadow private float equippedProgress;

    @Shadow @Final private Minecraft mc;

    @Shadow protected abstract void func_178101_a(float p_178101_1_, float p_178101_2_);

    @Shadow protected abstract void func_178109_a(AbstractClientPlayer p_178109_1_);

    @Shadow protected abstract void func_178110_a(EntityPlayerSP p_178110_1_, float p_178110_2_);

    @Shadow private ItemStack itemToRender;
    /**
     * @author xia__mc
     * @reason for Animations module.
     */
    @Inject(method = "renderItemInFirstPerson", at = @At("HEAD"), cancellable = true)
    public void renderItemInFirstPerson(final float partialTicks, CallbackInfo ci) {
        float animationProgression = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        float swingProgress = mc.thePlayer.getSwingProgress(partialTicks);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();

        if (this.itemToRender != null) {
            EnumAction enumaction = this.itemToRender.getItemUseAction();
            boolean useItem = mc.thePlayer.getItemInUseCount() > 0;

            final RenderItemEvent event = new RenderItemEvent(enumaction, useItem, animationProgression, partialTicks, swingProgress, itemToRender);
            MinecraftForge.EVENT_BUS.post(event);

            if (event.isCanceled()) {
                ci.cancel();
            }
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }
}
