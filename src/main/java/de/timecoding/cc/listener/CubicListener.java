package de.timecoding.cc.listener;

import de.timecoding.cc.CubicCountdown;
import de.timecoding.cc.event.CubeFilledOutEvent;
import de.timecoding.cc.event.CubeUnFilledEvent;
import de.timecoding.cc.file.DataHandler;
import de.timecoding.cc.util.CountdownModule;
import de.timecoding.cc.util.CubicSettings;
import de.timecoding.cc.util.type.Cube;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ConcurrentModificationException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CubicListener implements Listener {
    private CubicCountdown plugin;

    public CubicListener(CubicCountdown plugin) {
        this.plugin = plugin;
    }

    //WIN & LOSE COUNTER

    @EventHandler
    public void onCubeFilledOut(CubeFilledOutEvent event){
        if(event.getCubicSettings().getCube() != null){
            plugin.increaseWins(event.getCubicSettings().getCube());
        }
    }

    @EventHandler
    public void onCubeUnFilled(CubeUnFilledEvent event){
        if(event.getCubicSettings().getCube() != null){
            plugin.increaseLoses(event.getCubicSettings().getCube());
        }
    }

    //

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        proof(event.getPlayer(), event.getBlockPlaced().getLocation(), false);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        proof(event.getPlayer(), event.getBlock().getLocation(), true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        boolean stop = false;
        for(Block block : event.blockList()){
            if(!stop && proof(null, block.getLocation(), true)){
                stop = true;
            }
        }
    }

    public boolean proof(Player player, Location origin, boolean breakBlock) {
        DataHandler dataHandler = plugin.getDataHandler();
        AtomicReference<Cube> atomicCube = new AtomicReference<>();
        plugin.getCubes().forEach(searchedCube -> {
            if (searchedCube.inCube(origin)) {
                atomicCube.set(searchedCube);
            }
        });
        if (atomicCube.get() != null && atomicCube.get().filledOut() && !breakBlock) {
            CubicSettings cubicSettings = new CubicSettings(plugin, true);
            cubicSettings.setCube(atomicCube.get());
            if (player != null) {
                cubicSettings.addPlayer(player);
            }
            CountdownModule countdownModule = new CountdownModule(cubicSettings);
            CubeFilledOutEvent event = new CubeFilledOutEvent(player, countdownModule);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                countdownModule.start();
                return true;
            }
        } else if (atomicCube.get() != null && breakBlock) {
            AtomicBoolean r = new AtomicBoolean(false);
            try {
                plugin.getCountdownList().forEach(countdownModule -> {
                    if (countdownModule.getCubicSettings().getCube() != null && countdownModule.getCubicSettings().getCube().isSimilar(atomicCube.get())) {
                        CubeUnFilledEvent event = new CubeUnFilledEvent(player, countdownModule);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            countdownModule.cancel();
                            r.set(true);
                        }
                    }
                });
             //TODO
            }catch(ConcurrentModificationException exception){}
            return r.get();
        }
        return false;
    }
}
