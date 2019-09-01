package io.mcplugins.playertags.event;

import io.mcplugins.playertags.PlayerTags;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinListener implements Listener {

    private PlayerTags playerTags;
    public JoinListener(PlayerTags playerTags) {
        this.playerTags = playerTags;
        playerTags.getServer().getPluginManager().registerEvents(this, playerTags);
    }

    @EventHandler
    public void onPlayerJoin(final AsyncPlayerPreLoginEvent e) {
        playerTags.getStorageHandler().pullData(e.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                playerTags.getStorageHandler().pushData(e.getPlayer().getUniqueId());
            }
        }.runTaskLaterAsynchronously(playerTags, 20);
    }

}
