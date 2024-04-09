package keystrokesmod.mixins.impl.render;

import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.other.NameHider;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontRenderer.class)
public class MixinFontRenderer {
    @Inject(method = "renderStringAtPos", at = @At("HEAD"))
    private void renderStringAtPos(String p_renderStringAtPos_1_, boolean p_renderStringAtPos_2_, CallbackInfo ci) {
        if ((ModuleManager.nameHider != null) && ModuleManager.nameHider.isEnabled()) {
            p_renderStringAtPos_1_ = NameHider.getFakeName(p_renderStringAtPos_1_);
        }
    }

    @Inject(method = "getStringWidth", at = @At("HEAD"))
    public void getStringWidth(String p_getStringWidth_1_, CallbackInfoReturnable<Integer> cir) {
        if ((ModuleManager.nameHider != null) && ModuleManager.nameHider.isEnabled()) {
            p_getStringWidth_1_ = NameHider.getFakeName(p_getStringWidth_1_);
        }
    }
}
