package com.zulip.android.models;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "reactions")
public class Reaction {
    private static final String ID_FIELD = "id";
    private static final String NAME_FIELD = "name";
    private static final String USER_FIELD = "user";
    private static final String MESSAGE_FIELD = "message";


    @SerializedName("emoji_name")
    @DatabaseField(columnName = NAME_FIELD)
    private String emojiName;

    @SerializedName("user")
    @DatabaseField(foreign = true, columnName = USER_FIELD)
    private User user;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = MESSAGE_FIELD)
    private Message message;


    /**
     * Construct an empty Reaction object.
     */
    public Reaction() {

    }

    public String getEmoji() {
        return this.emojiName;
    }

    public User getUser() {
        return this.user;
    }

    @Override
    public String toString() {
        return ":" + getEmoji() + ":";
    }
}
