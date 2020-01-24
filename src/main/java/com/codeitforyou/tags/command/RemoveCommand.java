package com.codeitforyou.tags.command;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.tags.CIFYTags;
import com.codeitforyou.tags.api.Tag;
import com.codeitforyou.tags.config.Lang;
import org.bukkit.command.CommandSender;

public class RemoveCommand {
    @Command(aliases = {"remove"}, about = "Remove a tag.", permission = "cifytags.remove", usage = "<id>", requiredArgs = 1)
    public static void execute(final CommandSender sender, final CIFYTags plugin, final String[] args) {
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
