package com.codeitforyou.tags.util;

import com.codeitforyou.tags.CIFYTags;
import org.bukkit.plugin.java.JavaPlugin;

public class Common {
    private static final CIFYTags PLUGIN = (CIFYTags) JavaPlugin.getProvidingPlugin(CIFYTags.class);

    public static int getTotalTags() {
        return (int) PLUGIN.getTagManager().getTags().values().stream().filter(tag -> !tag.isPlaceholder()).count();
    }
}
