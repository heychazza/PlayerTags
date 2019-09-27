package io.felux.playertags.command;

import io.felux.playertags.PlayerTags;
import io.felux.playertags.command.util.Command;
import io.felux.playertags.config.Lang;
import org.bukkit.command.CommandSender;

public class VersionCommand {
    @Command(aliases = {"version"}, about = "List plugin version.", permission = "playertags.version", usage = "version")
    public static void execute(final CommandSender sender, final PlayerTags plugin, final String[] args) {
        Lang.MAIN_COMMAND.send(sender, Lang.PREFIX.asString(), plugin.getDescription().getName(), plugin.getDescription().getVersion(), plugin.getDescription().getAuthors().get(0));
    }
}
