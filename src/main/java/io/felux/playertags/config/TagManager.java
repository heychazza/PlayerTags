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

    public List<Tag> getTags(Player player, boolean includePlaceholders) {
        List<Tag> tags = new ArrayList<>();

        for (Map.Entry<String, Tag> tagEntry : getTags().entrySet()) {
            Tag tag = tagEntry.getValue();

            if (!includePlaceholders && tag.isPlaceholder()) continue;

            if (tag.hasPermission(player) || plugin.getConfig().getBoolean("settings.gui.show-no-perm")) {
                tags.add(tag);
            }
        }
        return tags;
    }

    public List<Tag> getPlaceholders() {
        List<Tag> tags = new ArrayList<>();

        for (Map.Entry<String, Tag> tagEntry : getTags().entrySet()) {
            Tag tag = tagEntry.getValue();
            if (tag.isPlaceholder()) tags.add(tag);
        }
        return tags;
    }

    public List<Tag> getUnlockedTags(Player player) {
        List<Tag> tags = new ArrayList<>();

        for (Map.Entry<String, Tag> tagEntry : getTags().entrySet()) {
            Tag tag = tagEntry.getValue();
            if (tag.hasPermission(player) && !tag.isPlaceholder()) tags.add(tag);
        }
        return tags;
    }

    public Tag getTag(String id) {
        return getTags().values().stream().filter(tag -> tag.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }
}
