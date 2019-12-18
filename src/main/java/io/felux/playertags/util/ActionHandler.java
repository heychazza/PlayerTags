package io.felux.playertags.util;

import io.felux.playertags.PlayerTags;
import io.felux.playertags.api.Tag;
import io.felux.playertags.config.Lang;
import io.felux.playertags.storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ActionHandler {
    private static final PlayerTags PLUGIN = (PlayerTags) JavaPlugin.getProvidingPlugin(PlayerTags.class);

    public static void executeActions(Player p, List<String> actions) {
        handle(p, actions);
    }

    private static void handle(Player p, List<String> list) {
        final PlayerData playerData = PlayerData.get(p.getUniqueId());
        if (playerData == null) return;

        final Tag tag = PLUGIN.getTagManager().getTag(playerData.getTag());

        for (String msg : list) {
            boolean singleAction = !msg.contains(" ");
            String actionPrefix = singleAction ? msg : msg.split(" ", 2)[0].toUpperCase();
            String actionData = singleAction ? "" : msg.split(" ", 2)[1];
            actionData = ChatColor.translateAlternateColorCodes('&', actionData.replace("%player%", p.getName()));

            switch (actionPrefix) {
                case "[CONSOLE]":
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), actionData);
                    break;
                case "[PLAYER]":
                    p.performCommand(actionData);
                    break;
                case "[BROADCAST]":
                    Bukkit.broadcastMessage(actionData);
                    break;
                case "[MESSAGE]":
                    p.sendMessage(actionData);
                    break;
                case "[CHAT]":
                    p.chat(actionData);
                    break;
                case "[CLOSE]":
                    p.closeInventory();
                    break;
                case "[RESET]":
                    Lang.TAG_UNSELECTED.send(p, Lang.PREFIX.asString(), tag.getId(), tag.getPrefix());
                    playerData.setTag(null);
                    break;
                default:
                    PLUGIN.getLogger().warning("No action exists for '" + actionPrefix + "'.");
                    break;
            }
        }
    }
}
