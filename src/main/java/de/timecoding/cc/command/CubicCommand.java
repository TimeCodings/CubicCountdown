package de.timecoding.cc.command;

import de.timecoding.cc.CubicCountdown;
import de.timecoding.cc.command.setup.CubicSetup;
import de.timecoding.cc.util.type.Cube;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CubicCommand implements CommandExecutor {

    private CubicCountdown plugin;

    private HashMap<String, HashMap<List<Material>, List<Integer>>> queue = new HashMap<>();
    private HashMap<String, Integer> fillTaskList = new HashMap<>();
    private HashMap<String, Integer> clearTaskList = new HashMap<>();
    private String help = " \n §cCommands: \n §e/cc setup §7- §eStarts a setup to create a cubic-map \n §e/cc delete MAPNAME §7- §eDeletes the provided map \n §e/cc fill MAPNAME BLOCKTYPE1,BLOCKTYPE2,... AMOUNT1,AMOUNT2,... §7- §eFills the cube with an specific amount of specific blocks \n §e/cc clear MAPNAME §7- §eClears the whole area inside the cube \n §e/cc cancel MAPNAME §7- §eStops a running countdown of a map \n §e/cc reload §7- §eReloads all the plugin files \n §e/cc cubes §7- §eDisplays all available cubes \n §e/cc help §7- §eOpens the help menu \n §e/cc setEntry KEY VALUE §7- §eEdits a specific config entry \n ";

    public CubicCommand(CubicCountdown plugin) {
        this.plugin = plugin;
        this.fillTaskList = plugin.getCubicAPI().getFillAnimationList();
        this.clearTaskList = plugin.getCubicAPI().getClearAnimationList();
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
                    plugin.getCubicAPI().startActionbar();
                } else if (strings[0].equalsIgnoreCase("cubes")) {
                    String maps = "";
                    for (Cube cube : plugin.getCubicAPI().getCubes()) {
                        maps = maps + "§e" + cube.getName() + "§7, ";
                    }
                    if (maps.length() > 1) {
                        maps = maps.substring(0, (maps.length() - 2));
                        commandSender.sendMessage(" \n §aAvailable Cubes/Maps: \n " + maps + " \n ");
                    } else {
                        commandSender.sendMessage("§cYou haven't created any cube yet! Use §e/cc setup §cto create one!");
                    }
                } else if (strings[0].equalsIgnoreCase("help")) {
                    commandSender.sendMessage(this.help);
                } else {
                    commandSender.sendMessage("§cUnknown command! Type §e/cc help §cto look up all commands!");
                }
            } else if (strings.length == 2) {
                if (strings[0].equalsIgnoreCase("cancel")) {
                    String map = strings[1].toUpperCase();
                    AtomicInteger i = new AtomicInteger();
                    plugin.getCubicAPI().getCubes().forEach(cube -> {
                        if (cube.getName().equals(map)) {
                            i.getAndIncrement();
                            if (plugin.getCubicAPI().getCountdownModuleFromCube(cube) != null) {
                                plugin.getCubicAPI().getCountdownModuleFromCube(cube).cancel();
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
                    plugin.getCubicAPI().getCubes().forEach(cube -> {
                        if (cube.getName().equals(map)) {
                            i.getAndIncrement();
                            if (plugin.getCubicAPI().getCountdownModuleFromCube(cube) != null) {
                                plugin.getCubicAPI().getCountdownModuleFromCube(cube).cancel();
                            }
                            plugin.getDataHandler().getConfig().set("Cube." + map, null);
                            plugin.getDataHandler().save();
                            commandSender.sendMessage("§aThe map §e" + map + " §awas deleted successfully!");
                        }
                    });
                    if (i.get() <= 0) {
                        commandSender.sendMessage("§cThe map §e" + map + " §cdoes not exist! You can create it with §e/cc setup");
                    }
                } else if (strings[0].equalsIgnoreCase("clear")) {
                    String cubeName = strings[1].toUpperCase();
                    if (plugin.getCubicAPI().cubeNameExists(cubeName)) {
                        Cube cube = plugin.getCubicAPI().getCubeByName(cubeName);
                        if(plugin.getConfigHandler().getBoolean("ClearAction.Animation")){
                                if(!clearTaskList.containsKey(cubeName)){
                                    int taskID;
                                    int animationTicks = plugin.getConfigHandler().getInteger("ClearAction.AnimationTicks");
                                    taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                                        List<Block> removedBlocks = new ArrayList<>();
                                        int i = 0;
                                        @Override
                                        public void run() {
                                            List<Block> blockList = cube.blockList(false);
                                            blockList.removeAll(removedBlocks);
                                            if(!blockList.isEmpty()){
                                                if (!plugin.getConfigHandler().getStringList("ClearAction.DisabledBlocks").contains(blockList.get(0).getType().toString().toUpperCase())) {
                                                    blockList.get(0).setType(Material.AIR);
                                                    if (plugin.getConfigHandler().getBoolean("ClearAction.Effect.Enabled")) {
                                                        for (int i = 0; i <= (plugin.getConfigHandler().getInteger("ClearAction.Effect.BlocksAhead")); i++) {
                                                            if (blockList.size() > (this.i + i)) {
                                                                blockList.get((this.i + i)).getLocation().getWorld().playEffect(blockList.get((this.i + i)).getLocation(), Effect.valueOf(plugin.getConfigHandler().getString("ClearAction.Effect.Type")), plugin.getConfigHandler().getConfig().getInt("ClearAction.Effect.Amount"));
                                                            }
                                                        }
                                                    }
                                                    if (plugin.getConfigHandler().getBoolean("ClearAction.ProceedSound.Enabled") && blockList.size() > 0) {
                                                        if(plugin.getConfigHandler().getString("ClearAction.ProceedSound.Custom").equalsIgnoreCase("")) {
                                                            blockList.get(0).getWorld().playSound(blockList.get(0).getLocation(), Sound.valueOf(plugin.getConfigHandler().getString("ClearAction.ProceedSound.Sound")), (float) plugin.getConfigHandler().getConfig().getDouble("ClearAction.ProceedSound.Volume"), (float) plugin.getConfigHandler().getConfig().getDouble("ClearAction.ProceedSound.Pitch"));
                                                        }else{
                                                            blockList.get(0).getWorld().playSound(blockList.get(0).getLocation(), plugin.getConfigHandler().getString("ClearAction.ProceedSound.Custom"), (float) plugin.getConfigHandler().getConfig().getDouble("ClearAction.ProceedSound.Volume"), (float) plugin.getConfigHandler().getConfig().getDouble("ClearAction.ProceedSound.Pitch"));
                                                        }
                                                    }
                                                }
                                                removedBlocks.add(blockList.get(0));
                                            }else{
                                                Bukkit.getScheduler().cancelTask(clearTaskList.get(cubeName));
                                                Player player = null;
                                                if (commandSender instanceof Player) {
                                                    player = (Player) commandSender;
                                                }
                                                plugin.getCubicListener().proof(player, cube.getPos1(), true);
                                            }
                                        }
                                    }, animationTicks, animationTicks);
                                    clearTaskList.put(cubeName, taskID);
                                    commandSender.sendMessage("§aSuccessfully cleared the whole area in the cube §e" + cubeName);

                                }else{
                                    commandSender.sendMessage("§cPlease wait until §e" + cubeName + "'s §crunning §eClear §canimation is finished!");
                                }
                        }else {
                            clearTaskList.put(cubeName, 00000);
                            cube.blockList(false).forEach(block -> {
                                if (!plugin.getConfigHandler().getStringList("ClearAction.DisabledBlocks").contains(block.getType().toString().toUpperCase())) {
                                    block.setType(Material.AIR);
                                }
                            });
                            Player player = null;
                            if (commandSender instanceof Player) {
                                player = (Player) commandSender;
                            }
                            plugin.getCubicListener().proof(player, cube.getPos1(), true);
                            commandSender.sendMessage("§aSuccessfully cleared the whole area in the cube §e" + cubeName);
                        }
                    } else {
                        commandSender.sendMessage("§cThe map §e" + cubeName + " §cdoes not exist! You can create it with §e/cc setup");
                    }
                } else {
                    commandSender.sendMessage("§cUnknown command! Type §e/cc help §cto look up all commands!");
                }
            } else if (strings.length == 4) {
                if (strings[0].equalsIgnoreCase("fill")) {
                    String cubeName = strings[1].toUpperCase();
                    if (plugin.getCubicAPI().cubeNameExists(cubeName)) {
                        List<String> typeList = Arrays.stream(strings[2].split(",")).collect(Collectors.toList());
                        final List<Integer>[] amountList = new List[]{new ArrayList<>()};
                        final String[] amountErrors = {""};
                        Arrays.stream(strings[3].split(",")).forEach(string -> {
                            if (isInteger(string)) {
                                amountList[0].add(Integer.parseInt(string));
                            } else {
                                amountErrors[0] = "§e" + amountErrors[0] + string + ", ";
                            }
                        });
                        if (amountErrors[0].length() > 0) {
                            commandSender.sendMessage("§cThe following parameters aren't numbers: §e" + amountErrors[0].substring(0, (amountErrors[0].length() - 2)));
                            return false;
                        }
                        if (typeList.size() == 0) {
                            typeList.add(strings[2]);
                        }
                        if (amountList[0].size() == 0) {
                            if (isInteger(strings[3])) {
                                amountList[0].add(Integer.parseInt(strings[3]));
                            } else {
                                commandSender.sendMessage("§e" + strings[3] + " §cis not a valid number!");
                                return false;
                            }
                        }
                        if (typeList.size() == amountList[0].size()) {
                            String notExist = "";
                            final List<Material>[] materialList = new List[]{new ArrayList<>()};
                            int i = 0;
                            for (String type : typeList) {
                                if (isMaterial(type.toUpperCase())) {
                                    materialList[0].add(Material.valueOf(type.toUpperCase()));
                                } else {
                                    notExist = notExist + type + ", ";
                                }
                            }
                                if (!fillTaskList.containsKey(cubeName)) {
                                    if (notExist.length() > 0) {
                                        commandSender.sendMessage("§cThe following material-type(s) does not exist: §e" + notExist.substring(0, notExist.length() - 2));
                                    } else {
                                        final AtomicInteger[] random = {new AtomicInteger(new Random().nextInt(materialList[0].size()))};
                                        Cube cube = plugin.getCubicAPI().getCubeByName(cubeName);
                                        int task = 0;
                                        int animationTicks = plugin.getConfigHandler().getInteger("FillAction.AnimationTicks");
                                        final List<Block>[] blockList = new List[]{cube.blockList(true)};
                                        if (plugin.getConfigHandler().getBoolean("FillAction.OnlyAir")) {
                                            blockList[0] = cube.airBlockList();
                                        }
                                        commandSender.sendMessage("§aSuccessfully performed §efill-action §afor map §e" + cubeName + "§a!");
                                        final List<Block>[] finalBlockList = new List[]{blockList[0]};
                                        List<Player> playerList = new ArrayList<>();
                                        if (plugin.getConfigHandler().getBoolean("Viewer.AllPlayers")) {
                                            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> playerList.add(onlinePlayer));
                                        } else if (plugin.getConfigHandler().getBoolean("Viewer.AllPlayersInCubeRadius.Enabled")) {
                                            cube.blockList(true).forEach(block -> {
                                                Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                                                    if (block.getLocation().distance(onlinePlayer.getLocation()) < plugin.getConfigHandler().getInteger("Viewer.AllPlayersInCubeRadius.RadiusInBlocks")) {
                                                        playerList.add(onlinePlayer);
                                                    }
                                                });
                                            });
                                        } else if (commandSender instanceof Player) {
                                            playerList.add((Player) commandSender);
                                        }
                                        if (plugin.getConfigHandler().getBoolean("FillAction.Animation")) {
                                            List<Block> finalBlockList1 = blockList[0];
                                            task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                                                int i = 0;
                                                List<Block> removedBlocks = new ArrayList<>();

                                                @Override
                                                public void run() {
                                                    finalBlockList[0] = new ArrayList<>();
                                                    if (plugin.getConfigHandler().getBoolean("FillAction.OnlyAir")) {
                                                        finalBlockList[0] = cube.airBlockList();
                                                    }else{
                                                        finalBlockList[0] = cube.blockList(true);
                                                    }
                                                    removedBlocks.forEach(block -> {
                                                        if(block != null && !block.getType().isAir()){
                                                            finalBlockList[0].remove(block);
                                                        }
                                                    });
                                                    if (amountList[0].size() > random[0].get() && amountList[0].get(random[0].get()) > 0 && i < finalBlockList[0].size()) {
                                                        if (!plugin.getConfigHandler().getStringList("FillAction.DisabledBlocks").contains(finalBlockList[0].get(i).getType().toString().toUpperCase())) {
                                                            removedBlocks.add(finalBlockList[0].get(i));
                                                            fillCube(amountList, materialList, i, random, finalBlockList[0]);
                                                            if (plugin.getConfigHandler().getBoolean("FillAction.Effect.Enabled")) {
                                                                for (int i = 0; i <= (plugin.getConfigHandler().getInteger("FillAction.Effect.BlocksAhead")); i++) {
                                                                    if (finalBlockList1.size() > (this.i + i)) {
                                                                        finalBlockList1.get((this.i + i)).getLocation().getWorld().playEffect(finalBlockList1.get((this.i + i)).getLocation(), Effect.valueOf(plugin.getConfigHandler().getString("FillAction.Effect.Type")), plugin.getConfigHandler().getConfig().getInt("FillAction.Effect.Amount"));
                                                                    }
                                                                }
                                                            }
                                                            if (plugin.getConfigHandler().getBoolean("FillAction.ProceedSound.Enabled") && finalBlockList[0].size() > 0) {
                                                                if(plugin.getConfigHandler().getString("FillAction.ProceedSound.Custom").equalsIgnoreCase("")) {
                                                                    finalBlockList[0].get(0).getWorld().playSound(finalBlockList[0].get(0).getLocation(), Sound.valueOf(plugin.getConfigHandler().getString("FillAction.ProceedSound.Sound")), (float) plugin.getConfigHandler().getConfig().getDouble("FillAction.ProceedSound.Volume"), (float) plugin.getConfigHandler().getConfig().getDouble("FillAction.ProceedSound.Pitch"));
                                                                }else{
                                                                    finalBlockList[0].get(0).getWorld().playSound(finalBlockList[0].get(0).getLocation(), plugin.getConfigHandler().getString("FillAction.ProceedSound.Custom"), (float) plugin.getConfigHandler().getConfig().getDouble("FillAction.ProceedSound.Volume"), (float) plugin.getConfigHandler().getConfig().getDouble("FillAction.ProceedSound.Pitch"));
                                                                }
                                                            }
                                                            finalBlockList[0].get(i).getWorld().getPlayers().forEach(player -> {
                                                                if (finalBlockList[0].get(i).getLocation().distance(player.getLocation()) <= 1.0 && plugin.getConfigHandler().getBoolean("FillAction.Teleport")) {
                                                                    player.teleport(player.getLocation().add(0, 1, 0));
                                                                }
                                                            });
                                                        }
                                                    } else {
                                                        boolean progress = true;
                                                        for (Integer i : amountList[0]) {
                                                            if (i > 0) {
                                                                progress = false;
                                                            }
                                                        }
                                                        if (progress && !queue.containsKey(cubeName.toUpperCase())) {
                                                            Bukkit.getScheduler().cancelTask(fillTaskList.get(cubeName));
                                                            Player player = null;
                                                            if (commandSender instanceof Player) {
                                                                player = (Player) commandSender;
                                                            }
                                                            plugin.getCubicListener().proof(player, cube.getPos1(), true);
                                                        } else if (progress && queue.containsKey(cubeName.toUpperCase())) {
                                                            HashMap<List<Material>, List<Integer>> mixedMap = queue.get(cubeName.toUpperCase());
                                                            materialList[0] = mixedMap.keySet().stream().collect(Collectors.toList()).get(0);
                                                            amountList[0] = mixedMap.values().stream().collect(Collectors.toList()).get(0);
                                                            random[0] = new AtomicInteger(new Random().nextInt(materialList[0].size()));
                                                            queue.get(cubeName.toUpperCase()).remove(materialList[0], amountList[0]);
                                                            if (mixedMap.size() < 1) {
                                                                queue.remove(cubeName.toUpperCase());
                                                            }
                                                        } else if (!progress) {
                                                            if (materialList[0].size() > random[0].get()) {
                                                                materialList[0].remove(materialList[0].get(random[0].get()));
                                                                amountList[0].remove(amountList[0].get(random[0].get()));
                                                                if (materialList[0].size() > 0) {
                                                                    random[0].set(new Random().nextInt(materialList[0].size()));
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }, animationTicks, animationTicks);
                                            fillTaskList.put(cubeName, task);
                                        } else {
                                            fillTaskList.put(cubeName, 00000);
                                            while (amountList[0].size() > random[0].get() && amountList[0].get(random[0].get()) > 0 && i < finalBlockList[0].size()) {
                                                if (!plugin.getConfigHandler().getStringList("FillAction.DisabledBlocks").contains(finalBlockList[0].get(i).getType().toString().toUpperCase())) {
                                                    fillCube(amountList, materialList, i, random, finalBlockList[0]);
                                                    int finalI = i;
                                                    finalBlockList[0].get(i).getWorld().getPlayers().forEach(player -> {
                                                        if (finalBlockList[0].get(finalI).getLocation().distance(player.getLocation()) <= 1.0 && plugin.getConfigHandler().getBoolean("FillAction.Teleport")) {
                                                            player.teleport(player.getLocation().add(0, 1, 0));
                                                        }
                                                    });
                                                }
                                                i++;
                                            }
                                            Player player = null;
                                            if (commandSender instanceof Player) {
                                                player = (Player) commandSender;
                                            }
                                            plugin.getCubicListener().proof(player, cube.getPos1(), true);
                                        }
                                    }
                                } else {
                                    if (plugin.getConfigHandler().getBoolean("FillAction.Queue")) {
                                        if (queue.containsKey(cubeName.toUpperCase())) {
                                            queue.get(cubeName.toUpperCase()).put(materialList[0], amountList[0]);
                                        } else {
                                            HashMap hashMap = new HashMap();
                                            hashMap.put(materialList[0], amountList[0]);
                                            queue.put(cubeName.toUpperCase(), hashMap);
                                        }
                                        commandSender.sendMessage("§aSuccessfully added your §efill-action §ato the §equeue§a!");
                                    } else {
                                        commandSender.sendMessage("§cPlease wait until §e" + cubeName + "'s §crunning §eFill animation is finished!");
                                    }
                                }
                        } else {
                            commandSender.sendMessage("§cPlease configurate an amount for every block-type you've added to the command!");
                        }
                    } else {
                        commandSender.sendMessage("§cThe map §e" + cubeName + " §cdoes not exist! You can create it with §e/cc setup");
                    }
                } else {
                    commandSender.sendMessage("§cUnknown command! Type §e/cc help §cto look up all commands!");
                }
            } else if (strings.length == 3) {
                if (strings[0].equalsIgnoreCase("setEntry") || strings[0].equalsIgnoreCase("set")) {
                    if (plugin.getConfigHandler().keyExists(strings[1])) {
                        if (isBoolean(strings[2])) {
                            plugin.getConfigHandler().getConfig().set(strings[1], Boolean.parseBoolean(strings[2]));
                        } else if (isInteger(strings[2])) {
                            plugin.getConfigHandler().getConfig().set(strings[1], Integer.parseInt(strings[2]));
                        } else {
                            plugin.getConfigHandler().getConfig().set(strings[1], strings[2]);
                        }
                        plugin.getConfigHandler().save();
                        plugin.getConfigHandler().reload();
                        plugin.getCubicAPI().startActionbar();
                        commandSender.sendMessage("§aSuccessfully set §e" + strings[1] + "'s §avalue to §e" + strings[2] + "§a!");
                    } else {
                        commandSender.sendMessage("§cThe key §e" + strings[1] + " §cdoes not exist!");
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

    private void fillCube(List<Integer>[] amountList, List<Material>[] materialList, int i, AtomicInteger[] random, List<Block> finalBlockList) {
        try {
            finalBlockList.get(i).setType(materialList[0].get(random[0].get()));
        } catch (IllegalArgumentException exception) {
        }
        amountList[0].set(random[0].get(), (amountList[0].get(random[0].get()) - 1));
        random[0].set(new Random().nextInt(materialList[0].size()));
    }

    private boolean isInteger(String toTest) {
        try {
            Integer.parseInt(toTest);
            if (Integer.parseInt(toTest) <= 0) {
                return false;
            }
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private boolean isBoolean(String toTest) {
        return (toTest.equals("true") || toTest.equals("false"));
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
