package com.codeitforyou.tags;

import com.codeitforyou.lib.api.command.CommandManager;
import com.codeitforyou.tags.api.Tag;
import com.codeitforyou.tags.command.*;
import com.codeitforyou.tags.config.Lang;
import com.codeitforyou.tags.config.TagManager;
import com.codeitforyou.tags.hook.PlaceholderAPIHook;
import com.codeitforyou.tags.listener.ChatListener;
import com.codeitforyou.tags.listener.JoinListener;
import com.codeitforyou.tags.maven.LibraryLoader;
import com.codeitforyou.tags.maven.MavenLibrary;
import com.codeitforyou.tags.maven.Repository;
import com.codeitforyou.tags.storage.PlayerData;
import com.codeitforyou.tags.storage.StorageHandler;
import com.codeitforyou.tags.storage.mongodb.MongoDBHandler;
import com.codeitforyou.tags.storage.mysql.MySQLHandler;
import com.codeitforyou.tags.storage.sqlite.SQLiteHandler;
import com.codeitforyou.tags.util.ConsoleFilter;
import com.codeitforyou.tags.util.Metrics;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@MavenLibrary(groupId = "dev.morphia.morphia", artifactId = "core", version = "1.5.2")
@MavenLibrary(groupId = "com.github.j256", artifactId = "ormlite-core", version = "4.43", repo = @Repository(url = "https://jitpack.io"))
@MavenLibrary(groupId = "com.github.j256", artifactId = "ormlite-jdbc", version = "4.43", repo = @Repository(url = "https://jitpack.io"))
@MavenLibrary(groupId = "org.apache.logging.log4j", artifactId = "log4j-core", version = "2.7")
@MavenLibrary(groupId = "org.xerial", artifactId = "sqlite-jdbc", version = "3.7.2")
public class CIFYTags extends JavaPlugin {

    private TagManager tagManager;
    private CommandManager commandManager;
    private StorageHandler storageHandler;

    public static CIFYTags getInstance() {
        return JavaPlugin.getPlugin(CIFYTags.class);
    }

    @Override
    public void onDisable() {
        PlayerData.users.forEach(((uuid, playerData) -> {
            getStorageHandler().pushData(uuid);
        }));

        saveTags();

        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        LibraryLoader.loadAll(CIFYTags.class);

        new JoinListener(this);
        if (getConfig().getBoolean("settings.chat-format.enabled", false)) new ChatListener(this);

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        org.apache.logging.log4j.core.Logger logger;
        logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        logger.addFilter(new ConsoleFilter());
        handleReload();

        registerCommands();

        hook();

        new Metrics(this);
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    private void setupStorage() {
        String storageType = Objects.requireNonNull(getConfig().getString("settings.storage.type", "SQLITE")).toUpperCase();

        if (!Arrays.asList("SQLITE", "MYSQL", "MONGODB").contains(storageType)) {
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
                        getConfig().getString("settings.storage.database", getDescription().getName().toLowerCase()),
                        getConfig().getString("settings.storage.username", "root"),
                        getConfig().getString("settings.storage.password", "qwerty123"));
                break;
            case "MONGODB":
                storageHandler = new MongoDBHandler(
                        getConfig().getString("settings.storage.prefix", ""),
                        getConfig().getString("settings.storage.host", "localhost"),
                        getConfig().getInt("settings.storage.port", 27017),
                        getConfig().getString("settings.storage.database", getDescription().getName().toLowerCase()),
                        getConfig().getString("settings.storage.username", ""),
                        getConfig().getString("settings.storage.password", "")
                );
                break;
        }

        if (getConfig().getBoolean("settings.autosave", true))
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        final PlayerData playerData = PlayerData.get(player.getUniqueId());
                        if (playerData == null) continue;

                        if (playerData.getTag() != null && !getTagManager().getTag(playerData.getTag()).hasPermission(player)) {
                            playerData.setTag(null);
                        }

                        getStorageHandler().pushData(player.getUniqueId());
                    }
                }
            }.runTaskTimerAsynchronously(this, (20L * 60) * 10, (20L * 60) * 10);

    }

    public void handleReload() {
        reloadConfig();
        Lang.init(this);
        setupTags();
        setupStorage();
    }

    private void registerCommands() {
        commandManager = new CommandManager(Arrays.asList(
                ListCommand.class,
                ReloadCommand.class,
                VersionCommand.class,
                HelpCommand.class,
                CreateCommand.class,
                RemoveCommand.class,
                SetPrefixCommand.class,
                SetDescCommand.class,
                SelectCommand.class,
                ResetCommand.class
        ), getDescription().getName().toLowerCase(), this);

        commandManager.setMainCommand(MainCommand.class);

        List<String> aliases = Arrays.asList(getDescription().getName().toLowerCase(), "tags", "tag", "ptags");
        aliases.forEach(commandManager::addAlias);

        CommandManager.Locale locale = commandManager.getLocale();
        locale.setNoPermission(Lang.COMMAND_NO_PERMISSION.asString(Lang.PREFIX.asString()));
        locale.setPlayerOnly(Lang.COMMAND_PLAYER_ONLY.asString(Lang.PREFIX.asString()));
        locale.setUsage(Lang.COMMAND_USAGE.asString(Lang.PREFIX.asString(), "{usage}"));
        locale.setUnknownCommand(Lang.COMMAND_INVALID.asString(Lang.PREFIX.asString()));

        commandManager.register();
    }

    public TagManager getTagManager() {
        return tagManager;
    }

    private void hook() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) new PlaceholderAPIHook(this).register();
    }

    public void saveTags() {
        getTagManager().getTags().forEach((tagName, tagObj) -> {
            if (!tagObj.persist()) return;
            if (tagObj.isPlaceholder()) return;
            getConfig().set("tags." + tagName + ".prefix", tagObj.getPrefix());
            getConfig().set("tags." + tagName + ".description", tagObj.getDescription());
            getConfig().set("tags." + tagName + ".permission", tagObj.requirePermission());
        });
        saveConfig();
    }

    public StorageHandler getStorageHandler() {
        return storageHandler;
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

            int hasPermData = getConfig().getInt("tags." + tag + ".item.has-perm.data", 0);
            int hasNoPermData = getConfig().getInt("tags." + tag + ".item.no-perm.data", 0);

            boolean placeholder = getConfig().getBoolean("tags." + tag + ".placeholder", false);

            List<String> actions = placeholder ? getConfig().getStringList("tags." + tag + ".actions") : Collections.emptyList();

            tagManager.addTag(
                    new Tag(tag)
                            .withPrefix(prefix)
                            .withDescription(desc)
                            .withPermission
                                    (perm)
                            .withSlot(slot)
                            .withItem(hasPermName, hasPermLore, hasPermItem, hasPermData, true)
                            .withTagType(Tag.Type.valueOf(getConfig().getString("tags." + tag + ".type", "PREFIX")))
                            .withPersist(true)
                            .setPlaceholder(placeholder)
                            .withActions(actions)
                            .withItem(hasNoPermName, hasNoPermLore, hasNoPermItem, hasNoPermData, false));
        });
    }
}
