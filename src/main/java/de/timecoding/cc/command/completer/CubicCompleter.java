package de.timecoding.cc.command.completer;

import de.timecoding.cc.CubicCountdown;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CubicCompleter implements TabCompleter {

    private CubicCountdown plugin;

    public CubicCompleter(CubicCountdown plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("cubiccountdown")) {
            List<String> complete = new ArrayList<>();
            List<String> completer = new ArrayList<>();
            if (strings.length == 1) {
                complete.add("setup");
                complete.add("cancel");
                complete.add("delete");
                complete.add("reload");
                complete.add("fill");
                complete.add("clear");
                complete.add("cubes");
                complete.add("simulate");
                complete.add("setEntry");
                complete.add("help");
                if(strings[0].length() > 0) {
                    complete.forEach(s1 -> {
                        if (s1.contains(strings[0])) {
                            completer.add(s1);
                        }
                    });
                }else{
                    completer.addAll(complete);
                }
            } else if (strings.length == 2) {
                if (strings[0].equalsIgnoreCase("cancel")) {
                    plugin.getCountdownList().forEach(countdownModule -> completer.add(countdownModule.getCubicSettings().getCube().getName()));
                } else if (strings[0].equalsIgnoreCase("delete") || strings[0].equalsIgnoreCase("fill") || strings[0].equalsIgnoreCase("clear")) {
                    plugin.getCubicAPI().getCubes().forEach(cube -> completer.add(cube.getName()));
                } else if (strings[0].equalsIgnoreCase("setEntry") || strings[0].equalsIgnoreCase("set")) {
                    plugin.getConfigHandler().getConfig().getKeys(true).forEach(string -> {
                        if(string.length() > 0 && string.contains(strings[1]) || strings[1].length() == 0){
                            completer.add(string);
                        }
                    });
                    plugin.getDataHandler().getConfig().getKeys(true).forEach(string -> {
                        if(string.length() > 0 && string.contains(strings[1]) || strings[1].length() == 0){
                            completer.add(string);
                        }
                    });
                }else if(strings[0].equalsIgnoreCase("simulate")){
                    complete.clear();
                    complete.add("win");
                    complete.add("lose");
                    complete.add("help");
                    if(strings[1].length() > 0) {
                        complete.forEach(s1 -> {
                            if (s1.contains(strings[0])) {
                                completer.add(s1);
                            }
                        });
                    }else{
                        completer.addAll(complete);
                    }
                }
            } else if (strings.length == 3) {
                if (strings[0].equalsIgnoreCase("fill")) {
                    String value = strings[2];
                    if (value.contains(",")) {
                        String[] split = value.split(",");
                        final String[] matList = {""};
                        Arrays.stream(split).forEach(string -> {
                            if (isMaterial(string.toUpperCase())) {
                                matList[0] = matList[0] + string + ",";
                            }
                        });
                        if(matList[0].equalsIgnoreCase("")){
                            matList[0] = value;
                        }
                        Arrays.stream(Material.values()).forEach(material -> completer.add(matList[0] + material.toString().toLowerCase()));
                    } else {
                        Arrays.stream(Material.values()).forEach(material -> completer.add(material.toString().toLowerCase()));
                    }
                } else if (strings[0].equalsIgnoreCase("setEntry") || strings[0].equalsIgnoreCase("set")) {
                    if (plugin.getConfigHandler().keyExists(strings[1])) {
                        completer.add(plugin.getConfigHandler().getConfig().get(strings[1]).toString());
                    }
                }else if(strings[0].equalsIgnoreCase("simulate")){
                    if(strings[1].equalsIgnoreCase("win") || strings[1].equalsIgnoreCase("lose") || strings[1].equalsIgnoreCase("help")){
                        plugin.getCubicAPI().getCubes().forEach(cube -> completer.add(cube.getName()));
                    }
                }
            } else if (strings.length == 4) {
                if (strings[0].equalsIgnoreCase("fill")) {
                    AtomicReference<String> after = new AtomicReference<>("");
                    String value = strings[2];
                    if(value.contains(",")){
                        String[] split = value.split(",");
                        Arrays.stream(split).forEach(string -> {
                            after.set(after + "PLACE, ");
                        });
                    }else{
                        after.set("PLACE, ");
                    }
                    for(int i = 1; i < 50; i++){
                        completer.add(after.get().substring(0, after.get().length()-2).replaceAll("PLACE", String.valueOf(i)));
                    }
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
