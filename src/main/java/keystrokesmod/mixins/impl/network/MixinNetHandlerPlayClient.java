package keystrokesmod.mixins.impl.network;


import keystrokesmod.Raven;
import keystrokesmod.event.PostVelocityEvent;
import keystrokesmod.utility.Utils;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {

    @Inject(method = "handleEntityVelocity", at = @At("RETURN"))
    public void onHandleEntityVelocity(S12PacketEntityVelocity packet, CallbackInfo ci) {
        if (!Utils.nullCheck()) return;

        if (packet.getEntityID() == Raven.mc.thePlayer.getEntityId()) {
            MinecraftForge.EVENT_BUS.post(new PostVelocityEvent());
        }
    }
}
