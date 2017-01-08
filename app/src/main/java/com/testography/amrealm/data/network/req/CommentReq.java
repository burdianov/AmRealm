package com.testography.amrealm.data.network.req;

public class CommentReq {
    public String id;
    public int remoteId;
    public String avatar;
    public String userName;
    public float rating;
    public String commentDate;
    public String comment;
    public boolean active;

    public CommentReq(String id, int remoteId, String avatar,
                      String userName, float rating, String commentDate,
                      String comment, boolean active) {
        this.id = id;
        this.remoteId = remoteId;
        this.avatar = avatar;
        this.userName = userName;
        this.rating = rating;
        this.commentDate = commentDate;
        this.comment = comment;
        this.active = active;
    }
}
