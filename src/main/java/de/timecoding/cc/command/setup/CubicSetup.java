package de.timecoding.cc.command.setup;

import de.timecoding.cc.CubicCountdown;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class CubicSetup implements Listener {

    private Player player;
    private CubicCountdown plugin;
    private int step = 0;

    private Location pos1 = null;
    private Location pos2 = null;
    private Integer countdown = 10;
    private String name = "";

    public CubicSetup(Player player, CubicCountdown plugin) {
        this.player = player;
        this.plugin = plugin;
        this.triggerNextStep();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Player getPlayer() {
        return player;
    }

    public int getStep() {
        return step;
    }

    private void triggerNextStep() {
        step++;
        if (!plugin.getSetupList().containsKey(player)) {
            plugin.getSetupList().put(player, this);
        }
        switch (step) {
            case 1:
                player.sendMessage("§7Hello and welcome to the CubicCountdown setup! If you want to start and create your first countdown please follow the following steps:");
                player.sendMessage("§cStep 1: §7Please §cright-click §7the §afirst §7edge of the (bedrock-)cube");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2, 2);
                break;
            case 2:
                player.sendMessage("§cStep 2: §7Please §cright-click §7the §esecond §7edge of the (bedrock-)cube");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2, 2);
                break;
            case 3:
                player.sendMessage("§cStep 3: §7Please write into the chat how long the countdown should go (in seconds) (press T to open the chat)");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2, 2);
                break;
            case 4:
                player.sendMessage("§cStep 4: §7Please write the name of the cube into the chat (press T to open the chat)");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2, 2);
                break;
            case 5:
                player.sendMessage("§aSetup completed! §7Next time, when the created cube is filled out with blocks, a countdown will start!");
                plugin.getSetupList().remove(player);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 2);
                plugin.getDataHandler().setLocation("Cube." + name.toUpperCase() + ".Pos1", pos1);
                plugin.getDataHandler().setLocation("Cube." + name.toUpperCase() + ".Pos2", pos2);
                plugin.getConfigHandler().getConfig().set("Countdown", countdown);
                plugin.getDataHandler().getConfig().set("Cube." + name + ".Countdown", countdown);
                plugin.getDataHandler().save();
                plugin.getConfigHandler().save();
                break;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().equals(player)) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                if(event.getHand() == EquipmentSlot.HAND) {
                    event.setCancelled(true);
                    if (step == 1) {
                        player.sendMessage("§aEdge 1 was set! §7You made a mistake? Just type §cCANCEL §7into the chat");
                        this.pos1 = event.getClickedBlock().getLocation();
                        triggerNextStep();
                    } else if (step == 2) {
                        player.sendMessage("§aEdge 2 was set! §7You made a mistake? Just type §cCANCEL §7into the chat");
                        this.pos2 = event.getClickedBlock().getLocation();
                        triggerNextStep();
                    } else {
                        event.setCancelled(false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.getPlayer().equals(player)) {
            event.setCancelled(true);
            String msg = event.getMessage().toUpperCase();
            if (step > 0 && msg.equals("CANCEL")) {
                player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, 2, 2);
                player.sendMessage("§cThe setup was cancelled successfully! To restart the setup type §e/cc setup");
                step = 0;
                plugin.getSetupList().remove(player);
            } else if (step == 3) {
                if (isInteger(msg)) {
                    player.sendMessage("§aThe countdown was set to §e" + msg + " seconds§a! §7You made a mistake? Just type §cCANCEL §7into the chat");
                    this.countdown = Integer.parseInt(msg);
                    triggerNextStep();
                } else {
                    player.sendMessage("§cThe message can only contain numbers!");
                }
            } else if (step == 4) {
                player.sendMessage("§aThe cube-name was set to §e" + msg + "§a!");
                this.name = msg;
                triggerNextStep();
            } else {
                event.setCancelled(false);
            }
        }
    }

    private boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

}
