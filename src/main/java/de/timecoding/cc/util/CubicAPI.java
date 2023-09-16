package de.timecoding.cc.util;

import de.timecoding.cc.CubicCountdown;
import de.timecoding.cc.command.setup.CubicSetup;
import de.timecoding.cc.util.type.Cube;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CubicAPI {

    private CubicCountdown plugin;
    private List<CountdownModule> countdownList = new ArrayList<>();
    private HashMap<Player, CubicSetup> setupList = new HashMap<>();
    private int actionRunnable = -1;

    public CubicAPI(CubicCountdown plugin) {
        this.plugin = plugin;
    }

    public List<CountdownModule> getCountdownList() {
        return countdownList;
    }

    public HashMap<Player, CubicSetup> getSetupList() {
        return setupList;
    }

    public void startActionbar() {
        if (!actionbarRunning()) {
            actionRunnable = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if (!plugin.getConfigHandler().getBoolean("Actionbar.Enabled")) {
                        cancelActionbar();
                    } else {
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            Cube cube = getNearestCube(player);
                            if (cube != null) {
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(plugin.getConfigHandler().getString("Actionbar.Format").replace("%win_counter%", getWins(cube).toString()).replace("%lose_counter%", getLoses(cube).toString()).replace("%games_played%", getGamesPlayed(cube).toString()).replace("%cube_name%", cube.getName().toString())));
                            }
                        });
                    }
                }
            }, 0, 20);
        }
    }

    public void cancelActionbar() {
        if (actionbarRunning()) {
            Bukkit.getScheduler().cancelTask(actionRunnable);
            actionRunnable = -1;
        }
    }

    private boolean actionbarRunning() {
        return (actionRunnable != -1);
    }

    public List<Cube> getCubes() {
        List<String> cubeStringList = new ArrayList<>();
        for (String key : plugin.getDataHandler().getConfig().getValues(true).keySet()) {
            String[] keys = key.split("\\.");
            if (keys.length == 3 && !cubeStringList.contains(keys[1])) {
                cubeStringList.add(keys[1]);
            }
        }
        List<Cube> cubeList = new ArrayList<>();
        cubeStringList.forEach(string -> cubeList.add(new Cube(string, plugin.getDataHandler().getLocation("Cube." + string + ".Pos1"), plugin.getDataHandler().getLocation("Cube." + string + ".Pos2"), plugin)));
        return cubeList;
    }

    public Cube getCubeAtLocation(Location location){
        for(Cube cube : getCubes()){
            for(Block block : cube.blockList(true)){
                if(location.equals(block.getLocation())){
                    return cube;
                }
            }
        }
        return null;
    }

    public Cube getCubeByName(String cubeName) {
        for (Cube cube : getCubes()) {
            if (cube.getName().equalsIgnoreCase(cubeName)) {
                return cube;
            }
        }
        return null;
    }

    public Cube getNearestCube(Player player) {
        for (Cube cube : getCubes()) {
            for (Block block : cube.blockList(true)) {
                if (block.getLocation().distance(player.getLocation()) < plugin.getConfigHandler().getInteger("CubeRadius")) {
                    return cube;
                }
            }
        }
        return null;
    }

    public boolean viewingCountdown(Player player) {
        AtomicBoolean viewing = new AtomicBoolean(false);
        countdownList.forEach(countdownModule -> {
            countdownModule.getCubicSettings().playerList().forEach(player1 -> {
                if (player1.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                    viewing.set(true);
                }
            });
        });
        return viewing.get();
    }

    public void removeCountdown(CountdownModule countdownModule) {
        countdownList.forEach(countdownModule1 -> {
            if (countdownModule1.getCountdownId() == countdownModule.getCountdownId()) {
                countdownList.remove(countdownModule1);
            }
        });
    }

    public CountdownModule getCountdownModuleFromCube(Cube cube) {
        AtomicReference<CountdownModule> finalCountdownModule = new AtomicReference<>();
        countdownList.forEach(countdownModule -> {
            if (countdownModule.getCubicSettings().getCube().isSimilar(cube)) {
                finalCountdownModule.set(countdownModule);
            }
        });
        return finalCountdownModule.get();
    }

    public Integer getWins(Cube cube) {
        return plugin.getDataHandler().getInteger("Cube." + cube.getName() + ".WinCounter");
    }

    public Integer getWins(String cube) {
        return plugin.getDataHandler().getInteger("Cube." + cube.toUpperCase() + ".WinCounter");
    }

    public void increaseWins(Cube cube) {
        Integer integer = 1;
        if (plugin.getDataHandler().keyExists("Cube." + cube.getName() + ".WinCounter")) {
            integer = (plugin.getDataHandler().getInteger("Cube." + cube.getName() + ".WinCounter") + 1);
        }
        plugin.getDataHandler().getConfig().set("Cube." + cube.getName() + ".WinCounter", integer);
        plugin.getDataHandler().save();
    }

    public Integer getLoses(Cube cube) {
        return plugin.getDataHandler().getInteger("Cube." + cube.getName() + ".LoseCounter");
    }

    public Integer getLoses(String cube) {
        return plugin.getDataHandler().getInteger("Cube." + cube.toUpperCase() + ".LoseCounter");
    }

    public Integer getGamesPlayed(Cube cube) {
        return getGamesPlayed(cube.getName());
    }

    public Integer getGamesPlayed(String cube) {
        return (getWins(cube) + getLoses(cube));
    }

    public void increaseLoses(Cube cube) {
        Integer integer = 1;
        if (plugin.getDataHandler().keyExists("Cube." + cube.getName() + ".LoseCounter")) {
            integer = (plugin.getDataHandler().getInteger("Cube." + cube.getName() + ".LoseCounter") + 1);
        }
        plugin.getDataHandler().getConfig().set("Cube." + cube.getName() + ".LoseCounter", integer);
        plugin.getDataHandler().save();
    }

    public boolean cubeNameExists(String name) {
        AtomicBoolean exists = new AtomicBoolean(false);
        getCubes().forEach(cube -> {
            if (cube.getName().equalsIgnoreCase(name)) {
                exists.set(true);
            }
        });
        return exists.get();
    }

}
