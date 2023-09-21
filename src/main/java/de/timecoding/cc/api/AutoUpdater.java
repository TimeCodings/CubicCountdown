package de.timecoding.cc.api;

import de.timecoding.cc.CubicCountdown;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;

public class AutoUpdater {


    private final CubicCountdown plugin;
    private final String downloadBase = "https://github.com/TimeCodings/CubicCountdown/releases/download/";
    private final String pluginVersion;
    private boolean autoUpdaterEnabled = true;
    private final String newestPluginVersion;
    private final boolean sent = false;

    public AutoUpdater(CubicCountdown plugin, String newestPluginVersion) {
        this.plugin = plugin;
        this.pluginVersion = plugin.getDescription().getVersion();
        if(this.plugin.getConfigHandler().keyExists("AutoUpdater")) {
            this.autoUpdaterEnabled = this.plugin.getConfigHandler().getBoolean("AutoUpdater");
        }
        this.newestPluginVersion = newestPluginVersion;
        checkForPluginUpdate();
    }

    public String getNewestPluginVersion() {
        String url = "https://api.github.com/repos/TimeCodings/CubicCountdown/releases/latest";
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            request.addHeader("content-type", "application/vnd.github.v3+json");
            HttpResponse result = httpClient.execute(request);
            JSONObject json = new JSONObject(EntityUtils.toString(result.getEntity(), "UTF-8"));
            if(json != null && json.has("tag_name")){
                return json.get("tag_name").toString();
            }else{
                Bukkit.getConsoleSender().sendMessage("§cCould not fetch the newest §eCubicCountdown §crelease! You may be offline!");
            }
        } catch (IOException ex) {
            Bukkit.getConsoleSender().sendMessage("§cCould not fetch the newest §eCubicCountdown §crelease! You may be offline!");
        }
        return pluginVersion;
    }

    public boolean pluginUpdateAvailable() {
        return !getNewestPluginVersion().replace("v", "").equalsIgnoreCase(pluginVersion);
    }

    public void checkForPluginUpdate() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Checking for updates...");
        if (pluginUpdateAvailable()) {
            if (autoUpdaterEnabled) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Update found! (" + getNewestPluginVersion() + ") Trying to download the newest update...");
                //Trying to download update
                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                    @Override
                    public void run() {
                        downloadUpdate();
                    }
                });
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Update found (" + getNewestPluginVersion() + ")! You can download it here: "+ChatColor.YELLOW+this.downloadBase + ""+getNewestPluginVersion()+"/CubicCountdown.jar");
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "No update found! You're running the latest version (v" + pluginVersion + ")");
        }
    }

    private boolean downloadUpdate() {
        try {
            URL download = new URL(this.downloadBase+getNewestPluginVersion()+"/CubicCountdown.jar?timestamp=" + System.currentTimeMillis());
            BufferedInputStream in = null;
            FileOutputStream fout = null;
            BufferedOutputStream bout = null;
            try {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Trying to download the newest CubicCountdown update...");
                in = new BufferedInputStream(download.openStream());
                fout = new FileOutputStream("plugins//" + this.getPluginNameByJar());
                bout = new BufferedOutputStream(fout);
                final byte[] data = new byte[1024];
                int count;
                while ((count = in.read(data, 0, 1024)) >= 0) {
                    bout.write(data, 0, count);
                }
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to download the newest CubicCountdown update!");
                return false;
            } finally {
                if (in != null) {
                    in.close();
                }
                if (bout != null) {
                    bout.close();
                }
            }
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Successfully downloaded the CubicCountdown update!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "The plugin will now try to reload the server...");
            Bukkit.reload();
            return true;
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to download the newest CubicCountdown update!");
            return false;
        }
    }

    private String getPluginNameByJar() {
        return new File(CubicCountdown.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
    }

}
