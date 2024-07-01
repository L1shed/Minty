package keystrokesmod.module.impl.render;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.RenderItemEvent;
import keystrokesmod.event.SwingAnimationEvent;
import keystrokesmod.mixins.impl.render.ItemRendererAccessor;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class Animations extends Module {
    private final ModeSetting blockAnimation = new ModeSetting("Block animation", new String[]{"None", "1.7", "Smooth"}, 1);
    private final ModeSetting swingAnimation = new ModeSetting("Swing animation", new String[]{"None", "1.9+", "Smooth"}, 0);
    private final SliderSetting x = new SliderSetting("X", 0, -2, 2, 0.05);
    private final SliderSetting y = new SliderSetting("Y", 0, -2, 2, 0.05);
    private final SliderSetting z = new SliderSetting("Z", 0, -2, 2, 0.05);
    private final SliderSetting swingSpeed = new SliderSetting("Swing speed", 0, -200, 50, 5);

    private int swing;

    public Animations() {
        super("Animations", category.render);
        this.registerSetting(blockAnimation, swingAnimation, x, y, z, swingSpeed);
    }

    @SubscribeEvent
    public void onRenderItem(@NotNull RenderItemEvent event) {
        if (event.getItemToRender().getItem() instanceof ItemMap) {
            return;
        }

        final EnumAction itemAction = event.getEnumAction();
        final ItemRendererAccessor itemRenderer = (ItemRendererAccessor) mc.getItemRenderer();
        final float animationProgression = event.getAnimationProgression();
        final float swingProgress = event.getSwingProgress();
        final float convertedProgress = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);

        if (event.isUseItem() && itemAction == EnumAction.BLOCK) {

            GlStateManager.translate(x.getInput(), y.getInput(), z.getInput());


            switch ((int) blockAnimation.getInput()) {
                case 0:
                    itemRenderer.transformFirstPersonItem(animationProgression, 0.0F);
                    itemRenderer.blockTransformation();

                    break;

                case 1:
                    itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
                    itemRenderer.blockTransformation();

                    break;

                case 2:
                    itemRenderer.transformFirstPersonItem(animationProgression, 0.0F);
                    final float y = -convertedProgress * 2.0F;
                    GlStateManager.translate(0.0F, y / 10.0F + 0.1F, 0.0F);
                    GlStateManager.rotate(y * 10.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(250, 0.2F, 1.0F, -0.6F);
                    GlStateManager.rotate(-10.0F, 1.0F, 0.5F, 1.0F);
                    GlStateManager.rotate(-y * 20.0F, 1.0F, 0.5F, 1.0F);

                    break;
            }

            event.setCanceled(true);

        } else if (!event.isUseItem() && (event.getItemToRender().getItem() instanceof ItemSword || event.getItemToRender().getItem() instanceof ItemAxe)) {

            switch ((int) swingAnimation.getInput()) {
                case 0:
                    func_178105_d(swingProgress);
                    itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
                    break;

                case 1:
                    func_178105_d(swingProgress);
                    itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
                    GlStateManager.translate(0, -((swing - 1) -
                            (swing == 0 ? 0 : Utils.getTimer().renderPartialTicks)) / 5f, 0);
                    break;

                case 2:
                    itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
                    func_178105_d(animationProgression);
                    break;
            }

            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent event) {
        if (mc.thePlayer.swingProgressInt == 1) {
            swing = 9;
        } else {
            swing = Math.max(0, swing - 1);
        }
    }

    @SubscribeEvent
    public void onSwingAnimation(@NotNull SwingAnimationEvent event) {
        event.setAnimationEnd(event.getAnimationEnd() * (int) ((-swingSpeed.getInput() / 100) + 1));
    }

    /**
     * LabyMod issue, but I need to compat it.
     * @see net.minecraft.client.renderer.ItemRenderer#func_178105_d(float swingProgress)
     */
    void func_178105_d(float swingProgress) {
        float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F * 2.0F);
        float f2 = -0.2F * MathHelper.sin(swingProgress * 3.1415927F);
        GlStateManager.translate(f, f1, f2);
    }
}
