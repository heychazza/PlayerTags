package com.codeitforyou.tags.api;

import com.codeitforyou.tags.config.Lang;
import com.hazebyte.base.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Tag {
    private String id;
    private String prefix;
    private String description;
    private int slot;
    private ItemBuilder itemHasPerm;
    private ItemBuilder itemNoPerm;
    private boolean permission;
    private Type tagType;
    private boolean placeholder;
    private boolean persist;
    private List<String> actions;

    public Tag(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Tag withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public Tag withDescription(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Tag withPermission(boolean permission) {
        this.permission = permission;
        return this;
    }

    public Tag withTagType(Type tagType) {
        this.tagType = tagType;
        return this;
    }

    public boolean hasPermission(Player player) {
        if (!permission) return true;
        return player.hasPermission("cifytags.use." + getId().toLowerCase());
    }

    public boolean requirePermission() {
        return permission;
    }

    public Type getTagType() {
        return tagType;
    }

    public Tag withSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public int getSlot() {
        return slot;
    }

    public Tag withItem(String name, List<String> lore, Material item, int data, boolean hasPerm) {
        String translatedName = Lang.format(name, getId(), getPrefix(), getDescription()).replace("{id}", getId()).replace("{prefix}", getPrefix()).replace("{description}", getDescription());
        List<String> translatedLore = new ArrayList<>();

        lore.forEach(loreStr -> {
            translatedLore.add(Lang.format(loreStr, getId(), getPrefix(), getDescription()).replace("{id}", getId()).replace("{prefix}", getPrefix()).replace("{description}", getDescription()));
        });

        if (hasPerm) {
            itemHasPerm = new ItemBuilder(item)
                    .displayName(translatedName)
                    .lore(translatedLore)
                    .durability((short) data);
        } else {
            itemNoPerm = new ItemBuilder(item)
                    .displayName(translatedName)
                    .lore(translatedLore)
                    .durability((short) data);

        }

        return this;
    }

    public ItemBuilder getItemHasPerm() {
        return itemHasPerm;
    }

    public ItemBuilder getItemNoPerm() {
        return itemNoPerm;
    }

    public Tag withPersist(boolean persist) {
        this.persist = persist;
        return this;
    }

    public Tag setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public boolean persist() {
        return persist;
    }

    public boolean isPlaceholder() {
        return placeholder;
    }

    public Tag withActions(List<String> actions) {
        this.actions = actions;
        return this;
    }

    public List<String> getActions() {
        return actions;
    }

    public enum Type {
        PREFIX,
        SUFFIX
    }
}
