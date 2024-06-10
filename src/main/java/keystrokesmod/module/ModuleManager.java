package keystrokesmod.module;

import keystrokesmod.module.impl.client.CommandLine;
import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.module.impl.client.Settings;
import keystrokesmod.module.impl.combat.*;
import keystrokesmod.module.impl.fun.Fun;
import keystrokesmod.module.impl.minigames.*;
import keystrokesmod.module.impl.movement.*;
import keystrokesmod.module.impl.other.*;
import keystrokesmod.module.impl.player.*;
import keystrokesmod.module.impl.render.*;
import keystrokesmod.module.impl.world.*;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.profile.Manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModuleManager {
    static List<Module> modules = new ArrayList<>();
    public static List<Module> organizedModules = new ArrayList<>();
    public static Module longJump;
    public static Module blink;
    public static Module nameHider;
    public static Module fastPlace;
    public static MurderMystery murderMystery;
    public static AntiFireball antiFireball;
    public static BedAura bedAura;
    public static FastMine fastMine;
    public static Module antiShuffle;
    public static Module commandLine;
    public static Module antiBot;
    public static Module noSlow;
    public static KillAura killAura;
    public static Module autoClicker;
    public static Module hitBox;
    public static Module reach;
    public static BedESP bedESP;
    public static HUD hud;
    public static Timer timer;
    public static Module fly;
    public static Module superKB;
    public static Potions potions;
    public static NoFall noFall;
    public static PlayerESP playerESP;
    public static Module reduce;
    public static SafeWalk safeWalk;
    public static Module keepSprint;
    public static Module antiKnockback;
    public static Tower tower;
    public static Module bedwars;
    public static Speed speed;
    public static Scaffold scaffold;
    public static MotionSkidder motionSkidder;
    public static MotionModifier motionModifier;
    public static AntiVoid antiVoid;
    public static Criticals criticals;
    public static TimerRange timerRange;
    public static TargetStrafe targetStrafe;
    public static AutoHeal autoHeal;
    public static HitSelect hitSelect;
    public static NoHurtCam noHurtCam;
    public static NoCameraClip noCameraClip;

    public void register() {
        this.addModule(autoClicker = new AutoClicker());
        this.addModule(longJump = new LongJump());
        this.addModule(new AimAssist());
        this.addModule(blink = new Blink());
        this.addModule(new BurstClicker());
        this.addModule(new ClickAssist());
        this.addModule(tower = new Tower());
        this.addModule(new DelayRemover());
        this.addModule(hitBox = new HitBox());
        this.addModule(new Radar());
        this.addModule(new Settings());
        this.addModule(reach = new Reach());
        this.addModule(new RodAimbot());
        this.addModule(speed = new Speed());
        this.addModule(new InvManager());
        this.addModule(scaffold = new Scaffold());
        this.addModule(new AntiAFK());
        this.addModule(new AutoTool());
        this.addModule(fly = new Fly());
        this.addModule(new InvMove());
        this.addModule(new Trajectories());
        this.addModule(potions = new Potions());
        this.addModule(new AutoSwap());
        this.addModule(keepSprint = new KeepSprint());
        this.addModule(bedAura = new BedAura());
        this.addModule(noSlow = new NoSlow());
        this.addModule(new Indicators());
        this.addModule(new LatencyAlerts());
        this.addModule(new Sprint());
        this.addModule(new StopMotion());
        this.addModule(timer = new Timer());
        this.addModule(new VClip());
        this.addModule(new AutoJump());
        this.addModule(new AutoPlace());
        this.addModule(fastPlace = new FastPlace());
        this.addModule(new Freecam());
        this.addModule(noFall = new NoFall());
        this.addModule(safeWalk = new SafeWalk());
        this.addModule(reduce = new Reduce());
        this.addModule(antiKnockback = new Velocity());
        this.addModule(antiBot = new AntiBot());
        this.addModule(antiShuffle = new AntiShuffle());
        this.addModule(new Chams());
        this.addModule(new ChestESP());
        this.addModule(new Nametags());
        this.addModule(playerESP = new PlayerESP());
        this.addModule(new Tracers());
        this.addModule(hud = new HUD());
        this.addModule(new Anticheat());
        this.addModule(new BreakProgress());
        this.addModule(superKB = new SuperKB());
        this.addModule(new Xray());
        this.addModule(new BridgeInfo());
        this.addModule(new TargetHUD());
        this.addModule(new DuelsStats());
        this.addModule(antiFireball = new AntiFireball());
        this.addModule(bedESP = new BedESP());
        this.addModule(murderMystery = new MurderMystery());
        this.addModule(new keystrokesmod.script.Manager());
        this.addModule(new SumoFences());
        this.addModule(new Fun.ExtraBobbing());
        this.addModule(killAura = new KillAura());
        this.addModule(new Fun.FlameTrail());
        this.addModule(new Fun.SlyPort());
        this.addModule(new ItemESP());
        this.addModule(new MobESP());
        this.addModule(new Fun.Spin());
        this.addModule(new NoRotate());
        this.addModule(new FakeChat());
        this.addModule(nameHider = new NameHider());
        this.addModule(new FakeLag());
        this.addModule(new WaterBucket());
        this.addModule(commandLine = new CommandLine());
        this.addModule(bedwars = new BedWars());
        this.addModule(fastMine = new FastMine());
        this.addModule(new JumpReset());
        this.addModule(new Manager());
        this.addModule(new ViewPackets());
        this.addModule(new AutoWho());
        this.addModule(new Gui());
        this.addModule(new Shaders());
        this.addModule(motionSkidder = new MotionSkidder());
        this.addModule(motionModifier = new MotionModifier());
        this.addModule(antiVoid = new AntiVoid());
        this.addModule(criticals = new Criticals());
        this.addModule(timerRange = new TimerRange());
        this.addModule(targetStrafe = new TargetStrafe());
        this.addModule(autoHeal = new AutoHeal());
        this.addModule(hitSelect = new HitSelect());
        this.addModule(noHurtCam = new NoHurtCam());
        this.addModule(noCameraClip = new NoCameraClip());
        antiBot.enable();
        modules.sort(Comparator.comparing(Module::getName));
    }

    public void addModule(Module m) {
        modules.add(m);
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> inCategory(Module.category categ) {
        ArrayList<Module> categML = new ArrayList<>();

        for (Module mod : this.getModules()) {
            if (mod.moduleCategory().equals(categ)) {
                categML.add(mod);
            }
        }

        return categML;
    }

    public Module getModule(String moduleName) {
        for (Module module : modules) {
            if (module.getName().equals(moduleName)) {
                return module;
            }
        }
        return null;
    }

    public static void sort() {
        if (HUD.alphabeticalSort.isToggled()) {
            organizedModules.sort(Comparator.comparing(Module::getName));
        } else {
            organizedModules.sort((o1, o2) -> Utils.mc.fontRendererObj.getStringWidth(o2.getName() + ((HUD.showInfo.isToggled() && !o2.getInfo().isEmpty()) ? " " + o2.getInfo() : "")) - Utils.mc.fontRendererObj.getStringWidth(o1.getName() + (HUD.showInfo.isToggled() && !(o1.getInfo().isEmpty()) ? " " + o1.getInfo() : "")));
        }
    }
}
