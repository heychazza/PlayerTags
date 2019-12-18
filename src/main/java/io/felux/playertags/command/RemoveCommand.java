package io.felux.playertags.command;

import io.felux.playertags.PlayerTags;
import io.felux.playertags.api.Tag;
import io.felux.playertags.command.util.Command;
import io.felux.playertags.config.Lang;
import org.bukkit.command.CommandSender;

public class RemoveCommand {
    @Command(aliases = {"remove"}, about = "Remove a tag.", permission = "playertags.remove", usage = "remove <id>", requiredArgs = 1)
    public static void execute(final CommandSender sender, final PlayerTags plugin, final String[] args) {
        String tagName = args[0];
        Tag tag = plugin.getTagManager().getTag(tagName);
        if (tag == null) {
            Lang.COMMAND_TAG_UNKNOWN.send(sender, Lang.PREFIX.asString());
            return;
        }

        plugin.getTagManager().removeTag(tag);
        plugin.getConfig().set("tags." + tag.getId(), null);
        plugin.saveConfig();
        plugin.setupTags();
        Lang.REMOVE_COMMAND.send(sender, Lang.PREFIX.asString(), tagName);
    }
}
