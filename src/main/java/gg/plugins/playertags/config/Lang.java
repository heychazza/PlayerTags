package gg.plugins.playertags.config;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

public enum Lang
{
    PREFIX("&8[&6Tools&8]"),
    MAIN_COMMAND("{0} &7Running &f{1} &7version &6{2} &7by &e{3}&7."),
    COMMAND_NO_PERMISSION("{0} &cYou don't have permission to do that."),
    COMMAND_PLAYER_ONLY("{0} &cThe command or args specified can only be used by a player."),
    COMMAND_INVALID("{0} &cThat command doesn't exist, use &f/tags help&c."),
    COMMAND_UNKNOWN("{0} &cThat player couldn't be found."),

    ;
    
    private String message;
    private static Config config;
    private static FileConfiguration c;
    
    Lang(final String... def) {
        this.message = String.join("\n", (CharSequence[])def);
    }
    
    private String getMessage() {
        return this.message;
    }
    
    public String getPath() {
        return this.name();
    }
    
    private String format(String s, final Object... objects) {
        for (int i = 0; i < objects.length; ++i) {
            s = s.replace("{" + i + "}", String.valueOf(objects[i]));
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static boolean init(final Config wrapper) {
        wrapper.loadConfig();
        if (wrapper.getConfig() == null) {
            return false;
        }
        Lang.config = wrapper;
        Lang.c = wrapper.getConfig();
        for (final Lang value : values()) {
            if (value.getMessage().split("\n").length == 1) {
                Lang.c.addDefault("message." + value.getPath().toLowerCase(), value.getMessage());
            } else {
                Lang.c.addDefault("message." + value.getPath().toLowerCase(), value.getMessage().split("\n"));
            }
        }
        Lang.c.options().copyDefaults(true);
        wrapper.saveConfig();
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
    
    public static Config getConfig() {
        return Lang.config;
    }

    public String asString(final Object... objects) {
        Optional<String> opt = Optional.empty();
        if (Lang.c.contains(this.name())) {
            if (Lang.c.isList(this.name())) {
                opt = Optional.of(String.join("\n", Lang.c.getStringList(this.name())));
            } else if (Lang.c.isString(this.name())) {
                opt = Optional.ofNullable(Lang.c.getString(this.name()));
            }
        }
        return this.format(opt.orElse(this.message), objects);
    }
    
    public static void reload() {
        Lang.config.loadConfig();
        Lang.c = Lang.config.getConfig();
    }
}
