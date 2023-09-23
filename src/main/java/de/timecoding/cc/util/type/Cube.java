package de.timecoding.cc.util.type;

import de.timecoding.cc.CubicCountdown;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Cube {

    private String name = "UNNAMED";
    private Location pos1;
    private Location pos2;

    private CubicCountdown plugin;

    public Cube(String name, Location pos1, Location pos2, CubicCountdown plugin) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.plugin = plugin;
    }

    public String getName() {
        return name;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public List<Block> blockList(boolean includeAir) {
        List<Block> blockList = new ArrayList<>();
        if (pos1 != null && pos2 != null) {
            int topBlockX = (pos1.getBlockX() < pos2.getBlockX() ? pos2.getBlockX() : pos1.getBlockX());
            int bottomBlockX = (pos1.getBlockX() > pos2.getBlockX() ? pos2.getBlockX() : pos1.getBlockX());

            int topBlockY = (pos1.getBlockY() < pos2.getBlockY() ? pos2.getBlockY() : pos1.getBlockY());
            int bottomBlockY = (pos1.getBlockY() > pos2.getBlockY() ? pos2.getBlockY() : pos1.getBlockY());

            int topBlockZ = (pos1.getBlockZ() < pos2.getBlockZ() ? pos2.getBlockZ() : pos1.getBlockZ());
            int bottomBlockZ = (pos1.getBlockZ() > pos2.getBlockZ() ? pos2.getBlockZ() : pos1.getBlockZ());

            for (int y = bottomBlockY; y <= topBlockY; y++) {
                for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                    for (int x = bottomBlockX; x <= topBlockX; x++) {
                        Block block = pos1.getWorld().getBlockAt(x, y, z);
                        if (!includeAir && block.getType() != null && block.getType() != Material.AIR || includeAir) {
                            blockList.add(block);
                        }
                    }
                }
            }
        }
        return blockList;
    }

    public List<Block> airBlockList() {
        List<Block> airBlockList = new ArrayList<>();
        for (Block block : blockList(true)) {
            if (block == null || block.getType() == Material.AIR) {
                airBlockList.add(block);
            }
        }
        return airBlockList;
    }

    public boolean filledOut() {
        boolean startWhenFullFirstLayer = plugin.getConfigHandler().getBoolean("StartWhenFullFirstLayer");

        if (startWhenFullFirstLayer) {
            AtomicBoolean filledOut = new AtomicBoolean(true);
            int topBlockX = Math.max(pos1.getBlockX(), pos2.getBlockX());
            int bottomBlockX = Math.min(pos1.getBlockX(), pos2.getBlockX());
            int topBlockZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
            int bottomBlockZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
            int topBlockY = Math.max(pos1.getBlockY(), pos2.getBlockY());

            for (int x = bottomBlockX; x <= topBlockX; x++) {
                for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                    Block block = pos1.getWorld().getBlockAt(x, topBlockY, z);
                    if (block == null || block.getType() == Material.AIR) {
                        filledOut.set(false);
                        break;
                    }
                }
                if (!filledOut.get()) {
                    break;
                }
            }
            return filledOut.get();
        } else {
            return blockList(true).stream().noneMatch(block -> block == null || block.getType() == Material.AIR);
        }
    }

    public boolean empty() {
        List<Block> blockList = blockList(false);
        List<Block> toRemove = new ArrayList<>();
        if (blockList != null) {
            for (Block block : blockList) {
                if (plugin.getConfigHandler().getStringList("ClearCube.DisabledBlocks").contains(block.getType().toString().toUpperCase())) {
                    toRemove.add(block);
                }
            }
        }
        blockList.removeAll(toRemove);
        return (blockList.size() == 0);
    }

    public boolean inCube(Location location) {
        AtomicBoolean inCube = new AtomicBoolean(false);
        blockList(true).forEach(block -> {
            if (block.getLocation().getWorld().getName().equals(location.getWorld().getName()) && block.getLocation().getBlockX() == location.getBlockX() && block.getLocation().getBlockY() == location.getBlockY() && block.getLocation().getBlockZ() == location.getBlockZ()) {
                inCube.set(true);
            }
        });
        return inCube.get();
    }

    public boolean isSimilar(Cube cube) {
        return (cube.getName().equals(name));
    }

    public String getDataPath() {
        return "Cube." + name + ".";
    }

}
