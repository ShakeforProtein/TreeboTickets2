package me.shakeforprotein.treebotickets.Commands;

import me.shakeforprotein.treebotickets.TreeboTickets;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
import java.util.*;

public class StaffReport implements CommandExecutor {


    private TreeboTickets pl;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private List<String> groups = new ArrayList<>();

    public StaffReport(TreeboTickets main) {
        this.pl = main;

        groups.add("t-helper");
        groups.add("helper");
        groups.add("sr-helper");
        groups.add("t-mod");
        groups.add("mod");
        groups.add("srmod");
        groups.add("admin");
        groups.add("dev");
        groups.add("srdev");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0) {
                openStaffListGui(p);
            } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                getStaffOnTimeReport(p);
            }
        }


        return true;
    }


    private void openStaffListGui(Player p) {
        //TODO: Create inventory containing staff and relevant data then send to player
    }

    public void getStaffOnTimeReport(Player messageRecipient) {
        if(messageRecipient.hasPermission("treebo,admin")) {
            ResultSet staffOntimeResult;

            Set<String> staff = new HashSet<>();

            for (String group : groups) {
                staff.addAll(pl.roots.externalApis.getUsersInPexGroup(group));
            }

            for (String uuid : staff) {
                if (uuid != null) {
                    String[] columns = new String[1];
                    columns[0] = "UUID";
                    String[] values = new String[1];
                    values[0] = uuid;
                    staffOntimeResult = pl.roots.mySQL.processPreparedSelectQuery("*", pl.ontimeTable, columns, values);

                    try {
                        while (staffOntimeResult.next()) {
                            String currentName = staffOntimeResult.getString("CurrentName");
                            long lastDisconnect = Long.parseLong(staffOntimeResult.getString("LastLeft"));
                            long timeAFK = Long.parseLong(staffOntimeResult.getString("AFKTIME"));
                            long currentTime = System.currentTimeMillis();


                            String locallyFormattedLastDisconnectDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastDisconnect), TimeZone.getDefault().toZoneId()).toString().split("T")[1] + " - " + dateFormat.format(new Date(lastDisconnect));


                            TextComponent msg = new TextComponent("");
                            String severityColour = pl.helpers.getSeverityColour(lastDisconnect);

                            msg.addExtra(ChatColor.GOLD + "[X]" + ChatColor.AQUA + "" + ChatColor.BOLD + "User: " + severityColour + ChatColor.BOLD + currentName);
                            if (severityColour.equals(ChatColor.YELLOW + "") || severityColour.equals(ChatColor.RED + "")) {
                                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                        ChatColor.AQUA + "" + ChatColor.BOLD + "LastLeft  -  " + ChatColor.RESET + severityColour + locallyFormattedLastDisconnectDateTime + " - (" + lastDisconnect + ")\n" +
                                                ChatColor.AQUA + "" + ChatColor.BOLD + "Offline for - " + ChatColor.RESET + severityColour + pl.helpers.formatTime(currentTime - lastDisconnect) + "\n" +
                                                ChatColor.AQUA + "" + ChatColor.BOLD + "Time AFK - " + ChatColor.RESET + severityColour + pl.helpers.formatTime(timeAFK * 60000)
                                ).create());
                                msg.setHoverEvent(hoverEvent);

                                messageRecipient.spigot().sendMessage(msg);

                            }
                        }
                    } catch (SQLException ex) {
                        pl.roots.errorLogger.logError(pl, ex);
                    }
                }
            }
        }
    }

}
