package me.shakeforprotein.treebotickets.HelpBook;

import me.shakeforprotein.treebotickets.TreeboTickets;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HelpBook {

    private TreeboTickets pl;
    private ArrayList<BaseComponent[]> pages = new ArrayList<>();
    private int counter = 2;

    public HelpBook(TreeboTickets main){
        this.pl = main;
        pages.add(createTitlePage("Treebo Tickets"));
        pages.add(createTableOfContents());
        pages.addAll(createOtherPages());
        pl.roots.helpHandler.registerHelpBook("TreeboTickets", "TreeboMC", pages);

        pl.roots.commandsGui.registerPlugin(pl, "openHelpBook-TreeboTickets", new ItemStack(Material.WRITTEN_BOOK, 1), ChatColor.translateAlternateColorCodes('&', "Treebo Tickets Help"), getLoreList());
    }

    private BaseComponent[] createTitlePage(String title){

        TextComponent titlePage = new TextComponent(title);
        titlePage.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
        titlePage.setUnderlined(true);

        BaseComponent[] titlePageComponent = new ComponentBuilder(titlePage).create();
        return titlePageComponent;
    }

    private BaseComponent[] createTableOfContents(){
        int i = counter;
        TextComponent textComponent = new TextComponent(ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "Contents\n");

        //Add TOC Link lines
        TextComponent newText = new TextComponent(ChatColor.DARK_BLUE + "/Discord\n");
        i = i+1;
        newText.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, i + ""));
        textComponent.addExtra(newText);

        newText = new TextComponent(ChatColor.DARK_BLUE + "/GetStat\n");
        i = i+1;
        newText.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, i + ""));
        textComponent.addExtra(newText);

        newText = new TextComponent(ChatColor.DARK_BLUE + "/Idea\n");
        i = i+1;
        newText.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, i + ""));
        textComponent.addExtra(newText);

        newText = new TextComponent(ChatColor.DARK_BLUE + "/OnHere\n");
        i = i+1;
        newText.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, i + ""));
        textComponent.addExtra(newText);

        newText = new TextComponent(ChatColor.DARK_BLUE + "/Review\n");
        i = i+1;
        newText.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, i + ""));
        textComponent.addExtra(newText);

        newText = new TextComponent(ChatColor.DARK_BLUE + "/Ticket\n");
        i = i+1;
        newText.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, i + ""));
        textComponent.addExtra(newText);



        //create TOC page
        BaseComponent[] tableOfContents= new ComponentBuilder(textComponent).create();
        counter = i;
        return tableOfContents;
    }



    private List<BaseComponent[]> createOtherPages(){

        List<BaseComponent[]> pageList = new ArrayList<>();

        //add command help page.
        pageList.add(new ComponentBuilder("/Discord\n\nThe Discord command is included as a shortcut to DiscordSRV's /discord <link> command. This shortcut is provided as some newer players are confused by DiscordSRV's built in commands.").create());
        pageList.add(new ComponentBuilder("/GetStat\n\nThe GetStat command is used to request player statistic data stored in our database. This database is no longer updated so this information is of limited use.\n\nUsage: /getstat <statistic>\neg /getstat zombiekills").create());
        pageList.add(new ComponentBuilder("/Idea\n\nThe Idea command is used for submitting feature requests and general ideas for improvements to the servers. Idea tickets are sent to a public channel on our discord for players to discuss.").create());
        pageList.add(new ComponentBuilder("/OnHere\n\nThe OnHere command provides a way for players to monitor the amount of time they have spent on the Treebo Games Minecraft Servers. In the future, this may include other games on the Treebo Games Network").create());
        pageList.add(new ComponentBuilder("/Review\n\nThe review command is used to request a visit from our build team, specifically in regards to the 'Plot Ranks' gamemode. These are sent to a private discord channel for the build team.").create());
        pageList.add(new ComponentBuilder("/Ticket\n\nThe Ticket command is used to submit, reply, view and close tickets concerning server issues or to report misbehaving players.").create());

        return pageList;
    }

    private List<String> getLoreList(){
        List<String> loreList = new ArrayList<>();

        loreList.add(ChatColor.translateAlternateColorCodes('&', pl.badge + "-Help"));
        loreList.add(ChatColor.translateAlternateColorCodes('&', ""));
        loreList.add(ChatColor.translateAlternateColorCodes('&', "This book will provide more detailed"));
        loreList.add(ChatColor.translateAlternateColorCodes('&', "help on Treebo's Ticket commands"));

        return loreList;
    }
}
