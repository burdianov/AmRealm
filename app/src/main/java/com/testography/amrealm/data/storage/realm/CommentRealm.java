package com.testography.amrealm.data.storage.realm;

import com.testography.amrealm.data.network.req.CommentReq;
import com.testography.amrealm.data.network.res.CommentRes;
import com.testography.amrealm.utils.DateConverter;

import java.text.ParseException;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CommentRealm extends RealmObject {
    @PrimaryKey
    private String id;
    private String userName;
    private String avatar;
    private float rating;
    private Date commentDate;
    private String comment;

    // Required for Realm
    public CommentRealm() {
    }

    public CommentRealm(CommentRes commentRes) {
        this.id = commentRes.getId();
        this.userName = commentRes.getUserName();
        this.avatar = commentRes.getAvatar();
        this.rating = commentRes.getRating();
        this.commentDate = commentRes.getCommentDate();
        this.comment = commentRes.getComment();
    }

    public CommentRealm(CommentReq commentReq) {
        try {
            this.id = commentReq.id;
            this.userName = commentReq.userName;
            this.avatar = commentReq.avatar;
            this.rating = commentReq.rating;
            this.commentDate = DateConverter.stringToDate(commentReq.commentDate);
            this.comment = commentReq.comment;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getAvatar() {
        return avatar;
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
}
