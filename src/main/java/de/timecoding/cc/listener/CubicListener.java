package de.timecoding.cc.listener;

import de.timecoding.cc.CubicCountdown;
import de.timecoding.cc.event.CubeCountdownEndEvent;
import de.timecoding.cc.event.CubeFilledOutEvent;
import de.timecoding.cc.event.CubeUnFilledEvent;
import de.timecoding.cc.file.DataHandler;
import de.timecoding.cc.util.CountdownModule;
import de.timecoding.cc.util.CubicSettings;
import de.timecoding.cc.util.type.Cube;
import me.clip.placeholderapi.PlaceholderAPI;
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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.server.ServerCommandEvent;

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
    public void onCubeCountdownEnd(CubeCountdownEndEvent event){
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
        proof(event.getPlayer(), event.getBlockPlaced().getLocation());
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event){
        plugin.getCubes().forEach(cube -> {
            proof(null, cube.blockList(true).get(0).getLocation());
        });
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        proof(event.getPlayer(), event.getBlock().getLocation());
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        boolean stop = false;
        for(Block block : event.blockList()){
            if(!stop && proof(null, block.getLocation())){
                stop = true;
            }
        }
    }

    public boolean proof(Player player, Location origin) {
        AtomicBoolean r = new AtomicBoolean(false);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
        DataHandler dataHandler = plugin.getDataHandler();
        AtomicReference<Cube> atomicCube = new AtomicReference<>();
        plugin.getCubes().forEach(searchedCube -> {
            if (searchedCube.inCube(origin)) {
                atomicCube.set(searchedCube);
                if (atomicCube.get() != null && atomicCube.get().filledOut()) {
                    CubicSettings cubicSettings = new CubicSettings(plugin, true);
                    cubicSettings.setCube(atomicCube.get());
                    if (plugin.getConfigHandler().getBoolean("Viewer.AllPlayers")) {
                        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> cubicSettings.addPlayer(onlinePlayer));
                    } else if (plugin.getConfigHandler().getBoolean("Viewer.AllPlayersInCubeRadius.Enabled")) {
                        atomicCube.get().blockList(true).forEach(block -> {
                            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                                if (block.getLocation().distance(onlinePlayer.getLocation()) < plugin.getConfigHandler().getInteger("Viewer.AllPlayersInCubeRadius.RadiusInBlocks")) {
                                    cubicSettings.addPlayer(onlinePlayer);
                                }
                            });
                        });
                    }
                    if (player != null && !cubicSettings.playerList().contains(player)) {
                        cubicSettings.addPlayer(player);
                    }

                    CountdownModule countdownModule = new CountdownModule(cubicSettings);
                    CubeFilledOutEvent event = new CubeFilledOutEvent(player, countdownModule);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        countdownModule.start();
                        r.set(true);
                    }
                } else if (atomicCube.get() != null && !atomicCube.get().filledOut()) {
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
                    } catch (ConcurrentModificationException exception) {
                    }
                }

            }
        });
            }
        }, 1);
        return r.get();
    }
}
