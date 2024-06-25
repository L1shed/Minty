package keystrokesmod.mixins.impl.client;

import keystrokesmod.event.PreTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "runTick", at = @At("HEAD"))
    private void runTickPre(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new PreTickEvent());
    }
}
