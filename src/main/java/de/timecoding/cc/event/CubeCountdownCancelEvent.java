package de.timecoding.cc.event;

import de.timecoding.cc.util.CountdownModule;
import de.timecoding.cc.util.CubicSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CubeCountdownCancelEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private Player player;
    private CubicSettings cubicSettings;
    private CountdownModule countdownModule;

    public CubeCountdownCancelEvent(Player player, CountdownModule countdownModule) {
        this.player = player;
        this.countdownModule = countdownModule;
        this.cubicSettings = this.countdownModule.getCubicSettings();
    }

    public boolean whileFillAnimation(){
        if(cubicSettings.getCube() != null){
            return countdownModule.getPlugin().getCubicAPI().getFillAnimationList().containsKey(cubicSettings.getCube().getName());
        }
        return false;
    }

    public boolean whileClearAnimation(){
        if(cubicSettings.getCube() != null){
            return countdownModule.getPlugin().getCubicAPI().getClearAnimationList().containsKey(cubicSettings.getCube().getName());
        }
        return false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public CountdownModule getCountdownModule() {
        return countdownModule;
    }

    public CubicSettings getCubicSettings() {
        return cubicSettings;
    }

    public void setCubicSettings(CubicSettings cubicSettings) {
        this.cubicSettings = cubicSettings;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
