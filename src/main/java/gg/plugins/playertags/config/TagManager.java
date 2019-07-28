package gg.plugins.playertags.config;

import gg.plugins.playertags.PlayerTags;
import gg.plugins.playertags.api.Tag;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagManager {

    private PlayerTags plugin;
    private Map<String, Tag> tags;
    public TagManager(PlayerTags plugin) {
        this.plugin = plugin;
        this.tags = new HashMap<>();
    }

    public Map<String, Tag> getTags() {
        return tags;
    }

    public void addTag(String id, Tag tag) {
        getTags().put(id, tag);
    }

    public List<Tag> getTags(Player player) {
        List<Tag> tags = new ArrayList<>();
        getTags().entrySet().stream().filter((tag -> tag.getValue().needPermission(player))).forEach(tag -> tags.add(tag.getValue()));
        return tags;
    }
}
