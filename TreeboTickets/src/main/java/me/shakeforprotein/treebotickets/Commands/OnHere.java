package me.shakeforprotein.treebotickets.Commands;

import me.shakeforprotein.treebotickets.TreeboTickets;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

public class OnHere implements CommandExecutor {

    private TreeboTickets pl;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public OnHere(TreeboTickets main) {
        pl = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!p.hasPermission("treebo.admin") || args.length == 0) {
                retrieveOntime(p.getName(), sender);
            } else if(p.hasPermission("treebo.admin") && args.length == 1){
                retrieveOntime(args[0], sender);
            } else {
                p.sendMessage(pl.badge + " This command accepts 0 - 1 argument.");
            }
        } else {
            pl.getLogger().info("This command must be run from in game.");
        }
        return true;
    }

    public void retrieveOntime(String queryName, CommandSender queryCommandSender) {
        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {

            String[] columnns = new String[1];
            String[] values = new String[1];
            columnns[0] = "CurrentName";
            values[0] = queryName;

            ResultSet playerOnTimeQueryResponseSet;
            try {
                playerOnTimeQueryResponseSet = pl.roots.mySQL.processPreparedSelectQuery("*", pl.ontimeTable, columnns, values, "ID", "DESC", 1);
                if (playerOnTimeQueryResponseSet.next()) {

                    String uUID = (playerOnTimeQueryResponseSet.getString("UUID"));
                    String currentName = playerOnTimeQueryResponseSet.getString("CurrentName");
                    String currentOnRaw = playerOnTimeQueryResponseSet.getString("CurrentOn");
                    String totalOnRaw = playerOnTimeQueryResponseSet.getString("TotalOn");
                    String currentIP = playerOnTimeQueryResponseSet.getString("CurrentIP");
                    String otherNames = playerOnTimeQueryResponseSet.getString("OtherNames");
                    String firstJoinRaw = playerOnTimeQueryResponseSet.getString("FirstJoin");
                    String lastOffRaw = playerOnTimeQueryResponseSet.getString("LastLeft");
                    String timeAFK = playerOnTimeQueryResponseSet.getString("AFKTIME");
                    long totalOn = Long.parseLong(totalOnRaw);
                    long firstJoin = Long.parseLong((firstJoinRaw));
                    long lastOff = Long.parseLong(lastOffRaw);
                    long currentOn = Long.parseLong(currentOnRaw);
                    long currentTime = System.currentTimeMillis();
                    String cColour = pl.helpers.getSeverityColour(lastOff);


                    String firstJoinDateRawDate = dateFormat.format(new Date(firstJoin));
                    String mostRecentConnectionRawDate = dateFormat.format(new Date(currentOn));
                    String lastDisconnectRawDate = dateFormat.format(new Date(lastOff));
                    String currentTimeRawDate = dateFormat.format(new Date(currentTime));


                    String locallyFormattedFirstJoinDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(firstJoin), TimeZone.getDefault().toZoneId()).toString().split("T")[1] + " - " + firstJoinDateRawDate;
                    String locallyFormattedMostRecentConnectionDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentOn), TimeZone.getDefault().toZoneId()).toString().split("T")[1] + " - " + mostRecentConnectionRawDate;
                    String locallyFormattedLastDisconnectDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastOff), TimeZone.getDefault().toZoneId()).toString().split("T")[1] + " - " + lastDisconnectRawDate;
                    String locallyFormattedCurrentDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTime), TimeZone.getDefault().toZoneId()).toString().split("T")[1] + " - " + currentTimeRawDate;

                    String chatGoldAqua = ChatColor.GOLD + "[X]" + ChatColor.AQUA + "" + ChatColor.BOLD;

                    if (!queryCommandSender.hasPermission("treebo.admin")) {
                        queryCommandSender.sendMessage(pl.badge + "Retrieving data for " + currentName);
                        queryCommandSender.sendMessage(chatGoldAqua + "FirstJoin  -  " + ChatColor.RESET + "" + locallyFormattedFirstJoinDateTime + " - (" + firstJoin + ")");
                        queryCommandSender.sendMessage(chatGoldAqua + "TotalOntime  -  " + ChatColor.RESET + ChatColor.GOLD + pl.helpers.formatTime(totalOn));
                        queryCommandSender.sendMessage(chatGoldAqua + "Time AFK - " + ChatColor.RESET + cColour + pl.helpers.formatTime(Integer.parseInt(timeAFK) * 60000));
                        queryCommandSender.sendMessage(chatGoldAqua + "Adjusted On Time - " + ChatColor.RESET + cColour + pl.helpers.formatTime(totalOn - (Integer.parseInt(timeAFK) * 60000)));

                    } else {
                        queryCommandSender.sendMessage(pl.badge + "Retrieving data for " + currentName);
                        queryCommandSender.sendMessage(chatGoldAqua + "Current Time  -  " + ChatColor.RESET + locallyFormattedCurrentDateTime + " - (" + currentTimeRawDate + ")");
                        queryCommandSender.sendMessage(chatGoldAqua + "UUID  -  " + ChatColor.RESET + uUID);
                        queryCommandSender.sendMessage(chatGoldAqua + "CurrentName  -  " + ChatColor.RESET + currentName);
                        queryCommandSender.sendMessage(chatGoldAqua + "OtherNames  -  " + ChatColor.RESET + otherNames);
                        queryCommandSender.sendMessage(chatGoldAqua + "CurrentIP  -  " + ChatColor.RESET + currentIP);
                        queryCommandSender.sendMessage(chatGoldAqua + "FirstJoin  -  " + ChatColor.RESET + "" + locallyFormattedFirstJoinDateTime + " - (" + firstJoin + ")");
                        queryCommandSender.sendMessage(chatGoldAqua + "Latest Connect  -  " + ChatColor.RESET + ChatColor.GREEN + locallyFormattedMostRecentConnectionDateTime + " - (" + currentOn + ")");
                        queryCommandSender.sendMessage(chatGoldAqua + "Latest Disconnect  -  " + ChatColor.RESET + cColour + locallyFormattedLastDisconnectDateTime + " - (" + lastOff + ")");
                        queryCommandSender.sendMessage(chatGoldAqua + "Offline for - " + ChatColor.RESET + cColour + pl.helpers.formatTime(currentTime - lastOff));
                        queryCommandSender.sendMessage(chatGoldAqua + "Total Time Online  -  " + ChatColor.RESET + ChatColor.GOLD + pl.helpers.formatTime(totalOn));
                        queryCommandSender.sendMessage(chatGoldAqua + "Time AFK - " + ChatColor.RESET + cColour + pl.helpers.formatTime(Integer.parseInt(timeAFK) * 60000));
                        queryCommandSender.sendMessage(chatGoldAqua + "Adjusted Time Online- " + ChatColor.RESET + cColour + pl.helpers.formatTime(totalOn - (Integer.parseInt(timeAFK) * 60000)));

                    }
                } else {
                    queryCommandSender.sendMessage(pl.err + "No Data Matching that player name");
                }
            } catch (SQLException ex) {
                System.out.println(pl.err + "Encountered " + ex.toString() + " during retrieveOntime()");
                pl.roots.errorLogger.logError(pl, ex);
            }
        });
    }
}