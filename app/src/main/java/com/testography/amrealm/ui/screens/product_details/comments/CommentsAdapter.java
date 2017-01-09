package com.testography.amrealm.ui.screens.product_details.comments;

import android.content.Context;
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
import com.testography.amrealm.utils.ConstantsManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter
        .CommentsViewHolder> {

    private List<CommentDto> mCommentsList = new ArrayList<>();

    @Inject
    Picasso mPicasso;

    private Context mContext;

    public CommentsAdapter(Context context) {
        mContext = context;
    }

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
        try {
            holder.dateTxt.setText(elapsedTime(comment.getCommentDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    private String elapsedTime(String dateString) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat(
                ConstantsManager.SERVER_DATE_FORMAT, Locale.US);

        Date commentDate = timeFormat.parse(dateString);
        long commentTime = commentDate.getTime();

        Date nowDate = new Date();
        long nowTime = nowDate.getTime();

        long elapsedMinutes = (nowTime - commentTime) / 1000 / 60;
        long elapsedHours = elapsedMinutes / 60;
        long elapsedDays = elapsedHours / 24;

        String elapsedTime;

        if (elapsedHours > 47) {
            elapsedTime = String.valueOf(elapsedDays) + getStr(R.string.days_ago);
        } else if (elapsedHours > 24) {
            elapsedTime = String.valueOf(elapsedDays) + getStr(R.string.day_ago);
        } else if (elapsedMinutes > 60) {
            elapsedTime = String.valueOf(elapsedHours) + getStr(R.string.hours_ago);
        } else if (elapsedMinutes == 60) {
            elapsedTime = String.valueOf(elapsedHours) + getStr(R.string.hour_ago);
        } else if (elapsedMinutes > 1 || elapsedMinutes == 0) {
            elapsedTime = String.valueOf(elapsedMinutes) + getStr(R.string
                    .minutes_ago);
        } else {
            elapsedTime = String.valueOf(elapsedMinutes) + getStr(R.string
                    .minute_ago);
        }
        return elapsedTime;
    }

    private String getStr(int resource) {
        return " " + mContext.getString(resource);
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
