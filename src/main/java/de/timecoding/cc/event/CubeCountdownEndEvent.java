package de.timecoding.cc.event;

import de.timecoding.cc.util.CountdownModule;
import de.timecoding.cc.util.CubicSettings;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CubeCountdownEndEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private CubicSettings cubicSettings;
    private CountdownModule countdownModule;

    public CubeCountdownEndEvent(CountdownModule countdownModule) {
        this.countdownModule = countdownModule;
        this.cubicSettings = this.countdownModule.getCubicSettings();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CountdownModule getCountdownModule() {
        return countdownModule;
    }

    public CubicSettings getCubicSettings() {
        return cubicSettings;
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
