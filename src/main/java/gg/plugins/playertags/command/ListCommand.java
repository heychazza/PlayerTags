package gg.plugins.playertags.command;

import gg.plugins.playertags.PlayerTags;
import gg.plugins.playertags.command.util.Command;
import gg.plugins.playertags.config.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommand {
    @Command(aliases = {"list"}, about = "List available tags.", permission = "playertags.list", usage = "list [player]")
    public static void execute(final CommandSender sender, final PlayerTags plugin, final String[] args) {
        if (args.length > 0) {
            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target == null) {
                Lang.COMMAND_PLAYER_ONLY.send(sender, Lang.PREFIX.asString());
                return;
            }

            // TODO: Player Based
        } else {
            if (!(sender instanceof Player)) {
                Lang.COMMAND_PLAYER_ONLY.send(sender, Lang.PREFIX.asString());
                return;
            }
            final Player player2 = (Player) sender;
        }
    }
}
