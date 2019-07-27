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
import org.bukkit.Material;

import java.util.Map;

public class TagsPage extends Base {
    private int counter = 0;

    public TagsPage(PlayerTags plugin, String title, int pages) {
        super(plugin, title, Size.from(27));

        int slot = 0;
        for (Map.Entry<String, Tag> entry : plugin.getTagManager().getTags().entrySet()) {
            String tagName = entry.getKey();
            Tag tagObj = entry.getValue();
            Button tagBtn = new Button(new ItemBuilder(Material.NAME_TAG).displayName(Common.translate(tagObj.getPrefix())).lore(
                    Common.translate("&7Tag: &f" + tagObj.getId()),
                    Common.translate("&7Desc: &f" + tagObj.getDescription()),
                    Common.translate("&7Perm: &f" + tagObj.needPermission())
            ).asItemStack());
            this.attach(tagBtn);
            this.setIcon(slot, tagBtn);
            slot++;
        }

        int[] navPages = {0, 1, 2, 3};
        this.setIcon(navPages, 21, new PreviousButton());
        this.setIcon(navPages, 22, new CloseButton());
        this.setIcon(navPages, 23, new NextButton());
    }
}
