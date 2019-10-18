package io.felux.playertags.config;

import io.felux.playertags.PlayerTags;
import io.felux.playertags.api.Tag;
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

    public void addTag(Tag tag) {
        getTags().put(tag.getId(), tag);
    }
    public void removeTag(Tag tag) {
        getTags().remove(tag.getId());
    }

    public List<Tag> getTags(Player player) {
        List<Tag> tags = new ArrayList<>();
        getTags().entrySet().stream().filter((tag -> tag.getValue().needPermission(player))).forEach(tag -> tags.add(tag.getValue()));
        return tags;
    }

    public Tag getTag(String id) {
        return getTags().values().stream().filter(tag -> tag.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }
}
