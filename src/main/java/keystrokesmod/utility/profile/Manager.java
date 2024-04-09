package keystrokesmod.utility.profile;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.Utils;

import java.awt.*;

public class Manager extends Module {
    private ButtonSetting loadProfiles, openFolder;
    public Manager() {
        super("Manager", category.profiles);
        this.registerSetting(loadProfiles = new ButtonSetting("Load profiles", () -> {
            if (Utils.nullCheck() && Raven.profileManager != null) {
                Utils.sendMessage("&b" + Raven.profileManager.getProfileFiles().size() + " &7profiles loaded.");
                Raven.profileManager.loadProfiles();
            }
        }));
        this.registerSetting(openFolder = new ButtonSetting("Open folder", () -> {
            try {
                Desktop.getDesktop().open(Raven.profileManager.directory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        ignoreOnSave = true;
        canBeEnabled = false;
    }
}