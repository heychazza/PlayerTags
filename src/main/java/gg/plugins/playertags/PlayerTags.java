package gg.plugins.playertags;

import gg.plugins.playertags.api.Tag;
import gg.plugins.playertags.command.util.CommandExecutor;
import gg.plugins.playertags.command.util.CommandManager;
import gg.plugins.playertags.config.TagManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class PlayerTags extends JavaPlugin {

    private TagManager tagManager;
    private CommandManager commandManager;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        commandManager = new CommandManager(this);
        getCommand("playertags").setExecutor(new CommandExecutor(this));
        if (getCommand("playertags").getPlugin() != this) {
            getLogger().warning("/playertags command is being handled by plugin other than " + getDescription().getName() + ". You must use /playertags:playertags instead.");
        }
        
        setupTags();
    }

    public TagManager getTagManager() {
        return tagManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public void log(String message) {
        if(getConfig().getBoolean("debug", false)) getLogger().info("[DEBUG] " + message);
    }

    public void setupTags() {
        tagManager = new TagManager(this);
        Objects.requireNonNull(getConfig().getConfigurationSection("tags")).getKeys(false).forEach(tag -> {
            String prefix = getConfig().getString("tags." + tag + ".prefix", "&c[None]");
            String desc = getConfig().getString("tags." + tag + ".description", "You didn't specify one.. oops?");
            boolean perm = getConfig().getBoolean("tags." + tag + ".description", false);

            tagManager.addTag(tag, new Tag(tag).withPrefix(prefix).withDescription(desc).withPermission(perm));
            log("Tag '" + tag + "' added.");
        });

        log("A total of " + tagManager.getTags().size() + " tag(s) registered.");
    }

    @Override
    public void onDisable() {

    }
}
