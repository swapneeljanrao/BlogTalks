package com.mrcoder.blogtalks.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mrcoder.blogtalks.Activities.PostDetailsActivity;
import com.mrcoder.blogtalks.Models.Post;
import com.mrcoder.blogtalks.R;

import java.util.List;

public class Post_Adapter extends RecyclerView.Adapter<Post_Adapter.MyViewVolder> {

    Context mContext;
    List<Post> mData;

    public Post_Adapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public Post_Adapter.MyViewVolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item, parent, false);

        return new MyViewVolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull Post_Adapter.MyViewVolder holder, int position) {

        holder.tvPost_title.setText(mData.get(position).getTitle());
        Glide.with(mContext).load(mData.get(position).getPostImage()).into(holder.ivPostImage);
        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.ivPost_userProfilePhoto);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewVolder extends RecyclerView.ViewHolder {

        TextView tvPost_title;
        ImageView ivPostImage, ivPost_userProfilePhoto;


        public MyViewVolder(@NonNull View itemView) {
            super(itemView);

            tvPost_title = itemView.findViewById(R.id.row_post_title);
            ivPost_userProfilePhoto = itemView.findViewById(R.id.row_post_userImage);
            ivPostImage = itemView.findViewById(R.id.row_post_image);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postDetailsActivity = new Intent(mContext, PostDetailsActivity.class);
                    int position = getAdapterPosition();
                    postDetailsActivity.putExtra("title", mData.get(position).getTitle());
                    postDetailsActivity.putExtra("description", mData.get(position).getDescription());

                    postDetailsActivity.putExtra("postImage", mData.get(position).getPostImage());
                    postDetailsActivity.putExtra("postedByUserPhoto", mData.get(position).getUserPhoto());
                    postDetailsActivity.putExtra("postKey", mData.get(position).getPostKey());

                    long timeStamp = (long) mData.get(position).getTimeStamp();
                    postDetailsActivity.putExtra("timeStamp", timeStamp);
                    postDetailsActivity.putExtra("postedByUserName", mData.get(position).getUserName());

                    mContext.startActivity(postDetailsActivity);


                }
            });
        }
    }
}
