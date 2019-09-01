package io.mcplugins.playertags;

import io.mcplugins.playertags.api.Tag;
import io.mcplugins.playertags.command.util.CommandExecutor;
import io.mcplugins.playertags.command.util.CommandManager;
import io.mcplugins.playertags.config.Lang;
import io.mcplugins.playertags.config.TagManager;
import io.mcplugins.playertags.event.JoinListener;
import io.mcplugins.playertags.hook.PlaceholderAPIHook;
import io.mcplugins.playertags.storage.PlayerData;
import io.mcplugins.playertags.storage.StorageHandler;
import io.mcplugins.playertags.storage.mongodb.MongoDBHandler;
import io.mcplugins.playertags.storage.mysql.MySQLHandler;
import io.mcplugins.playertags.storage.sqlite.SQLiteHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
        handleReload();
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
        if (getConfig().getBoolean("settings.debug", false)) getLogger().info("[DEBUG] " + message);
    }

    public void handleReload() {
        reloadConfig();
        saveDefaultConfig();
        Lang.init(this);
        setupTags();
        setupStorage();
    }

    public void setupTags() {
        tagManager = new TagManager(this);
        Objects.requireNonNull(getConfig().getConfigurationSection("tags")).getKeys(false).forEach(tag -> {
            String prefix = getConfig().getString("tags." + tag + ".prefix", "&c[None]");
            String desc = getConfig().getString("tags." + tag + ".description", "You didn't specify one.. oops?");
            int slot = getConfig().getInt("tags." + tag + ".slot", -1);
            boolean perm = getConfig().getBoolean("tags." + tag + ".permission", false);

            String hasPermName = getConfig().getString("tags." + tag + ".item.has-perm.name", Lang.GUI_TAG_HAS_PERM_NAME.asString());
            List<String> hasPermLore = getConfig().getStringList("tags." + tag + ".item.has-perm.lore").size() == 0 ? Arrays.asList(Lang.GUI_TAG_HAS_PERM_LORE.asString().split("\n")) : getConfig().getStringList("tags." + tag + ".item.has-perm.lore");

            String hasNoPermName = getConfig().getString("tags." + tag + ".item.no-perm.name", Lang.GUI_TAG_HAS_NO_PERM_NAME.asString());
            List<String> hasNoPermLore = getConfig().getStringList("tags." + tag + ".item.no-perm.lore").size() == 0 ? Arrays.asList(Lang.GUI_TAG_HAS_NO_PERM_LORE.asString().split("\n")) : getConfig().getStringList("tags." + tag + ".item.no-perm.lore");

            Material hasPermItem = Material.valueOf(getConfig().getString("tags." + tag + ".item.has-perm.item", getConfig().getString("settings.gui.item.has-perm")));
            Material hasNoPermItem = Material.valueOf(getConfig().getString("tags." + tag + ".item.no-perm.item", getConfig().getString("settings.gui.item.no-perm")));

            tagManager.addTag(
                    new Tag(tag)
                            .withPrefix(prefix)
                            .withDescription(desc)
                            .withPermission(perm)
                            .withSlot(slot)
                            .withItem(hasPermName, hasPermLore, hasPermItem, true)
                            .withTagType(Tag.Type.valueOf(getConfig().getString("tags." + tag + ".type", "PREFIX")))
                            .withPersist(true)
                            .withItem(hasNoPermName, hasNoPermLore, hasNoPermItem, false));
            log("Tag '" + tag + "' added.");
        });
        log("A total of " + tagManager.getTags().size() + " tag(s) registered.");
    }

    public void saveTags() {
        getTagManager().getTags().forEach((tagName, tagObj) -> {
            if (!tagObj.persist()) return;
            getConfig().set("tags." + tagName + ".prefix", tagObj.getPrefix());
            getConfig().set("tags." + tagName + ".description", tagObj.getDescription());
            getConfig().set("tags." + tagName + ".permission", tagObj.requirePermission());
        });
        saveConfig();
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

        saveTags();
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
