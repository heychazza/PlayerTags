package com.codeitforyou.tags.command;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.tags.CIFYTags;
import com.codeitforyou.tags.util.TagsPage;
import org.bukkit.entity.Player;

public class MainCommand {
    @Command(about = "Main command.")
    public static void execute(final Player player, final CIFYTags plugin, final String[] args) {
        TagsPage page = new TagsPage(plugin, player);
        page.open(player);
    }
}
