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

    private HashMap<String, Integer> fillAnimationList = new HashMap<>();
    private HashMap<String, Integer> clearAnimationList = new HashMap<>();
    private int actionRunnable = -1;
    private HashMap<String, Integer> session_wins = new HashMap<>();
    private HashMap<String, Integer> session_loses = new HashMap<>();
    private HashMap<String, Integer> session_helps = new HashMap<>();

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
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(plugin.getCubicAPI().replaceWithPlaceholders(cube, plugin.getConfigHandler().getString("Actionbar.Format"))));
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

    public HashMap<String, Integer> getFillAnimationList() {
        return fillAnimationList;
    }

    public HashMap<String, Integer> getClearAnimationList() {
        return clearAnimationList;
    }

    public String replaceWithPlaceholders(Cube cube, String title) {
        return title.replaceAll("%total_win_counter%", plugin.getCubicAPI().getTotalWins(cube).toString())
                .replaceAll("%total_lose_counter%", plugin.getCubicAPI().getTotalLoses(cube).toString())
                .replaceAll("%total_games_played%", plugin.getCubicAPI().getTotalGamesPlayed(cube).toString())
                .replaceAll("%total_help_counter%", plugin.getCubicAPI().getTotalHelps(cube).toString())
                .replaceAll("%session_win_counter%", plugin.getCubicAPI().getSessionWins(cube).toString())
                .replaceAll("%session_lose_counter%", plugin.getCubicAPI().getSessionLoses(cube).toString())
                .replaceAll("%session_games_played%", plugin.getCubicAPI().getSessionGamesPlayed(cube).toString())
                .replaceAll("%session_help_counter%", plugin.getCubicAPI().getSessionHelps(cube).toString())
                .replaceAll("%cube_height%", String.valueOf(cube.height()))
                .replaceAll("%cube_current_height%", String.valueOf(cube.currentHeight()))
                .replaceAll("%map%", cube.getName());
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

    public Cube getCubeAtLocation(Location location) {
        for (Cube cube : getCubes()) {
            for (Block block : cube.blockList(true)) {
                if (location.equals(block.getLocation())) {
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

    public Integer getTotalWins(Cube cube) {
        return plugin.getDataHandler().getInteger("Cube." + cube.getName() + ".WinCounter");
    }

    public Integer getTotalWins(String cube) {
        return plugin.getDataHandler().getInteger("Cube." + cube.toUpperCase() + ".WinCounter");
    }

    public void increaseTotalWins(Cube cube) {
        Integer integer = 1;
        if (plugin.getDataHandler().keyExists("Cube." + cube.getName() + ".WinCounter")) {
            integer = (plugin.getDataHandler().getInteger("Cube." + cube.getName() + ".WinCounter") + 1);
        }
        plugin.getDataHandler().getConfig().set("Cube." + cube.getName() + ".WinCounter", integer);
        plugin.getDataHandler().save();
    }

    public Integer getTotalHelps(Cube cube) {
        return plugin.getDataHandler().getInteger("Cube." + cube.getName() + ".HelpCounter");
    }

    public Integer getTotalHelps(String cube) {
        return plugin.getDataHandler().getInteger("Cube." + cube.toUpperCase() + ".HelpCounter");
    }

    public void increaseTotalHelps(Cube cube) {
        Integer integer = 1;
        if (plugin.getDataHandler().keyExists("Cube." + cube.getName() + ".HelpCounter")) {
            integer = (plugin.getDataHandler().getInteger("Cube." + cube.getName() + ".HelpCounter") + 1);
        }
        plugin.getDataHandler().getConfig().set("Cube." + cube.getName() + ".HelpCounter", integer);
        plugin.getDataHandler().save();
    }

    public Integer getTotalLoses(Cube cube) {
        return plugin.getDataHandler().getInteger("Cube." + cube.getName() + ".LoseCounter");
    }

    public Integer getTotalLoses(String cube) {
        return plugin.getDataHandler().getInteger("Cube." + cube.toUpperCase() + ".LoseCounter");
    }

    public Integer getTotalGamesPlayed(Cube cube) {
        return getTotalGamesPlayed(cube.getName());
    }

    public Integer getTotalGamesPlayed(String cube) {
        return (getTotalWins(cube) + getTotalLoses(cube));
    }

    public void increaseTotalLoses(Cube cube) {
        Integer integer = 1;
        if (plugin.getDataHandler().keyExists("Cube." + cube.getName() + ".LoseCounter")) {
            integer = (plugin.getDataHandler().getInteger("Cube." + cube.getName() + ".LoseCounter") + 1);
        }
        plugin.getDataHandler().getConfig().set("Cube." + cube.getName() + ".LoseCounter", integer);
        plugin.getDataHandler().save();
    }

    public Integer getSessionWins(Cube cube) {
        return getSessionWins(cube.getName());
    }

    public Integer getSessionWins(String cube) {
        if (session_wins.containsKey(cube)) {
            return session_wins.get(cube);
        }
        return 0;
    }

    public void increaseSessionWins(Cube cube) {
        Integer integer = 1;
        if (session_wins.containsKey(cube.getName())) {
            integer = session_wins.get(cube.getName()) + 1;
        }
        session_wins.put(cube.getName(), (integer));
    }

    public Integer getSessionHelps(Cube cube) {
        return getSessionHelps(cube.getName());
    }

    public Integer getSessionHelps(String cube) {
        if (session_helps.containsKey(cube)) {
            return session_helps.get(cube);
        }
        return 0;
    }

    public void increaseSessionHelps(Cube cube) {
        Integer integer = 1;
        if (session_helps.containsKey(cube.getName())) {
            integer = session_helps.get(cube.getName()) + 1;
        }
        session_helps.put(cube.getName(), (integer));
    }

    public Integer getSessionLoses(Cube cube) {
        return getSessionLoses(cube.getName());
    }

    public Integer getSessionLoses(String cube) {
        if (session_loses.containsKey(cube)) {
            return session_loses.get(cube);
        }
        return 0;
    }

    public void increaseSessionLoses(Cube cube) {
        Integer integer = 1;
        if (session_loses.containsKey(cube.getName())) {
            integer = session_loses.get(cube.getName()) + 1;
        }
        session_loses.put(cube.getName(), (integer));
    }

    public Integer getSessionGamesPlayed(Cube cube) {
        return getSessionGamesPlayed(cube.getName());
    }

    public Integer getSessionGamesPlayed(String cube) {
        return (getSessionWins(cube) + getSessionLoses(cube));
    }

    public void increaseWins(Cube cube) {
        increaseTotalWins(cube);
        increaseSessionWins(cube);
    }

    public void increaseLoses(Cube cube) {
        increaseTotalLoses(cube);
        increaseSessionLoses(cube);
    }

    public void increaseHelpCounter(Cube cube) {
        increaseTotalHelps(cube);
        increaseSessionHelps(cube);
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
