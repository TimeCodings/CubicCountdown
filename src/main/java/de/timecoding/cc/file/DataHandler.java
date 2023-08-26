package de.timecoding.cc.file;

import de.timecoding.cc.CubicCountdown;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DataHandler {

    private final CubicCountdown plugin;
    private final ConfigHandler configHandler;
    public YamlConfiguration cfg = null;
    private File file = null;

    public DataHandler(CubicCountdown plugin) {
        this.plugin = plugin;
        this.configHandler = this.plugin.getConfigHandler();
        init();
    }

    public void init() {
        file = new File(plugin.getDataFolder(), "datas.yml");
        if (!file.exists()) {
            plugin.saveResource("datas.yml", false);
        }
        cfg = YamlConfiguration.loadConfiguration(file);
        cfg.options().copyDefaults(true);
    }

    public String getPluginVersion() {
        return plugin.getDescription().getVersion();
    }

    public void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        cfg = YamlConfiguration.loadConfiguration(file);
    }

    public YamlConfiguration getConfig() {
        return cfg;
    }


    public void setString(String key, String value) {
        cfg.set(key, value);
        save();
    }

    public void setLocation(String key, Location location) {
        cfg.set(key, location);
        save();
    }

    public Integer getInteger(String key) {
        if (keyExists(key)) {
            return cfg.getInt(key);
        }
        return 0;
    }

    public Location getLocation(String key) {
        if (keyExists(key)) {
            return cfg.getLocation(key);
        }
        return null;
    }

    public String getString(String key) {
        if (keyExists(key)) {
            return ChatColor.translateAlternateColorCodes('&', cfg.getString(key));
        }
        return "";
    }

    public Boolean getBoolean(String key) {
        if (keyExists(key)) {
            return cfg.getBoolean(key);
        }
        return false;
    }

    public boolean keyExists(String key) {
        return cfg.get(key) != null;
    }
}
