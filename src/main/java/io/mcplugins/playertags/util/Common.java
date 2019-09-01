package io.mcplugins.playertags.util;

import org.bukkit.ChatColor;

import java.text.DecimalFormat;

public class Common {
    public static String translate(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
