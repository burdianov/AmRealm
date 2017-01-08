package com.testography.amrealm.data.network.res;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import com.testography.amrealm.data.network.req.CommentReq;
import com.testography.amrealm.utils.DateConverter;

import java.text.ParseException;
import java.util.Date;

public class CommentJsonAdapter {
    @FromJson
    CommentRes fromJson(CommentJson commentJson) {
        try {
            return new CommentRes(
                    commentJson.id,
                    commentJson.avatar,
                    commentJson.userName,
                    commentJson.rating,
                    DateConverter.stringToDate(commentJson.commentDate),
                    commentJson.comment,
                    commentJson.active
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new CommentRes(
                commentJson.id,
                commentJson.avatar,
                commentJson.userName,
                commentJson.rating,
                new Date(),
                commentJson.comment,
                commentJson.active
        );
    }

    @ToJson
    CommentJson toJson(CommentReq commentReq) {
        CommentJson commentJson = new CommentJson();
        commentJson.id = commentReq.id;
        commentJson.remoteId = commentReq.remoteId;
        commentJson.avatar = commentReq.avatar;
        commentJson.userName = commentReq.userName;
        commentJson.rating = commentReq.rating;
        commentJson.commentDate = commentReq.commentDate;
        commentJson.comment = commentReq.comment;
        commentJson.active = commentReq.active;
        return commentJson;
    }

    @ToJson
    CommentJson toJson(CommentRes commentRes) {
        CommentJson commentJson = new CommentJson();
        commentJson.id = commentRes.getId();
        commentJson.avatar = commentRes.getAvatar();
        commentJson.userName = commentRes.getUserName();
        commentJson.rating = commentRes.getRating();
        commentJson.commentDate = DateConverter.dateToString(commentRes
                .getCommentDate());
        commentJson.comment = commentRes.getComment();
        commentJson.active = commentRes.isActive();
        return commentJson;
    }
}