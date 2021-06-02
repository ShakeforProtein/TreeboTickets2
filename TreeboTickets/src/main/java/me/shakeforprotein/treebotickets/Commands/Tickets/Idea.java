package me.shakeforprotein.treebotickets.Commands.Tickets;

import me.shakeforprotein.treebotickets.Objects.TicketObject;
import me.shakeforprotein.treebotickets.TreeboTickets;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Idea implements CommandExecutor {

    private TreeboTickets pl;

    public Idea(TreeboTickets main) {
        this.pl = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (args.length == 0) {
                p.sendMessage(pl.badge + "This command requires a description.");
                p.sendMessage("eg: /idea Hold weekly events on the games server.");
            }   else {
                doIdea(p, args);
            }

        } else {
            pl.getLogger().warning("This command can only be executed by a player");
        }

        return true;
    }
    private void doIdea(Player player, String[] args) {
        if(args.length > 2){
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < args.length; i++){
                builder.append(args[i]);
                builder.append(" ");
            }
            new TicketObject(pl, player, "idea", "idea", builder.toString()).submitTicket();
        } else {
            player.sendMessage(pl.badge + "This command requires a longer description");
            player.sendMessage("eg: /idea Add Mcmmo to survival.");
        }
    }
}
