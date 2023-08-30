package de.timecoding.cc;

import de.timecoding.cc.api.CubicExpansion;
import de.timecoding.cc.api.Metrics;
import de.timecoding.cc.command.CubicCommand;
import de.timecoding.cc.command.completer.CubicCompleter;
import de.timecoding.cc.command.setup.CubicSetup;
import de.timecoding.cc.file.ConfigHandler;
import de.timecoding.cc.file.DataHandler;
import de.timecoding.cc.listener.CubicListener;
import de.timecoding.cc.util.CountdownModule;
import de.timecoding.cc.util.type.Cube;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CubicCountdown extends JavaPlugin {

    private ConfigHandler configHandler;
    private DataHandler dataHandler;
    private List<CountdownModule> countdownList = new ArrayList<>();
    private HashMap<Player, CubicSetup> setupList = new HashMap<>();

    //FOR API USAGE ONLY
    private CubicCountdown plugin;
    private Metrics metrics = null;

    public void onEnable() {
        this.plugin = this;
        this.configHandler = new ConfigHandler(this);
        this.dataHandler = new DataHandler(this);
        this.getServer().getPluginManager().registerEvents(new CubicListener(this), this);
        PluginCommand pluginCommand = this.getCommand("cubiccountdown");
        pluginCommand.setExecutor(new CubicCommand(this));
        pluginCommand.setTabCompleter(new CubicCompleter(this));
        Bukkit.getConsoleSender().sendMessage("§cCubicCountdown §7(v" + this.getDescription().getVersion() + ") §aby §eTimeCode §awas enabled!");
        Bukkit.getConsoleSender().sendMessage("§cTHIS IS A BETA VERSION OF THE PLUGIN! PLEASE REPORT ALL ISSUES OR WISHES TO OUR DISCORD: https://discord.tikmc.de/");
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            Bukkit.getConsoleSender().sendMessage("§aSuccessfully registered the CubicCountdown §ePlaceholderAPI Expansion§a!");
            new CubicExpansion(this).register();
        }
        if(!configHandler.keyExists("bStats") || configHandler.getBoolean("bStats")){
            this.metrics = new Metrics(this, 19676);
        }
        for(String key : plugin.getDataHandler().getConfig().getKeys(true)){
            if(key.endsWith("Wins") || key.endsWith("Loses")){
                plugin.getDataHandler().getConfig().set(key, null);
                Bukkit.getConsoleSender().sendMessage("§aSuccessfully deleted the §ewin and/or lose §acounter of the map §e"+key.split("\\.")[1]);
                plugin.getDataHandler().save();
            }
        }
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public List<CountdownModule> getCountdownList() {
        return countdownList;
    }

    public HashMap<Player, CubicSetup> getSetupList() {
        return setupList;
    }

    public CubicCountdown getPlugin() {
        return plugin;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public List<Cube> getCubes() {
        List<String> cubeStringList = new ArrayList<>();
        for (String key : getDataHandler().getConfig().getValues(true).keySet()) {
            String[] keys = key.split("\\.");
            if (keys.length == 3 && !cubeStringList.contains(keys[1])) {
                cubeStringList.add(keys[1]);
            }
        }
        List<Cube> cubeList = new ArrayList<>();
        cubeStringList.forEach(string -> cubeList.add(new Cube(string, dataHandler.getLocation("Cube." + string + ".Pos1"), dataHandler.getLocation("Cube." + string + ".Pos2"), this)));
        return cubeList;
    }

    public boolean viewingCountdown(Player player){
        AtomicBoolean viewing = new AtomicBoolean(false);
        getCountdownList().forEach(countdownModule -> {
            if(countdownModule.getCubicSettings().playerList().contains(player)){
                viewing.set(true);
            }
        });
        return viewing.get();
    }

    public void removeCountdown(CountdownModule countdownModule){
        countdownList.forEach(countdownModule1 -> {
            if(countdownModule1.getCountdownId() == countdownModule.getCountdownId()){
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

    public Integer getWins(Cube cube){
        return getDataHandler().getInteger("Cube."+cube.getName()+".WinCounter");
    }

    public Integer getWins(String cube){
        return getDataHandler().getInteger("Cube."+cube+".WinCounter");
    }

    public void increaseWins(Cube cube){
        Integer integer = 1;
        if(getDataHandler().keyExists("Cube."+cube.getName()+".WinCounter")){
            integer = (getDataHandler().getInteger("Cube."+cube.getName()+".WinCounter")+1);
        }
        getDataHandler().getConfig().set("Cube."+cube.getName()+".WinCounter", integer);
        getDataHandler().save();
    }

    public Integer getLoses(Cube cube){
        return getDataHandler().getInteger("Cube."+cube.getName()+".LoseCounter");
    }

    public Integer getLoses(String cube){
        return getDataHandler().getInteger("Cube."+cube+".LoseCounter");
    }

    public void increaseLoses(Cube cube){
        Integer integer = 1;
        if(getDataHandler().keyExists("Cube."+cube.getName()+".LoseCounter")){
            integer = (getDataHandler().getInteger("Cube."+cube.getName()+".LoseCounter")+1);
        }
        getDataHandler().getConfig().set("Cube."+cube.getName()+".LoseCounter", integer);
        getDataHandler().save();
    }

    public boolean cubeNameExists(String name){
        AtomicBoolean exists = new AtomicBoolean(false);
        getCubes().forEach(cube -> {
            if(cube.getName().equalsIgnoreCase(name)){
                exists.set(true);
            }
        });
        return exists.get();
    }

}
