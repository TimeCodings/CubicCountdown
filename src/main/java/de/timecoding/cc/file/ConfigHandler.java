package de.timecoding.cc.file;

import de.timecoding.cc.CubicCountdown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigHandler {

    private final CubicCountdown plugin;
    private final String newconfigversion = "1.1.0";
    private final boolean retry = false;
    public YamlConfiguration cfg = null;
    private File f = null;

    public ConfigHandler(CubicCountdown plugin) {
        this.plugin = plugin;
        init();
    }

    public void init() {
        plugin.saveDefaultConfig();
        f = new File(plugin.getDataFolder(), "config.yml");
        cfg = YamlConfiguration.loadConfiguration(f);
        cfg.options().copyDefaults(true);
        checkForConfigUpdate();
    }

    public String getPluginVersion() {
        return plugin.getDescription().getVersion();
    }

    public void save() {
        try {
            cfg.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        cfg = YamlConfiguration.loadConfiguration(f);
    }

    public YamlConfiguration getConfig() {
        return cfg;
    }

    public void setString(String key, String value) {
        cfg.set(key, value);
        save();
    }

    public Integer getInteger(String key) {
        if (keyExists(key)) {
            return cfg.getInt(key);
        }
        return 0;
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

    public List<String> getStringList(String key) {
        if (keyExists(key)) {
            List<String> list = getConfig().getStringList(key);
            list.replaceAll(msg -> msg.replace("&", "§"));
            return list;
        }
        return new ArrayList<>();
    }

    public Material getMaterialByString(String material) {
        for (Material mats : Material.values()) {
            if (mats.name().equalsIgnoreCase(material)) {
                return Material.valueOf(material);
            }
        }
        return Material.GRASS_BLOCK;
    }

    public Enchantment getEnchantmentByString(String enchant) {
        for (Enchantment ench : Enchantment.values()) {
            if (ench.toString().equalsIgnoreCase(enchant)) {
                return Enchantment.getByName(enchant);
            }
        }
        return Enchantment.ARROW_DAMAGE;
    }

    public String getNewestConfigVersion() {
        return this.newconfigversion;
    }

    public boolean configUpdateAvailable() {
        return !getNewestConfigVersion().equalsIgnoreCase(getString("config-version"));
    }

    public void checkForConfigUpdate() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Checking for config updates...");
        if (configUpdateAvailable()) {
            final Map<String, Object> quicksave = getConfig().getValues(true);
            File file = new File("plugins//JustOneBlock", "config.yml");
            if (file.exists()) {
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Config Update found! (" + getNewestConfigVersion() + ") Updating config...");
                Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, new Runnable() {

                    public void run() {
                        plugin.saveResource("config.yml", true);
                        reload();
                        for (String save : quicksave.keySet()) {
                            if (keyExists(save) && quicksave.get(save) != null && !save.equalsIgnoreCase("config-version") && !save.equalsIgnoreCase("License-Key")) {
                                getConfig().set(save, quicksave.get(save));
                            }
                        }
                        save();
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Config got updated!");
                    }
                }, 50);
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "No Config found! Creating a new one...");
                this.plugin.saveResource("config.yml", false);
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "No config update found!");
        }
    }


    public Integer getItemSlot(String key) {
        return getInteger("Item." + key + ".Slot");
    }

    public Integer readItemSlot(String key) {
        return getInteger(key + ".Slot");
    }

    public boolean keyExists(String key) {
        return cfg.get(key) != null;
    }
}
