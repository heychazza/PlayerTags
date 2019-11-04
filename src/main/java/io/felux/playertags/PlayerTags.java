package io.felux.playertags;

import io.felux.playertags.api.Tag;
import io.felux.playertags.command.util.CommandExecutor;
import io.felux.playertags.command.util.CommandManager;
import io.felux.playertags.config.Lang;
import io.felux.playertags.config.TagManager;
import io.felux.playertags.hook.PlaceholderAPIHook;
import io.felux.playertags.listener.ChatListener;
import io.felux.playertags.listener.JoinListener;
import io.felux.playertags.maven.LibraryLoader;
import io.felux.playertags.maven.MavenLibrary;
import io.felux.playertags.maven.Repository;
import io.felux.playertags.storage.PlayerData;
import io.felux.playertags.storage.StorageHandler;
import io.felux.playertags.storage.mongodb.MongoDBHandler;
import io.felux.playertags.storage.mysql.MySQLHandler;
import io.felux.playertags.storage.sqlite.SQLiteHandler;
import io.felux.playertags.util.Common;
import io.felux.playertags.util.ConsoleFilter;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@MavenLibrary(groupId = "dev.morphia.morphia", artifactId = "core", version = "1.5.2")
@MavenLibrary(groupId = "com.github.j256", artifactId = "ormlite-core", version = "4.43", repo = @Repository(url = "https://jitpack.io"))
@MavenLibrary(groupId = "com.github.j256", artifactId = "ormlite-jdbc", version = "4.43", repo = @Repository(url = "https://jitpack.io"))
@MavenLibrary(groupId = "org.apache.logging.log4j", artifactId = "log4j-core", version = "2.7")
@MavenLibrary(groupId = "org.xerial", artifactId = "sqlite-jdbc", version = "3.7.2")
public class PlayerTags extends JavaPlugin {

    private TagManager tagManager;
    private CommandManager commandManager;
    private StorageHandler storageHandler;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        saveDefaultConfig();
        getBanner();

        Common.loading("libraries");
        LibraryLoader.loadAll(PlayerTags.class);

        Common.loading("events");
        new JoinListener(this);

        if(getConfig().getBoolean("settings.chat-format.enabled", false)) {
            new ChatListener(this);
        }

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        org.apache.logging.log4j.core.Logger logger;
        logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        logger.addFilter(new ConsoleFilter());
        handleReload();

        Common.loading("commands");
        registerCommands();

        Common.loading("hooks");
        hook("PlaceholderAPI");

        Common.sendConsoleMessage(" ");
        getLogger().info("Successfully enabled in " + (System.currentTimeMillis() - start) + "ms.");
    }

    private void getBanner() {
        Common.sendConsoleMessage("&b ");
        Common.sendConsoleMessage("&b   ___  ______");
        Common.sendConsoleMessage("&b  / _ \\/_  __/");
        Common.sendConsoleMessage("&b / ___/ / /   " + "  &7" + getDescription().getName() + " v" + getDescription().getVersion());
        Common.sendConsoleMessage("&b/_/    /_/    " + "  &7Running on Bukkit - " + getServer().getName());
        Common.sendConsoleMessage("&b ");
        Common.sendMessage("Created by Felux.io Development.");
        Common.sendConsoleMessage("&b ");
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

    private void setupStorage() {
        String storageType = Objects.requireNonNull(getConfig().getString("settings.storage.type", "SQLITE")).toUpperCase();

        if (!Arrays.asList("SQLITE", "MYSQL", "MONGODB").contains(storageType)) {
            storageType = "SQLITE";
        }

        Common.loading(storageType.toLowerCase() + " storage");

        switch (storageType) {
            case "SQLITE":
                storageHandler = new SQLiteHandler(getDataFolder().getPath());
                break;
            case "MYSQL":
                storageHandler = new MySQLHandler(
                        getConfig().getString("settings.storage.prefix", ""),
                        getConfig().getString("settings.storage.host", "localhost"),
                        getConfig().getInt("settings.storage.port", 3306),
                        getConfig().getString("settings.storage.database", "levellingtools"),
                        getConfig().getString("settings.storage.username", "root"),
                        getConfig().getString("settings.storage.password", "qwerty123"));
                break;
            case "MONGODB":
                storageHandler = new MongoDBHandler(
                        getConfig().getString("settings.storage.prefix", ""),
                        getConfig().getString("settings.storage.host", "localhost"),
                        getConfig().getInt("settings.storage.port", 27017),
                        getConfig().getString("settings.storage.database", "levellingtools"),
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
                        PlayerData playerData = PlayerData.get(player.getUniqueId());

                        if (playerData.getTag() != null && !getTagManager().getTag(playerData.getTag()).needPermission(player)) {
                            playerData.setTag(null);
                        }

                        getStorageHandler().pushData(player.getUniqueId());
                    }
                }
            }.runTaskTimerAsynchronously(this, (20L * 60) * 10, (20L * 60) * 10);

    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    private void registerCommands() {
        commandManager = new CommandManager(this);
        getCommand("playertags").setExecutor(new CommandExecutor(this));
        if (getCommand("playertags").getPlugin() != this) {
            getLogger().warning("/playertags command is being handled by plugin other than " + getDescription().getName() + ". You must use /playertags:playertags instead.");
        }
    }

    public void handleReload() {
        reloadConfig();

        Common.loading("config");
        Lang.init(this);
        setupTags();

        setupStorage();
    }

    private void hook(final String plugin) {
        final boolean enabled = Bukkit.getPluginManager().isPluginEnabled(plugin);
        if (enabled) {
            if (plugin.equalsIgnoreCase("PlaceholderAPI")) {
                new PlaceholderAPIHook(this).register();
                Common.loading("PlaceholderAPI hook");
            }
        }
    }

    public TagManager getTagManager() {
        return tagManager;
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

            tagManager.addTag(
                    new Tag(tag)
                            .withPrefix(prefix)
                            .withDescription(desc)
                            .withPermission(perm)
                            .withSlot(slot)
                            .withItem(hasPermName, hasPermLore, hasPermItem, hasPermData, true)
                            .withTagType(Tag.Type.valueOf(getConfig().getString("tags." + tag + ".type", "PREFIX")))
                            .withPersist(true)
                            .setPlaceholder(getConfig().getBoolean("tags." + tag + ".placeholder", false))
                            .withItem(hasNoPermName, hasNoPermLore, hasNoPermItem, hasNoPermData, false));
        });
    }

    public void saveTags() {
        getTagManager().getTags().forEach((tagName, tagObj) -> {
            if (!tagObj.persist()) return;
            if(tagObj.isPlaceholder()) return;
            getConfig().set("tags." + tagName + ".prefix", tagObj.getPrefix());
            getConfig().set("tags." + tagName + ".description", tagObj.getDescription());
            getConfig().set("tags." + tagName + ".permission", tagObj.requirePermission());
        });
        saveConfig();
    }

    public StorageHandler getStorageHandler() {
        return storageHandler;
    }

    public static PlayerTags getInstance() {
        return JavaPlugin.getPlugin(PlayerTags.class);
    }
}
