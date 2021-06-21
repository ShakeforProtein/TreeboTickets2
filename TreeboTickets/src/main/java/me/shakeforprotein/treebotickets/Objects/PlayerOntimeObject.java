package me.shakeforprotein.treebotickets.Objects;

import me.shakeforprotein.treebotickets.TreeboTickets;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerOntimeObject {


    //TODO: CRITICAL - collect all required Bukkit API data into variables early so we can make the database call Asynchronous without
    // crashing the server or causing ConcurrentModification Exceptions.
    private TreeboTickets pl;
    private Player player;
    private Long connectionEstablished;
    private Long afkTime;
    private Location lastLocation;
    private boolean isAFK;
    private boolean existsInOntimeDatabase;
    private boolean existsInStatsTable;
    private String uuid;
    private String displayName;
    private String ipAddress;

    public PlayerOntimeObject(TreeboTickets main, Player player) {
        this.pl = main;
        this.player = player;
        this.connectionEstablished = System.currentTimeMillis();
        this.lastLocation = player.getLocation();
        this.isAFK = false;
        this.afkTime = 0L;
        this.existsInOntimeDatabase = false;
        this.existsInStatsTable = false;

        this.uuid = player.getUniqueId().toString();
        this.displayName = player.getDisplayName();
        this.ipAddress = player.getAddress().getAddress().toString();

        pl.playerOntimeHash.put(player, this);
    }

    public void processDisconnection() {
        /*
        Scenario: Player is disconnecting from the server.
        Process: Store session details in database.
          Check whether player has previous data stored in the database.
          Player has data - Update the data with details of current session.
          Player does not have data - Insert a new row with details about player and current session.
         */

        //define reused variables
        Long totalOn = 0L;
        Long dbAFK = 0L;
        Long playTime = 0L;

        ResultSet onTimeQueryCountResponse;
        ResultSet serverStatsQueryCountResponse;

        String[] columns = new String[1];
        String[] values = new String[1];
        //END define reused variables


        columns[0] = "UUID";
        values[0] = uuid;

        ResultSet onTimeQueryDataResponse = pl.roots.mySQL.processPreparedSelectQuery("*", pl.ontimeTable, columns, values);

        //Process: Check for existing data
        onTimeQueryCountResponse = pl.roots.mySQL.processPreparedSelectQuery("Count(*) AS TOTAL", pl.ontimeTable, columns, values);
        serverStatsQueryCountResponse = pl.roots.mySQL.processPreparedSelectQuery("Count(*) AS TOTAL", "stats_" + pl.roots.getConfig().getString("General.ServerDetails.ServerName"), columns, values);

        try {

            while (onTimeQueryCountResponse.next()) {
                existsInOntimeDatabase = (onTimeQueryCountResponse.getInt("TOTAL") != 0);
                if(existsInOntimeDatabase) {
                    while(onTimeQueryDataResponse.next()) {
                        totalOn = onTimeQueryDataResponse.getLong("TotalOn");
                        dbAFK = onTimeQueryDataResponse.getLong("AFKTIME");
                        afkTime = dbAFK + afkTime;
                    }
                }
            }

            while (serverStatsQueryCountResponse.next()) {
                existsInOntimeDatabase = (serverStatsQueryCountResponse.getInt("TOTAL") != 0);
            }

            if(existsInOntimeDatabase){
                /*
                Scenario: Player exists in onTime table.
                Process: Update the values that have likely changed.
                 LastLeft - (Last time player disconnected)
                 TotalOn - (The total amount of time in millis that the player has played on the server ever.
                   + This needs to be a combination of the value in the database and the current session.
                 AFKTIME -  The total time the player has spent afk ever.
                   + This needs to be a combination of the value in the database and the current session.
                 CurrentName - The players current DisplayName value
                   + This can simply overwrite the stored value. (We should alter this to track changes to the players name in the OtherNames column)
                 CurrentOn - The time the player last connected.
                 CurrentIP = The value of the plaerys getAdress().getAddress() (This will return their IP or their spoofed IP if they are trying to connect via their own proxy (That is theoretically not possible))
                 */

                columns = new String[6];
                columns[0] = "LastLeft";
                columns[1] = "TotalOn";
                columns[2] = "AFKTIME";

                columns[3] = "CurrentName";
                columns[4] = "CurrentOn";
                columns[5] = "CurrentIP";


                values = new String[6];
                values[0] = System.currentTimeMillis() + "";
                values[1] = (totalOn + (System.currentTimeMillis() - connectionEstablished)) + "";
                values[2] = afkTime + "";

                values[3] = displayName;
                values[4] = connectionEstablished + "";
                values[5] = ipAddress;

                pl.roots.mySQL.processPreparedUpdate(pl.ontimeTable, columns, values, "UUID", "=", uuid);
            } else {
                /*
                Scenario: Player does not exist in the onTime table
                Process: Populate all non automatic fields with data from player object and current expiring session.
                 UUID - The players unique ID
                 CurrentName - The players current DisplayName
                 CurrentOn - The length of the current session
                 TotalOn - This would theoretically be the same as the CurrentOn value
                 AFKTime - The amount of time the player spent AFK. As it's a new entry in the database, we only need to use data from this session.
                 CurrentIP - The getAddress().getAddress() value of the player.
                 OtherIP - This will be blank, but the datanase (May) require a value during insert.
                 FirstJoin - This will be the value stored in connectionEstablished (The time this Object was created)
                 LastLeft - This will be the current time in millis
                 */

                columns = new String[9];
                values = new String [9];

                columns[0] = "UUID";
                columns[1] = "CurrentName";
                columns[2] = "CurrentOn";
                columns[3] = "TotalOn";
                columns[4] = "AFKTIME";
                columns[5] = "CurrentIP";
                columns[6] = "OtherIP";
                columns[7] = "FirstJoin";
                columns[8] = "LastLeft";

                values[0] = uuid;
                values[1] = displayName;
                values[2] = connectionEstablished + "";
                values[3] = (totalOn + (System.currentTimeMillis() - connectionEstablished)) + "";
                values[4] = afkTime + "";
                values[5] = ipAddress;
                values[6] = "";
                values[7] = connectionEstablished + "";
                values[8] = System.currentTimeMillis() + "";

                pl.roots.mySQL.processPreparedInsert(pl.ontimeTable, columns, values);

            }

            if(existsInStatsTable){
                /*
                Scenario: Update the stats database to record playtime value of current session.
                 */
                columns = new String[1];
                values = new String[1];

                columns[0] = "PLAYTIME";
                values[0] = playTime + "";

                pl.roots.mySQL.processPreparedUpdate("stats_" + pl.roots.getConfig().getString("General.ServerDetails.ServerName"), columns, values, "UUID", "=", uuid);
              } else {
                //insert
                columns = new String[3];
                values = new String[3];
                columns[0] = "UUID";
                columns[1] = "IGNAME";
                columns[2] = "PLAYTIME";
                values[0] = uuid;
                values[1] = displayName;
                values[2] = playTime + "";
                pl.roots.mySQL.processPreparedInsert("stats_" +  pl.roots.getConfig().getString("General.ServerDetails.ServerName"), columns, values);
            }
        } catch(SQLException ex){
            pl.roots.errorLogger.logError(pl, ex);
        }
        pl.playerOntimeHash.remove(player);
    }


    //Getters
    public Player getPlayer() {
        return player;
    }

    public Long getAfkTime() {
        return afkTime;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public boolean isAFK() {
        return isAFK;
    }
    //END Getters


    //Setters
    public void setAFK(boolean AFK) {
        isAFK = AFK;
    }

    public void setAfkTime(Long afkTime) {
        this.afkTime = afkTime;
    }
    //END Setters
}
