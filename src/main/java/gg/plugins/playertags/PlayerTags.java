package gg.plugins.playertags;

import gg.plugins.playertags.api.Tag;
import gg.plugins.playertags.command.util.CommandExecutor;
import gg.plugins.playertags.command.util.CommandManager;
import gg.plugins.playertags.config.Lang;
import gg.plugins.playertags.config.TagManager;
import gg.plugins.playertags.event.JoinListener;
import gg.plugins.playertags.hook.PlaceholderAPIHook;
import gg.plugins.playertags.storage.PlayerData;
import gg.plugins.playertags.storage.StorageHandler;
import gg.plugins.playertags.storage.mongodb.MongoDBHandler;
import gg.plugins.playertags.storage.mysql.MySQLHandler;
import gg.plugins.playertags.storage.sqlite.SQLiteHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PlayerTags extends JavaPlugin {

    private TagManager tagManager;
    private CommandManager commandManager;
    private StorageHandler storageHandler;

    @Override
    public void onEnable() {
        handleReload(false);
        commandManager = new CommandManager(this);
        getCommand("playertags").setExecutor(new CommandExecutor(this));
        if (getCommand("playertags").getPlugin() != this) {
            getLogger().warning("/playertags command is being handled by plugin other than " + getDescription().getName() + ". You must use /playertags:playertags instead.");
        }

        new JoinListener(this);
        hook("PlaceholderAPI");
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

    public void handleReload(boolean reload) {
        if(reload) reloadConfig();
        else saveDefaultConfig();
        setupConfig();
        setupTags();
        setupStorage();
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

            tagManager.addTag(tag, new Tag(tag).withPrefix(prefix).withDescription(desc).withPermission(perm).withSlot(slot).withItem(hasPermName, hasPermLore, true).withItem(hasNoPermName, hasNoPermLore, false));
            log("Tag '" + tag + "' added.");
        });

        log("A total of " + tagManager.getTags().size() + " tag(s) registered.");
    }

    public void setupConfig() {
        Lang.init(this);
    }

    public void setupStorage() {
        String storageType = Objects.requireNonNull(getConfig().getString("settings.storage.type", "SQLITE")).toUpperCase();

        if (Arrays.asList("SQLITE", "MYSQL", "MONGODB").contains(storageType)) {
            getLogger().info("Using '" + storageType + "' for data storage.");
        } else {
            getLogger().info("The storage type '" + storageType + "' is invalid, defaulting to SQLITE.");
            storageType = "SQLITE";
        }

        switch (storageType) {
            case "SQLITE":
                storageHandler = new SQLiteHandler(getDataFolder().getPath());
                break;
            case "MYSQL":
                storageHandler = new MySQLHandler(
                        getConfig().getString("settings.storage.prefix", ""),
                        getConfig().getString("settings.storage.host", "localhost"),
                        getConfig().getInt("settings.storage.port", 3306),
                        getConfig().getString("settings.storage.database", "playertags"),
                        getConfig().getString("settings.storage.username", "root"),
                        getConfig().getString("settings.storage.password", "qwerty123"));
                break;
            case "MONGODB":
                storageHandler = new MongoDBHandler(
                        getConfig().getString("settings.storage.prefix", ""),
                        getConfig().getString("settings.storage.host", "localhost"),
                        getConfig().getInt("settings.storage.port", 27017),
                        getConfig().getString("settings.storage.database", "playertags"),
                        getConfig().getString("settings.storage.username", ""),
                        getConfig().getString("settings.storage.password", "")
                );
                break;
        }
    }

    public StorageHandler getStorageHandler() {
        return storageHandler;
    }

    @Override
    public void onDisable() {
        PlayerData.users.forEach(((uuid, playerData) -> {
            getStorageHandler().pushData(uuid);
        }));
    }

    private void hook(final String plugin) {
        final boolean enabled = Bukkit.getPluginManager().isPluginEnabled(plugin);
        if (enabled) {
            getLogger().info(String.format("Hooked into %s.", plugin));

            if (plugin.equalsIgnoreCase("PlaceholderAPI")) {
                new PlaceholderAPIHook(this).register();
            }
        }
    }

    public static PlayerTags getInstance() {
        return JavaPlugin.getPlugin(PlayerTags.class);
    }
}
