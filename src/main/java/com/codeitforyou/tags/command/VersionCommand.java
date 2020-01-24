package com.codeitforyou.tags.command;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.tags.CIFYTags;
import com.codeitforyou.tags.config.Lang;
import org.bukkit.command.CommandSender;

public class VersionCommand {
    @Command(aliases = {"version"}, about = "List plugin version.", permission = "cifytags.version", usage = "version")
    public static void execute(final CommandSender sender, final CIFYTags plugin, final String[] args) {
        Lang.MAIN_COMMAND.send(sender, Lang.PREFIX.asString(), plugin.getDescription().getName(), plugin.getDescription().getVersion(), plugin.getDescription().getAuthors().get(0));
    }
}
