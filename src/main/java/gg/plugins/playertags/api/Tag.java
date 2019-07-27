package gg.plugins.playertags.api;

public class Tag {

    private String id;
    private String description;
    private boolean permission;
    public Tag(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    public boolean needPermission() {
        return permission;
    }
}
