package net.chazza.playertags.command;

import net.chazza.playertags.PlayerTags;
import net.chazza.playertags.api.Tag;
import net.chazza.playertags.command.util.Command;
import net.chazza.playertags.config.Lang;
import net.chazza.playertags.util.Common;
import org.bukkit.command.CommandSender;

public class SetPrefixCommand {
    @Command(aliases = {"set"}, about = "Set a tags prefix.", permission = "playertags.setprefix", usage = "setprefix <id> <prefix>")
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

        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]);
        }

        String prefix = Common.translate(sb.toString());
        tag.setPrefix(prefix);
        plugin.getTagManager().getTags().put(tag.getId(), tag);
        plugin.saveTags();
        plugin.setupTags();
        Lang.SET_PREFIX_COMMAND.send(sender, Lang.PREFIX.asString(), tagName, prefix);
    }
}
