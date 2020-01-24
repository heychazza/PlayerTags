package com.codeitforyou.tags.listener;

import com.codeitforyou.lib.api.general.PAPIUtil;
import com.codeitforyou.tags.CIFYTags;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private CIFYTags plugin;

    public ChatListener(CIFYTags plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        String endFormat = PAPIUtil.parse(e.getPlayer(), plugin.getConfig().getString("settings.chat-format.format")).replace("%message%", e.getMessage());
        e.setFormat(endFormat.replace("%", "%%"));
    }
}
