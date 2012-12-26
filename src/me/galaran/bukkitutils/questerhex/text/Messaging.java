package me.galaran.bukkitutils.questerhex.text;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

/*
utils.no-player: &cNo player with name &3$1
utils.no-player-matching: &cNo player with name matching &3$1
utils.no-world: &cWorld $1 not loaded
utils.cs-not-player: &cThis command can be executed only by a player
utils.no-perm: &cYou don't have permission
*/

public class Messaging {

    private static Logger log;
    private static String chatPrefix;

    public static interface Translation {
        String getString(String key);
    }
    private static Translation translation;

    public static void init(Logger logger, String chatPrefixx, Translation tr) {
        log = logger;
        chatPrefix = chatPrefixx + ChatColor.WHITE;
        translation = tr;
    }

    public static void log(String message, Object... params) {
        log(Level.INFO, message, params);
    }

    public static void log(Level level, String message, Object... params) {
        String parameterized = StringUtils.parameterizeString(message, params);
        log.log(level, ChatColor.stripColor(parameterized));
    }

    public static void send(CommandSender sender, String key, Object... params) {
        String decorated = getDecoratedTranslation(key, params);
        if (!decorated.equals("$suppress")) {
            sender.sendMessage(chatPrefix + decorated);
        }
    }

    public static void sendNoPrefix(CommandSender sender, String key, Object... params) {
        String decorated = getDecoratedTranslation(key, params);
        if (!decorated.equals("$suppress")) {
            sender.sendMessage(decorated);
        }
    }

    public static void sendRaw(CommandSender sender, String raw, Object... params) {
        String decorated = StringUtils.decorateString(raw, params);
        if (!decorated.equals("$suppress")) {
            sender.sendMessage(chatPrefix + decorated);
        }
    }

    /** Does nothing, if player with specified name is not online */
    public static void send(String playerName, String key, Object... params) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null) {
            send(player, key, params);
        }
    }

    /** Does nothing, if player with specified name is not online */
    public static void sendNoPrefix(String playerName, String key, Object... params) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null) {
            sendNoPrefix(player, key, params);
        }
    }

    public static void broadcast(Location loc, double radius, String key, Object... params) {
        broadcast(true, loc, radius, getDecoratedTranslation(key, params));
    }

    public static void broadcastNoPrefix(Location loc, double radius, String key, Object... params) {
        broadcast(false, loc, radius, getDecoratedTranslation(key, params));
    }

    public static void broadcastRaw(Location loc, double radius, String raw, Object... params) {
        broadcast(true, loc, radius, StringUtils.decorateString(raw, params));
    }

    public static void broadcastRawNoPrefix(Location loc, double radius, String raw, Object... params) {
        broadcast(false, loc, radius, StringUtils.decorateString(raw, params));
    }

    private static void broadcast(boolean prefix, Location loc, double radius, String message) {
        if (!message.equals("$suppress")) {
            for (Player curPlayer : Bukkit.getOnlinePlayers()) {
                Location curPlayerLoc = curPlayer.getLocation();
                if (!curPlayerLoc.getWorld().equals(loc.getWorld())) continue;
                if (curPlayerLoc.distance(loc) <= radius) {
                    curPlayer.sendMessage(prefix ? chatPrefix + message : message);
                }
            }
        }
    }

    public static void broadcastServerNoPrefix(String key, Object... params) {
        String decorated = getDecoratedTranslation(key, params);
        if (!decorated.equals("$suppress")) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "say " + decorated);
        }
    }

    public static String getDecoratedTranslation(String key, Object... params) {
        return StringUtils.decorateString(translation.getString(key), params);
    }

    public static String enDis(boolean state) {
        return state ? ChatColor.DARK_GREEN + "enabled" : ChatColor.DARK_RED + "disabled";
    }

    public static Player getPlayer(String name, CommandSender notifySender) {
        Player player = Bukkit.getPlayerExact(name);
        if (player == null) {
            send(notifySender, "utils.no-player", name);
        }
        return player;
    }

    public static Player getPlayerFuzzy(String name, CommandSender notifySender) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            Messaging.send(notifySender, "utils.no-player-matching", name);
        }
        return player;
    }

    public static World getWorld(String worldName, CommandSender notifySender) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Messaging.send(notifySender, "utils.no-world", worldName);
        }
        return world;
    }

    public static boolean isPlayer(CommandSender notifySender) {
        if (!(notifySender instanceof Player)) {
            Messaging.send(notifySender, "utils.cs-not-player");
            return false;
        }
        return true;
    }

    public static boolean hasPermission(Player player, String perm, CommandSender notifySender) {
        boolean has = player.hasPermission(perm);
        if (!has) {
            Messaging.send(notifySender, "utils.no-perm");
        }
        return has;
    }
}
