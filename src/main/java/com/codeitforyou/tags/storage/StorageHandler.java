package com.codeitforyou.tags.storage;

import com.codeitforyou.tags.CIFYTags;

import java.util.UUID;

public interface StorageHandler {

    void pullData(UUID uuid);

    void pushData(UUID uuid);

    default PlayerData getPlayer(UUID uuid) {
        PlayerData playerData = PlayerData.get().get(uuid);
        if(playerData == null) {
            CIFYTags.getInstance().getStorageHandler().pullData(uuid);
            playerData = PlayerData.get().get(uuid);
        }

        return playerData;
    }
}
