package de.timecoding.cc.api;

import de.timecoding.cc.CubicCountdown;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CubicExpansion extends PlaceholderExpansion {

    private CubicCountdown plugin;

    public CubicExpansion(CubicCountdown plugin){
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "CC_PAPI";
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
    public boolean persist(){
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.startsWith("%win_counter_") && params.endsWith("%")){
            String cubeName = params.substring(13, (params.length()-1));
            if(plugin.cubeNameExists(cubeName)){
                return plugin.getWins(cubeName).toString();
            }else{
                return null;
            }
        }else if(params.startsWith("%lose_counter_") && params.endsWith("%")){
                String cubeName = params.substring(14, (params.length()-1));
                if(plugin.cubeNameExists(cubeName)){
                    return plugin.getLoses(cubeName).toString();
                }else{
                    return null;
                }
        }
        return null;
    }
}
