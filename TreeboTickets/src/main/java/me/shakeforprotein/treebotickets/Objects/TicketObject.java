package me.shakeforprotein.treebotickets.Objects;

import me.shakeforprotein.treeboroots.Discord.WebhookHandler;
import me.shakeforprotein.treebotickets.TreeboTickets;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TicketObject {

    private TreeboTickets pl;

    private Player player;
    private String type;
    private String actualCommand;
    private String description;
    private String userSteps = "";

    public TicketObject(TreeboTickets main, Player player, String type, String actualCommand, String description){
        this.pl = main;
        this.player = player;
        this.type = type;
        this.actualCommand = actualCommand;
        this.description = description;
    }




    public void submitTicket() {
        String[] columns = new String[17];
        String[] values = new String[17];

        columns[0] = "UUID";
        columns[1] = "IGNAME";
        columns[2] = "OPENED";
        columns[3] = "STATUS";
        columns[4] = "STAFF";
        columns[5] = "SERVER";
        columns[6] = "WORLD";
        columns[7] = "X";
        columns[8] = "Y";
        columns[9] = "Z";
        columns[10] = "TYPE";
        columns[11] = "SEVERITY";
        columns[12] = "DESCRIPTION";
        columns[13] = "USERSTEPS";
        columns[14] = "STAFFSTEPS";
        columns[15] = "ATTN";
        columns[16] = "ACTUALCOMMAND";

        values[0] = player.getUniqueId().toString();
        values[1] = player.getDisplayName();
        values[2] = LocalDateTime.now().toString();
        values[3] = "OPEN";
        values[4] = "Unassigned";
        values[5] = pl.roots.getConfig().getString("General.ServerDetails.ServerName");
        values[6] = player.getWorld().getName();
        values[7] = player.getLocation().getBlockX() + "";
        values[8] = player.getLocation().getBlockY() + "";
        values[9] = player.getLocation().getBlockZ() + "";
        values[10] = type;
        values[11] = 0 + "";
        values[12] = description;
        values[13] = userSteps;
        values[14] = "";
        values[15] = "Staff";
        values[16] = actualCommand;

        pl.roots.mySQL.processPreparedInsert(pl.ticketTable, columns, values);

        String[] requestCols = new String[1];
        String[] withValues = new String[1];

        requestCols[0] = "IGNAME";
        withValues[0] = player.getDisplayName();

        ResultSet ticketData = pl.roots.mySQL.processPreparedSelectQuery("*", pl.ticketTable, requestCols, withValues, "id", "DESC", 1);
        try {
            while (ticketData.next()) {
                int ticketID = ticketData.getInt("ID");

                if(pl.roots.discord.getExternalHook("TreeboTickets_" + type.toUpperCase() + "_Hook") != null){
                    WebhookHandler webhook = pl.roots.discord.getExternalHook("TreeboTickets_" + type.toUpperCase() + "_Hook");
                    WebhookHandler.EmbedObject embedObject = new WebhookHandler.EmbedObject();


                    embedObject.setAuthor("Treebo Tickets", "", "");
                    embedObject.setTitle("TreeboTickets - " + type.toUpperCase() + " - " + ticketID);
                    embedObject.setThumbnail(pl.getConfig().getString("Connections.Discord.WebHooks." + type.toUpperCase() + ".Avatar"));
                    StringBuilder details = new StringBuilder();
                    StringBuilder content = new StringBuilder();

                    if(type.toUpperCase().equalsIgnoreCase("ticket") || type.toUpperCase().equalsIgnoreCase("review")){
                        details.append("\\nServer:");
                        details.append(pl.roots.getConfig().getString("General.ServerDetails.ServerName"));
                        details.append("\\nWorld: ");
                        details.append(player.getWorld().getName());
                        details.append("\\nLocation: ");
                        details.append(player.getLocation().getBlockX());
                        details.append(" ");
                        details.append(player.getLocation().getBlockY());
                        details.append(" ");
                        details.append(player.getLocation().getBlockZ());
                        details.append("\\nPlayer: ");
                        details.append(player.getDisplayName());
                        embedObject.addField("Ticket Details", details.toString(), true);

                        content.append(description);
                        embedObject.addField("Player Description", JSONObject.escape(content.toString()), false);


                    } else if (type.toUpperCase().equalsIgnoreCase("idea") || type.toUpperCase().equalsIgnoreCase("other")){

                        content.append(description);
                        embedObject.addField("How do we feel about...", JSONObject.escape(content.toString()), false);
                        embedObject.addField("Suggested by", player.getDisplayName(), false);
                    }

                    if(type.equalsIgnoreCase("ticket")){
                        embedObject.setColor(Color.RED);
                    } else if (type.equalsIgnoreCase("review")){
                        embedObject.setColor(Color.GREEN);
                    } else if (type.equalsIgnoreCase("idea")){
                        embedObject.setColor(Color.YELLOW);
                    } else if (type.equalsIgnoreCase("other")){
                        embedObject.setColor(Color.BLUE);
                    }

                    webhook.addEmbed(embedObject);
                    webhook.execute();
                }
            }
        } catch(SQLException | IOException ex){
            pl.roots.errorLogger.logError(pl, ex);
        }
    }
}
