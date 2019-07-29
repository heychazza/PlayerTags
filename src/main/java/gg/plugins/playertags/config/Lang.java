package gg.plugins.playertags.config;

import gg.plugins.playertags.PlayerTags;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

public enum Lang {
    PREFIX("&8[&bTags&8]"),
    COMMAND_NO_PERMISSION("{0} &cYou don't have permission to do that."),
    COMMAND_PLAYER_ONLY("{0} &7The command or args specified can only be used by a player."),
    COMMAND_INVALID("{0} &7That command doesn't exist, use &f/tags help&7."),
    COMMAND_TAG_EXISTS("{0} &7A tag by that id already exists."),
    COMMAND_TAG_UNKNOWN("{0} &7A tag by that id doesn't exist."),
    COMMAND_UNKNOWN("{0} &7That player couldn't be found."),
    COMMAND_INVALID_SYNTAX("{0} &7Invalid syntax, please use &f/tags help&7."),

    LIST_COMMAND_SELF("{0} &7Tags ({1}): {2}"),
    LIST_COMMAND_OTHER("{0} &7{1}'s Tags ({2}): {3}"),
    LIST_SEPARATOR("&f{0}&7, "),

    MAIN_COMMAND("{0} &7Running &f{1} &7version &3{2} &7by &b{3}&7."),
    RELOAD_COMMAND("{0} &7Configuration reloaded with {1} tag(s)."),
    CREATE_COMMAND("{0} &7Created tag &b'{1}'&7."),
    SET_PREFIX_COMMAND("{0} &7The prefix to &b'{1}' &7has been set to {2}&7."),

    HELP_COMMAND_HEADER("", "{0} &7Listing Commands:", "&7"),
    HELP_COMMAND_FORMAT(" &b/tags {1} &8- &7{2}"),
    HELP_COMMAND_FOOTER("", "{0} &7Total of &f{1} &7commands."),

    GUI_TITLE("Player Tags ({1}/{2} Unlocked)"),
    GUI_TAG_HAS_PERM_NAME("{0}"),
    GUI_TAG_HAS_PERM_LORE("{1}", "{2}", "{3}"),
    GUI_TAG_HAS_NO_PERM_NAME("{0}"),
    GUI_TAG_HAS_NO_PERM_LORE("{1}", "{2}", "{3}"),
    TAG_SELECTED("{0} &7Selected the '{1}' tag."),
    TAG_UNSELECTED("{0} &7Unselected the '{1}' tag."),

    NO_TAG_ID("None"),
    NO_TAG_PREFIX(""),
    ;

    private String message;
    private static FileConfiguration c;

    Lang(final String... def) {
        this.message = String.join("\n", def);
    }

    private String getMessage() {
        return this.message;
    }

    public String getPath() {
        return "message." + this.name().toLowerCase().toLowerCase();
    }

    public static String format(String s, final Object... objects) {
        for (int i = 0; i < objects.length; ++i) {
            s = s.replace("{" + i + "}", String.valueOf(objects[i]));
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static boolean init(PlayerTags playerTags) {
        Lang.c = playerTags.getConfig();
        for (final Lang value : values()) {
            if (value.getMessage().split("\n").length == 1) {
                Lang.c.addDefault(value.getPath().toLowerCase(), value.getMessage());
            } else {
                Lang.c.addDefault(value.getPath().toLowerCase(), value.getMessage().split("\n"));
            }
        }
        Lang.c.options().copyDefaults(true);
        playerTags.saveConfig();
        return true;
    }

    public void send(final Player player, final Object... args) {
        final String message = this.asString(args);
        Arrays.stream(message.split("\n")).forEach(player::sendMessage);
    }

    public void sendRaw(final Player player, final Object... args) {
        final String message = this.asString(args);
        Arrays.stream(message.split("\n")).forEach(player::sendRawMessage);
    }

    public void send(final CommandSender sender, final Object... args) {
        if (sender instanceof Player) {
            this.send((Player) sender, args);
        } else {
            Arrays.stream(this.asString(args).split("\n")).forEach(sender::sendMessage);
        }
    }

    public String asString(final Object... objects) {
        Optional<String> opt = Optional.empty();
        if (Lang.c.contains(this.getPath())) {
            if (Lang.c.isList(getPath())) {
                opt = Optional.of(String.join("\n", Lang.c.getStringList(this.getPath())));
            } else if (Lang.c.isString(this.getPath())) {
                opt = Optional.ofNullable(Lang.c.getString(this.getPath()));
            }
        }
        return this.format(opt.orElse(this.message), objects);
    }
}
