package gg.plugins.playertags.command;

import gg.plugins.playertags.PlayerTags;
import gg.plugins.playertags.api.Tag;
import gg.plugins.playertags.command.util.Command;
import gg.plugins.playertags.config.Lang;
import gg.plugins.playertags.util.Common;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class SetPrefixCommand {
    @Command(aliases = {"setprefix"}, about = "Set a tags prefix.", permission = "playertags.setprefix", usage = "setprefix <id> <prefix>")
    public static void execute(final CommandSender sender, final PlayerTags plugin, final String[] args) {

        if (args.length < 1) {
            Lang.COMMAND_INVALID_SYNTAX.send(sender, Lang.PREFIX.asString());
            return;
        }

        String tagName = args[0];
        Tag tag = plugin.getTagManager().getTag(tagName);
        if (tag == null) {
            Lang.COMMAND_TAG_UNKNOWN.send(sender, Lang.PREFIX.asString());
            return;
        }

        String prefix = Common.translate(args[1]);
        tag.setPrefix(prefix);
        plugin.getTagManager().getTags().put(tag.getId(), tag);
        plugin.setupTags();
        Lang.SET_PREFIX_COMMAND.send(sender, Lang.PREFIX.asString(), tagName, prefix);
    }
}
