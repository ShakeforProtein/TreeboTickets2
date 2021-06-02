package me.shakeforprotein.treebotickets.Commands.Tickets;

import me.shakeforprotein.treeboroots.Discord.WebhookHandler;
import me.shakeforprotein.treebotickets.Objects.TicketObject;
import me.shakeforprotein.treebotickets.TreeboTickets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Ticket implements CommandExecutor, Listener {//TODO

    private TreeboTickets pl;

    public Ticket(TreeboTickets main) {
        this.pl = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (args.length == 0) {
                displayHelp(p);
            }  else if (args[0].equalsIgnoreCase("view")) {
                doView(p, args);
            }  else if (args[0].equalsIgnoreCase("close")) {
                doClose(p, args);
            }  else if (args[0].equalsIgnoreCase("list")) {
                doList(p);
            }  else if (args[0].equalsIgnoreCase("update")) {
                doUpdate(p, args);
            }  else {
                doTicket(p, args);
            }

        } else {
            pl.getLogger().warning("This command can only be executed by a player");
        }

        return true;
    }

    private void doView(Player player, String[] args) {
        if (args.length == 2) {
            if (pl.helpers.isNumeric(args[1])) {
                String[] columns = new String[1];
                String[] values = new String[1];
                columns[0] = "ID";
                values[0] = args[1];
                ResultSet resultSet = pl.roots.mySQL.processPreparedSelectQuery("*", pl.ticketTable, columns, values);
                try {
                    while (resultSet.next()){
                        if(player.getUniqueId().toString().equalsIgnoreCase(resultSet.getString("UUID"))){

                            player.sendMessage((pl.badge + " " + ChatColor.RED + "Ticket System"));
                            player.sendMessage(ChatColor.GOLD + "[X]" + ChatColor.RESET + ChatColor.GREEN + "Ticket ID: " + ChatColor.WHITE + resultSet.getString("ID"));
                            player.sendMessage(ChatColor.GOLD + "[X]" + ChatColor.RESET + ChatColor.GREEN + "Opened by Player: " + ChatColor.WHITE + resultSet.getString("IGNAME"));
                            player.sendMessage(ChatColor.GOLD + "[X]" + ChatColor.RESET + ChatColor.GREEN + "Status: " + ChatColor.WHITE + resultSet.getString("STATUS"));
                            player.sendMessage(ChatColor.GOLD + "[X]" + ChatColor.RESET + ChatColor.GREEN + "Opened at: " + ChatColor.WHITE + resultSet.getString("OPENED"));
                            player.sendMessage(ChatColor.GOLD + "[X]" + ChatColor.RESET + ChatColor.GREEN + "Last Updated: " + ChatColor.WHITE + resultSet.getString("MODIFIED"));
                            player.sendMessage(ChatColor.GOLD + "[X]" + ChatColor.RESET + ChatColor.GREEN + "On World: " + ChatColor.WHITE + resultSet.getString("WORLD"));
                            player.sendMessage(ChatColor.GOLD + "[X]" + ChatColor.RESET + ChatColor.GREEN + "At Coordinates: " + ChatColor.GOLD + resultSet.getString("X") + " " + resultSet.getString("Y") + " " + resultSet.getString("Z"));
                            player.sendMessage("");
                            player.sendMessage(ChatColor.GOLD + "[X]" + ChatColor.RESET + ChatColor.GREEN + "User Description: " + ChatColor.WHITE + resultSet.getString("DESCRIPTION").replace("APOSTR","'").replace("BSlash","\\").replace(" FSlash ","/"));
                            player.sendMessage("");
                            player.sendMessage(ChatColor.GOLD + "[X]" + ChatColor.RESET + ChatColor.BLUE + "Steps taken by user: " + ChatColor.WHITE + resultSet.getString("USERSTEPS").replace("APOSTR","'").replace("BSlash","\\").replace(" FSlash ","/"));
                            player.sendMessage("");
                            player.sendMessage(ChatColor.GOLD + "[X]" + ChatColor.RESET + ChatColor.RED + "Staff comments / actions: " + ChatColor.WHITE + resultSet.getString("STAFFSTEPS").replace("APOSTR","'").replace("BSlash","\\").replace(" FSlash ","/"));

                        }
                        else {
                            player.sendMessage(pl.badge + "That ticket either doesn't exist, or doesn't belong to you.");
                        }
                    }
                } catch(SQLException ex){
                    pl.roots.errorLogger.logError(pl, ex);
                }
            } else {
                player.sendMessage(pl.badge + "Tickets are assigned a numerical identifier which is the required second argument for this command");
                player.sendMessage("eg: /ticket view 567");
            }
        }
        else {
            player.sendMessage(pl.badge + "This command requires exactly 2 arguments.");
            player.sendMessage("eg: /ticket view 567");
        }
    }



    private void doClose(Player player, String[] args){
        if (args.length == 2) {
            if (pl.helpers.isNumeric(args[1])) {
                String[] columns = new String[1];
                String[] values = new String[1];
                columns[0] = "ID";
                values[0] = args[1];
                ResultSet resultSet = pl.roots.mySQL.processPreparedSelectQuery("*", pl.ticketTable, columns, values);
                try {
                    while (resultSet.next()) {
                        if (player.getUniqueId().toString().equalsIgnoreCase(resultSet.getString("UUID"))) {
                            columns = new String[1];
                            values = new String[1];
                            columns[0] = "STATUS";
                            values[0] = "CLOSED";
                            int statement = pl.roots.mySQL.processPreparedUpdate(pl.ticketTable, columns, values, "ID", "=", args[1]);
                            player.sendMessage(pl.badge + "Ticket with ID " + ChatColor.RED + "" + ChatColor.UNDERLINE + args[1] +ChatColor.RESET + " has been closed");
                        } else {
                            player.sendMessage(pl.badge + "That ticket either doesn't exist, or doesn't belong to you.");
                        }
                    }
                } catch (SQLException ex) {
                    pl.roots.errorLogger.logError(pl, ex);
                }
            } else {
                player.sendMessage(pl.badge + "Tickets are assigned a numerical identifier which is the required second argument for this command");
                player.sendMessage("eg: /ticket close 567");
            }
        } else {
            player.sendMessage(pl.badge + "This command requires exactly 2 arguments.");
            player.sendMessage("eg: /ticket close 567");
        }
    }



    private void doUpdate(Player player, String[] args){
        if (args.length > 2) {
            if (pl.helpers.isNumeric(args[1])) {
                String[] columns = new String[1];
                String[] values = new String[1];
                columns[0] = "ID";
                values[0] = args[1];
                ResultSet resultSet = pl.roots.mySQL.processPreparedSelectQuery("*", pl.ticketTable, columns, values);
                try {
                    while (resultSet.next()) {
                        if (player.getUniqueId().toString().equalsIgnoreCase(resultSet.getString("UUID"))) {
                            columns = new String[1];
                            values = new String[1];
                            columns[0] = "USERSTEPS";

                            StringBuilder builder = new StringBuilder();
                            builder.append(resultSet.getString("USERSTEPS"));
                            builder.append(LocalDateTime.now());
                            builder.append(" - ");
                            for(int i = 2; i < args.length; i++){
                                builder.append(args[i]);
                                builder.append(" ");
                            }

                            values[0] = builder.toString();
                            int statement = pl.roots.mySQL.processPreparedUpdate(pl.ticketTable, columns, values, "ID", "=", args[1]);
                            player.sendMessage(pl.badge + "Ticket with ID " + ChatColor.RED + "" + ChatColor.UNDERLINE + args[1] +ChatColor.RESET + " has been updated");

                            WebhookHandler webhook = pl.roots.discord.getExternalHook("TreeboTickets_TICKET_Hook");
                            WebhookHandler.EmbedObject embedObject = new WebhookHandler.EmbedObject();

                            embedObject.setTitle("Treebo Tickets");
                            embedObject.setThumbnail(pl.getConfig().getString("Connections.Discord.WebHooks.OTHER.Avatar"));
                            embedObject.setColor(Color.MAGENTA);
                            embedObject.setDescription("Ticket " + args[1] + " has been updated by player - " + player.getDisplayName() + " - "+ builder.toString());
                            webhook.addEmbed(embedObject);
                            try{webhook.execute();}
                            catch(IOException ex){
                                pl.roots.errorLogger.logError(pl, ex);
                            }
                        } else {
                            player.sendMessage(pl.badge + "That ticket either doesn't exist, or doesn't belong to you.");
                        }
                    }
                } catch (SQLException ex) {
                    pl.roots.errorLogger.logError(pl, ex);
                }
            } else {
                player.sendMessage(pl.badge + "Tickets are assigned a numerical identifier which is the required second argument for this command");
                player.sendMessage("eg: /ticket update 567 The castle on the left, not the right.");
            }
        } else {
            player.sendMessage(pl.badge + "This command requires 2 arguments and a description.");
            player.sendMessage("eg: /ticket update 567 The castle on the left, not the right.");
        }
    }

    private void doTicket(Player player, String[] args) {
        if(args.length > 2){
            StringBuilder builder = new StringBuilder();
            for(String arg : args){
                builder.append(arg);
                builder.append(" ");
            }
            new TicketObject(pl, player, "ticket", "ticket", builder.toString()).submitTicket();
        } else {
            player.sendMessage(pl.badge + "This command requires a longer description");
            player.sendMessage("eg: /ticket Someone has lava griefed my creative build.");
        }
    }

    private void doList(Player player){

        String[] columns = new String[2];
        String[] values = new String[2];
        columns[0] = "IGNAME";
        columns[1] = "STATUS";
        values[0] = player.getName();
        values[1] = "OPEN";
        ResultSet playerTicketListResultSet = pl.roots.mySQL.processPreparedSelectQuery("*", pl.ticketTable, columns, values, "id", "DESC", 35);

        player.sendMessage(pl.badge);
        player.sendMessage(ChatColor.AQUA + "Id  -   Player  -   World   -   Coordinates -   Status");


        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
                try {
                    while (playerTicketListResultSet.next()) {
                        String tPlayer = playerTicketListResultSet.getString("IGNAME");
                        if (tPlayer.equalsIgnoreCase(player.getName())) {
                            int tId = playerTicketListResultSet.getInt("ID");
                            int tX = playerTicketListResultSet.getInt("X");
                            int tY = playerTicketListResultSet.getInt("Y");
                            int tZ = playerTicketListResultSet.getInt("Z");
                            String tStatus = playerTicketListResultSet.getString("STATUS");
                            if (tStatus.equalsIgnoreCase("OPEN")) {
                                tStatus = ChatColor.GREEN + tStatus + ChatColor.WHITE;
                            }
                            if (tStatus.equalsIgnoreCase("closed")) {
                                tStatus = ChatColor.RED + tStatus + ChatColor.WHITE;
                            }
                            String tWorld = playerTicketListResultSet.getString("WORLD");
                            player.sendMessage(ChatColor.GOLD + "[X]" + ChatColor.WHITE + "" + tId + "  -   " + tPlayer + "    -   " + tWorld + "     -   " + tX + " " + tY + " " + tZ + "   -   " + tStatus);
                        }
                    }
                } catch (SQLException ex) {
                    player.sendMessage(pl.err + "Something went wrong");
                    pl.roots.errorLogger.logError(pl, ex);
                }
        });
    }


    private void displayHelp(Player p) {
        p.sendMessage(pl.badge + "The ticket command allows you to submit tickets when something is not working as intended or the available in game help is insufficient.");
    }
}
