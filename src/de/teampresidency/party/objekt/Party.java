package de.teampresidency.party.objekt;

import de.teampresidency.party.Main;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.TimeUnit;

/**
 * User: Timo
 * Date: 03.02.2021
 * Time: 13:49
 */
public class Party {

    private ProxiedPlayer owner;
    private final ArrayList<ProxiedPlayer> players;
    private final int maxPlayers;

    public Party(ProxiedPlayer owner, int maxPlayers) {
        this.owner = owner;
        players = new ArrayList<ProxiedPlayer>();
        players.add(owner);
        Main.getPartyManager().getParties().add(this);
        this.maxPlayers = maxPlayers;
    }

    public void setOwner(ProxiedPlayer owner) {
        this.owner = owner;
    }

    public ProxiedPlayer getOwner() {
        return owner;
    }

    public ArrayList<ProxiedPlayer> getPlayers() {
        return players;
    }

    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    public void addPlayer(ProxiedPlayer player) {
        if(getPlayers().size() >= getMaxPlayers()) {
            player.sendMessage(ChatColor.RED + "Die Party ist bereits voll");
            return;
        }
        if(players.contains(player)) {
            player.sendMessage(ChatColor.RED + "Du bist bereits in der Party");
            return;
        }
       players.add(player);
       Main.getPartyManager().getParty().put(player, this);
    }

    public void removePlayer(ProxiedPlayer player) {
        if(!Main.getPartyManager().isInAnyParty(player)) {
            player.sendMessage(ChatColor.RED + "Du bist in keiner Party");
            return;
        }

        if(getPlayers().size() > 1) {
            if(isOwner(player)) {
                Main.getPartyManager().getParty(player).sendSystemMessage(ChatColor.RED + "Die Party wurde aufgelöst");
                delete();
                return;
            }
        } else {
            Main.getPartyManager().getParty(player).sendSystemMessage(ChatColor.RED + "Die Party wurde aufgelöst");
            delete();
            Main.getPartyManager().getParty().remove(player);
            return;
        }
        Main.getPartyManager().getParty().remove(player);
        players.remove(player);
        Main.getPartyManager().getRequests().remove(player);
    }

    public void sendSystemMessage(String message) {
        for(ProxiedPlayer current : players) {
            current.sendMessage(message);
        }
    }

    public void sendMessage(ProxiedPlayer player, String message) {
        for(ProxiedPlayer current : players) {
            current.sendMessage(Main.getPrefix() + ChatColor.YELLOW + player.getName() + " " + ChatColor.WHITE + message);
        }
    }

    public void delete() {
        try {
            for (ProxiedPlayer current : this.getPlayers()) {
                if(!this.isOwner(current)) {
                    this.getPlayers().remove(current);
                    Main.getPartyManager().getParty().remove(current);
                    Main.getPartyManager().getRequests().remove(current);
                }
            }
        }catch (ConcurrentModificationException ignored) {
        }
        this.getPlayers().remove(getOwner());
        Main.getPartyManager().getParty().remove(getOwner());
        Main.getPartyManager().getRequests().remove(getOwner());
        Main.getPartyManager().getParties().remove(Main.getPartyManager().getParty(getOwner()));
        this.setOwner(null);
    }

    public boolean isOwner(ProxiedPlayer player) {
        return owner == player;
    }

    public void invitePlayer(ProxiedPlayer player) {
        player.sendMessage(" ");
        player.sendMessage(ChatColor.GRAY + "Du wurdest von " + ChatColor.YELLOW + owner.getName() + ChatColor.GRAY + " in die Party eingeladen");
        TextComponent accept = new TextComponent("§8«[§aAnnehmen§8]»");
        TextComponent refuse = new TextComponent("§8«[§cAblehnen§8]»");

        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + owner.getName()));
        refuse.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny " + owner.getName()));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aAnnehmen")));
        refuse.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("§cAblehnen")));
        accept.addExtra(refuse);
        player.sendMessage(accept);
        Main.getPartyManager().getRequests().put(player, this);
        startResetInvite(player);
    }

    public static ScheduledTask taskID;

    public void startResetInvite(ProxiedPlayer player) {
        Party party = this;
        taskID = BungeeCord.getInstance().getScheduler().schedule(Main.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (Main.getPartyManager().getRequests().containsKey(player)) {
                    player.sendMessage(ChatColor.RED + "Einladung von " + ChatColor.YELLOW + party.getOwner().getName() + ChatColor.RED + " ist abgelaufen");
                    Main.getPartyManager().getRequests().get(player).getOwner().sendMessage(ChatColor.RED +
                            "Einladung an " + ChatColor.YELLOW + player.getName() + ChatColor.RED + " ist abgelaufen");
                    Main.getPartyManager().getRequests().remove(player);
                }
            }
        }, 30, TimeUnit.SECONDS);
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
