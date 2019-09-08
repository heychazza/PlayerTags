package net.chazza.playertags.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface PlayerData {

    Map<UUID, PlayerData> users = new HashMap<>();

    static Map<UUID, PlayerData> get() {
        return users;
    }

    String getUuid();

    String getUsername();

    void setUsername(String username);

    String getTag();

    void setTag(String tag);
}
