package gg.plugins.playertags.config;

import gg.plugins.playertags.PlayerTags;
import gg.plugins.playertags.api.Tag;

import java.util.HashMap;
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
}
