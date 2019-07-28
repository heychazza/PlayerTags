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
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class TagsPage extends Base {
    private int counter = 0;

    public TagsPage(PlayerTags plugin, Player player) {
        super(plugin, Lang.GUI_TITLE.asString(Lang.PREFIX.asString(), plugin.getTagManager().getTags(player).size(), plugin.getTagManager().getTags().size()), Size.from(27));

        int slot = 0;
        for (Tag tag : plugin.getTagManager().getTags(player)) {
            Button tagBtn = new Button(new ItemBuilder(Material.NAME_TAG).displayName(Common.translate(tag.getPrefix())).lore(
                    Common.translate("&7Tag: &f" + tag.getId()),
                    Common.translate("&7Desc: &f" + tag.getDescription()),
                    Common.translate("&7Perm: &f" + tag.needPermission(player))
            ).asItemStack());

            tagBtn.setProperty("id", tag.getId());
            tagBtn.setAction(buttonClickEvent -> {
                Player playerClicked = (Player) buttonClickEvent.getEntity();
                Lang.TAG_SELECTED.send(playerClicked, Lang.PREFIX.asString(), buttonClickEvent.getButton().getProperty("id"));
                close(buttonClickEvent.getEntity());
            });
            this.attach(tagBtn);
            this.addIcon(tagBtn);
            slot++;
        }

        int[] navPages = {0, 1, 2, 3};
        this.setIcon(navPages, 21, new PreviousButton());
        this.setIcon(navPages, 22, new CloseButton());
        this.setIcon(navPages, 23, new NextButton());
    }
}
