package io.mcplugins.playertags.command;

import io.mcplugins.playertags.PlayerTags;
import io.mcplugins.playertags.api.Tag;
import io.mcplugins.playertags.command.util.Command;
import io.mcplugins.playertags.config.Lang;
import org.bukkit.command.CommandSender;

public class RemoveCommand {
    @Command(aliases = {"remove"}, about = "Remove a tag.", permission = "playertags.remove", usage = "remove <id>")
    public static void execute(final CommandSender sender, final PlayerTags plugin, final String[] args) {

        if (args.length == 0) {
            Lang.COMMAND_INVALID_SYNTAX.send(sender, Lang.PREFIX.asString());
            return;
        }

        String tagName = args[0];
        Tag tag = plugin.getTagManager().getTag(tagName);
        if (tag == null) {
            Lang.COMMAND_TAG_UNKNOWN.send(sender, Lang.PREFIX.asString());
            return;
        }

        plugin.getTagManager().getTags().remove(tag.getId());
        plugin.getConfig().set("tags." + tag.getId(), null);
        plugin.saveConfig();
        plugin.setupTags();
        Lang.REMOVE_COMMAND.send(sender, Lang.PREFIX.asString(), tagName);
    }
}
