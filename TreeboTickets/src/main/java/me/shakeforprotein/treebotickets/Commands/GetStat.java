package me.shakeforprotein.treebotickets.Commands;

import me.shakeforprotein.treebotickets.TreeboTickets;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GetStat implements CommandExecutor {

    private TreeboTickets pl;

    public GetStat(TreeboTickets main) {
        this.pl = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("getstat")){
            if(sender instanceof Player) {
                if (args.length == 2) {
                    sender.sendMessage(getStatistic(args[0], args[1]));

                } else if (args.length == 1) {
                    if(args[0].equalsIgnoreCase("bukkit")){
                        sender.sendMessage(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]);
                    }
                    else {
                        sender.sendMessage(pl.err + "No playername detected, getting own stat");
                        sender.sendMessage(getStatistic(args[0], sender.getName()));
                    }} else {
                    sender.sendMessage(pl.err + "Incorrect command usage. Please specify stat and player.");
                    sender.sendMessage("/getstat <stat> <playername>");
                }
            }
            else {
                if (args.length == 3) {
                    getStatistic2(args[0], args[1], args[2]);
                } else if (args.length == 1) {
                    sender.sendMessage(pl.err + "This command requires a player argument and server name / skyblock gamemode name when run from console");
                    sender.sendMessage("/getstat <stat> <playername> <tableName>");
                } else {
                    sender.sendMessage(pl.err + "Incorrect command usage. Please specify stat and player.");
                    sender.sendMessage("/getstat <stat> <playername> <tableName>");
                }
            }
        }
        return true;
    }


    private String getStatistic(String stat, String playerName){

        if(!stat.equalsIgnoreCase("UUID")){
            String server = pl.roots.getConfig().getString("General.ServerDetails.ServerName");
            String[] columns = new String[1];
            String[] values = new String[1];
            columns[0] = "IGNAME";
            values[0] = playerName;
            String returnedStat = "";
            ResultSet response;
            try {
                response = pl.roots.mySQL.processPreparedSelectQuery("*", "stats_" + server, columns, values);
                while(response.next()){
                    returnedStat = response.getString(stat.toUpperCase());
                }
            } catch (SQLException ex) {
                returnedStat = pl.err + "No statistic data matching those search terms";
            }


            return playerName + " - " + stat + " - " + returnedStat;}


        else{
            return pl.err + "You may not look up a player's UUID";
        }
    }


    private String getStatistic2(String stat, String playerName, String server){

        if(!stat.equalsIgnoreCase("UUID")){
            String[] columns = new String[1];
            String[] values = new String[1];

            columns[0] = "IGNAME";
            values[0] = playerName;

            String returnedStat = "";
            ResultSet response;
            try {
                response = pl.roots.mySQL.processPreparedSelectQuery("*", "stats_" + server, columns, values);
                while(response.next()){
                    returnedStat = response.getString(stat.toUpperCase());
                }
            } catch (SQLException ex) {
                System.out.println(pl.err + "Something went wrong");
                System.out.println(pl.err + "Encountered " + ex.toString() + " during genericQuery()");
                pl.roots.errorLogger.logError(pl, ex);
            }


            return playerName + " - " + stat + " - " + returnedStat;}
        else{
            return pl.err + "You may not look up a player's UUID";
        }
    }
}
