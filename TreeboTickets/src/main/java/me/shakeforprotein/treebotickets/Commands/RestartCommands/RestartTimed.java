package me.shakeforprotein.treebotickets.Commands.RestartCommands;

import me.shakeforprotein.treebotickets.TreeboTickets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class RestartTimed implements CommandExecutor {

    private TreeboTickets pl;

    public RestartTimed(TreeboTickets main) {
        pl = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            String command = "restart";
            Integer timer = Integer.parseInt(args[0]) + 100;
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
                public void run() {
                    pushToLobby();
                    pl.saveConfig();
                }
            }, Integer.parseInt(args[0]));

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
                public void run() {
                    sender.sendMessage(pl.badge + "Restarting Now");
                    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                    Bukkit.dispatchCommand(console, command);
                }
            }, timer);
        return true;
    }

    public void pushToLobby() {
        if (pl.getConfig().getString("isLobbyServer").equalsIgnoreCase("false")) {

            for (Player p : getServer().getOnlinePlayers()) {
                pl.getConfig().set("shutdownPlayerList." + p.getName(), p.getName());
                p.sendMessage(pl.badge + "This server is restarting.");
                p.sendMessage(ChatColor.GOLD + "[X]" + ChatColor.RESET + "Moving you temporarily to the Lobby");
                Bukkit.dispatchCommand(p, "lobby");
            }
        }
    }
}
