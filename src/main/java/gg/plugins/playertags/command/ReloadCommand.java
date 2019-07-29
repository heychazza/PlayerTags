package gg.plugins.playertags.command;

import gg.plugins.playertags.PlayerTags;
import gg.plugins.playertags.command.util.Command;
import gg.plugins.playertags.config.Lang;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    @Command(aliases = {"reload"}, about = "Reload the plugin.", permission = "ltools.reload", usage = "reload")
    public static void execute(final CommandSender sender, final PlayerTags plugin, final String[] args) {
        plugin.handleReload(true);
        Lang.RELOAD_COMMAND.send(sender, Lang.PREFIX.asString(), plugin.getTagManager().getTags().size());
    }
}
