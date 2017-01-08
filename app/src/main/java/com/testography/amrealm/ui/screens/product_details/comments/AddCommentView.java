package com.testography.amrealm.ui.screens.product_details.comments;

import android.content.Context;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;

import com.testography.amrealm.R;
import com.testography.amrealm.data.network.req.CommentReq;
import com.testography.amrealm.di.DaggerService;
import com.testography.amrealm.mvp.views.AbstractView;
import com.testography.amrealm.utils.ConstantsManager;
import com.testography.amrealm.utils.DateConverter;
import com.testography.amrealm.utils.RandomIdGenerator;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddCommentView extends AbstractView<AddCommentScreen
        .AddCommentPresenter> {

    @BindView(R.id.save_comment_btn)
    Button mSaveCommentBtn;
    @BindView(R.id.set_rating)
    AppCompatRatingBar mRating;
    @BindView(R.id.add_comment_et)
    EditText mCommentEt;

    @Inject
    AddCommentScreen.AddCommentPresenter mPresenter;

    public AddCommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<AddCommentScreen.Component>getDaggerComponent(context).inject
                (this);
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    //region ==================== Events ===================

    @OnClick(R.id.save_comment_btn)
    void saveComment() {
        CommentReq commentReq = new CommentReq(
                RandomIdGenerator.generateId(),
                RandomIdGenerator.generateRemoteId(),
                ConstantsManager.TEMPORARY_USER_AVATAR,
                ConstantsManager.TEMPORARY_USER_NAME,
                mRating.getRating(), DateConverter.dateToString(new Date()),
                mCommentEt.getText().toString(), true);
        mPresenter.clickOnSaveComment(commentReq);
    }

    //endregion
}
