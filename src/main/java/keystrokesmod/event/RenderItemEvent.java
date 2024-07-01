package keystrokesmod.event;

import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public final class RenderItemEvent extends Event {
    private EnumAction enumAction;
    private boolean useItem;
    private float animationProgression, partialTicks, swingProgress;
    private ItemStack itemToRender;

    public RenderItemEvent(EnumAction enumAction, boolean useItem, float animationProgression, float partialTicks, float swingProgress, ItemStack itemToRender) {
        this.enumAction = enumAction;
        this.useItem = useItem;
        this.animationProgression = animationProgression;
        this.partialTicks = partialTicks;
        this.swingProgress = swingProgress;
        this.itemToRender = itemToRender;
    }

    public EnumAction getEnumAction() {
        return enumAction;
    }

    public void setEnumAction(EnumAction enumAction) {
        this.enumAction = enumAction;
    }

    public boolean isUseItem() {
        return useItem;
    }

    public void setUseItem(boolean useItem) {
        this.useItem = useItem;
    }

    public float getAnimationProgression() {
        return animationProgression;
    }

    public void setAnimationProgression(float animationProgression) {
        this.animationProgression = animationProgression;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getSwingProgress() {
        return swingProgress;
    }

    public void setSwingProgress(float swingProgress) {
        this.swingProgress = swingProgress;
    }

    public ItemStack getItemToRender() {
        return itemToRender;
    }

    public void setItemToRender(ItemStack itemToRender) {
        this.itemToRender = itemToRender;
    }
}