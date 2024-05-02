package keystrokesmod.script;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.Utils;

import javax.tools.ToolProvider;
import java.awt.*;
import java.io.IOException;

public class Manager extends Module {
    private ButtonSetting loadScripts;
    private ButtonSetting openFolder;
    private long a;
    public Manager() {
        super("Manager", category.scripts);
        this.registerSetting(loadScripts = new ButtonSetting("Load scripts", () -> {
            if (Raven.scriptManager.compiler == null) {
                Utils.sendMessage("&cCompiler error, JDK not found");
            }
            else {
                final long currentTimeMillis = System.currentTimeMillis();
                if (Utils.getDifference(this.a, currentTimeMillis) > 1500) {
                    this.a = currentTimeMillis;
                    Raven.scriptManager.loadScripts();
                    if (Raven.scriptManager.scripts.isEmpty()) {
                        Utils.sendMessage("&7No scripts found.");
                    }
                    else {
                        Utils.sendMessage("&7Loaded &b" + Raven.scriptManager.scripts.size() + " &7script" + ((Raven.scriptManager.scripts.size() == 1) ? "." : "s."));
                    }
                }
                else {
                    Utils.sendMessage("&cYou are on cooldown.");
                }
            }
        }));
        this.registerSetting(openFolder = new ButtonSetting("Open folder", () -> {
            try {
                Desktop.getDesktop().open(Raven.scriptManager.directory);
            }
            catch (IOException ex) {
                Raven.scriptManager.directory.mkdirs();
                Utils.sendMessage("&cError locating folder, recreated.");
            }
        }));
        this.canBeEnabled = false;
        this.ignoreOnSave = true;
    }
}
