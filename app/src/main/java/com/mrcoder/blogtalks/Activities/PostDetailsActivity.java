package com.mrcoder.blogtalks.Activities;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mrcoder.blogtalks.Adapters.Comments_Adapter;
import com.mrcoder.blogtalks.Models.Comments;
import com.mrcoder.blogtalks.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PostDetailsActivity extends AppCompatActivity {

    //static String COMMENT_KEY = "Comments";
    ImageView postDetails_ImgView_postImage, postDetails_ImgView_current_userImage, postDetails_ImgView_postedByUserPhoto;
    TextView postDetails_txtView_title, postDetails_txtView_description, postDetails_txtView_postedBy_name_date;
    EditText postDetails_edtText_commentBox;
    Button postDetails_btn_addComment;
    RecyclerView commentRecycler;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebasecurrentUser;
    Comments_Adapter comments_adapter;
    List<Comments> commentsList;
    String PostKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();

        postDetails_ImgView_postImage = findViewById(R.id.postDetails_postImage);
        postDetails_ImgView_current_userImage = findViewById(R.id.postDetails_commentUserPhoto);
        postDetails_ImgView_postedByUserPhoto = findViewById(R.id.postDetails_currentUserImage);

        postDetails_txtView_title = findViewById(R.id.postDetails_title);
        postDetails_txtView_description = findViewById(R.id.postDetails_Description);
        postDetails_txtView_postedBy_name_date = findViewById(R.id.postDetails_postedByName_Date);

        postDetails_edtText_commentBox = findViewById(R.id.postDetails_commentBox);
        postDetails_btn_addComment = findViewById(R.id.postDetails_btn_addComment);

        commentRecycler = findViewById(R.id.postDeatils_commentContent);

        //commentRecycler.hasFixedSize();

        firebaseAuth = FirebaseAuth.getInstance();
        firebasecurrentUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        String postTitle = getIntent().getExtras().getString("title");
        String postDescription = getIntent().getExtras().getString("description");
        String postImage = getIntent().getExtras().getString("postImage");
        String postedByUserPhoto = getIntent().getExtras().getString("postedByUserPhoto");
        String postedByUserName = firebasecurrentUser.getDisplayName();
        String timeStamp = getIntent().getExtras().getString("timeStamp");
        PostKey = getIntent().getExtras().getString("postKey");

        postDetails_txtView_title.setText(postTitle);
        postDetails_txtView_description.setText(postDescription);
        postDetails_txtView_postedBy_name_date.setText(timeStamp);


        postDetails_btn_addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDetails_btn_addComment.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = firebaseDatabase.getReference("Comments").child(PostKey).push();
                String Comment_content = postDetails_edtText_commentBox.getText().toString();
                String uid = firebasecurrentUser.getUid();
                String uname = firebasecurrentUser.getDisplayName();
                String uimg = firebasecurrentUser.getPhotoUrl().toString();
                Comments comments = new Comments(Comment_content, uid, uimg, uname);

                commentReference.setValue(comments).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        showMessage("Comment Added");
                        postDetails_edtText_commentBox.setText("");
                        postDetails_btn_addComment.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Comment Failed : " + e.getMessage());
                    }
                });
            }
        });

        //user who posted the post
        Glide.with(this).load(postedByUserPhoto).into(postDetails_ImgView_postedByUserPhoto);

        //currently logged in user, that is nothing but who will post comment


        Glide.with(this).load(firebasecurrentUser.getPhotoUrl()).into(postDetails_ImgView_current_userImage);

        //the post/Image posted by the user
        Glide.with(this).load(postImage).into(postDetails_ImgView_postImage);
        String postDate = timeStampToString(getIntent().getExtras().getLong("timeStamp"));
        postDetails_txtView_postedBy_name_date.setText(postedByUserName + "\n" + postDate);

        iniRVComment();
    }

    private void iniRVComment() {
        commentRecycler.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference commentRef = firebaseDatabase.getReference("Comments").child(PostKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Comments comments = snap.getValue(Comments.class);
                    commentsList.add(comments);
                }
                comments_adapter = new Comments_Adapter(getApplicationContext(), commentsList);
                commentRecycler.setAdapter(comments_adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private String timeStampToString(Long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;
    }
}
