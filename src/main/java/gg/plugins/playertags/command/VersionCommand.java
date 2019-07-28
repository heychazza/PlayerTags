package gg.plugins.playertags.command;

import gg.plugins.playertags.PlayerTags;
import gg.plugins.playertags.api.Tag;
import gg.plugins.playertags.command.util.Command;
import gg.plugins.playertags.config.Lang;
import gg.plugins.playertags.util.Common;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class VersionCommand {
    @Command(aliases = {"version"}, about = "List plugin version.", permission = "playertags.version", usage = "version")
    public static void execute(final CommandSender sender, final PlayerTags plugin, final String[] args) {
        Lang.MAIN_COMMAND.send(sender, Lang.PREFIX.asString(), plugin.getDescription().getName(), plugin.getDescription().getVersion(), plugin.getDescription().getAuthors().get(0));
    }
}
