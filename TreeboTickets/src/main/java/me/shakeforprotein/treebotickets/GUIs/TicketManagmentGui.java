package me.shakeforprotein.treebotickets.GUIs;

import me.shakeforprotein.treebotickets.TreeboTickets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketManagmentGui {

    private TreeboTickets pl;

    public TicketManagmentGui(TreeboTickets main) {
        this.pl = main;
    }

    public Inventory ticketManagmentMasterGui() {
        Inventory menu = Bukkit.createInventory(null, 9, "[X]" + pl.badge + " Managment Gui");

        ItemStack assignedIcon = new ItemStack(Material.PAPER, 1);
        ItemStack unassignedIcon = new ItemStack(Material.PAPER, 1);
        ItemStack openIcon = new ItemStack(Material.PAPER, 1);
        ItemStack closedIcon = new ItemStack(Material.PAPER, 1);
        //ItemStack builderIcon = new ItemStack(Material.PAPER, 1);

        //Assign M
        ItemMeta assignedMeta = assignedIcon.getItemMeta();
        ItemMeta unassignedMeta = unassignedIcon.getItemMeta();
        ItemMeta openMeta = openIcon.getItemMeta();
        ItemMeta closedMeta = closedIcon.getItemMeta();
        //ItemMeta builderMeta = builderIcon.getItemMeta();

        assignedMeta.setDisplayName("Your assigned tickets");
        unassignedMeta.setDisplayName("Unassigned tickets");
        openMeta.setDisplayName("All open tickets");
        closedMeta.setDisplayName("All closed tickets");
        //builderMeta.setDisplayName("Builder tickets");

        assignedIcon.setItemMeta(assignedMeta);
        unassignedIcon.setItemMeta(unassignedMeta);
        openIcon.setItemMeta(openMeta);
        closedIcon.setItemMeta(closedMeta);
        //builderIcon.setItemMeta(builderMeta);

        menu.addItem(assignedIcon);
        menu.addItem(unassignedIcon);
        menu.addItem(openIcon);
        menu.addItem(closedIcon);
        //menu.addItem(builderIcon);

        return menu;
    }


    public void openAssignedTicketsGui(Player player) {
        String[] columns = new String[2];
        String[] values = new String[2];
        columns[0] = "STAFF";
        columns[1] = "STATUS";
        values[0] = player.getName();
        values[1] = "OPEN";

        ResultSet openAssignedTickesGuiResultSet = pl.roots.mySQL.processPreparedSelectQuery("*", pl.ticketTable, columns, values, "id", "DESC", 54);
        generateInventory(player, openAssignedTickesGuiResultSet, "Assigned Ticket Gui");
    }

    public void openUnassignedTicketGui(Player player) {
        String[] columns = new String[2];
        String[] values = new String[2];
        columns[0] = "STAFF";
        columns[1] = "STATUS";
        values[0] = "UNASSIGNED";
        values[1] = "OPEN";

        ResultSet openUnassignedTickesGuiResultSet = pl.roots.mySQL.processPreparedSelectQuery("*", pl.ticketTable, columns, values, "id", "DESC", 54);
        generateInventory(player, openUnassignedTickesGuiResultSet, "Unassigned Ticket Gui");

    }

    public void openOpenTicketsGui(Player player) {
        String[] columns = new String[1];
        String[] values = new String[1];
        columns[0] = "STATUS";
        values[0] = "OPEN";
        ResultSet openOpenTickesGuiResultSet = pl.roots.mySQL.processPreparedSelectQuery("*", pl.ticketTable, columns, values, "id", "DESC", 54);
        generateInventory(player, openOpenTickesGuiResultSet, "Open Ticket Gui");

    }

    public void openClosedTicketGui(Player player) {
        String[] columns = new String[1];
        String[] values = new String[1];
        columns[0] = "STATUS";
        values[0] = "CLOSED";
        ResultSet openClosedTickesGuiResultSet = pl.roots.mySQL.processPreparedSelectQuery("*", pl.ticketTable, columns, values, "id", "DESC", 54);
        generateInventory(player, openClosedTickesGuiResultSet, "Closed Ticket Gui");

    }



    /*
    public void builderListOpenGui(Player p){
        guiStaffList.guiStaffList(p, "SELECT * FROM `" + pl.getConfig().getString("table") + "` WHERE STATUS='OPEN' AND STAFF='Builders' ORDER BY id DESC", "Ticket List - Builder List");
    }
     */

    private void generateInventory(Player player, ResultSet resultSet, String menuTitle) {
        Inventory menu = Bukkit.createInventory(null, 54, "[X]" + pl.badge + " " + menuTitle);
        try {
            while (resultSet.next()) {
                String tStaff = resultSet.getString("STAFF");
                String tPlayer = resultSet.getString("IGNAME");

                int tId = resultSet.getInt("ID");
                int tX = resultSet.getInt("X");
                int tY = resultSet.getInt("Y");
                int tZ = resultSet.getInt("Z");
                String tWorld = resultSet.getString("WORLD");
                String tStatus = resultSet.getString("STATUS");


                String fromMenu = "Main";
                ItemStack newTicket = new ItemStack(Material.PAPER, 1);
                ItemMeta newTicketMeta = newTicket.getItemMeta();
                newTicketMeta.setDisplayName("Ticket - " + tId);
                List<String> newTicketLore = new ArrayList<String>();
                String coloredStatus;
                if (tStatus.equalsIgnoreCase("OPEN")) {
                    coloredStatus = ChatColor.RED + tStatus + ChatColor.RESET;
                } else if (tStatus.equalsIgnoreCase("CLOSED")) {
                    coloredStatus = ChatColor.RED + tStatus + ChatColor.RESET;
                } else {
                    coloredStatus = tStatus;
                }

                newTicketLore.add("Assigned to - " + tStaff);
                newTicketLore.add("Status - " + coloredStatus);
                newTicketLore.add("Player - " + tPlayer);
                newTicketLore.add("World - " + tWorld);
                newTicketLore.add("Coords - " + tX + "," + tY + "," + tZ);


                newTicketMeta.setLore(newTicketLore);
                newTicket.setItemMeta(newTicketMeta);
                menu.addItem(new ItemStack(newTicket));

            }
        } catch (SQLException ex) {
            pl.getLogger().warning("Exception while generating " + menuTitle);
            pl.roots.errorLogger.logError(pl, ex);
        }

        ItemStack backIcon = new ItemStack(Material.BARRIER, 1);
        ItemMeta backIconMeta = backIcon.getItemMeta();
        backIconMeta.setDisplayName("Previous Menu");
        List<String> backIconLore = new ArrayList<String>();
        backIconLore.add("");
        backIconLore.add("Main Menu");
        backIconMeta.setLore(backIconLore);
        backIcon.setItemMeta(backIconMeta);
        menu.setItem(53, backIcon);

        player.openInventory(menu);
    }


    public void openIndividualTicket(Player player, String ticketId, String previousMenu) {
        String[] colunms = new String[1];
        String[] values = new String[1];
        colunms[0] = "id";
        values[0] = ticketId;

        ResultSet resultSet = pl.roots.mySQL.processPreparedSelectQuery("*", pl.ticketTable, colunms, values);

        Inventory menu = Bukkit.createInventory(null, 27, "[X]" + pl.badge + " Ticket - " + ticketId);
        ItemStack ticketItem = new ItemStack(Material.BOOK);
        ItemStack claimItem = new ItemStack(Material.NAME_TAG);
        ItemStack unclaimItem = new ItemStack(Material.SHEARS);
        ItemStack openItem = new ItemStack(Material.WRITABLE_BOOK);
        ItemStack closeItem = new ItemStack(Material.WRITTEN_BOOK);
        ItemStack teleportItem = new ItemStack(Material.ENDER_PEARL);
        ItemStack deleteItem = new ItemStack(Material.FLINT_AND_STEEL);
        ItemStack backItem = new ItemStack(Material.BARRIER);
        ItemStack replyItem = new ItemStack(Material.INK_SAC);

        ItemMeta ticketItemMeta = ticketItem.getItemMeta();
        ItemMeta claimItemMeta = claimItem.getItemMeta();
        ItemMeta unclaimItemMeta = unclaimItem.getItemMeta();
        ItemMeta openItemMeta = openItem.getItemMeta();
        ItemMeta closeItemMeta = closeItem.getItemMeta();
        ItemMeta teleportItemMeta = teleportItem.getItemMeta();
        ItemMeta deleteItemMeta = deleteItem.getItemMeta();
        ItemMeta backItemMeta = backItem.getItemMeta();
        ItemMeta replyItemMeta = replyItem.getItemMeta();

        List<String> ticketItemLoreList = new ArrayList<>();
        List<String> claimItemLoreList = new ArrayList<>();
        List<String> unclaimItemLoreList = new ArrayList<>();
        List<String> openItemLoreList = new ArrayList<>();
        List<String> closeItemLoreList = new ArrayList<>();
        List<String> teleportItemLoreList = new ArrayList<>();
        List<String> deleteItemLoreList = new ArrayList<>();
        List<String> backItemLoreList = new ArrayList<>();
        List<String> replyItemLoreList = new ArrayList<>();

        ticketItemMeta.setDisplayName("[Ticket Details - " + ticketId + "]");
        claimItemMeta.setDisplayName("[Claim Ticket]");
        unclaimItemMeta.setDisplayName("[Unclaim Ticket]");
        closeItemMeta.setDisplayName("[Close Ticket]");
        openItemMeta.setDisplayName("[Re-Open Ticket]");
        teleportItemMeta.setDisplayName("[Teleport to Ticket]");
        deleteItemMeta.setDisplayName("[Delete Ticket]");
        backItemMeta.setDisplayName("[Back]");
        replyItemMeta.setDisplayName("[Reply]");

        claimItemLoreList.add("Claims a ticket for yourself");
        unclaimItemLoreList.add("Unclaims a ticket claimed by yourself");
        closeItemLoreList.add("Closes this ticket");
        openItemLoreList.add("Reopens this ticket");
        teleportItemLoreList.add("Teleports to this ticket");
        deleteItemLoreList.add("Deletes this ticket");
        backItemLoreList.add("Returns you to the previous menu");
        replyItemLoreList.add("Reply to this ticket");

        backItemLoreList.add(previousMenu);

        try {
            while (resultSet.next()) {
                String ticketStaff = resultSet.getString("STAFF");
                String ticketPlayer = resultSet.getString("IGNAME");
                String ticketType = resultSet.getNString("TYPE");
                String ticketDescription = resultSet.getString("DESCRIPTION");
                String ticketStaffSteps = resultSet.getString("STAFFSTEPS");
                String ticketUserSteps = resultSet.getString("USERSTEPS");
                String ticketStatus = resultSet.getString("STATUS");
                String ticketWorld = resultSet.getString("WORLD");
                String ticketServer = resultSet.getString("server");

                String ticketX = resultSet.getString("X");
                String ticketY = resultSet.getString("Y");
                String ticketZ = resultSet.getString("Z");

                String ticketModified = "Blank Value";

                if(resultSet.getDate("MODIFIED").toString() != null){
                    ticketModified = resultSet.getDate("MODIFIED").toString();
                }

                String coloredStatus;
                if (ticketStatus.equalsIgnoreCase("OPEN")) {
                    coloredStatus = ChatColor.RED + ticketStatus + ChatColor.RESET;
                } else if (ticketStatus.equalsIgnoreCase("CLOSED")) {
                    coloredStatus = ChatColor.RED + ticketStatus + ChatColor.RESET;
                } else {
                    coloredStatus = ticketStatus;
                }

                ticketItemLoreList.add("Assigned to: " + ticketStaff);
                ticketItemLoreList.add("Status: " + coloredStatus);
                ticketItemLoreList.add("Player: " + ticketPlayer);
                ticketItemLoreList.add("World: " + ticketWorld);
                ticketItemLoreList.add("Modified: " + ticketModified);
                ticketItemLoreList.add("Coords:" + ticketX + "," + ticketY + "," + ticketZ);
                ticketItemLoreList.add("");
                ticketItemLoreList.add("Description: " + ticketDescription.replace("APOSTR", "'").replace("BSlash", "\\").replace(" FSlash ", "/"));
                ticketItemLoreList.add("");
                ticketItemLoreList.add("User Steps:" + ticketUserSteps);
                ticketItemLoreList.add("");
                ticketItemLoreList.add("Staff Notes:" + ticketStaffSteps);

                teleportItemLoreList.add(ticketWorld);
                teleportItemLoreList.add(ticketX);
                teleportItemLoreList.add(ticketY);
                teleportItemLoreList.add(ticketZ);
                teleportItemLoreList.add(ticketServer);

                ticketItemMeta.setLore(ticketItemLoreList);
                claimItemMeta.setLore(claimItemLoreList);
                unclaimItemMeta.setLore(unclaimItemLoreList);
                closeItemMeta.setLore(claimItemLoreList);
                openItemMeta.setLore(openItemLoreList);
                teleportItemMeta.setLore(teleportItemLoreList);
                deleteItemMeta.setLore(deleteItemLoreList);
                backItemMeta.setLore(backItemLoreList);
                replyItemMeta.setLore(replyItemLoreList);

                ticketItem.setItemMeta(ticketItemMeta);
                claimItem.setItemMeta(claimItemMeta);
                unclaimItem.setItemMeta(unclaimItemMeta);
                closeItem.setItemMeta(closeItemMeta);
                openItem.setItemMeta(openItemMeta);
                teleportItem.setItemMeta(teleportItemMeta);
                deleteItem.setItemMeta(deleteItemMeta);
                backItem.setItemMeta(backItemMeta);
                replyItem.setItemMeta(replyItemMeta);

                if (ticketStaff.equalsIgnoreCase("Unassigned")) {
                    menu.setItem(8 + 2, claimItem);
                } else if (ticketStaff.equalsIgnoreCase(player.getName())) {
                    menu.setItem(8 + 2, unclaimItem);
                }

                if (ticketStatus.equalsIgnoreCase("CLOSED")) {
                    menu.setItem(8 + 4, openItem);
                } else if (ticketStatus.equalsIgnoreCase("OPEN")) {
                    menu.setItem(8 + 4, closeItem);
                }

                menu.setItem(5, ticketItem);
                menu.setItem(8 + 6, teleportItem);
                menu.setItem(8 + 8, deleteItem);
                menu.setItem(26, backItem);
                menu.setItem(23, replyItem);
            }

        } catch (SQLException ex) {
            pl.roots.errorLogger.logError(pl, ex);
        }

        player.openInventory(menu);
    }
}