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

public class CreateCommand {
    @Command(aliases = {"create"}, about = "Create a tag.", permission = "playertags.create", usage = "create <id>")
    public static void execute(final CommandSender sender, final PlayerTags plugin, final String[] args) {

        if(args.length == 0) {
            // no id specified
            return;
        }

        // check if id exists
        // create tag
    }
}
