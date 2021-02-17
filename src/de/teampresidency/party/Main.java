package de.teampresidency.party;

import de.teampresidency.party.commands.PartyCommand;
import de.teampresidency.party.utils.Config;
import de.teampresidency.party.utils.PartyManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * User: Timo
 * Date: 03.02.2021
 * Time: 12:42
 */
public class Main extends Plugin {
    private static Main plugin;
    private static PartyManager partyManager;
    private static final String prefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + ChatColor.BOLD + "Party" + ChatColor.RESET + ChatColor.GRAY + "]" + ChatColor.RESET + " ";
    private static final String nextLine = "\n";
    private static final Config settings = new Config("Party", "Settings");

    @Override
    public void onEnable() {
        BungeeCord.getInstance().getConsole().sendMessage(ChatColor.GREEN + "Name: " + ChatColor.GOLD + "Party");
        BungeeCord.getInstance().getConsole().sendMessage(ChatColor.GREEN + "Authors: " + ChatColor.GOLD + "DerTaktischeHase");
        BungeeCord.getInstance().getConsole().sendMessage(ChatColor.GREEN + "Version: " + ChatColor.GOLD + "1.2-SNAPSHOT");
    }

    @Override
    public void onLoad() {
        BungeeCord.getInstance().getConsole().sendMessage(ChatColor.DARK_GREEN + "Plugin loading...");

        plugin = this;
        partyManager = new PartyManager();

        try {
            commands();
        }catch (Exception exception) {
            BungeeCord.getInstance().getConsole().sendMessage(ChatColor.RED + "ERROR : commands can't load");
            exception.printStackTrace();
        }

        try {
            listener();
        }catch (Exception exception) {
            BungeeCord.getInstance().getConsole().sendMessage(ChatColor.RED + "ERROR : listener can't load");
            exception.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
    }

    private void commands() {
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PartyCommand("party"));
    }

    private void listener() {
        ProxyServer.getInstance().getPluginManager().registerListener(this, partyManager);
    }

    public static Main getPlugin() {
        return plugin;
    }

    public static PartyManager getPartyManager() {
        return partyManager;
    }

    public static String getNextLine() {
        return nextLine;
    }

    public static Config getSettings() {
        return settings;
    }

    public static String getPrefix() {
        return prefix;
    }
}
