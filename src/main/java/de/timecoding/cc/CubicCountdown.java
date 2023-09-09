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
import de.timecoding.cc.util.CubicAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public class CubicCountdown extends JavaPlugin {

    private ConfigHandler configHandler;
    private DataHandler dataHandler;
    private CubicAPI cubicAPI;

    //FOR API USAGE ONLY
    private CubicCountdown plugin;
    private CubicListener cubicListener;
    private Metrics metrics = null;

    public void onEnable() {
        this.plugin = this;
        this.configHandler = new ConfigHandler(this);
        this.dataHandler = new DataHandler(this);
        this.cubicListener = new CubicListener(this);
        this.getServer().getPluginManager().registerEvents(this.cubicListener, this);
        cubicAPI = new CubicAPI(this);
        PluginCommand pluginCommand = this.getCommand("cubiccountdown");
        pluginCommand.setExecutor(new CubicCommand(this));
        pluginCommand.setTabCompleter(new CubicCompleter(this));
        Bukkit.getConsoleSender().sendMessage("§cCubicCountdown §7(v" + this.getDescription().getVersion() + ") §aby §eTimeCode §awas enabled!");
        Bukkit.getConsoleSender().sendMessage("§cTHIS IS A BETA VERSION OF THE PLUGIN! PLEASE REPORT ALL ISSUES OR WISHES TO OUR DISCORD: https://discord.tikmc.de/");
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Bukkit.getConsoleSender().sendMessage("§aSuccessfully registered the CubicCountdown §ePlaceholderAPI Expansion§a!");
            new CubicExpansion(this).register();
        }
        if (!configHandler.keyExists("bStats") || configHandler.getBoolean("bStats")) {
            this.metrics = new Metrics(this, 19676);
        }
        cubicAPI.startActionbar();
        for (String key : plugin.getDataHandler().getConfig().getKeys(true)) {
            if (key.endsWith("Wins") || key.endsWith("Loses")) {
                plugin.getDataHandler().getConfig().set(key, null);
                Bukkit.getConsoleSender().sendMessage("§aSuccessfully deleted the §ewin and/or lose §acounter of the map §e" + key.split("\\.")[1]);
                plugin.getDataHandler().save();
            }
        }
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public CubicListener getCubicListener() {
        return cubicListener;
    }

    public List<CountdownModule> getCountdownList() {
        return getCubicAPI().getCountdownList();
    }

    public HashMap<Player, CubicSetup> getSetupList() {
        return getCubicAPI().getSetupList();
    }

    public CubicCountdown getInstance() {
        return plugin;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public CubicAPI getCubicAPI() {
        return cubicAPI;
    }

}
