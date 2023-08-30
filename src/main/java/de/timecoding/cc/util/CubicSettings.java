package de.timecoding.cc.util;

import de.timecoding.cc.CubicCountdown;
import de.timecoding.cc.file.ConfigHandler;
import de.timecoding.cc.util.type.Cube;
import de.timecoding.cc.util.type.CubicStateType;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CubicSettings {

    //PLUGIN, PLAYER, SOUNDS, SECONDS, START, FORMAT, CANCELLED, END, FIREWORK

    private CubicCountdown plugin = null;
    private List<Player> playerList = new ArrayList<>();
    private HashMap<CubicStateType, Sound> soundMap = new HashMap<>();
    private HashMap<CubicStateType, String> titleMap = new HashMap<>();
    private HashMap<CubicStateType, String> subTitleMap = new HashMap<>();
    private List<Location> fireworkLocations = new ArrayList<>();

    private Integer countdownSeconds = 10;
    private FireworkMeta firework = null;

    private Cube cube = null;

    private Integer startDelay = 0;

    public CubicSettings(CubicCountdown plugin, boolean fromConfig) {
        this.plugin = plugin;
        if (fromConfig) {
            ConfigHandler configHandler = this.plugin.getConfigHandler();
            setCountdownSeconds(configHandler.getInteger("Countdown"));
            setStartDelay(configHandler.getInteger("StartDelayInTicks"));
            Arrays.stream(CubicStateType.values()).forEach(cubicStateType -> {
                setTitle(cubicStateType, configHandler.getString("Settings." + cubicStateType.toString().toUpperCase() + ".Title"));
                setSubtitle(cubicStateType, configHandler.getString("Settings." + cubicStateType.toString().toUpperCase() + ".Subtitle"));
                String soundString = configHandler.getString("Settings." + cubicStateType.toString().toUpperCase() + ".Sound");
                Sound sound = getSoundFromString(soundString);
                if (sound != null) {
                    setSound(cubicStateType, sound);
                }
            });
        }
    }

    public Cube getCube() {
        return cube;
    }

    public void setCube(Cube cube) {
        this.cube = cube;
    }

    private Sound getSoundFromString(String soundString) {
        if (Sound.valueOf(soundString) != null) {
            return Sound.valueOf(soundString);
        }
        return null;
    }

    public Integer getCountdownSeconds() {
        return countdownSeconds;
    }

    public void setCountdownSeconds(Integer countdownSeconds) {
        this.countdownSeconds = countdownSeconds;
    }

    public Integer getStartDelay() {
        return startDelay;
    }

    public void setStartDelay(Integer startDelay) {
        this.startDelay = startDelay;
    }

    public CubicCountdown getPlugin() {
        return plugin;
    }

    public List<Player> playerList() {
        return playerList;
    }

    public void addPlayer(Player player) {
        if(!plugin.viewingCountdown(player)) {
            playerList.add(player);
        }
    }

    public void removePlayer(Player player) {
        playerList.remove(player);
    }

    public FireworkMeta getFireworkMeta() {
        return firework;
    }

    private HashMap<CubicStateType, Sound> getSoundMap() {
        return soundMap;
    }

    public Sound getSound(CubicStateType cubicStateType) {
        return getSoundMap().get(cubicStateType);
    }

    public void setSound(CubicStateType cubicStateType, Sound sound) {
        getSoundMap().put(cubicStateType, sound);
    }

    public void removeSound(CubicStateType cubicStateType) {
        getSoundMap().remove(cubicStateType);
    }

    public boolean hasSound(CubicStateType cubicStateType) {
        return (getSoundMap().containsKey(cubicStateType));
    }

    private HashMap<CubicStateType, String> getTitleMap() {
        return titleMap;
    }

    public boolean hasTitle(CubicStateType cubicStateType){
        return (getTitleMap().containsKey(cubicStateType));
    }

    public String getTitle(CubicStateType cubicStateType) {
        if(hasTitle(cubicStateType)) {
            return getTitleMap().get(cubicStateType).replace("%win_counter%", plugin.getWins(cube).toString()).replace("%lose_counter%", plugin.getLoses(cube).toString());
        }else{
            return "";
        }
    }

    public void setTitle(CubicStateType cubicStateType, String string) {
        getTitleMap().put(cubicStateType, string);
    }

    public void removeTitle(CubicStateType cubicStateType) {
        getTitleMap().remove(cubicStateType);
    }

    public boolean hasSubtitle(CubicStateType cubicStateType) {
        return (getSubTitleMap().containsKey(cubicStateType));
    }

    public String getSubtitle(CubicStateType cubicStateType) {
        if(hasSubtitle(cubicStateType)) {
            return getSubTitleMap().get(cubicStateType).replace("%win_counter%", plugin.getWins(cube).toString()).replace("%lose_counter%", plugin.getLoses(cube).toString());
        }else{
            return "";
        }
    }

    public void setSubtitle(CubicStateType cubicStateType, String string) {
        getSubTitleMap().put(cubicStateType, string);
    }

    public void removeSubtitle(CubicStateType cubicStateType) {
        getSubTitleMap().remove(cubicStateType);
    }

    private HashMap<CubicStateType, String> getSubTitleMap() {
        return subTitleMap;
    }

    public List<Location> fireworkLocations() {
        return fireworkLocations;
    }

    public void addFireworkLocation(Location location) {
        fireworkLocations.add(location);
    }

    public void removeFireworkLocation(Location location) {
        fireworkLocations.remove(location);
    }

    public void setFirework(FireworkMeta firework) {
        this.firework = firework;
    }

}
