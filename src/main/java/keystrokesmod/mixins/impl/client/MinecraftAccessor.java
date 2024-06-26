package keystrokesmod.mixins.impl.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {

    @Invoker("clickMouse")
    void leftClickMouse();

    @Invoker("rightClickMouse")
    void rightClickMouse();
}
