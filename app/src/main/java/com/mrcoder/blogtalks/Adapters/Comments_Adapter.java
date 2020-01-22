package com.mrcoder.blogtalks.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mrcoder.blogtalks.Models.Comments;
import com.mrcoder.blogtalks.R;

import java.util.Calendar;
import java.util.List;

public class Comments_Adapter extends RecyclerView.Adapter<Comments_Adapter.CommentViewHolder> {

    private Context mContext;
    private List<Comments> mCommentData;

    public Comments_Adapter(Context mContext, List<Comments> mCommentData) {
        this.mContext = mContext;
        this.mCommentData = mCommentData;
    }

    @NonNull
    @Override
    public Comments_Adapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comments, parent, false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull Comments_Adapter.CommentViewHolder holder, int position) {

        Glide.with(mContext).load(mCommentData.get(position).getUserImage()).into(holder.ivCommentUserPhoto);

        holder.tvCommentUserName.setText(mCommentData.get(position).getUserName());
        holder.tvCommentContent.setText(mCommentData.get(position).getContent());
        holder.tvCommentDate.setText(timeStampToString((Long)mCommentData.get(position).getTimeStamp()));
    }

    @Override
    public int getItemCount() {
        return mCommentData.size();
    }

    private String timeStampToString(Long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm", calendar).toString();
        return date;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView tvCommentUserName, tvCommentContent, tvCommentDate;
        ImageView ivCommentUserPhoto;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCommentUserName = itemView.findViewById(R.id.comment_userName);
            tvCommentContent = itemView.findViewById(R.id.comment_commentContent);
            tvCommentDate = itemView.findViewById(R.id.comment_date);
            ivCommentUserPhoto = itemView.findViewById(R.id.comment_userImage);
        }
    }
}
