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
        if (params.startsWith("total_win_counter_")) {
            String cubeName = params.substring(18);
            if (plugin.getCubicAPI().cubeNameExists(cubeName)) {
                return plugin.getCubicAPI().getTotalWins(cubeName).toString();
            } else {
                return null;
            }
        } else if (params.startsWith("total_lose_counter_")) {
            String cubeName = params.substring(19);
            if (plugin.getCubicAPI().cubeNameExists(cubeName)) {
                return plugin.getCubicAPI().getTotalLoses(cubeName).toString();
            } else {
                return null;
            }
        } else if (params.startsWith("total_games_played_")) {
            String cubeName = params.substring(19);
            if (plugin.getCubicAPI().cubeNameExists(cubeName)) {
                return plugin.getCubicAPI().getTotalGamesPlayed(cubeName).toString();
            } else {
                return null;
            }
        }else if (params.startsWith("session_win_counter_")) {
            String cubeName = params.substring(20);
            if (plugin.getCubicAPI().cubeNameExists(cubeName)) {
                return plugin.getCubicAPI().getSessionWins(cubeName).toString();
            } else {
                return null;
            }
        } else if (params.startsWith("session_lose_counter_")) {
            String cubeName = params.substring(21);
            if (plugin.getCubicAPI().cubeNameExists(cubeName)) {
                return plugin.getCubicAPI().getSessionLoses(cubeName).toString();
            } else {
                return null;
            }
        } else if (params.startsWith("session_games_played_")) {
            String cubeName = params.substring(21);
            if (plugin.getCubicAPI().cubeNameExists(cubeName)) {
                return plugin.getCubicAPI().getSessionGamesPlayed(cubeName).toString();
            } else {
                return null;
            }
        }
        return null;
    }
}
