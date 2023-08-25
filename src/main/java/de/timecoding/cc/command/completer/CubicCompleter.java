package de.timecoding.cc.command.completer;

import de.timecoding.cc.CubicCountdown;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
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
                completer.add("help");
            } else if (strings.length == 2) {
                if (strings[0].equalsIgnoreCase("cancel")) {
                    plugin.getCountdownList().forEach(countdownModule -> completer.add(countdownModule.getCubicSettings().getCube().getName()));
                } else if (strings[0].equalsIgnoreCase("delete")) {
                    plugin.getCubes().forEach(cube -> completer.add(cube.getName()));
                }
            }
            return completer;
        }
        return null;
    }
}
