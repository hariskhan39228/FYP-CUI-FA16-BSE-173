package com.example.icebuild2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar settingsToolbar;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Button updateAccountSettings, homeButton;
    private EditText userName;
    private CircleImageView userProfileImage;
    String UserType="";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private StorageReference userProfileImageRef;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int GalleryPickUp =1;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ////////////////////////////////////////////////////////////////////////////////////////////
        UserType=getIntent().getStringExtra("userType");
        ////////////////////////////////////////////////////////////////////////////////////////////
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        rootRef= FirebaseDatabase.getInstance().getReference();
        userProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile Images");
        ////////////////////////////////////////////////////////////////////////////////////////////
        initializeFields();
        ////////////////////////////////////////////////////////////////////////////////////////////
        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkToHome();
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////
        retrieveUserInfo();
        ////////////////////////////////////////////////////////////////////////////////////////////
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPickUp);

            }
        });
    }

    private void initializeFields() {
        settingsToolbar=(Toolbar)findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");
        updateAccountSettings=(Button)findViewById(R.id.update_settings_button);
        homeButton=(Button)findViewById(R.id.home_button);
        userName=(EditText)findViewById(R.id.set_user_name);
        userProfileImage=(CircleImageView)findViewById(R.id.set_profile_image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== GalleryPickUp && resultCode==RESULT_OK && data!=null){
            Uri imageUri=data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == this.RESULT_OK){
                Uri resultUri=result.getUri();
                final StorageReference filePath=userProfileImageRef.child(currentUserID + ".jpg");
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Profile image uploaded successfully...", Toast.LENGTH_SHORT).show();
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                final String downloadUrl = uri.toString();
                                rootRef.child("Users").child(currentUserID).child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(SettingsActivity.this, "Image saved in DataBase successfully...", Toast.LENGTH_SHORT).show();

                                        }else{
                                            String message=task.getException().toString();
                                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
            }
        }
    }

    private void updateSettings() {
        String setUserName= userName.getText().toString();
        if(setUserName.isEmpty()){
            userName.setError("UserName Required!");
            userName.requestFocus();
            return;
        }else{
            HashMap<String,Object> profileMap=new HashMap<>();
            profileMap.put("uid",currentUserID);
            profileMap.put("name",setUserName);
            rootRef.child("Users").child(currentUserID).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SettingsActivity.this,"Profile Updated Successfully!",Toast.LENGTH_SHORT).show();
                        if(UserType.equalsIgnoreCase("student")){
                            sendUserToStudentActivity();
                        }else{
                            sendUserToTeacherActivity();
                        }

                    }else{
                        String message=task.getException().getMessage();
                        Toast.makeText(SettingsActivity.this,"Error: "+message,Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void checkToHome() {
        String currentUserID=mAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists())){
                    if(UserType.equalsIgnoreCase("student")){
                        sendUserToStudentActivity();
                    }else{
                        sendUserToTeacherActivity();
                    }
                }else{
                    String message="Please set and update your Profile info first...";
                    Toast.makeText(SettingsActivity.this,message,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToStudentActivity() {
        Intent studentIntent=new Intent(SettingsActivity.this, StudentActivity.class);
        studentIntent.putExtra("flag",1);
        startActivity(studentIntent);
    }

    private void sendUserToTeacherActivity() {
        Intent teacherIntent=new Intent(SettingsActivity.this, MainActivity.class);
        teacherIntent.putExtra("flag",1);
        startActivity(teacherIntent);
    }

    private void retrieveUserInfo() {
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name"))&&(dataSnapshot.hasChild("image"))){
                    String retrievedUserName = dataSnapshot.child("name").getValue().toString();
                    String retrievedProfileImage=dataSnapshot.child("image").getValue().toString();
                    userName.setText(retrievedUserName);
                    Picasso.get().load(retrievedProfileImage).into(userProfileImage);

                }else if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name"))){
                    String retrievedUserName = dataSnapshot.child("name").getValue().toString();
                    userName.setText(retrievedUserName);
                }else{
                    Toast.makeText(SettingsActivity.this,"Please set & update you profile information...",Toast.LENGTH_SHORT).show();
                    homeButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
