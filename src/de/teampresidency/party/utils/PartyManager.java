package de.teampresidency.party.utils;

import de.teampresidency.party.Main;
import de.teampresidency.party.objekt.Party;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: Timo
 * Date: 03.02.2021
 * Time: 13:44
 */
public class PartyManager implements Listener {

    private final ArrayList<Party> parties;
    private final HashMap<ProxiedPlayer, Party> party;
    private final HashMap<ProxiedPlayer, Party> requests;

    public PartyManager() {
        parties = new ArrayList<Party>();
        requests = new HashMap<ProxiedPlayer, Party>();
        party = new HashMap<ProxiedPlayer, Party>();
    }

    public ArrayList<Party> getParties() {
        return parties;
    }

    public HashMap<ProxiedPlayer, Party> getRequests() {
        return requests;
    }

    public HashMap<ProxiedPlayer, Party> getParty() {
        return party;
    }

    public Party getParty(ProxiedPlayer player) {
        try {
            return this.party.get(player);
        }catch (NullPointerException exception) {
            BungeeCord.getInstance().getConsole().sendMessage("§cError get Party(player) null point exception");
        }
        return null;
    }

    public boolean isInAnyParty(ProxiedPlayer player) {
        return party.containsKey(player);
    }

    public boolean isPartyLeader(ProxiedPlayer player) {
        if(isInAnyParty(player)) {
            return party.get(player).isOwner(player);
        } else
        return false;
    }

    public static ChatColor getRankPrefix(ProxiedPlayer player) {
        ChatColor prefix;
        if(player.hasPermission(Rank.ADMINISTRATOR.toString())) {
            prefix = ChatColor.DARK_RED;
        } else if(player.hasPermission(Rank.DEVELOPER.toString())) {
            prefix = ChatColor.AQUA;
        } else if(player.hasPermission(Rank.BUILDER.toString())) {
            prefix = ChatColor.YELLOW;
        } else if(player.hasPermission(Rank.SUPPORTER.toString())) {
            prefix = ChatColor.LIGHT_PURPLE;
        } else if(player.hasPermission(Rank.YOUTUBER.toString())) {
            prefix = ChatColor.DARK_PURPLE;
        } else if(player.hasPermission(Rank.PREMIUM.toString())) {
            prefix = ChatColor.GOLD;
        } else {
            prefix = ChatColor.GRAY;
        }
        return prefix;
    }

    @EventHandler
    public void onSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if(isPartyLeader(player)) {
            for(ProxiedPlayer current : party.get(player).getPlayers()) {
                if(!current.equals(player)) {
                    current.connect(player.getServer().getInfo());
                    current.sendMessage(Main.getPrefix() + ChatColor.GREEN + "Die Party verbindet sich mit  " + ChatColor.RED + player.getServer().getInfo().getName());
                }
            }
            player.sendMessage(Main.getPrefix() + ChatColor.GREEN + "Du hast die Party mit auf deinen Server gezogen");
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        try {
            if (isInAnyParty(player)) {
                party.get(player).removePlayer(player);
                if(BungeeCord.getInstance().getPlayers().contains(player)) {
                    getParty(player).sendSystemMessage(Main.getPrefix() + ChatColor.YELLOW + player.getName() + ChatColor.RED + " hat die Party verlassen");
                }
            }
            requests.remove(player);
        }catch (Exception ignored) {
        }
    }

    public static String getMemberString(ProxiedPlayer player) {
        String member = "";
            for (int i = 0; i < Main.getPartyManager().getParty(player).getPlayers().size(); i++) {
                if (!Main.getPartyManager().getParty(player).getPlayers().get(i).equals(Main.getPartyManager().getParty(player).getOwner())) {
                    member = member + Main.getNextLine() + ChatColor.GRAY + " - " + ChatColor.YELLOW + Main.getPartyManager().getParty(player).getPlayers().get(i).getName();
                }
            }
        ChatColor nummberPrefix;
        if(Main.getPartyManager().getParty(player).getPlayers().size() > 1) {
            nummberPrefix = ChatColor.GREEN;
        } else
            nummberPrefix = ChatColor.RED;
        String list = ChatColor.RESET
                + Main.getPrefix() + ChatColor.GRAY + "Von " + ChatColor.YELLOW + Main.getPartyManager().getParty(player).getOwner().getName() + ChatColor.GRAY + " Mitglieder (" + nummberPrefix + (Main.getPartyManager().getParty(player).getPlayers().size()-1) + ChatColor.GRAY + ")" + member;
        return list;
    }
}
