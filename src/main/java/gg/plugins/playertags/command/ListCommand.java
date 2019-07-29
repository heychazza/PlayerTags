package gg.plugins.playertags.command;

import gg.plugins.playertags.PlayerTags;
import gg.plugins.playertags.api.Tag;
import gg.plugins.playertags.command.util.Command;
import gg.plugins.playertags.config.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListCommand {
    @Command(aliases = {"list"}, about = "List available tags.", permission = "playertags.list", usage = "list [player]")
    public static void execute(final CommandSender sender, final PlayerTags plugin, final String[] args) {
        if (args.length > 0) {
            final Player target = Bukkit.getPlayer(args[0]);
            List<Tag> tagList = plugin.getTagManager().getTags(target);
            String tagStr = tagList.stream().map(Tag::getId).collect(Collectors.joining(Lang.LIST_SEPARATOR.asString()));
            Lang.LIST_COMMAND_OTHER.send(sender, Lang.PREFIX.asString(), tagList.size(), tagStr);
        } else {
            if (!(sender instanceof Player)) {
                Map<String, Tag> tagList = plugin.getTagManager().getTags();
                String tagStr = tagList.values().stream().map(Tag::getId).collect(Collectors.joining(Lang.LIST_SEPARATOR.asString()));
                Lang.LIST_COMMAND_SELF.send(sender, Lang.PREFIX.asString(), tagList.size(), tagStr);
                return;
            }
            final Player player = (Player) sender;
            List<Tag> tagList = plugin.getTagManager().getTags(player);
            String tagStr = tagList.stream().map(Tag::getId).collect(Collectors.joining(Lang.LIST_SEPARATOR.asString()));
            Lang.LIST_COMMAND_SELF.send(player, Lang.PREFIX.asString(), tagList.size(), tagStr);
        }
    }
}
