package me.shakeforprotein.treebotickets.Commands.TicketManagment;

import me.shakeforprotein.treebotickets.GUIs.TicketManagmentGui;
import me.shakeforprotein.treebotickets.TreeboTickets;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TicketGui implements CommandExecutor {

    private TreeboTickets pl;
    private TicketManagmentGui ticketManagmentGui;

    public TicketGui(TreeboTickets main){
        this.pl = main;
        this.ticketManagmentGui = new TicketManagmentGui(pl);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player){
            if(sender.hasPermission("treebo.admin")){
                if(args.length == 0 || !args[0].equalsIgnoreCase("reply") || !pl.helpers.isNumeric(args[1])){
                    ((Player) sender).openInventory(ticketManagmentGui.ticketManagmentMasterGui());
                } else {

                    String[] columns = new String[1];
                    String[] values = new String[1];
                    columns[0] = "id";
                    values[0] = args[1];
                    ResultSet queryResult = pl.roots.mySQL.processPreparedSelectQuery("*", pl.ticketTable, columns, values);

                    try {
                        while (queryResult.next()) {
                            String previousStaffResponse = queryResult.getString("STAFFSTEPS");

                            StringBuilder updateString = new StringBuilder();

                            updateString.append(previousStaffResponse);
                            updateString.append(" -NEW RESPONSE- ");

                            int i = 0;
                            for(String arg : args){
                                if(i > 1){
                                    updateString.append(arg);
                                    updateString.append(" ");
                                }
                                i++;
                            }

                            columns[0] = "STAFFSTEPS";
                            values[0] = updateString.toString();
                            pl.roots.mySQL.processPreparedUpdate(pl.ticketTable, columns, values, "id", "=", args[1]);

                        }
                    } catch(SQLException ex){
                        pl.roots.errorLogger.logError(pl, ex);
                    }

                }
            }
        } else {
            pl.getLogger().warning("This command opens a GUI and as such can only be run from in game.");
        }

        return true;
    }
}
