package me.shakeforprotein.treebotickets.Listeners;

import me.shakeforprotein.treebotickets.Commands.StaffReport;
import me.shakeforprotein.treebotickets.Objects.PlayerOntimeObject;
import me.shakeforprotein.treebotickets.TreeboTickets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerConnectionListener implements Listener {

    private TreeboTickets pl;

    public PlayerConnectionListener(TreeboTickets main) {
        this.pl = main;
    }

    @EventHandler
    public void playerConnectionEstablishedEvent(PlayerJoinEvent e) {
        //Scenario: Player has joined.
        //Process: Create onject to track players onTime.
        PlayerOntimeObject playerOntimeObject = new PlayerOntimeObject(pl, e.getPlayer());
        pl.playerOntimeHash.putIfAbsent(e.getPlayer(), playerOntimeObject);


        //Scenario: Admin join the server
        //Process: Check if hub (or test) server and run the staff activity check, and notify of any outstanding tickets..
        // The best way to do this would be to call it directly from it's class,
        if (e.getPlayer().hasPermission("treebo.admin") && (pl.roots.getConfig().getString("General.ServerDetails.ServerName").equalsIgnoreCase("Hub") || pl.roots.getConfig().getString("General.ServerDetails.ServerName").equalsIgnoreCase("test"))) {
            Bukkit.getScheduler().runTaskLater(pl, () -> {
                adminStats(e.getPlayer());
                pl.staffReport.getStaffOnTimeReport(e.getPlayer());
            }, 100L);
        }
    }

    @EventHandler
    public void playerConnectionTerminateEvent(PlayerQuitEvent e) {
        //Scenario: Player has joined.
        //Process: Send onTime data to database and destroy onTime object.
        pl.playerOntimeHash.get(e.getPlayer()).processDisconnection();

    }


    private void adminStats(Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
            try {
                String[] columns = new String[1];
                String[] values = new String[1];
                columns[0] = "STATUS";
                values[0] = "OPEN";

                ResultSet totalTicketsQueryResultSet = pl.roots.mySQL.processPreparedSelectQuery("Count(*) AS TOTAL", pl.ticketTable, columns, values);

                while (totalTicketsQueryResultSet.next()) {
                    p.sendMessage(ChatColor.GOLD + "[X]" + ChatColor.RESET + "Open Tickets: " + ChatColor.GOLD + totalTicketsQueryResultSet.getInt("TOTAL"));
                }

            } catch (SQLException ex) {
                pl.roots.errorLogger.logError(pl, ex);
            }
        });
    }
}
