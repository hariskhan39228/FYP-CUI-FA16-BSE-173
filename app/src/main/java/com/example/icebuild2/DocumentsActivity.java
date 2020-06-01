package com.example.icebuild2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class DocumentsActivity extends AppCompatActivity {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Toolbar mToolbar;
    private ImageButton sendMessageButton;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessages;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String currentBoardName, currentUserID, currentUserName, currentDate, currentTime;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef, boardNameRef, boardMessageKeyRef;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        ////////////////////////////////////////////////////////////////////////////////////////////
        currentBoardName=getIntent().getStringExtra("BoardName");
        ////////////////////////////////////////////////////////////////////////////////////////////
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        boardNameRef=FirebaseDatabase.getInstance().getReference().child("Boards").child(currentBoardName);
        ////////////////////////////////////////////////////////////////////////////////////////////
        initializeFields();
        ////////////////////////////////////////////////////////////////////////////////////////////
        getUserInfo();
        ////////////////////////////////////////////////////////////////////////////////////////////
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageIntoDatabase();
                userMessageInput.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        boardNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    displayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    displayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    private void initializeFields() {
        mToolbar=(Toolbar)findViewById(R.id.boards_docs_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentBoardName);
        sendMessageButton=(ImageButton)findViewById(R.id.send_message_button);
        userMessageInput=(EditText)findViewById(R.id.input_board_message);
        displayTextMessages=(TextView)findViewById(R.id.boards_docs_text_display);
        mScrollView=(ScrollView)findViewById(R.id.docs_scroll_view);

    }

    private void getUserInfo() {
        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentUserName=dataSnapshot.child("name").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveMessageIntoDatabase() {
        String message= userMessageInput.getText().toString();
        String messageKEY=boardNameRef.push().getKey();
        if(TextUtils.isEmpty(message)){
            String ToastMessage="Please Enter a message or attach a document!";
            Toast.makeText(DocumentsActivity.this,ToastMessage,Toast.LENGTH_SHORT).show();
        }else{
            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat currentDataFormat= new SimpleDateFormat("MMM dd, yyyy");
            currentDate=currentDataFormat.format(calForDate.getTime());

            Calendar calForTime=Calendar.getInstance();
            SimpleDateFormat currentTimeFormat= new SimpleDateFormat("hh:mm a");
            currentTime=currentTimeFormat.format(calForTime.getTime());

            HashMap<String ,Object> boardsMessageKey=new HashMap<>();
            boardNameRef.updateChildren(boardsMessageKey);
            boardMessageKeyRef=boardNameRef.child(messageKEY);

            HashMap<String ,Object> messageInfoMap=new HashMap<>();
                messageInfoMap.put("name",currentUserName);
                messageInfoMap.put("message",message);
                messageInfoMap.put("date",currentDate);
                messageInfoMap.put("time",currentTime);
            boardMessageKeyRef.updateChildren(messageInfoMap);
        }
    }

    private void displayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator=dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()){
            String chatDate=(String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage=(String) ((DataSnapshot)iterator.next()).getValue();
            String chatName=(String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime=(String) ((DataSnapshot)iterator.next()).getValue();

            displayTextMessages.append(chatName+" : \n" + chatMessage + "\n"+ chatTime+"     "+chatDate+ "\n\n\n");

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

}
