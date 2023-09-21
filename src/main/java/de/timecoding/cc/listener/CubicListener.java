package de.timecoding.cc.listener;

import de.timecoding.cc.CubicCountdown;
import de.timecoding.cc.event.CubeCountdownCancelEvent;
import de.timecoding.cc.event.CubeCountdownEndEvent;
import de.timecoding.cc.event.CubeCountdownStartEvent;
import de.timecoding.cc.file.DataHandler;
import de.timecoding.cc.util.CountdownModule;
import de.timecoding.cc.util.CubicSettings;
import de.timecoding.cc.util.type.Cube;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CubicListener implements Listener {
    private CubicCountdown plugin;

    public CubicListener(CubicCountdown plugin) {
        this.plugin = plugin;
        this.startChecker();
    }

    //WIN & LOSE COUNTER + CommandExecuter

    @EventHandler
    public void onCubeCountdownEnd(CubeCountdownEndEvent event) {
        if (event.getCubicSettings().getCube() != null) {
            executeCommands("OnCountdownEnd", event.getCubicSettings().getCube().getName());
            plugin.getCubicAPI().increaseWins(event.getCubicSettings().getCube());
        }
    }

    @EventHandler
    public void onCubeCountdownCancel(CubeCountdownCancelEvent event) {
        if (event.getCubicSettings().getCube() != null) {
            executeCommands("OnCountdownCancel", event.getCubicSettings().getCube().getName());
            plugin.getCubicAPI().increaseLoses(event.getCubicSettings().getCube());
        }
    }

    @EventHandler
    public void onCubeCountdownStart(CubeCountdownStartEvent event){
        if (event.getCubicSettings().getCube() != null) {
            executeCommands("OnCountdownStart", event.getCubicSettings().getCube().getName());
        }
    }

    private void executeCommands(String key, String map){
        plugin.getConfigHandler().getStringList("Commands."+key+"").forEach(command -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%map%", map));
        });
    }

    //

    //CAUSE CHECKER

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        addCause(plugin.getCubicAPI().getCubeAtLocation(event.getBlock().getLocation()), event.getPlayer());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        addCause(plugin.getCubicAPI().getCubeAtLocation(event.getBlock().getLocation()), event.getPlayer());
    }

    //

    private int checkID = -1;

    private boolean checkerRunning(){
        return (checkID != -1);
    }

    private void startChecker(){
        if(!checkerRunning()){
            Integer checker = 40;
            if(plugin.getConfigHandler().keyExists("CheckerTicks")){
                checker = plugin.getConfigHandler().getInteger("CheckerTicks");
            }
            Integer finalChecker = checker;
            this.checkID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if(plugin.getConfigHandler().keyExists("CheckerTicks") && plugin.getConfigHandler().getInteger("CheckerTicks") != finalChecker){
                        stopChecker();
                        startChecker();
                    }
                    plugin.getCubicAPI().getCubes().forEach(cube -> {
                        if(proofForAll(lastCauses.get(cube.getName()), cube.getPos1())){
                            clearCauses(cube);
                        }
                    });;
                }
            },checker, checker);
        }
    }

    private HashMap<String, List<Player>> lastCauses = new HashMap<>();

    private void addCause(Cube cube, Player player){
        if(cube != null) {
            List<Player> last = new ArrayList<>();
            if (lastCauses.containsKey(cube.getName())) {
                last = lastCauses.get(cube.getName());
                lastCauses.remove(cube.getName());
            }
            if (!last.contains(player)) {
                last.add(player);
            }
            lastCauses.put(cube.getName(), last);
        }
    }

    private void clearCauses(Cube cube){
        if(lastCauses.containsKey(cube.getName())){
            lastCauses.remove(cube.getName());
        }
    }

    private void stopChecker(){
        if(checkerRunning()){
            Bukkit.getScheduler().cancelTask(checkID);
            checkID = -1;
        }
    }

    public boolean proofForAll(List<Player> playerList, Location origin){
        AtomicBoolean proofed = new AtomicBoolean(false);
        if(playerList != null && playerList.size() > 0) {
            playerList.forEach(player -> {
                if(!proofed.get() && proof(player, origin)){
                    proofed.set(true);
                }
            });
        }else{
            proof(null, origin);
        }
        return proofed.get();
    }

    public boolean proof(Player player, Location origin) {
        AtomicBoolean r = new AtomicBoolean(false);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                DataHandler dataHandler = plugin.getDataHandler();
                AtomicReference<Cube> atomicCube = new AtomicReference<>();
                plugin.getCubicAPI().getCubes().forEach(searchedCube -> {
                    if (searchedCube.inCube(origin)) {
                        atomicCube.set(searchedCube);
                        boolean reverse = plugin.getConfigHandler().getBoolean("Reverse");
                        if (!reverse && atomicCube.get() != null && atomicCube.get().filledOut() || reverse && atomicCube.get() != null && atomicCube.get().empty()) {
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
                            CubeCountdownStartEvent event = new CubeCountdownStartEvent(player, countdownModule);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                countdownModule.start();
                                r.set(true);
                            }
                        } else if (!reverse && atomicCube.get() != null && !atomicCube.get().filledOut() || reverse && atomicCube.get() != null && !atomicCube.get().empty()) {
                            try {
                                plugin.getCountdownList().forEach(countdownModule -> {
                                    if (countdownModule.getCubicSettings().getCube() != null && countdownModule.getCubicSettings().getCube().isSimilar(atomicCube.get())) {
                                        CubeCountdownCancelEvent event = new CubeCountdownCancelEvent(player, countdownModule);
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
