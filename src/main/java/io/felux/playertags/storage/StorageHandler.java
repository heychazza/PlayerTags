package io.felux.playertags.storage;

import io.felux.playertags.PlayerTags;

import java.util.UUID;

public interface StorageHandler {

    void pullData(UUID uuid);

    void pushData(UUID uuid);

    default PlayerData getPlayer(UUID uuid) {
        PlayerData playerData = PlayerData.get().get(uuid);
        if(playerData == null) {
            PlayerTags.getInstance().getStorageHandler().pullData(uuid);
            playerData = PlayerData.get().get(uuid);
        }

        return playerData;
    }
}
