package io.felux.playertags.listener;

import io.felux.playertags.PlayerTags;
import io.felux.playertags.storage.PlayerData;
import io.felux.playertags.util.Common;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ChatListener implements Listener {

    private PlayerTags plugin;

    public ChatListener(PlayerTags plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        String endFormat = Common.parse(e.getPlayer(), plugin.getConfig().getString("settings.chat-format.format")).replace("%message%", e.getMessage());
        e.setFormat(endFormat.replace("%", "%%"));
    }

}
