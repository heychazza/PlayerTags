package io.mcplugins.playertags.command;

import io.mcplugins.playertags.PlayerTags;
import io.mcplugins.playertags.command.util.Command;
import io.mcplugins.playertags.config.Lang;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    @Command(aliases = {"reload"}, about = "Reload the plugin.", permission = "ltools.reload", usage = "reload")
    public static void execute(final CommandSender sender, final PlayerTags plugin, final String[] args) {
        plugin.handleReload();
        Lang.RELOAD_COMMAND.send(sender, Lang.PREFIX.asString(), plugin.getTagManager().getTags().size());
    }
}
