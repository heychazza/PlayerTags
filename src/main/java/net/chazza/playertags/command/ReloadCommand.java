package net.chazza.playertags.command;

import net.chazza.playertags.PlayerTags;
import net.chazza.playertags.command.util.Command;
import net.chazza.playertags.config.Lang;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    @Command(aliases = {"reload"}, about = "Reload the plugin.", permission = "playertags.reload", usage = "reload")
    public static void execute(final CommandSender sender, final PlayerTags plugin, final String[] args) {
        plugin.handleReload();
        Lang.RELOAD_COMMAND.send(sender, Lang.PREFIX.asString(), plugin.getTagManager().getTags().size());
    }
}
