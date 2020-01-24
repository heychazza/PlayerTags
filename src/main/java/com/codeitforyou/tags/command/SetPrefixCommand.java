package com.codeitforyou.tags.command;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.lib.api.general.StringUtil;
import com.codeitforyou.tags.CIFYTags;
import com.codeitforyou.tags.api.Tag;
import com.codeitforyou.tags.config.Lang;
import com.codeitforyou.tags.util.Common;
import org.bukkit.command.CommandSender;

public class SetPrefixCommand {
    @Command(aliases = {"setprefix"}, about = "Set a tags prefix.", permission = "cifytags.setprefix", usage = "<id> <prefix>", requiredArgs = 1)
    public static void execute(final CommandSender sender, final CIFYTags plugin, final String[] args) {
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

        String prefix = StringUtil.translate(sb.toString());
        tag.setPrefix(prefix);
        plugin.getTagManager().getTags().put(tag.getId(), tag);
        plugin.saveTags();
        plugin.setupTags();
        Lang.SET_PREFIX_COMMAND.send(sender, Lang.PREFIX.asString(), tagName, prefix);
    }
}
