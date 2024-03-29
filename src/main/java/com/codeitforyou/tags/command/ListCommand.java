package com.codeitforyou.tags.command;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.tags.CIFYTags;
import com.codeitforyou.tags.api.Tag;
import com.codeitforyou.tags.config.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommand {
    @Command(aliases = {"list"}, about = "List available tags.", permission = "cifytags.list", usage = "[player]")
    public static void execute(final CommandSender sender, final CIFYTags plugin, final String[] args) {
        if (args.length > 0) {
            final Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                Lang.COMMAND_UNKNOWN.send(sender, Lang.PREFIX.asString());
                return;
            }

            List<Tag> tagList = new ArrayList<>(plugin.getTagManager().getTags(target, false));
            String tagStr = tagList.stream().map(Tag::getId).collect(Collectors.joining(Lang.LIST_SEPARATOR.asString()));

            if (sender instanceof Player) {
                Player senderPlayer = (Player) sender;
                if (senderPlayer.getUniqueId() == target.getUniqueId()) {
                    Lang.LIST_COMMAND_SELF.send(sender, Lang.PREFIX.asString(), tagList.size(), tagStr);
                    return;
                }
            }
            Lang.LIST_COMMAND_OTHER.send(sender, Lang.PREFIX.asString(), target.getName(), tagList.size(), tagStr);
        } else {
            if (!(sender instanceof Player)) {
                List<Tag> tagList = plugin.getTagManager().getTags().values().stream().filter(tag -> !tag.isPlaceholder()).collect(Collectors.toList());
                String tagStr = tagList.stream().map(Tag::getId).collect(Collectors.joining(Lang.LIST_SEPARATOR.asString()));
                Lang.LIST_COMMAND_SELF.send(sender, Lang.PREFIX.asString(), tagList.size(), tagStr);
                return;
            }
            final Player player = (Player) sender;
            List<Tag> tagList = plugin.getTagManager().getTags().values().stream().filter(tag -> !tag.isPlaceholder()).collect(Collectors.toList());
            String tagStr = tagList.stream().map(Tag::getId).collect(Collectors.joining(Lang.LIST_SEPARATOR.asString()));
            Lang.LIST_COMMAND_SELF.send(player, Lang.PREFIX.asString(), tagList.size(), tagStr);
        }
    }
}
