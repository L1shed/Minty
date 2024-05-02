package keystrokesmod.script.classes;

import keystrokesmod.utility.BlockUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.init.Blocks;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {
    private Minecraft mc = Minecraft.getMinecraft();

    public Block getBlockAt(int x, int y, int z) {
        net.minecraft.block.Block block = BlockUtils.getBlock(new BlockPos(x, y, z));
        if (block == null) {
            return new Block(Blocks.air);
        }
        return new Block(block);
    }

    public String getDimension() {
        if (mc.thePlayer.dimension == -1) {
            return "NETHER";
        }
        else if (mc.thePlayer.dimension == 1) {
            return "END";
        }
        else {
            return "OVERWORLD";
        }
    }

    public List<Entity> getEntities() {
        List<Entity> entities = new ArrayList<>();
        for (net.minecraft.entity.Entity entity : mc.theWorld.loadedEntityList) {
            entities.add(new Entity(entity));
        }
        return entities;
    }

    public Entity getEntityById(int entityId) {
        for (net.minecraft.entity.Entity entity : mc.theWorld.loadedEntityList) {
            if (entity.getEntityId() == entityId) {
                return new Entity(entity);
            }
        }
        return null;
    }

    public List<NetworkPlayer> getNetworkPlayers() {
        List<NetworkPlayer> entities = new ArrayList<>();
        for (NetworkPlayerInfo networkPlayerInfo : Utils.getTablist()) {
            entities.add(new NetworkPlayer(networkPlayerInfo));
        }
        return entities;
    }

    public List<Entity> getPlayerEntities() {
        List<Entity> entities = new ArrayList<>();
        for (net.minecraft.entity.Entity entity : mc.theWorld.playerEntities) {
            entities.add(new Entity(entity));
        }
        return entities;
    }

    public List<String> getScoreboard() {
        if (Utils.getSidebarLines().isEmpty()) {
            return null;
        }
        return Utils.getSidebarLines();
    }

    public Map<String, List<String>> getTeams() {
        Map<String, List<String>> teams = new HashMap<>();
        for (Team team : mc.theWorld.getScoreboard().getTeams()) {
            teams.put(team.getRegisteredName(), (List<String>) team.getMembershipCollection());
        }
        return teams;
    }

    public List<TileEntity> getTileEntities() {
        List<TileEntity> tileEntities = new ArrayList<>();
        for (net.minecraft.tileentity.TileEntity entity : mc.theWorld.loadedTileEntityList) {
            tileEntities.add(new TileEntity(entity));
        }
        return tileEntities;
    }
}
