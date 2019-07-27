package gg.plugins.playertags;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class PlayerTags extends JavaPlugin {

    @Override
    public void onEnable() {

    }

    public void setupTags() {
        Objects.requireNonNull(getConfig().getConfigurationSection("tags")).getKeys(false).forEach(tag -> {
            String prefix = getConfig().getString("tags." + tag + ".prefix", "&c[None]");
            String desc = getConfig().getString("tags." + tag + ".description", "You didn't specify one.. oops?");
        });
    }

    @Override
    public void onDisable() {

    }
}
