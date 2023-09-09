package de.timecoding.cc.api;

import de.timecoding.cc.CubicCountdown;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class CubicExpansion extends PlaceholderExpansion {

    private CubicCountdown plugin;

    public CubicExpansion(CubicCountdown plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "cc";
    }

    @Override
    public String getAuthor() {
        return "TimeCode";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.startsWith("win_counter_")) {
            String cubeName = params.substring(12);
            if (plugin.getCubicAPI().cubeNameExists(cubeName)) {
                return plugin.getCubicAPI().getWins(cubeName).toString();
            } else {
                return null;
            }
        } else if (params.startsWith("lose_counter_")) {
            String cubeName = params.substring(13);
            if (plugin.getCubicAPI().cubeNameExists(cubeName)) {
                return plugin.getCubicAPI().getLoses(cubeName).toString();
            } else {
                return null;
            }
        } else if (params.startsWith("games_played_")) {
            String cubeName = params.substring(13);
            if (plugin.getCubicAPI().cubeNameExists(cubeName)) {
                return plugin.getCubicAPI().getGamesPlayed(cubeName).toString();
            } else {
                return null;
            }
        }
        return null;
    }
}
