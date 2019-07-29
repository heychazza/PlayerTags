package gg.plugins.playertags.api;

import com.hazebyte.base.util.ItemBuilder;
import gg.plugins.playertags.config.Lang;
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
    private boolean persist;

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
        if (!permission) return true;
        return player.hasPermission("playertags.use." + getId());
    }

    public boolean requirePermission() {
        return permission;
    }

    public Tag withSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public int getSlot() {
        return slot;
    }

    public Tag withItem(String name, List<String> lore, boolean hasPerm) {
        String translatedName = Lang.format(name, getId(), getPrefix(), getDescription()).replace("{id}", getId()).replace("{prefix}", getPrefix()).replace("{description}", getDescription());
        List<String> translatedLore = new ArrayList<>();

        lore.forEach(loreStr -> {
            translatedLore.add(Lang.format(loreStr, getId(), getPrefix(), getDescription()).replace("{id}", getId()).replace("{prefix}", getPrefix()).replace("{description}", getDescription()));
        });

        if (hasPerm) {
            itemHasPerm = new ItemBuilder(Material.NAME_TAG)
                    .displayName(translatedName)
                    .lore(translatedLore);
        } else {
            itemNoPerm = new ItemBuilder(Material.NAME_TAG)
                    .displayName(translatedName)
                    .lore(translatedLore);

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

    public boolean persist() {
        return persist;
    }
}
