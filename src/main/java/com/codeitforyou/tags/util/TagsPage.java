package com.codeitforyou.tags.util;

import com.codeitforyou.lib.api.general.PAPIUtil;
import com.codeitforyou.tags.CIFYTags;
import com.codeitforyou.tags.api.Tag;
import com.codeitforyou.tags.config.Lang;
import com.codeitforyou.tags.storage.PlayerData;
import com.hazebyte.base.Base;
import com.hazebyte.base.Button;
import com.hazebyte.base.Size;
import com.hazebyte.base.foundation.NextButton;
import com.hazebyte.base.foundation.PreviousButton;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TagsPage extends Base {

    public TagsPage(CIFYTags plugin, Player player) {
        super(plugin, Lang.GUI_TITLE.asString(Lang.PREFIX.asString(), plugin.getTagManager().getTags(player, false).size(), (plugin.getTagManager().getTags().size() - plugin.getTagManager().getPlaceholders().size())), Size.from(plugin.getConfig().getInt("settings.gui.size", 9)));
        PlayerData playerData = plugin.getStorageHandler().getPlayer(player.getUniqueId());

        int guiSlots = plugin.getConfig().getInt("settings.gui.size", 27);
        int playerTagCount = 0;
        for (Tag tag : plugin.getTagManager().getTags(player, true)) {
            boolean hasPerm = tag.hasPermission(player);

            if (tag.isPlaceholder()) {
                ItemStack item = hasPerm ? tag.getItemHasPerm().asItemStack().clone() : tag.getItemNoPerm().asItemStack().clone();
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(PAPIUtil.parse(player, itemMeta.getDisplayName()));
                itemMeta.getLore().replaceAll(lore -> PAPIUtil.parse(player, lore));
                item.setItemMeta(itemMeta);
                Button tagBtn = new Button(item);

                tagBtn.setAction(buttonClickEvent -> {
                    ActionHandler.executeActions(player, tag.getActions());
                });

                if (tag.getSlot() == -1) this.addIcon(tagBtn);
                else this.setIcon(tag.getSlot(), tagBtn);
                continue;
            }

            playerTagCount++;

            ItemStack item = hasPerm ? tag.getItemHasPerm().asItemStack().clone() : tag.getItemNoPerm().asItemStack().clone();
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(PAPIUtil.parse(player, itemMeta.getDisplayName()));
            itemMeta.getLore().replaceAll(lore -> PAPIUtil.parse(player, lore));
            item.setItemMeta(itemMeta);

            if (playerData.getTag() != null && playerData.getTag().equalsIgnoreCase(tag.getId())) {
                if (plugin.getConfig().getBoolean("settings.legacy", false)) {
                    item = LegacyGlow.addGlow(item);
                } else {
                    item.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
                    ItemMeta localItemMeta = item.getItemMeta();
                    localItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    item.setItemMeta(localItemMeta);
                }
            }
            Button tagBtn = new Button(item);
            if (hasPerm) tagBtn.setProperty("id", tag.getId());

            tagBtn.setAction(buttonClickEvent -> {
                if (!hasPerm) return;

                if (tag.getId().equalsIgnoreCase(playerData.getTag())) {
                    Lang.TAG_UNSELECTED.send(player, Lang.PREFIX.asString(), tag.getId(), PAPIUtil.parse(player, tag.getPrefix()));
                    playerData.setTag(null);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            plugin.getStorageHandler().pushData(player.getUniqueId());
                        }
                    }.runTaskAsynchronously(plugin);
                } else {
                    Lang.TAG_SELECTED.send(player, Lang.PREFIX.asString(), tag.getId(), PAPIUtil.parse(player, tag.getPrefix()));
                    playerData.setTag(tag.getId());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            plugin.getStorageHandler().pushData(player.getUniqueId());
                        }
                    }.runTaskAsynchronously(plugin);
                }
                close(player);
            });
            this.attach(tagBtn);

            if (tag.getSlot() == -1) this.addIcon(tagBtn);
            else this.setIcon(tag.getSlot(), tagBtn);
        }

        int pages = (int) Math.ceil((double) playerTagCount / guiSlots - 9);
        List<Integer> navPages = IntStream.range(0, pages).boxed().collect(Collectors.toList());

        this.setIcon(navPages.stream().mapToInt(i -> i).toArray(), guiSlots - 6, new PreviousButton());
        this.setIcon(navPages.stream().mapToInt(i -> i).toArray(), guiSlots - 4, new NextButton());
    }
}
