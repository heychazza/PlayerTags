package gg.plugins.playertags.command;

import gg.plugins.playertags.PlayerTags;
import gg.plugins.playertags.command.util.Command;
import gg.plugins.playertags.config.Lang;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HelpCommand {
    @Command(aliases = {"help"}, about = "View this menu.", permission = "playertags.help", usage = "help")
    public static void execute(final CommandSender sender, final PlayerTags plugin, final String[] args) {
        final List<Method> commandMethods = new ArrayList<>();
        for (final Method method : plugin.getCommandManager().getCommands().values()) {
            if (!commandMethods.contains(method)) {
                commandMethods.add(method);
            }
        }
        Lang.HELP_COMMAND_HEADER.send(sender, Lang.PREFIX.asString(), commandMethods.size());
        for (final Method commandMethod : commandMethods) {
            final Command commandAnnotation = commandMethod.getAnnotation(Command.class);
            if (!sender.hasPermission(commandAnnotation.permission())) {
                continue;
            }
            Lang.HELP_COMMAND_FORMAT.send(sender, String.join(",", commandAnnotation.aliases()), commandAnnotation.usage(), commandAnnotation.about());
        }
        Lang.HELP_COMMAND_FOOTER.send(sender, Lang.PREFIX.asString(), commandMethods.size());
    }
}
