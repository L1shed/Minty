package keystrokesmod.mixins.impl.client;


import keystrokesmod.module.impl.other.SlotHandler;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Shadow private int currentPlayerItem;

    @Shadow @Final private NetHandlerPlayClient netClientHandler;

    /**
     * @author xia__mc
     * @reason for SlotHandler (silent switch)
     */
    @Overwrite
    private void syncCurrentPlayItem() {
        int i = SlotHandler.getCurrentSlot();
        if (i != this.currentPlayerItem) {
            this.currentPlayerItem = i;
            this.netClientHandler.addToSendQueue(new C09PacketHeldItemChange(this.currentPlayerItem));
        }

    }
}
