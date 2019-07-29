package gg.plugins.playertags.util;

import com.hazebyte.base.Base;
import com.hazebyte.base.Button;
import com.hazebyte.base.Size;
import com.hazebyte.base.foundation.CloseButton;
import com.hazebyte.base.foundation.NextButton;
import com.hazebyte.base.foundation.PreviousButton;
import com.hazebyte.base.util.ItemBuilder;
import gg.plugins.playertags.PlayerTags;
import gg.plugins.playertags.api.Tag;
import gg.plugins.playertags.config.Lang;
import gg.plugins.playertags.storage.PlayerData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TagsPage extends Base {
    private int counter = 0;

    public TagsPage(PlayerTags plugin, Player player) {
        super(plugin, Lang.GUI_TITLE.asString(Lang.PREFIX.asString(), plugin.getTagManager().getTags().size(), plugin.getTagManager().getTags().size()), Size.from(plugin.getConfig().getInt("settings.gui.size", 27)));

        PlayerData playerData = plugin.getStorageHandler().getPlayer(player.getUniqueId());

        for (Tag tag : plugin.getTagManager().getTags().values()) {

            boolean hasPerm = tag.needPermission(player);

            ItemStack item = hasPerm ? tag.getItemHasPerm().asItemStack().clone() : tag.getItemNoPerm().asItemStack().clone();
            if(playerData.getTag() != null && playerData.getTag().equalsIgnoreCase(tag.getId())) {
                item.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(itemMeta);
            }
            Button tagBtn = new Button(item);

            if(hasPerm) tagBtn.setProperty("id", tag.getId());
            tagBtn.setAction(buttonClickEvent -> {
                if(buttonClickEvent.getButton().getProperty("id") == null) return;

                Player playerClicked = (Player) buttonClickEvent.getEntity();
                Lang.TAG_SELECTED.send(playerClicked, Lang.PREFIX.asString(), buttonClickEvent.getButton().getProperty("id"));
                plugin.getStorageHandler().getPlayer(playerClicked.getUniqueId()).setTag(buttonClickEvent.getButton().getProperty("id"));
                close(buttonClickEvent.getEntity());
            });
            this.attach(tagBtn);

            if (tag.getSlot() == -1) this.addIcon(tagBtn);
            else this.setIcon(tag.getSlot(), tagBtn);
        }

        int[] navPages = {0, 1, 2, 3};
        this.setIcon(navPages, plugin.getConfig().getInt("settings.gui.size", 27) - 6, new PreviousButton());
        this.setIcon(navPages, plugin.getConfig().getInt("settings.gui.size", 27) - 5, new CloseButton());
        this.setIcon(navPages, plugin.getConfig().getInt("settings.gui.size", 27) - 4, new NextButton());
    }
}
