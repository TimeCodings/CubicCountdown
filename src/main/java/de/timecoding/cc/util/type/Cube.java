package de.timecoding.cc.util.type;

import de.timecoding.cc.CubicCountdown;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
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

            for (int x = bottomBlockX; x <= topBlockX; x++) {
                for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                    for (int y = bottomBlockY; y <= topBlockY; y++) {
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

    public boolean filledOut() {
        AtomicBoolean filledOut = new AtomicBoolean(true);
        AtomicInteger highestY = new AtomicInteger();
        List<Block> highestList = new ArrayList<>();
        blockList(true).forEach(block -> {
            if(block.getLocation().getBlockY() > highestY.get()){
                highestY.set(block.getLocation().getBlockY());
                highestList.clear();
                highestList.add(block);
            }else if(block.getLocation().getBlockY() == highestY.get()){
                highestList.add(block);
            }
            if (block == null || block.getType() == Material.AIR) {
                filledOut.set(false);
            }
        });
        if(plugin.getConfigHandler().getBoolean("StartWhenFullFirstLayer") || !plugin.getConfigHandler().keyExists("StartWhenFullFirstLayer")){
            filledOut.set(true);
            highestList.forEach(block -> {
                if(block == null || block.getType() == Material.AIR){
                    filledOut.set(false);
                }
            });
        }
        return filledOut.get();
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
