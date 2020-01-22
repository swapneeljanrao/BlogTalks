package com.mrcoder.blogtalks.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.Gravity;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mrcoder.blogtalks.Models.Post;
import com.mrcoder.blogtalks.R;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends AppCompatActivity {

    private static final int REQUESTCODE = 2;
    private static final int PReqCode = 2;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Dialog popupadd_newPost;
    ProgressBar new_blogpost_popup_Progress;
    ImageView new_blogpost_popup_userImage, new_blogpost_popup_postImage, new_blogpost_popup_newPostbtn;
    EditText new_blogpost_popup_title, new_blogpost_popup_description;
    private AppBarConfiguration mAppBarConfiguration;
    private Uri pickedImgURI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        iniPopup();

        setupPopupImageClick();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupadd_newPost.show();

            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile,
                R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        updateNavheader();
    }

    private void setupPopupImageClick() {
        new_blogpost_popup_postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestforPermission();
            }
        });
    }

    private void checkAndRequestforPermission() {
        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(Home.this, "Permissions are needed to continue", Toast.LENGTH_SHORT).show();
            } else {

                ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);

            }
        } else {
            openGallery();
        }

    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE);
    }

    private void iniPopup() {
        popupadd_newPost = new Dialog(this);
        popupadd_newPost.setContentView(R.layout.new_blogpost_popup);
        popupadd_newPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupadd_newPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popupadd_newPost.getWindow().getAttributes().gravity = Gravity.TOP;

        //new blogpost popup ImageViews
        new_blogpost_popup_userImage = popupadd_newPost.findViewById(R.id.new_blogpost_userPhoto);
        new_blogpost_popup_postImage = popupadd_newPost.findViewById(R.id.new_blogpost_imagePost);
        new_blogpost_popup_newPostbtn = popupadd_newPost.findViewById(R.id.new_blogpost_editblog);

        //new blogpost popup textViews
        new_blogpost_popup_title = popupadd_newPost.findViewById(R.id.new_blogpost_edt_title);
        new_blogpost_popup_description = popupadd_newPost.findViewById(R.id.new_blogpost_edt_description);

        //new blogpost popup progress dialog
        new_blogpost_popup_Progress = popupadd_newPost.findViewById(R.id.new_blogpost_progressbar);

        Glide.with(this).load(currentUser.getPhotoUrl()).into(new_blogpost_popup_userImage);

        new_blogpost_popup_newPostbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_blogpost_popup_newPostbtn.setVisibility(View.INVISIBLE);
                new_blogpost_popup_Progress.setVisibility(View.VISIBLE);

                if (!new_blogpost_popup_title.getText().toString().trim().isEmpty() &&
                        !new_blogpost_popup_description.getText().toString().trim().isEmpty() &&
                        pickedImgURI != null) {

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                    final StorageReference imagefilePath = storageReference.child(pickedImgURI.getLastPathSegment());
                    imagefilePath.putFile(pickedImgURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagefilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownloadLink = uri.toString();
                                    Post post = new Post(new_blogpost_popup_title.getText().toString(), new_blogpost_popup_description.getText().toString(), imageDownloadLink, currentUser.getUid(), currentUser.getPhotoUrl().toString());

                                    addPost(post);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showMessage(e.getMessage());
                                    new_blogpost_popup_Progress.setVisibility(View.INVISIBLE);
                                    new_blogpost_popup_newPostbtn.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                } else {
                    showMessage("No field should be empty");

                }
            }
        });
    }

    private void addPost(Post post) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("Posts").push();

        //get unique post id and update post key
        String key = myRef.getKey();
        post.setPostKey(key);


        // add post to firebase database
        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("New Post Created");
                new_blogpost_popup_newPostbtn.setVisibility(View.VISIBLE);
                new_blogpost_popup_Progress.setVisibility(View.INVISIBLE);
                popupadd_newPost.dismiss();
            }
        });


    }

    private void showMessage(String message) {
        Toast.makeText(Home.this, message, Toast.LENGTH_LONG).show();
        new_blogpost_popup_newPostbtn.setVisibility(View.VISIBLE);
        new_blogpost_popup_Progress.setVisibility(View.INVISIBLE);
    }

    public void signOut() {
        Intent signoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(signoutIntent);
        Toast.makeText(this, "You are currently Signed out. Login again to go back", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void updateNavheader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navHeader_userName = headerView.findViewById(R.id.nav_Username);
        TextView navHeader_userEmail = headerView.findViewById(R.id.nav_userEmail);
        ImageView navHeader_profilePhoto = headerView.findViewById(R.id.nav_Profile_photo);

        navHeader_userName.setText(currentUser.getDisplayName());
        navHeader_userEmail.setText(currentUser.getEmail());

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navHeader_profilePhoto);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {
            pickedImgURI = data.getData();
            new_blogpost_popup_postImage.setImageURI(pickedImgURI);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            signOut();
        }
    }

}