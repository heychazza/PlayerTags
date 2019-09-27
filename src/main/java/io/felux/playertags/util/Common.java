package io.felux.playertags.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

public class Common {
    public static String translate(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private static String prefix = "[PT]";

    public static void loading(String object) {
        sendConsoleMessage(prefix + " Loading " + object + "..");
    }

    public static void sendMessage(String object) {
        sendConsoleMessage(prefix + " " + object);
    }

    public static void sendConsoleMessage(String msg) {
        Bukkit.getConsoleSender().sendMessage(translate(msg));
    }

    private static String parsePlaceholders(OfflinePlayer p, String text) {
        return PlaceholderAPI.setPlaceholders(p, text);
    }

    public static String parse(OfflinePlayer p, String text) {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") ? parsePlaceholders(p, text) : text;
    }
}
