package com.example.icebuild2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentActivity extends AppCompatActivity {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    String CreatorName="";
    String CreatorProfileImage="";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        ////////////////////////////////////////////////////////////////////////////////////////////
        mAuth=FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();
        currentUser=mAuth.getCurrentUser();
        ////////////////////////////////////////////////////////////////////////////////////////////
        mToolbar=(Toolbar)findViewById(R.id.student_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("I.C.E Students");
        ////////////////////////////////////////////////////////////////////////////////////////////
        myViewPager=(ViewPager)findViewById(R.id.student_tabs_pager);
        myTabsAccessorAdapter=new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);
        ////////////////////////////////////////////////////////////////////////////////////////////
        myTabLayout=(TabLayout)findViewById(R.id.student_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
        ////////////////////////////////////////////////////////////////////////////////////////////
        if (currentUser == null) {
            sendUserToLoginActivity();
        }else{
            verifyUserExistence();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            sendUserToLoginActivity();
        }else{
            verifyUserExistence();
        }
    }

    private void verifyUserExistence() {
        String currentUserID=mAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists())){
                    Toast.makeText(StudentActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }else{
                    sendUserToSettingsActivityForcibly();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToSettingsActivityForcibly() {
        Intent intent=new Intent(StudentActivity.this,SettingsActivity.class);
        intent.putExtra("userType","student");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToLoginActivity() {
        Intent intent=new Intent(StudentActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToSettingsActivity() {
        Intent intent=new Intent(StudentActivity.this,SettingsActivity.class);
        intent.putExtra("userType","student");
        startActivity(intent);
        finish();
    }

    private void sendUserToFindBoardsActivity() {
        Intent intent=new Intent(StudentActivity.this,FindBoardsActivity.class);
        intent.putExtra("userType","student");
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu_students, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.student_log_out_option){
            mAuth.signOut();
            sendUserToLoginActivity();
        }
        if(item.getItemId()==R.id.student_view_Profile_option){
            sendUserToSettingsActivity();
        }
        if(item.getItemId()==R.id.student_find_boards_option){
            sendUserToFindBoardsActivity();
        }
        return true;
    }
}
