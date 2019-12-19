package io.felux.playertags.command;

import io.felux.playertags.PlayerTags;
import io.felux.playertags.api.Tag;
import io.felux.playertags.command.util.Command;
import io.felux.playertags.config.Lang;
import io.felux.playertags.storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetCommand {
    @Command(aliases = {"reset"}, about = "Reset a players tag.", permission = "playertags.reset", usage = "reset [player] [silent]")
    public static void execute(final CommandSender sender, final PlayerTags plugin, final String[] args) {
        if (args.length == 0) {
            // No target player.
            if (!(sender instanceof Player)) {
                Lang.COMMAND_PLAYER_ONLY.send(sender, Lang.PREFIX.asString());
                return;
            }

            final Player player = (Player) sender;
            final PlayerData playerData = PlayerData.get(player.getUniqueId());
            final Tag tag = plugin.getTagManager().getTag(playerData.getTag());

            if (tag == null) {
                Lang.NO_TAG_SELECTED.send(sender, Lang.PREFIX.asString());
                return;
            }

            Lang.TAG_UNSELECTED.send(sender, Lang.PREFIX.asString(), tag.getId(), tag.getPrefix());
            playerData.setTag(null);
            return;
        }

        final Player target = Bukkit.getPlayer(args[0]);

        if (!sender.hasPermission("playertags.reset.other")) {
            Lang.COMMAND_NO_PERMISSION.send(sender, Lang.PREFIX.asString());
            return;
        }

        if (target == null) {
            // invalid player.
            Lang.COMMAND_UNKNOWN.send(sender, Lang.PREFIX.asString());
            return;
        }

        final PlayerData playerData = PlayerData.get(target.getUniqueId());
        final Tag tag = plugin.getTagManager().getTag(playerData.getTag());
        playerData.setTag(null);

        final boolean isSilent = args.length == 2 && args[1].equalsIgnoreCase("-s");

        if (sender instanceof Player) {
            final Player player = (Player) sender;
            if (player.getUniqueId() == target.getUniqueId()) {
                if (tag == null) {
                    Lang.NO_TAG_SELECTED.send(sender, Lang.PREFIX.asString());
                    return;
                }

                Lang.TAG_UNSELECTED.send(sender, Lang.PREFIX.asString(), tag.getId(), tag.getPrefix());
                return;
            }
        }

        if (tag == null) {
            if (!isSilent) Lang.NO_TAG_SELECTED_OTHER.send(sender, Lang.PREFIX.asString(), target.getName());
            return;
        }

        if (!isSilent)
            Lang.TAG_UNSELECTED_OTHER.send(sender, Lang.PREFIX.asString(), target.getName(), tag.getId(), tag.getPrefix());
        Lang.TAG_UNSELECTED.send(target, Lang.PREFIX.asString(), tag.getId(), tag.getPrefix());
    }
}
