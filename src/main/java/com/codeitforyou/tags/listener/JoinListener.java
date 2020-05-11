package com.codeitforyou.tags.listener;

import com.codeitforyou.tags.CIFYTags;
import com.codeitforyou.tags.storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinListener implements Listener {

    private final CIFYTags plugin;

    public JoinListener(CIFYTags plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(final AsyncPlayerPreLoginEvent e) {
        plugin.getStorageHandler().pullData(e.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                final PlayerData playerData = PlayerData.get(player.getUniqueId());
                if (playerData == null) return;
                if (playerData.getTag() == null) return;
                if (plugin.getTagManager().getTag(playerData.getTag()) == null) return;
                if (!plugin.getTagManager().getTag(playerData.getTag()).hasPermission(player)) playerData.setTag(null);
            }
        }.runTaskLater(plugin, 20);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getStorageHandler().pushData(e.getPlayer().getUniqueId());
                PlayerData.get().remove(e.getPlayer().getUniqueId());
            }
        }.runTaskLaterAsynchronously(plugin, 5);
    }

}
