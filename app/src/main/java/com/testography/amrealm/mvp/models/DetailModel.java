package com.testography.amrealm.mvp.models;

import com.testography.amrealm.data.network.res.CommentRes;

public class DetailModel extends AbstractModel {
    public void saveComment(String productId,
                            CommentRes commentRes) {
        mDataManager.saveCommentToNetworkAndRealm(productId, commentRes);
    }
}
