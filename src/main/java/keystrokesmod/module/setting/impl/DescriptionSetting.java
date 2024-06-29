package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DescriptionSetting extends Setting {
    private String desc;

    public DescriptionSetting(String t) {
        this(t, () -> true);
    }

    public DescriptionSetting(String t, @NotNull Supplier<Boolean> visibleCheck) {
        super(t, visibleCheck);
        this.desc = t;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String t) {
        this.desc = t;
    }

    @Override
    public void loadProfile(JsonObject data) {
    }
}
