package gg.plugins.playertags.api;

import org.bukkit.entity.Player;

public class Tag {

    private String id;
    private String prefix;
    private String description;
    private int slot;
    private boolean permission;
    public Tag(String id) {
        this.id = id.toLowerCase();
    }

    public String getId() {
        return id;
    }

    public Tag withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    public Tag withDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Tag withPermission(boolean permission) {
        this.permission = permission;
        return this;
    }

    public boolean needPermission(Player player) {
        if(permission) return player.hasPermission("playertags.use." + getId());
        return true;
    }

    public Tag withSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public int getSlot() {
        return slot;
    }
}
