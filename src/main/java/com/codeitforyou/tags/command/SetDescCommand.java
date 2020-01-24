package com.codeitforyou.tags.command;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.lib.api.general.StringUtil;
import com.codeitforyou.tags.CIFYTags;
import com.codeitforyou.tags.api.Tag;
import com.codeitforyou.tags.config.Lang;
import org.bukkit.command.CommandSender;

public class SetDescCommand {
    @Command(aliases = {"setdesc"}, about = "Set a tags description.", permission = "cifytags.setdesc", usage = "<id> <prefix>", requiredArgs = 1)
    public static void execute(final CommandSender sender, final CIFYTags plugin, final String[] args) {
        String tagName = args[0];
        Tag tag = plugin.getTagManager().getTag(tagName);
        if (tag == null) {
            Lang.COMMAND_TAG_UNKNOWN.send(sender, Lang.PREFIX.asString());
            return;
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            builder.append(StringUtil.translate(args[i] + " "));
        }

        String desc = builder.toString().trim();
        tag.setDescription(desc);
        plugin.getTagManager().getTags().put(tag.getId(), tag);
        plugin.saveTags();
        plugin.setupTags();
        Lang.SET_PREFIX_COMMAND.send(sender, Lang.PREFIX.asString(), tagName, desc);
    }
}
