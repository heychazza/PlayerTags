package gg.plugins.playertags.command;

import gg.plugins.playertags.PlayerTags;
import gg.plugins.playertags.api.Tag;
import gg.plugins.playertags.command.util.Command;
import gg.plugins.playertags.config.Lang;
import gg.plugins.playertags.util.Common;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class ListCommand {
    @Command(aliases = {"list"}, about = "List available tags.", permission = "playertags.list", usage = "list [player]")
    public static void execute(final CommandSender sender, final PlayerTags plugin, final String[] args) {
        if (args.length > 0) {
            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target == null) {
                Lang.COMMAND_PLAYER_ONLY.send(sender, Lang.PREFIX.asString());
                return;
            }

            // TODO: Player Based
        } else {
            if (!(sender instanceof Player)) {
                Lang.COMMAND_PLAYER_ONLY.send(sender, Lang.PREFIX.asString());
                return;
            }
            final Player player = (Player) sender;

            // TODO: Use Config Values
            List<Tag> tagList = plugin.getTagManager().getTags(player);
            player.sendMessage("[DEBUG] Tags: " + tagList.size());
            String tagStr = tagList.stream().map(Tag::getId).collect(Collectors.joining(","));
            player.sendMessage(Common.translate("&7Tags: " + tagStr));
        }
    }
}
