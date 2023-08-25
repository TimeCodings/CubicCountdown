package de.timecoding.cc;

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
import java.util.concurrent.atomic.AtomicReference;

public class CubicCountdown extends JavaPlugin {

    private ConfigHandler configHandler;
    private DataHandler dataHandler;
    private List<CountdownModule> countdownList = new ArrayList<>();
    private HashMap<Player, CubicSetup> setupList = new HashMap<>();

    //FOR API USAGE ONLY
    private CubicCountdown plugin;

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
        cubeStringList.forEach(string -> cubeList.add(new Cube(string, dataHandler.getLocation("Cube." + string + ".Pos1"), dataHandler.getLocation("Cube." + string + ".Pos2"))));
        return cubeList;
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
        return getDataHandler().getInteger("Cube."+cube.getName()+".Wins");
    }

    public void increaseWins(Cube cube){
        Integer integer = 1;
        if(getDataHandler().keyExists("Cube."+cube.getName()+".Wins")){
            integer = (getDataHandler().getInteger("Cube."+cube.getName()+".Wins")+1);
        }
        getDataHandler().getConfig().set("Cube."+cube.getName()+".Wins", integer);
        getDataHandler().save();
    }

    public Integer getLoses(Cube cube){
        return getDataHandler().getInteger("Cube."+cube.getName()+".Loses");
    }

    public void increaseLoses(Cube cube){
        Integer integer = 1;
        if(getDataHandler().keyExists("Cube."+cube.getName()+".Loses")){
            integer = (getDataHandler().getInteger("Cube."+cube.getName()+".Loses")+1);
        }
        getDataHandler().getConfig().set("Cube."+cube.getName()+".Loses", integer);
        getDataHandler().save();
    }

}
