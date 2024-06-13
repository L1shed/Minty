package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.DescriptionSetting;

public class CustomName extends Module {
    public CustomName() {
        super("CustomName", category.render);
        this.registerSetting(new DescriptionSetting("allow you change module's name."));
        this.registerSetting(new DescriptionSetting("Command: rename <module> <name>"));
    }
}
