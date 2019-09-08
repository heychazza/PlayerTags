package net.chazza.playertags.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Common {
    public static String translate(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendConsoleMessage(String msg) {
        Bukkit.getConsoleSender().sendMessage(translate(msg));
    }

    public static void loading(String object) {
        sendConsoleMessage("[PT] Loading " + object + "..");
    }
}
