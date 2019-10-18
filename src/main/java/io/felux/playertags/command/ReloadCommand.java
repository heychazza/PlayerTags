package io.felux.playertags.command;

import io.felux.playertags.PlayerTags;
import io.felux.playertags.command.util.Command;
import io.felux.playertags.config.Lang;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    @Command(aliases = {"reload"}, about = "Reload the plugin.", permission = "playertags.reload", usage = "reload")
    public static void execute(final CommandSender sender, final PlayerTags plugin, final String[] args) {
        plugin.handleReload();
        Lang.RELOAD_COMMAND.send(sender, Lang.PREFIX.asString(), plugin.getTagManager().getTags().size());
    }
}
