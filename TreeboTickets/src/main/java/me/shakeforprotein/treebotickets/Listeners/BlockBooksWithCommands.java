package me.shakeforprotein.treebotickets.Listeners;

import me.shakeforprotein.treebotickets.TreeboTickets;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBooksWithCommands implements Listener {

    //TODO shift this into TreeboTweaksSP perhaps?

    private TreeboTickets pl;

    public BlockBooksWithCommands(TreeboTickets main) {
        this.pl = main;
    }

    @EventHandler
    public void playerRunCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        ItemStack mainHandItem = e.getPlayer().getInventory().getItemInMainHand();
        ItemStack offHandItem = e.getPlayer().getInventory().getItemInOffHand();
        if(mainHandItem.getType() == Material.WRITTEN_BOOK || offHandItem.getType() == Material.WRITTEN_BOOK){
            e.setCancelled(true);
            p.sendMessage("For your protection, commands have been disabled while holding a written book.");
        }
    }
}