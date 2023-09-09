package de.timecoding.cc.command.completer;

import de.timecoding.cc.CubicCountdown;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CubicCompleter implements TabCompleter {

    private CubicCountdown plugin;

    public CubicCompleter(CubicCountdown plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("cubiccountdown")) {
            List<String> completer = new ArrayList<>();
            if (strings.length == 1) {
                completer.add("setup");
                completer.add("cancel");
                completer.add("delete");
                completer.add("reload");
                completer.add("fill");
                completer.add("clear");
                completer.add("cubes");
                completer.add("setEntry");
                completer.add("help");
            } else if (strings.length == 2) {
                if (strings[0].equalsIgnoreCase("cancel")) {
                    plugin.getCountdownList().forEach(countdownModule -> completer.add(countdownModule.getCubicSettings().getCube().getName()));
                } else if (strings[0].equalsIgnoreCase("delete") || strings[0].equalsIgnoreCase("fill") || strings[0].equalsIgnoreCase("clear")) {
                    plugin.getCubicAPI().getCubes().forEach(cube -> completer.add(cube.getName()));
                } else if (strings[0].equalsIgnoreCase("setEntry") || strings[0].equalsIgnoreCase("set")) {
                    plugin.getConfigHandler().getConfig().getKeys(true).forEach(string -> completer.add(string));
                }
            } else if (strings.length == 3) {
                if (strings[0].equalsIgnoreCase("fill")) {
                    String value = strings[2];
                    if (value.contains(",")) {
                        String[] split = value.split(",");
                        final String[] matList = {""};
                        Arrays.stream(split).forEach(string -> {
                            if (isMaterial(string)) {
                                matList[0] = matList[0] + ",";
                            }
                        });
                        Arrays.stream(Material.values()).forEach(material -> completer.add(matList[0] + material.toString().toLowerCase()));
                    } else {
                        Arrays.stream(Material.values()).forEach(material -> completer.add(material.toString().toLowerCase()));
                    }
                } else if (strings[0].equalsIgnoreCase("setEntry") || strings[0].equalsIgnoreCase("set")) {
                    if (plugin.getConfigHandler().keyExists(strings[1])) {
                        completer.add(plugin.getConfigHandler().getConfig().get(strings[1]).toString());
                    }
                }
            } else if (strings.length == 4) {
                if (strings[0].equalsIgnoreCase("fill")) {
                    completer.add("1");
                    completer.add("10");
                    completer.add("100");
                    completer.add("1000");
                }
            }
            return completer;
        }
        return null;
    }

    private boolean isMaterial(String toTest) {
        try {
            Material.valueOf(toTest);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
