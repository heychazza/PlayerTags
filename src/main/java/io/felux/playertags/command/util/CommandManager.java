package io.felux.playertags.command.util;

import io.felux.playertags.PlayerTags;
import io.felux.playertags.command.*;
import io.felux.playertags.config.Lang;
import io.felux.playertags.util.TagsPage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {

    private Map<String, Method> commands = new HashMap<>();

    public Map<String, Method> getCommands() {
        return commands;
    }

    private PlayerTags plugin;

    public CommandManager(PlayerTags plugin) {
        this.plugin = plugin;

        List<Class<?>> commandClasses = Arrays.asList(
                ListCommand.class,
                ReloadCommand.class,
                VersionCommand.class,
                HelpCommand.class,
                CreateCommand.class,
                RemoveCommand.class,
                SetPrefixCommand.class,
                SetDescCommand.class,
                SelectCommand.class,
                ResetCommand.class
        );

        for (Class cmdClass : commandClasses) {
            for (Method method : cmdClass.getMethods()) {
                if (!method.isAnnotationPresent(Command.class)) continue; // make sure method is marked as an annotation

                if (method.getParameters().length != 3) {
                    plugin.getLogger().warning("Method " + method.toGenericString().replace("public static void ", "") + " annotated as command but parameters count != 2");
                    continue;
                }
                if (method.getParameters()[0].getType() != CommandSender.class && method.getParameters()[0].getType() != Player.class) {
                    plugin.getLogger().warning("Method " + method.toGenericString().replace("public static void ", "") + " annotated as command but parameter 1's type != CommandSender || Player");
                    continue;
                }
                if (method.getParameters()[2].getType() != String[].class) {
                    plugin.getLogger().warning("Method " + method.toGenericString().replace("public static void ", "") + " annotated as command but parameter 3's type != String[]");
                    continue;
                }

                Command annotation = method.getAnnotation(Command.class);
                for (String commandName : annotation.aliases()) commands.put(commandName.toLowerCase(), method);
            }
        }
    }

    boolean handle(CommandSender sender, String command, String[] args) {

        // Command: /tags
        if (command == null) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                TagsPage page = new TagsPage(plugin, player);
                page.open(player);
            } else {
                Lang.COMMAND_PLAYER_ONLY.send(sender, Lang.PREFIX.asString());
            }

            //Lang.MAIN_COMMAND.send(sender, Lang.PREFIX.asString(), plugin.getDescription().getName(), plugin.getDescription().getVersion(), plugin.getDescription().getAuthors().get(0));
            return true;
        }

        if (commands.containsKey(command.toLowerCase())) {
            try {
                Method commandMethod = commands.get(command.toLowerCase());
                Command commandAnnotation = commandMethod.getAnnotation(Command.class);

                if (!sender.hasPermission(commandAnnotation.permission()) && (sender instanceof Player)) {
                    Lang.COMMAND_NO_PERMISSION.send(sender, Lang.PREFIX.asString());
                    return true;
                }

                if (commandMethod.getParameters()[0].getType() == Player.class && !(sender instanceof Player)) {
                    Lang.COMMAND_PLAYER_ONLY.send(sender, Lang.PREFIX.asString());
                    return true;
                }

                if (commandAnnotation.requiredArgs() > args.length) {
                    Lang.COMMAND_USAGE.send(sender, Lang.PREFIX.asString(), commandAnnotation.usage());
                    return true;
                }

                commandMethod.invoke(null, sender, plugin, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Lang.COMMAND_INVALID.send(sender, Lang.PREFIX.asString());
        }

        return true;
    }

}