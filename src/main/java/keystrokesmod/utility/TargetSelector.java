package keystrokesmod.utility;

import keystrokesmod.module.impl.other.anticheats.utils.phys.Vec2;
import keystrokesmod.module.impl.other.anticheats.utils.world.BlockUtils;
import keystrokesmod.script.classes.Vec3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

import static keystrokesmod.Raven.mc;

public class TargetSelector {
    private static int lastSwitchSince = Integer.MAX_VALUE;
    private static final Deque<EntityLivingBase> switchedTarget = new LinkedBlockingDeque<>();

    public static Optional<TargetData> getTarget(double swingRange, double attackRange, double blockRange,
                                                 double aps, double fov,
                                                 boolean allowPlayer, boolean allowEntity, boolean allowTeammates,
                                                 SelectMode mode, boolean rayCast,
                                                 int switchTargets, int switchDelay) {
        boolean canAttack = false;
        if (aps >= 20) canAttack = true;
        else if ((Math.random() - 1) + aps / 20 > 0) {
            canAttack = true;
        }

        lastSwitchSince++;
        if (attackRange > swingRange)
            return Optional.empty();

        List<TargetData> possibleTargets = new ArrayList<>();
        for (Entity baseEntity : mc.theWorld.loadedEntityList) {
            if (!(baseEntity instanceof EntityLivingBase)) {
                continue;
            }
            EntityLivingBase entity = (EntityLivingBase) baseEntity;

            if (!allowPlayer && entity instanceof EntityPlayer) {
                continue;
            }
            if (!allowEntity && !(entity instanceof EntityPlayer)) {
                continue;
            }
            if (!allowTeammates && Utils.isTeamMate(entity)) {
                continue;
            }

            double distance = new Vec3(entity).add(new Vec3(0, entity.getEyeHeight(), 0))
                    .distanceTo(new Vec3(mc.thePlayer).add(new Vec3(0, mc.thePlayer.getEyeHeight(), 0)));
            boolean swing = distance <= swingRange;
            boolean attack = distance <= attackRange;
            boolean block = distance <= blockRange;
            if (!swing && !attack && !block) {
                continue;
            }

            final float[] rotations = RotationUtils.getRotations(entity);
            if (rotations == null || rotations.length < 2) {
                Utils.sendMessage("Can't get rotation for " + entity.getName() + "!");
            }
            assert rotations != null;
            if (fov <= 360) {
                if (Math.abs(rotations[0] % 360 - mc.thePlayer.rotationYaw % 360) > fov) {
                    continue;
                }
            }
            if (rayCast) {
                MovingObjectPosition hitResult = RotationUtils.rayCast(attackRange, rotations[0], rotations[1]);
                if (hitResult != null) {
                    if (BlockUtils.isFullBlock(mc.theWorld.getBlockState(hitResult.getBlockPos()))) {
                        continue;
                    }
                }
            }

            possibleTargets.add(new TargetData(entity, distance, new Vec2(rotations[0], rotations[1]), swing && canAttack, attack && canAttack, block));
        }

        while (switchTargets > switchedTarget.size()) {
            switchedTarget.poll();
        }
        if (lastSwitchSince < switchDelay) {
            EntityLivingBase lastTarget = switchedTarget.peekLast();
            Optional<TargetData> targetData = possibleTargets.stream().filter(data -> data.getTarget().equals(lastTarget)).findAny();
            if (lastTarget != null && targetData.isPresent()) {
                return targetData;
            }
        } else {
            switchedTarget.poll();
            lastSwitchSince = 0;
        }

        if (possibleTargets.isEmpty()) {
            return Optional.empty();
        }

        possibleTargets.removeIf(target -> switchedTarget.contains(target.getTarget()));

        Optional<TargetData> finalTarget = possibleTargets.stream()
                .min((data1, data2) -> {
                    switch (mode) {
                        default:
                        case DISTANCE:
                            return (int) ((data1.getDistance() - data2.getDistance()) * 100);
                        case HEALTH:
                            return (int) ((data1.getTarget().getHealth() - data2.getTarget().getHealth()) * 2);
                        case HURT_TIME:
                            return (data1.getTarget().maxHurtTime - data1.getTarget().hurtTime) - (data2.getTarget().maxHurtTime - data2.getTarget().hurtTime);
                    }
                });

        finalTarget.ifPresent(data -> switchedTarget.add(data.getTarget()));
        return finalTarget;
    }


    public enum SelectMode {
        DISTANCE,
        HEALTH,
        HURT_TIME
    }

    public static class TargetData {
        private final EntityLivingBase target;
        private final double distance;
        private final boolean swing;
        private final boolean attack;
        private final boolean block;
        private final Vec2 rotation;

        public TargetData(EntityLivingBase target, double distance, Vec2 rotation, boolean swing, boolean attack, boolean block) {
            this.target = target;
            this.distance = distance;
            this.rotation = rotation;
            this.swing = swing;
            this.attack = attack;
            this.block = block;
        }

        public EntityLivingBase getTarget() {
            return target;
        }
        protected double getDistance() {
            return distance;
        }
        public Vec2 getRotation() {
            return rotation;
        }
        public boolean isSwing() {
            return swing;
        }
        public boolean isAttack() {
            return attack;
        }
        public boolean isBlock() {
            return block;
        }

        @Override
        public String toString() {
            return "TargetData{" +
                    "target=" + target +
                    ", distance=" + distance +
                    ", swing=" + swing +
                    ", attack=" + attack +
                    ", block=" + block +
                    ", rotation=" + rotation +
                    '}';
        }
    }
}
