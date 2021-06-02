package me.shakeforprotein.treebotickets.Listeners;

import me.shakeforprotein.treebotickets.TreeboTickets;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class TicketManagmentGuiListener implements Listener {

    private TreeboTickets pl;

    public TicketManagmentGuiListener(TreeboTickets main) {
        this.pl = main;
    }

    @EventHandler
    public void ticketManagmentGui(InventoryClickEvent e) {
        //Scenario: Admin clicks in ANY ticket management gui
        if (e.getView().getTitle().startsWith("[X]" + pl.badge)) {
            e.setCancelled(true);

            //Scenario: Admin clicks in MAIN ticket management gui
            if (e.getView().getTitle().endsWith("Managment Gui")) {
                if (e.getClickedInventory().equals(e.getView().getTopInventory())) {
                    if (e.getClickedInventory().getItem(e.getSlot()) != null && e.getClickedInventory().getItem(e.getSlot()).getType().equals(Material.PAPER)) {
                        if (e.getClickedInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().equalsIgnoreCase("Your assigned tickets")) {
                            pl.ticketManagmentGui.openAssignedTicketsGui((Player) e.getWhoClicked());
                        } else if (e.getClickedInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().equalsIgnoreCase("Unassigned tickets")) {
                            pl.ticketManagmentGui.openUnassignedTicketGui((Player) e.getWhoClicked());
                        } else if (e.getClickedInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().equalsIgnoreCase("All open tickets")) {
                            pl.ticketManagmentGui.openOpenTicketsGui((Player) e.getWhoClicked());
                        } else if (e.getClickedInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().equalsIgnoreCase("All closed tickets")) {
                            pl.ticketManagmentGui.openClosedTicketGui((Player) e.getWhoClicked());
                        }
                    }
                }
            }

            //Scenario: Admin clicks in ASSIGNED ticket management gui
            if (e.getView().getTitle().endsWith("Ticket Gui")) {
                if (e.getClickedInventory().equals(e.getView().getTopInventory())) {
                    if (e.getClickedInventory().getItem(e.getSlot()) != null && e.getClickedInventory().getItem(e.getSlot()).getType().equals(Material.PAPER)) {

                        String ticketId = e.getClickedInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().split(" - ")[1];
                        pl.ticketManagmentGui.openIndividualTicket((Player) e.getWhoClicked(), ticketId, "Assigned");
                    } else if (e.getClickedInventory().getItem(e.getSlot()) != null && e.getClickedInventory().getItem(e.getSlot()).getType().equals(Material.BARRIER)) {
                        e.getWhoClicked().openInventory(pl.ticketManagmentGui.ticketManagmentMasterGui());
                    }
                }
            }

            //Scenario: Admin clicks in UNASSIGNED ticket management gui
            if (e.getView().getTitle().endsWith("Unassigned Ticket Gui")) {
                if (e.getClickedInventory().equals(e.getView().getTopInventory())) {
                    if (e.getClickedInventory().getItem(e.getSlot()) != null && e.getClickedInventory().getItem(e.getSlot()).getType().equals(Material.PAPER)) {

                        String ticketId = e.getClickedInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().split(" - ")[1];
                        pl.ticketManagmentGui.openIndividualTicket((Player) e.getWhoClicked(), ticketId, "Unssigned");
                    } else if (e.getClickedInventory().getItem(e.getSlot()) != null && e.getClickedInventory().getItem(e.getSlot()).getType().equals(Material.BARRIER)) {
                        e.getWhoClicked().openInventory(pl.ticketManagmentGui.ticketManagmentMasterGui());
                    }

                }
            }
        }

        //Scenario: Admin clicks in OPEN ticket management gui
        if (e.getView().getTitle().endsWith("Open Ticket Gui")) {
            if (e.getClickedInventory().equals(e.getView().getTopInventory())) {
                if (e.getClickedInventory().getItem(e.getSlot()) != null && e.getClickedInventory().getItem(e.getSlot()).getType().equals(Material.PAPER)) {

                    String ticketId = e.getClickedInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().split(" - ")[1];
                    pl.ticketManagmentGui.openIndividualTicket((Player) e.getWhoClicked(), ticketId, "Open");
                } else if (e.getClickedInventory().getItem(e.getSlot()) != null && e.getClickedInventory().getItem(e.getSlot()).getType().equals(Material.BARRIER)) {
                    e.getWhoClicked().openInventory(pl.ticketManagmentGui.ticketManagmentMasterGui());
                }

            }
        }

        //Scenario: Admin clicks in CLOSED ticket management gui
        if (e.getView().getTitle().endsWith("Closed Ticket Gui")) {
            if (e.getClickedInventory().equals(e.getView().getTopInventory())) {
                if (e.getClickedInventory().getItem(e.getSlot()) != null && e.getClickedInventory().getItem(e.getSlot()).getType().equals(Material.PAPER)) {

                    String ticketId = e.getClickedInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().split(" - ")[1];
                    pl.ticketManagmentGui.openIndividualTicket((Player) e.getWhoClicked(), ticketId, "Closed");
                } else if (e.getClickedInventory().getItem(e.getSlot()) != null && e.getClickedInventory().getItem(e.getSlot()).getType().equals(Material.BARRIER)) {
                    e.getWhoClicked().openInventory(pl.ticketManagmentGui.ticketManagmentMasterGui());
                }

            }
        }

        //Scenario: Admin opens individual ticket managment guil
        //Process: Determine clicked icon and act accordingly. In most sceneraios we update the database.
        if (e.getView().getTitle().startsWith("[X]" + pl.badge + " Ticket - ")) {
            if (e.getClickedInventory() != null && !e.getClickedInventory().getItem(e.getSlot()).getType().isAir()) {
                String action = e.getClickedInventory().getItem(e.getSlot()).getItemMeta().getDisplayName();
                String ticketId = e.getView().getTitle().split("-")[1].replace("\\]", "").trim();

                String previousMenu = e.getClickedInventory().getItem(26).getItemMeta().getLore().get(1);

                String[] columns = new String[1];
                String[] values = new String[1];

                switch (action) {
                    case "[Claim Ticket]":
                        columns[0] = "STAFF";
                        values[0] = e.getWhoClicked().getName();
                        pl.roots.mySQL.processPreparedUpdate(pl.ticketTable, columns, values, "id", "=", ticketId);
                        pl.ticketManagmentGui.openIndividualTicket((Player) e.getWhoClicked(), ticketId, previousMenu);
                        break;
                    case "[Unclaim Ticket]":
                        columns[0] = "STAFF";
                        values[0] = "UNASSIGNED";
                        pl.roots.mySQL.processPreparedUpdate(pl.ticketTable, columns, values, "id", "=", ticketId);
                        pl.ticketManagmentGui.openIndividualTicket((Player) e.getWhoClicked(), ticketId, previousMenu);
                        e.getWhoClicked().sendMessage(pl.badge + "You have unclaimed ticket " + ticketId);
                        break;
                    case "[Close Ticket]":
                        columns[0] = "STATUS";
                        values[0] = "CLOSED";
                        pl.roots.mySQL.processPreparedUpdate(pl.ticketTable, columns, values, "id", "=", ticketId);
                        pl.ticketManagmentGui.openIndividualTicket((Player) e.getWhoClicked(), ticketId, previousMenu);
                        e.getWhoClicked().sendMessage(pl.badge + "Ticket " + ticketId + " has been closed.");
                        break;
                    case "[Re-Open Ticket]":
                        columns[0] = "STATUS";
                        values[0] = "OPEN";
                        pl.roots.mySQL.processPreparedUpdate(pl.ticketTable, columns, values, "id", "=", ticketId);
                        pl.ticketManagmentGui.openIndividualTicket((Player) e.getWhoClicked(), ticketId, previousMenu);
                        e.getWhoClicked().sendMessage(pl.badge + "Ticket " + ticketId + " has been re-opened.");
                        break;
                    case "[Teleport to Ticket]":
                        ItemMeta meta = e.getClickedInventory().getItem(e.getSlot()).getItemMeta();
                        if(meta.getLore().get(5).equalsIgnoreCase(pl.roots.getConfig().getString("General.ServerDetails.ServerName"))) {
                            Location location = new Location(Bukkit.getWorld(meta.getLore().get(1)), Integer.parseInt(meta.getLore().get(2)), Integer.parseInt(meta.getLore().get(3)), Integer.parseInt(meta.getLore().get(4)));
                            e.getWhoClicked().teleport(location);
                            e.getWhoClicked().sendMessage(pl.badge + "Teleporting you to the location of ticket " + ticketId);
                        } else {
                            Bukkit.dispatchCommand(e.getWhoClicked(), "jsaw " + meta.getLore().get(5) + " " + meta.getLore().get(1));
                        }
                        break;

                    case "[Delete Ticket]":
                        columns = new String[2];
                        values = new String[2];
                        columns[0] = "STATUS";
                        columns[1] = "STAFF";
                        values[0] = "DELETED";
                        values[1] = "DELETED";
                        pl.roots.mySQL.processPreparedUpdate(pl.ticketTable, columns, values, "id", "=", ticketId);
                        pl.ticketManagmentGui.openIndividualTicket((Player) e.getWhoClicked(), ticketId, previousMenu);
                        e.getWhoClicked().sendMessage(pl.badge + "Ticket " + ticketId + " has been deleted.");
                        break;

                    case "[Back]":
                        openPreviousMenu((Player) e.getWhoClicked(), previousMenu);
                        break;

                    case "[Reply]":
                        BaseComponent baseComponent = new TextComponent("[Click Here to reply to ticket " + ticketId + "]");
                        baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "ticketgui reply " + ticketId + " "));
                        baseComponent.setColor(ChatColor.GOLD);
                        e.getWhoClicked().spigot().sendMessage(baseComponent);
                        break;
                    default: {
                        if (action.endsWith(ticketId + "]")) {
                            e.getWhoClicked().sendMessage("Shake may make this output all the ticket details into chat, but this is low priority in the scheme of getting this update released.");
                        }
                    }
                }
            }
        }
    }

    private void openPreviousMenu(Player player, String previousMenu) {
        switch (previousMenu) {
            case "Closed":
                pl.ticketManagmentGui.openClosedTicketGui(player);
                break;
            case "Open":
                pl.ticketManagmentGui.openOpenTicketsGui(player);
                break;
            case "Assigned":
                pl.ticketManagmentGui.openAssignedTicketsGui(player);
                break;
            case "Unassigned":
                pl.ticketManagmentGui.openUnassignedTicketGui(player);
                break;
        }
    }
}
