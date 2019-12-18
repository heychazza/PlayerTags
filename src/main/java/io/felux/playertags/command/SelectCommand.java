package io.felux.playertags.command;

import io.felux.playertags.PlayerTags;
import io.felux.playertags.api.Tag;
import io.felux.playertags.command.util.Command;
import io.felux.playertags.config.Lang;
import io.felux.playertags.storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SelectCommand {
    @Command(aliases = {"select"}, about = "Select a tag.", permission = "playertags.select", usage = "select <id> [player]", requiredArgs = 1)
    public static void execute(final Player sender, final PlayerTags plugin, final String[] args) {

        final Tag tag = plugin.getTagManager().getTag(args[0]);

        if (tag == null) {
            Lang.COMMAND_TAG_UNKNOWN.send(sender, Lang.PREFIX.asString());
            return;
        }

        if (args.length == 1) {
            // No target player.
            final PlayerData playerData = PlayerData.get(sender.getUniqueId());
            playerData.setTag(tag.getId());
            Lang.SELECTED_TAG.send(sender, Lang.PREFIX.asString(), tag.getId(), tag.getPrefix());
            return;
        }

        final Player target = Bukkit.getPlayer(args[1]);

        if (!sender.hasPermission("playertags.select.other")) {
            Lang.COMMAND_NO_PERMISSION.send(sender, Lang.PREFIX.asString());
            return;
        }

        if (target == null) {
            // invalid player.
            Lang.COMMAND_UNKNOWN.send(sender, Lang.PREFIX.asString());
            return;
        }

        final PlayerData playerData = PlayerData.get(target.getUniqueId());
        playerData.setTag(tag.getId());

        if (sender.getUniqueId() == target.getUniqueId()) {
            Lang.SELECTED_TAG.send(sender, Lang.PREFIX.asString(), tag.getId(), tag.getPrefix());
            return;
        }

        Lang.SELECTED_TAG_OTHER.send(sender, Lang.PREFIX.asString(), target.getName(), tag.getId(), tag.getPrefix());
        Lang.TAG_CHANGED_TO.send(target, Lang.PREFIX.asString(), tag.getId(), tag.getPrefix());
    }
}
