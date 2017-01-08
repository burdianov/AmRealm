package com.testography.amrealm.ui.screens.product_details.comments;

import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.testography.amrealm.R;
import com.testography.amrealm.data.storage.dto.CommentDto;
import com.testography.amrealm.di.DaggerService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter
        .CommentsViewHolder> {

    private List<CommentDto> mCommentsList = new ArrayList<>();

    @Inject
    Picasso mPicasso;

    public void addItem(CommentDto commentDto) {
        mCommentsList.add(commentDto);
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        DaggerService.<CommentsScreen.Component>getDaggerComponent(recyclerView
                .getContext()).inject(this);
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentsViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int position) {
        CommentDto comment = mCommentsList.get(position);
        holder.userNameTxt.setText(comment.getUserName());
        holder.dateTxt.setText(comment.getCommentDate());
        holder.rating.setRating(comment.getRating());
        holder.commentTxt.setText(comment.getComment());

        mPicasso.load(comment.getAvatarUrl())
                .fit()
                .into(holder.commentAvatarImg);
    }

    @Override
    public int getItemCount() {
        return mCommentsList.size();
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.comment_avatar_img)
        ImageView commentAvatarImg;
        @BindView(R.id.user_name_txt)
        TextView userNameTxt;
        @BindView(R.id.date_txt)
        TextView dateTxt;
        @BindView(R.id.comment_txt)
        TextView commentTxt;
        @BindView(R.id.rating)
        AppCompatRatingBar rating;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
