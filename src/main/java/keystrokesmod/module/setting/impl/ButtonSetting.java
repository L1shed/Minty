package keystrokesmod.module.setting.impl;

import com.google.gson.JsonObject;
import keystrokesmod.module.setting.Setting;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ButtonSetting extends Setting {
    private final String name;
    private boolean isEnabled;
    public boolean isMethodButton;
    private Runnable method;

    public ButtonSetting(String name, boolean isEnabled) {
        this(name, isEnabled, () -> true);
    }

    public ButtonSetting(String name, boolean isEnabled, @NotNull Supplier<Boolean> visibleCheck) {
        super(name, visibleCheck);
        this.name = name;
        this.isEnabled = isEnabled;
        this.isMethodButton = false;
    }

    public ButtonSetting(String name, Runnable method) {
        this(name, method, () -> true);
    }

    public ButtonSetting(String name, Runnable method, @NotNull Supplier<Boolean> visibleCheck) {
        super(name, visibleCheck);
        this.name = name;
        this.isEnabled = false;
        this.isMethodButton = true;
        this.method = method;
    }

    public void runMethod() {
        if (method != null) {
            method.run();
        }
    }

    public String getName() {
        return this.name;
    }

    public boolean isToggled() {
        return this.isEnabled;
    }

    public void toggle() {
        this.isEnabled = !this.isEnabled;
    }

    public void enable() {
        this.isEnabled = true;
    }

    public void disable() {
        this.isEnabled = false;
    }

    public void setEnabled(boolean b) {
        this.isEnabled = b;
    }

    @Override
    public void loadProfile(JsonObject data) {
        if (data.has(getName()) && data.get(getName()).isJsonPrimitive() && !this.isMethodButton) {
            boolean booleanValue = data.getAsJsonPrimitive(getName()).getAsBoolean();
            setEnabled(booleanValue);
        }
    }
}
