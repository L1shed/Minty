package keystrokesmod.module.impl.combat;

import keystrokesmod.module.Module;
import net.minecraft.entity.EntityLivingBase;

public class KillAura extends Module {
    public static EntityLivingBase target;

    public KillAura() {
        super("KillAura", category.combat);
    }


}
