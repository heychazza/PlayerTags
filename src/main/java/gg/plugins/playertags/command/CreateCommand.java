package gg.plugins.playertags.command;

import gg.plugins.playertags.PlayerTags;
import gg.plugins.playertags.api.Tag;
import gg.plugins.playertags.command.util.Command;
import gg.plugins.playertags.config.Lang;
import gg.plugins.playertags.util.Common;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CreateCommand {
    @Command(aliases = {"create"}, about = "Create a tag.", permission = "playertags.create", usage = "create <id>")
    public static void execute(final CommandSender sender, final PlayerTags plugin, final String[] args) {

        if(args.length == 0) {
            // no id specified
            Lang.COMMAND_INVALID_SYNTAX.send(sender, Lang.PREFIX.asString());
            return;
        }

        String tagName = args[0];

        if(plugin.getTagManager().getTag(tagName) != null) {
            Lang.COMMAND_ALREADY_EXISTS.send(sender, Lang.PREFIX.asString());
            return;
        }


        String hasPermName = Lang.GUI_TAG_HAS_PERM_NAME.asString();
        List<String> hasPermLore = Arrays.asList(Lang.GUI_TAG_HAS_PERM_LORE.asString().split("\n"));

        String hasNoPermName =Lang.GUI_TAG_HAS_NO_PERM_NAME.asString();
        List<String> hasNoPermLore = Arrays.asList(Lang.GUI_TAG_HAS_NO_PERM_LORE.asString().split("\n"));

        plugin.getTagManager().addTag(new Tag(tagName).withPrefix("&7[" + tagName + "]").withDescription("Default description..").withPermission(true).withSlot(-1).withItem(hasPermName, hasPermLore, true).withPersist(true).withItem(hasNoPermName, hasNoPermLore, false));
        Lang.CREATE_COMMAND.send(sender, Lang.PREFIX.asString(), tagName);
        // check if id exists
        // create tag
    }
}
