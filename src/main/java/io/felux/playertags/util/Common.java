package io.felux.playertags.util;

import io.felux.playertags.PlayerTags;
import io.felux.playertags.api.Tag;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Common {
    private static final PlayerTags PLUGIN = (PlayerTags) JavaPlugin.getProvidingPlugin(PlayerTags.class);

    public static String translate(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static int getTotalTags() {
        int tagSize = 0;

        for (Tag tag : PLUGIN.getTagManager().getTags().values()) {
            if (!tag.isPlaceholder()) tagSize++;
        }

        return tagSize;
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
