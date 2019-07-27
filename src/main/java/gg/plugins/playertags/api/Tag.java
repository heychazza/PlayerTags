package gg.plugins.playertags.api;

public class Tag {

    private String id;
    private String prefix;
    private String description;
    private boolean permission;
    public Tag(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Tag withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    public Tag withDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Tag withPermission(boolean permission) {
        this.permission = permission;
        return this;
    }

    public boolean needPermission() {
        return permission;
    }
}
