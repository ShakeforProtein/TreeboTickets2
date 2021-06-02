package me.shakeforprotein.treebotickets.Commands;

import me.shakeforprotein.treebotickets.TreeboTickets;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Discord implements CommandExecutor {

    private TreeboTickets pl;

    public Discord(TreeboTickets main) {
        this.pl = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("discord")) {
            StringBuilder argsStringBuilder = new StringBuilder();
            String baseCommand = "discordsrv:discord";
            argsStringBuilder.append(baseCommand);
            if (args.length == 0) {
                argsStringBuilder.append(" link");
            } else if (args.length == 1 && args[0].equalsIgnoreCase("unlink")){
                argsStringBuilder.append(" unlink");
            } else if(args.length != 1 || !args[0].equalsIgnoreCase("default")){
                for (String argString : args) {
                    argsStringBuilder.append(argString);
                    argsStringBuilder.append(" ");
                }
            }
            Bukkit.dispatchCommand(sender, argsStringBuilder.toString());
        }

        return true;
    }
}
