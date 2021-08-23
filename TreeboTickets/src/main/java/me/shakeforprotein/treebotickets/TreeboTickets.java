package me.shakeforprotein.treebotickets;

import com.vdurmont.semver4j.Semver;
import me.shakeforprotein.treeboroots.TreeboRoots;
import me.shakeforprotein.treebotickets.Commands.Discord;
import me.shakeforprotein.treebotickets.Commands.GetStat;
import me.shakeforprotein.treebotickets.Commands.OnHere;
import me.shakeforprotein.treebotickets.Commands.RestartCommands.RestartTimed;
import me.shakeforprotein.treebotickets.Commands.RestartCommands.RestartWhenEmpty;
import me.shakeforprotein.treebotickets.Commands.StaffReport;
import me.shakeforprotein.treebotickets.Commands.TicketManagment.TicketGui;
import me.shakeforprotein.treebotickets.Commands.Tickets.Idea;
import me.shakeforprotein.treebotickets.Commands.Tickets.Review;
import me.shakeforprotein.treebotickets.Commands.Tickets.Ticket;
import me.shakeforprotein.treebotickets.GUIs.TicketManagmentGui;
import me.shakeforprotein.treebotickets.HelpBook.HelpBook;
import me.shakeforprotein.treebotickets.Listeners.BlockBooksWithCommands;
import me.shakeforprotein.treebotickets.Listeners.PlayerConnectionListener;
import me.shakeforprotein.treebotickets.Listeners.TicketManagmentGuiListener;
import me.shakeforprotein.treebotickets.Objects.PlayerOntimeObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

public final class TreeboTickets extends JavaPlugin {


    //define api variables
    public TreeboRoots roots;
    public TreeboTickets instance;


    //define data vatiables
    private Semver requiredTreeboRootsVersion = new Semver("0.0.4");
    public String badge = getConfig().getString("General.Messages.Badge") == null ? ChatColor.translateAlternateColorCodes('&', "&3&l[&2TreeboTicketsP&3&l]&r") : ChatColor.translateAlternateColorCodes('&', getConfig().getString("General.Messages.Badge"));
    public String err = badge + ChatColor.RED + "Error: " + ChatColor.RESET;
    public String ticketTable = "tickets";
    public String ontimeTable = "newontime";

    public HashMap<Player, PlayerOntimeObject> playerOntimeHash = new HashMap<>();

    //define File variables
    private File playerDataFolder;


    //define class variables
    public BasicHelperMethods helpers = new BasicHelperMethods();
    public StaffReport staffReport;
    public TicketManagmentGui ticketManagmentGui;


    @Override
    public void onEnable() {
        instance = this;
        if (this.getServer().getPluginManager().getPlugin("TreeboRoots") != null && new Semver(this.getServer().getPluginManager().getPlugin("TreeboRoots").getDescription().getVersion()).isGreaterThanOrEqualTo(requiredTreeboRootsVersion)) {
            backupModifiedConfig();
            saveConfig();
            roots = ((TreeboRoots) this.getServer().getPluginManager().getPlugin("TreeboRoots")).getInstance();
            setPlayerDataFolder();
            ensurePlayerDataFolerExists();
            staffReport = new StaffReport(this);
            ticketManagmentGui = new TicketManagmentGui(this);
            registerCommands();
            registerListeners();

            scheduleRootsIntegrations();

            activateAfkChecker();
        } else {
            getLogger().warning("was unable to find dependency 'TreeboRoots' or it's version was too low to be compatible. Disabling Self");
            this.getServer().getPluginManager().disablePlugin(this);
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @SuppressWarnings("ConstantConditions")
    private void registerCommands(){
        this.getCommand("discord").setExecutor(new Discord(this));
        this.getCommand("getstat").setExecutor(new GetStat(this));
        this.getCommand("onhere").setExecutor(new OnHere(this));
        this.getCommand("restarttimed").setExecutor(new RestartTimed(this));
        this.getCommand("restartwhenempty").setExecutor(new RestartWhenEmpty(this));
        this.getCommand("ticket").setExecutor(new Ticket(this));
        this.getCommand("review").setExecutor(new Review(this));
        this.getCommand("idea").setExecutor(new Idea(this));
        this.getCommand("staffreport").setExecutor(staffReport);
        this.getCommand("ticketgui").setExecutor(new TicketGui(this));
    }

    public void registerListeners(){
        Bukkit.getPluginManager().registerEvents(new BlockBooksWithCommands(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new TicketManagmentGuiListener(this), this);
    }

    public File getPlayerDataFolder() {
        return playerDataFolder;
    }

    private void setPlayerDataFolder() {
        playerDataFolder = new File(roots.getDataFolder() + File.separator + "PlayerData");
    }

    private void registerDefaultDiscordHooks(){
        for(String hookId : getConfig().getConfigurationSection("Connections.Discord.WebHooks").getKeys(false)){
            roots.discord.registerExternalHook("TreeboTickets_" + hookId + "_Hook", getConfig().getString("Connections.Discord.WebHooks." + hookId + ".URL"));
        }
    }

    private void scheduleRootsIntegrations(){
        Bukkit.getScheduler().runTaskLater(this, () -> {
            roots.updateHandler.registerPlugin(instance, "TreeboMC", "TreeboTickets2", Material.PAPER);
            new HelpBook(instance);
            registerDefaultDiscordHooks();
        }, 100L);
    }

    private void ensurePlayerDataFolerExists(){
        if (playerDataFolder.mkdirs()) {
            getLogger().info("Created player data folder in TreeboRoots data tree");
        }
    }

    private void activateAfkChecker() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for(PlayerOntimeObject playerOntimeObject : playerOntimeHash.values()){
                    if(playerOntimeObject.getPlayer().isOnline()){
                        if(playerOntimeObject.isAFK()){
                            if(playerOntimeObject.getLastLocation().getWorld() == playerOntimeObject.getPlayer().getLocation().getWorld() && playerOntimeObject.getLastLocation().distance(playerOntimeObject.getPlayer().getLocation()) > 10){
                                playerOntimeObject.setAFK(false);
                            } else {
                                playerOntimeObject.setAfkTime(playerOntimeObject.getAfkTime() + 1200);
                            }
                        } else {
                            if(playerOntimeObject.getLastLocation().getWorld() == playerOntimeObject.getPlayer().getLocation().getWorld() && playerOntimeObject.getLastLocation().distance(playerOntimeObject.getPlayer().getLocation()) < 10){
                                playerOntimeObject.setAFK(true);
                            }
                        }
                    }
                }
            }
        }, 100L, 1200L);
    }

    private void backupModifiedConfig() {
        if (getConfig().getString("version") == null || (getConfig().getString("version") != null && !getConfig().getString("version").equalsIgnoreCase(this.getDescription().getVersion()))) {
            File oldConfig = new File(getDataFolder(), "config-" + this.getDescription().getVersion() + "-" + LocalDateTime.now().toString().replace(":", "_").replace("T", "__") + ".yml");
            try {
                getConfig().save(oldConfig);
            } catch (IOException ex) {
                roots.errorLogger.logError(this, ex);
            }
            getConfig().options().copyDefaults(true);
            getConfig().set("version", this.getDescription().getVersion());
        } else {
            getConfig().options().copyDefaults(true);
        }
    }
}