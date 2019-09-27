package io.felux.playertags.storage.mongodb;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.PrePersist;
import io.felux.playertags.storage.PlayerData;
import org.bson.types.ObjectId;

import java.util.Date;

@Entity(value = "player", noClassnameStored = true)
public class MongoDBPlayerData implements PlayerData {
    @Id
    private ObjectId id;
    private Date creationDate;
    private Date lastChange;

    @Indexed
    private String uuid;
    private String username;
    private String tag;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    public ObjectId getId() {
        return this.id;
    }

    public long getCreationDate() {
        return this.creationDate.getTime();
    }

    public long getLastChange() {
        return this.lastChange.getTime();
    }

    @PrePersist
    public void prePersist() {
        this.creationDate = ((this.creationDate == null) ? new Date() : this.creationDate);
        this.lastChange = ((this.lastChange == null) ? this.creationDate : new Date());
    }
}
