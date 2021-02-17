package de.teampresidency.party.commands;

import de.teampresidency.party.Main;
import de.teampresidency.party.objekt.Party;
import de.teampresidency.party.utils.PartyManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;

/**
 * User: Timo
 * Date: 03.02.2021
 * Time: 15:22
 */
public class PartyCommand extends Command {
    private static final String help = ""
            + " " + Main.getNextLine()
            + ChatColor.YELLOW + "/party invite <Spieler>" + Main.getNextLine()
            + ChatColor.YELLOW + "/party accept <Spieler>" + Main.getNextLine()
            + ChatColor.YELLOW + "/party deny <Spieler>" + Main.getNextLine()
            + ChatColor.YELLOW + "/party kick <Spieler>" + Main.getNextLine()
            + ChatColor.YELLOW + "/party promote <Spieler>" + Main.getNextLine()
            + ChatColor.YELLOW + "/party leave" + Main.getNextLine()
            + ChatColor.YELLOW + "/party delete" + Main.getNextLine()
            + ChatColor.YELLOW + "/party list" + Main.getNextLine()
            + ChatColor.YELLOW + "/party jump" + Main.getNextLine()
            + ChatColor.YELLOW + "/party msg [Nachricht]";

    private final String usage = ChatColor.RED + "Bitte benutze: " + ChatColor.YELLOW + "/party help";

    public PartyCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (args.length == 0) {
                player.sendMessage(usage);
            } else if (args[0].equalsIgnoreCase("invite")) {
                if(args.length != 2) {
                    player.sendMessage(ChatColor.RED + "/party invite <Spieler>");
                    return;
                }
                if (!Main.getPartyManager().isInAnyParty(player)) {
                    Party party = new Party(player, 10);
                    Main.getPartyManager().getParty().put(player, party);
                    Main.getPartyManager().getParties().add(party);
                }
                if(Main.getPartyManager().isInAnyParty(player)) {
                    if (Main.getPartyManager().isPartyLeader(player)) {
                        Party party = Main.getPartyManager().getParty(player);
                        if (party.getPlayers().size() < party.getMaxPlayers()) {
                            if (isOnline(args[1])) {
                                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                                if (target.equals(player)) {
                                    player.sendMessage(ChatColor.RED + "Du kannst dich nicht selber einladen");
                                    return;
                                }
                                if (!Main.getPartyManager().getParty(player).getPlayers().contains(target)) {
                                    if (!Main.getPartyManager().isInAnyParty(target)) {
                                            party.invitePlayer(target);
                                            player.sendMessage(ChatColor.GREEN + "Die Einladung wurde versendet");
                                    } else
                                        player.sendMessage(ChatColor.RED + "Der Spieler ist bereits in einer Party");
                                } else
                                    player.sendMessage(ChatColor.RED + "Der Spieler ist bereits in deiner Party");
                            } else
                                player.sendMessage(ChatColor.RED + "Der Spieler ist nicht online");
                        } else
                            player.sendMessage(ChatColor.RED + "Deine Party ist bereits voll");
                    } else
                        player.sendMessage(ChatColor.RED + "Du bist keine Party Leitung");
                } else
                    player.sendMessage(ChatColor.RED + "Du bist in keiner Party");
            } else if (args[0].equalsIgnoreCase("accept")) {
                if(args.length != 2) {
                    player.sendMessage(ChatColor.RED + "/party accept <Spieler>");
                    return;
                }
                try{
                if(!Main.getPartyManager().isInAnyParty(player)) {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                    if(isOnline(target.getName())) {
                        if(Main.getPartyManager().getRequests().containsKey(player)) {
                            if (Main.getPartyManager().isInAnyParty(target)) {
                                if (!Main.getPartyManager().getParty(target).isFull()) {
                                    Main.getPartyManager().getParty(target).addPlayer(player);
                                    Main.getPartyManager().getParty(player).sendSystemMessage(Main.getPrefix() + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " ist der Party beigetreten");
                                    Main.getPartyManager().getRequests().remove(player);
                                } else
                                    player.sendMessage(ChatColor.RED + "Die Party ist bereits voll");
                            } else
                                player.sendMessage(ChatColor.RED + "Der Spieler hat keine Party");
                        } else
                            player.sendMessage(ChatColor.RED + "Du hast keine Einladung erhalten");
                    } else
                        player.sendMessage(ChatColor.RED + "Der Spieler ist nicht online");
                } else
                    player.sendMessage(ChatColor.RED + "Du bist bereits in einer Party");
                }catch (Exception exception) {
                    player.sendMessage(ChatColor.RED + "Der Spieler ist nicht verfügbar");
                }
            } else if (args[0].equalsIgnoreCase("deny")) {
                if(args.length != 2) {
                    player.sendMessage(ChatColor.RED + "/party deny <Spieler>");
                    return;
                }
                try {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);

                    if (isOnline(target.getName())) {
                        if (Main.getPartyManager().isInAnyParty(target)) {
                            if (Main.getPartyManager().getRequests().containsKey(player)) {
                                Main.getPartyManager().getRequests().get(player).getOwner().sendMessage(Main.getPrefix() + ChatColor.YELLOW + player.getName() + ChatColor.RED + " hat die Einladung abgelehnt");

                                Main.getPartyManager().getRequests().remove(player);
                                player.sendMessage(Main.getPrefix() + ChatColor.RED + "Du hast die Einladung abgelehnt");
                            } else
                                player.sendMessage(ChatColor.RED + "Du hast keine Einladung erhalten");
                        } else
                            player.sendMessage(ChatColor.RED + "Der Spieler hat keine Party");
                    } else
                        player.sendMessage(ChatColor.RED + "Der Spieler ist nicht online");
                }catch (Exception exception) {
                    player.sendMessage(ChatColor.RED + "Der Spieler ist nicht verfügbar");
                }
            } else if (args[0].equalsIgnoreCase("kick")) {
                if(args.length != 2) {
                    player.sendMessage(ChatColor.RED + "/party kick <Spieler>");
                    return;
                }
                if(Main.getPartyManager().isInAnyParty(player)) {
                    if(Main.getPartyManager().isPartyLeader(player)) {
                        ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[1]);
                        if (!(target == player)) {
                            if (isOnline(target.getName())) {
                                Main.getPartyManager().getParty(player).sendSystemMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() +
                                        ChatColor.RED + " wurde aus der Party geworfen");
                                Main.getPartyManager().getParty(player).removePlayer(target);
                            } else
                                player.sendMessage(ChatColor.RED + "Der Spieler ist nicht online");
                        } else
                        player.sendMessage(ChatColor.RED + "Du darfst dich nicht selber aus der Party werfen");
                    } else
                        player.sendMessage(ChatColor.RED + "Du bist keine Party Leitung");
                } else
                    player.sendMessage(ChatColor.RED + "Du bist in keiner Party");
            } else if (args[0].equalsIgnoreCase("promote")) {
                if(args.length != 2) {
                    player.sendMessage(ChatColor.RED + "/party promote <Spieler>");
                    return;
                }
                if (Main.getPartyManager().isInAnyParty(player)) {
                    if (Main.getPartyManager().isPartyLeader(player)) {
                        Party party = Main.getPartyManager().getParty(player);
                        if (isOnline(args[1])) {
                            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                            if(target.equals(player)) {
                                player.sendMessage(ChatColor.RED + "Du kannst dich nicht selber befördern");
                                return;
                            }

                            ArrayList<ProxiedPlayer> oldParty = new ArrayList<ProxiedPlayer>(Main.getPartyManager().getParty(player).getPlayers());
                            party.sendSystemMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + " wurde zur Party Leitung befördert");
                            Main.getPartyManager().getParty(player).delete();
                            Party promoted = new Party(target, 10);
                            Main.getPartyManager().getParty().put(target, promoted);
                            Main.getPartyManager().getParties().add(promoted);
                            for(int i = 0; i < oldParty.size(); i++) {
                                if(!promoted.isOwner(oldParty.get(i))) {
                                    promoted.addPlayer(oldParty.get(i));
                                }
                            }
                            oldParty.clear();
                        } else
                            player.sendMessage(ChatColor.RED + "Der Spieler ist nicht Online");
                    } else
                        player.sendMessage(ChatColor.RED + "Du bist keine Party Leitung");
                } else
                    player.sendMessage(ChatColor.RED + "Du bist in keiner Party");
            } else if (args[0].equalsIgnoreCase("leave")) {
                if(Main.getPartyManager().isInAnyParty(player)) {
                    Main.getPartyManager().getParty(player).sendSystemMessage(Main.getPrefix() + ChatColor.YELLOW + player.getName() + ChatColor.RED + " hat die Party verlassen");
                    Main.getPartyManager().getParty(player).removePlayer(player);
                } else
                    player.sendMessage(ChatColor.RED + "Du bist in keiner Party");
            } else if (args[0].equalsIgnoreCase("delete")) {
                if (Main.getPartyManager().isInAnyParty(player)) {
                    if (Main.getPartyManager().isPartyLeader(player)) {
                        Party party = Main.getPartyManager().getParty(player);
                        party.sendSystemMessage(Main.getPrefix() + ChatColor.RED + "Die Party wurde aufgelöst");
                        party.delete();
                    } else
                        player.sendMessage(ChatColor.RED + "Du bist keine Party Leitung");
                } else
                    player.sendMessage(ChatColor.RED + "Du bist in keiner Party");
            } else if (args[0].equalsIgnoreCase("msg") || args[0].equalsIgnoreCase("message")) {
                if(args.length < 2) {
                    player.sendMessage(ChatColor.RED + "/party msg [Nachricht]");
                    return;
                }
                if(Main.getPartyManager().isInAnyParty(player)) {
                        StringBuilder message = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            message.append(args[i]).append(" ");
                        }
                        Main.getPartyManager().getParty(player).sendMessage(player, message.toString());
                } else
                    player.sendMessage(ChatColor.RED + "Du bist in keiner Party");
            } else if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("info")) {
                if (Main.getPartyManager().isInAnyParty(player)) {
                    player.sendMessage(PartyManager.getMemberString(player));
                } else
                    player.sendMessage(ChatColor.RED + "Du bist in keiner Party");
            } else if(args[0].equalsIgnoreCase("jump")) {
                if(Main.getPartyManager().isInAnyParty(player)) {
                    if (!Main.getPartyManager().getParty(player).getOwner().getServer().getInfo().equals(player.getServer().getInfo())) {
                        player.connect(Main.getPartyManager().getParty(player).getOwner().getServer().getInfo());
                        player.sendMessage(Main.getPrefix() + ChatColor.GREEN + "Verbunden mit " + ChatColor.RED + Main.getPartyManager().getParty(player).getOwner().getServer().getInfo().getName());
                    } else
                        player.sendMessage(ChatColor.RED + "Du bist bereits auf dem Server");
                } else
                    player.sendMessage(ChatColor.RED + "Du bist in keiner Party");
            } else if(args[0].equalsIgnoreCase("help")) {
                player.sendMessage(help);
            } else
                player.sendMessage(usage);

        } else
            sender.sendMessage("du musst ein Spieler sein");
    }

    public static boolean isOnline(String name) {
        ProxiedPlayer player = BungeeCord.getInstance().getPlayer(name);
        return player != null;
    }

    public static String getHelp() {
        return help;
    }
}
