package me.shakeforprotein.treebotickets.Commands.RestartCommands;

import me.shakeforprotein.treebotickets.TreeboTickets;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class RestartWhenEmpty implements CommandExecutor {

    private TreeboTickets pl;


    public RestartWhenEmpty(TreeboTickets main) {
        this.pl = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run() {
                if(pl.getServer().getOnlinePlayers().isEmpty()){
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
                }
                else{
                    for(Player player : pl.getServer().getOnlinePlayers()){
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(pl.badge + ChatColor.RED + ChatColor.BOLD + "This server needs to restart for maintenance. Expected downtime is 2 minutes").create());
                    }
                }
            }
        }, 100L, 100L);
        return true;
    }
}