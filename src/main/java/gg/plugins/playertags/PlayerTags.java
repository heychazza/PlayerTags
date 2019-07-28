package gg.plugins.playertags;

import gg.plugins.playertags.api.Tag;
import gg.plugins.playertags.command.util.CommandExecutor;
import gg.plugins.playertags.command.util.CommandManager;
import gg.plugins.playertags.config.Config;
import gg.plugins.playertags.config.Lang;
import gg.plugins.playertags.config.TagManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PlayerTags extends JavaPlugin {

    private TagManager tagManager;
    private CommandManager commandManager;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupConfig();
        setupTags();

        commandManager = new CommandManager(this);
        getCommand("playertags").setExecutor(new CommandExecutor(this));
        if (getCommand("playertags").getPlugin() != this) {
            getLogger().warning("/playertags command is being handled by plugin other than " + getDescription().getName() + ". You must use /playertags:playertags instead.");
        }
    }

    public TagManager getTagManager() {
        return tagManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public void log(String message) {
        if (getConfig().getBoolean("debug", false)) getLogger().info("[DEBUG] " + message);
    }

    public void handleReload() {
        reloadConfig();
        setupConfig();
        setupTags();
    }

    public void setupTags() {
        tagManager = new TagManager(this);
        Objects.requireNonNull(getConfig().getConfigurationSection("tags")).getKeys(false).forEach(tag -> {
            String prefix = getConfig().getString("tags." + tag + ".prefix", "&c[None]");
            String desc = getConfig().getString("tags." + tag + ".description", "You didn't specify one.. oops?");
            int slot = getConfig().getInt("tags." + tag + ".slot", -1);
            boolean perm = getConfig().getBoolean("tags." + tag + ".description", false);

            String hasPermName = getConfig().getString("tags." + tag + ".item.has-perm.name", Lang.GUI_TAG_HAS_PERM_NAME.asString());
            List<String> hasPermLore = getConfig().getStringList("tags." + tag + ".item.has-perm.lore").size() == 0 ? Arrays.asList(Lang.GUI_TAG_HAS_PERM_LORE.asString().split("\n")) : getConfig().getStringList("tags." + tag + ".item.has-perm.lore");

            String hasNoPermName = getConfig().getString("tags." + tag + ".item.no-perm.name", Lang.GUI_TAG_HAS_NO_PERM_NAME.asString());
            List<String> hasNoPermLore = getConfig().getStringList("tags." + tag + ".item.no-perm.lore").size() == 0 ? Arrays.asList(Lang.GUI_TAG_HAS_NO_PERM_LORE.asString().split("\n")) : getConfig().getStringList("tags." + tag + ".item.no-perm.lore");

            tagManager.addTag(tag, new Tag(tag).withPrefix(prefix).withDescription(desc).withPermission(perm).withSlot(slot).withItem(hasPermName, hasPermLore,true).withItem(hasNoPermName, hasNoPermLore,false));
            log("Tag '" + tag + "' added.");
        });

        log("A total of " + tagManager.getTags().size() + " tag(s) registered.");
    }

    public void setupConfig() {
        Lang.init(this);
    }

    @Override
    public void onDisable() {

    }
}
