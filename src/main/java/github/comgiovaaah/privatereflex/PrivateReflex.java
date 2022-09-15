package github.comgiovaaah.privatereflex;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.*;
import github.comgiovaaah.privatereflex.Files.Config;


public final class PrivateReflex extends JavaPlugin implements Listener {

    private Map<Player, Player> lastMessageSender = new HashMap<>();

    @Override
    public void onEnable() {

        //Setup Logic

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        Config.setup();
        Config.get().addDefault("Taco", "Rice");
        Config.get().options().copyDefaults(true);
        Config.save();



        System.out.println("Hello Developer! The plugin is started");

        //Plugin function

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginCommand("msg").setTabCompleter(this);
        Bukkit.getPluginCommand("msg").setExecutor(this);
        Bukkit.getPluginCommand("reply").setExecutor(this);



    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            final Player senders = (Player) sender;
            if (command.getName().equals("msg")) {
                if (sender.hasPermission("private-message.msg")) {
                    if (args.length >= 2) {
                        String targetName = args[0];
                        Player target = Bukkit.getPlayer(targetName);
                        if (target != sender) {
                            String message = getMessage(args, 1);
                            if (target != null) {
                                target.sendMessage( ChatColor.GREEN + "[From" + sender.getName() + " ] " + message);
                                target.sendMessage( ChatColor.GREEN + "[To" + sender.getName() +  "] " + message);
                                lastMessageSender.put(target, (Player) sender);
                            } else {
                                sender.sendMessage(targetName + ChatColor.YELLOW + " is disconnected. ");
                                }
                            } else {
                            sender.sendMessage( ChatColor.RED + "You cant send message to yourself ");
                        }
                    }
                } else {
                    //no permission
                    sender.sendMessage(" You don't have any permission to perform this command");
                }
            } else if (command.getName().equals("reply")) {

                if (sender.hasPermission("private-message.reply")) {
                    if (args.length >= 1) {
                        Player target = lastMessageSender.get(sender);
                        String message = getMessage(args, 0);
                        if (target == null) {
                            sender.sendMessage("You don't have received any message");
                            return true;
                        }
                        if (!target.isOnline()) {
                            sender.sendMessage(target.getName() + " is disconnected");
                            return true;
                        }

                        target.sendMessage("[From" + sender.getName() + "] " + message);
                        sender.sendMessage("[To" + sender.getName() + "] " + message);
                        lastMessageSender.put(target, senders);
                    }
                } else {
                    sender.sendMessage(" You dont have any permission to performe this command");
                }
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            List<String> players = new ArrayList<>();
            if (args.length == 1) {


            for (Player player : Bukkit.getOnlinePlayers()) {
                if ( player !=  sender && player.getName().startsWith(args[0])) {
                    players.add(player.getName());
                    }
                }
            }
            return players;
        }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        lastMessageSender.remove(e.getPlayer());
    }

    private static String getMessage(String[] args, int index) {
        StringBuilder sb = new StringBuilder();
        for (int i = index; i < args.length; i++) {

            sb.append(args[i]).append(" ");
        }
        sb.setLength(sb.length() - 1);
       return sb.toString();
    }
}