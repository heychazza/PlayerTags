package net.chazza.playertags.command;

import net.chazza.playertags.PlayerTags;
import net.chazza.playertags.api.Tag;
import net.chazza.playertags.command.util.Command;
import net.chazza.playertags.config.Lang;
import net.chazza.playertags.util.Common;
import org.bukkit.command.CommandSender;

public class SetDescCommand {
    @Command(aliases = {"setdesc"}, about = "Set a tags description.", permission = "playertags.setdesc", usage = "setdesc <id> <prefix>")
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

        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            builder.append(Common.translate(args[i] + " "));
        }

        String desc = builder.toString().trim();
        tag.setDescription(desc);
        plugin.getTagManager().getTags().put(tag.getId(), tag);
        plugin.saveTags();
        plugin.setupTags();
        Lang.SET_PREFIX_COMMAND.send(sender, Lang.PREFIX.asString(), tagName, desc);
    }
}
