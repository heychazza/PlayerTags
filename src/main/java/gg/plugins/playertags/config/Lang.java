package gg.plugins.playertags.config;

import gg.plugins.playertags.PlayerTags;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

public enum Lang
{
    PREFIX("&8[&bTags&8]"),
    MAIN_COMMAND("{0} &7Running &f{1} &7version &3{2} &7by &b{3}&7."),
    COMMAND_NO_PERMISSION("{0} &cYou don't have permission to do that."),
    COMMAND_PLAYER_ONLY("{0} &7The command or args specified can only be used by a player."),
    COMMAND_INVALID("{0} &7That command doesn't exist, use &f/tags help&7."),
    COMMAND_UNKNOWN("{0} &7That player couldn't be found."),

    RELOAD_COMMAND("{0} &7Configuration reloaded with {1} tag(s)."),

    GUI_TITLE("Player Tags ({1}/{2} Unlocked)"),
    TAG_SELECTED("{0} &7Selected the '{1}' tag.")

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
    
    private String format(String s, final Object... objects) {
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
            this.send((Player)sender, args);
        }
        else {
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
