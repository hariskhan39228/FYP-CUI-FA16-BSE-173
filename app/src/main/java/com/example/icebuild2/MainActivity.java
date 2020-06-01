package com.example.icebuild2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;
    private int welcomeFlag=0;
    String CreatorName="";
    String CreatorProfileImage="";
    String UserType="Teacher";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ////////////////////////////////////////////////////////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();
        ////////////////////////////////////////////////////////////////////////////////////////////
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("I.C.E Faculty");
        ////////////////////////////////////////////////////////////////////////////////////////////
        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);
        ////////////////////////////////////////////////////////////////////////////////////////////
        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
        ////////////////////////////////////////////////////////////////////////////////////////////
        if (currentUser != null) {
            rootRef.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {
                        CreatorName = dataSnapshot.child("name").getValue().toString();
                        CreatorProfileImage = dataSnapshot.child("image").getValue().toString();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }else{
            sendUserToLoginActivity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            sendUserToLoginActivity();
        }else{
            verifyUserType();
            Intent intent=getIntent();
            welcomeFlag=intent.getIntExtra("flag",0);
            if(welcomeFlag==0){
                verifyUserExistence();
            }
        }
    }

    public String getUserType(){
        return UserType;
    }

    private void verifyUserType() {
        String currentUserID=mAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("email").exists())){
                    String email=dataSnapshot.child("email").getValue().toString();
                    if(email.contains("-")){
                        sendUserToStudentsActivity();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void verifyUserExistence() {
        String currentUserID=mAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists())){
                        Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                    welcomeFlag=1;
                }else{
                    sendUserToSettingsActivityForcibly();
                    welcomeFlag=1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToSettingsActivityForcibly() {
        Button homeButton=(Button)findViewById(R.id.home_button);
        homeButton.setVisibility(View.INVISIBLE);
        homeButton.setEnabled(false);
        Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("userType","teacher");
        startActivity(intent);
        finish();
    }

    private void sendUserToLoginActivity() {
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void sendUserToStudentsActivity() {
        Intent intent=new Intent(MainActivity.this,StudentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToSettingsActivity() {
        Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
        intent.putExtra("userType","teacher");
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.main_log_out_option){
            mAuth.signOut();
            sendUserToLoginActivity();
        }
        if(item.getItemId()==R.id.main_view_Profile_option){
            sendUserToSettingsActivity();
        }
        if(item.getItemId()==R.id.main_create_board_option){
            requestNewGroup();
        }
        return true;
    }

    private void requestNewGroup() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Board Name and Class: ");

        LinearLayout layout=new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText boardName=new EditText(MainActivity.this);
        boardName.setHint("e.g. Advance OOP");
        layout.addView(boardName);
        final EditText boardClass=new EditText(MainActivity.this);
        boardClass.setHint("e.g. BSE 8C");
        layout.addView(boardClass);
        builder.setView(layout);

        builder.setPositiveButton("Create",null);
        builder.setNegativeButton("Cancel",null);

        final AlertDialog alertDialog=builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveButton=((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String board_name=boardName.getText().toString();
                        String board_class=boardClass.getText().toString().toUpperCase();
                        if(board_name.isEmpty()){
                            boardName.setError("Board name Required!");
                            boardName.requestFocus();
                            return;
                        }
                        if(board_class.isEmpty()){
                            boardClass.setError("Board Class Required!");
                            boardClass.requestFocus();
                            return;
                        }
                        if(!(board_class.length()>=5)){
                            boardClass.setError("Valid Board Class Required!, e.g BSE 8C");
                            boardClass.requestFocus();
                            return;
                        }

                        createNewGroup(board_name,board_class);
                        alertDialog.dismiss();
                    }
                });

                Button negativeButton=((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
            }
        });

     alertDialog.show();
    }

    private void createNewGroup(final String boardName, final String boardClass) {
        Map<String, String > boardValues = new HashMap<>();
        boardValues.put("creator",CreatorName);
        String message="Board "+boardName+" for Class "+boardClass+" created..."+CreatorName+"  "+currentUser.getUid()+CreatorProfileImage;
        boardValues.put("name",boardName);
        boardValues.put("boardClass",boardClass);
        boardValues.put("image",CreatorProfileImage);
        boardValues.put("creator_id",currentUser.getUid());

        Map<String, Object > boardUpdates = new HashMap<>();
        boardUpdates.put(boardName+"-"+boardClass, boardValues);
        rootRef.child("Boards").updateChildren(boardUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String message="Board "+boardName+" for Class "+boardClass+" created..."+CreatorName+"  "+currentUser.getUid()+CreatorProfileImage;
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
