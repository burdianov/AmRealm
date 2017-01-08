package com.testography.amrealm.data.network.res;

import com.squareup.moshi.Json;

import java.util.Date;

public class CommentRes {
    @Json(name = "_id")
    private String id;
    private String avatar;
    private String userName;
    @Json(name = "raiting")
    private float rating;
    private Date commentDate;
    private String comment;
    private boolean active;

    public CommentRes(String id, String avatar, String userName,
                      float rating, Date commentDate, String comment,
                      boolean active) {
        this.id = id;
        this.avatar = avatar;
        this.userName = userName;
        this.rating = rating;
        this.commentDate = commentDate;
        this.comment = comment;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUserName() {
        return userName;
    }

    public float getRating() {
        return rating;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public String getComment() {
        return comment;
    }

    public boolean isActive() {
        return active;
    }
}
