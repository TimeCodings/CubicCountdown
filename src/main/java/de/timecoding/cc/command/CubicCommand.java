package de.timecoding.cc.command;

import de.timecoding.cc.CubicCountdown;
import de.timecoding.cc.command.setup.CubicSetup;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class CubicCommand implements CommandExecutor {

    private CubicCountdown plugin;
    private String help = " \n §cCommands: \n §e/cc setup §7- §eStarts a setup to create a cubic-map \n §e/cc delete MAPNAME §7- §eDeletes the provided map \n §e/cc cancel MAPNAME §7- §eStops a running countdown of a map \n §e/cc reload §7- §eReloads all the plugin files \n §e/cc help §7- §eOpens the help menu \n ";

    public CubicCommand(CubicCountdown plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.isOp()) {
            if (strings.length == 1) {
                if (strings[0].equalsIgnoreCase("setup")) {
                    if (commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        if (!plugin.getSetupList().containsKey(player)) {
                            new CubicSetup((Player) commandSender, plugin);
                        } else {
                            player.playSound(player.getLocation(), Sound.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, 2, 2);
                            player.sendMessage("§cYou already started another setup! You need to cancel it to start a new one! §7(type §cCANCEL §7into the chat)");
                        }
                    } else {
                        commandSender.sendMessage("§cSorry, but only players are able to use this command!");
                    }
                } else if (strings[0].equalsIgnoreCase("reload")) {
                    this.plugin.getDataHandler().reload();
                    this.plugin.getConfigHandler().reload();
                    commandSender.sendMessage("§aThe §econfig.yml §aand §edata.yml §awere successfully reloaded!");
                } else if (strings[0].equalsIgnoreCase("help")) {
                    commandSender.sendMessage(this.help);
                } else {
                    commandSender.sendMessage("§cUnknown command! Type §e/cc help §cto look up all commands!");
                }
            } else if (strings.length == 2) {
                if (strings[0].equalsIgnoreCase("cancel")) {
                    String map = strings[1].toUpperCase();
                    AtomicInteger i = new AtomicInteger();
                    plugin.getCubes().forEach(cube -> {
                        if (cube.getName().equals(map)) {
                            i.getAndIncrement();
                            if (plugin.getCountdownModuleFromCube(cube) != null) {
                                plugin.getCountdownModuleFromCube(cube).cancel();
                                commandSender.sendMessage("§aThe Countdown was cancelled for map §e" + map);
                            } else {
                                commandSender.sendMessage("§cThere's no countdown running for the map §e" + map);
                            }
                        }
                    });
                    if (i.get() <= 0) {
                        commandSender.sendMessage("§cThe map §e" + map + " §cdoes not exist! You can create it with §e/cc setup");
                    }
                } else if (strings[0].equalsIgnoreCase("delete")) {
                    String map = strings[1].toUpperCase();
                    AtomicInteger i = new AtomicInteger();
                    plugin.getCubes().forEach(cube -> {
                        if (cube.getName().equals(map)) {
                            i.getAndIncrement();
                            if (plugin.getCountdownModuleFromCube(cube) != null) {
                                plugin.getCountdownModuleFromCube(cube).cancel();
                            }
                            plugin.getDataHandler().getConfig().set("Cube." + map, null);
                            plugin.getDataHandler().save();
                            commandSender.sendMessage("§aThe map §e" + map + " §awas deleted successfully!");
                        }
                    });
                    if (i.get() <= 0) {
                        commandSender.sendMessage("§cThe map §e" + map + " §cdoes not exist! You can create it with §e/cc setup");
                    }
                } else {
                    commandSender.sendMessage("§cUnknown command! Type §e/cc help §cto look up all commands!");
                }
            } else {
                commandSender.sendMessage("§cUnknown command! Type §e/cc help §cto look up all commands!");
            }
        } else {
            commandSender.sendMessage("§cYou do not have the permission to use that command! §cType §eop " + commandSender.getName() + " §cinto the server-console to get permission!");
        }
        return false;
    }
}
