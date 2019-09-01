package io.mcplugins.playertags.hook;

import io.mcplugins.playertags.PlayerTags;
import io.mcplugins.playertags.api.Tag;
import io.mcplugins.playertags.config.Lang;
import io.mcplugins.playertags.storage.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private PlayerTags plugin;

    public PlaceholderAPIHook(final PlayerTags plugin) {
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return plugin.getDescription().getName().toLowerCase();
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     * <p>
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    public String onRequest(final OfflinePlayer player, final String identifier) {
        final PlayerData playerEntity = plugin.getStorageHandler().getPlayer(player.getUniqueId());

        if (identifier.equals("id")) {
            return playerEntity.getTag() != null && !playerEntity.getTag().isEmpty() ? playerEntity.getTag() : Lang.NO_TAG_ID.asString();
        }

        if (identifier.equals("prefix")) {
            Tag tag = plugin.getTagManager().getTag(playerEntity.getTag());
            return tag != null && tag.getTagType() == Tag.Type.PREFIX ? tag.getPrefix() : Lang.NO_TAG_PREFIX.asString();
        }

        if (identifier.equals("suffix")) {
            Tag tag = plugin.getTagManager().getTag(playerEntity.getTag());
            return tag != null && tag.getTagType() == Tag.Type.SUFFIX ? tag.getPrefix() : Lang.NO_TAG_PREFIX.asString();
        }
        return null;
    }
}
