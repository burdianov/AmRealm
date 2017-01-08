package com.testography.amrealm.mvp.models;

import com.testography.amrealm.data.network.req.CommentReq;
import com.testography.amrealm.data.network.res.CommentRes;

import retrofit2.Call;

public class DetailModel extends AbstractModel {
    public Call<CommentRes> saveComment(String productId,
                                        CommentReq commentReq) {
        return mDataManager.saveCommentToNetworkAndRealm(productId, commentReq);
    }
}
