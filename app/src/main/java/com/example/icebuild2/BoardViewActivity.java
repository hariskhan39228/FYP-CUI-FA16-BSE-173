package com.example.icebuild2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BoardViewActivity extends AppCompatActivity {

    private String receiverBoardID, currentState , senderUserID, recieverUserID;


    private CircleImageView boardProfileImage;
    private TextView boardName, boardClass, boardTeacher;
    private Button sendJoinRequestButton;

    private DatabaseReference boardsRef, NotificationRef, joinRequestsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_view);
        ////////////////////////////////////////////////////////////////////////////////////////////
        mAuth=FirebaseAuth.getInstance();
        boardsRef= FirebaseDatabase.getInstance().getReference().child("Boards");
        NotificationRef= FirebaseDatabase.getInstance().getReference().child("Notifications");
        joinRequestsRef= FirebaseDatabase.getInstance().getReference().child("Join Requests");
        ////////////////////////////////////////////////////////////////////////////////////////////
        receiverBoardID =getIntent().getStringExtra("visit_board_id");
        senderUserID = mAuth.getCurrentUser().getUid();
        ////////////////////////////////////////////////////////////////////////////////////////////
        boardsRef.child(receiverBoardID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    recieverUserID= dataSnapshot.child("creator_id").getValue().toString();
                    Toast.makeText(BoardViewActivity.this, "Check 5:"+recieverUserID, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////
        boardProfileImage=(CircleImageView)findViewById(R.id.visit_profile_image);
        boardName=(TextView)findViewById(R.id.visit_board_name);
        boardClass=(TextView)findViewById(R.id.visit_class_name);
        boardTeacher=(TextView)findViewById(R.id.visit_teacher_name);
        sendJoinRequestButton=(Button)findViewById(R.id.send_join_request_button);
        currentState="new";
        ////////////////////////////////////////////////////////////////////////////////////////////
        retrieveBoardInfo();
        //Toast.makeText(BoardViewActivity.this, "Check 4:"+recieverUserID, Toast.LENGTH_SHORT).show();
    }


    private void retrieveBoardInfo() {
        boardsRef.child(receiverBoardID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists())&&(dataSnapshot.hasChild("image"))){

                    String retrievedProfileImage=dataSnapshot.child("image").getValue().toString();
                    String retrievedBoardName = dataSnapshot.child("name").getValue().toString();
                    String retrievedBoardClass = dataSnapshot.child("boardClass").getValue().toString();
                    String retrievedBoardTeacher = dataSnapshot.child("creator").getValue().toString();
                    recieverUserID = dataSnapshot.child("creator_id").getValue().toString();

                    Picasso.with(BoardViewActivity.this).load(retrievedProfileImage).placeholder(R.drawable.profile_image).into(boardProfileImage);
                    boardName.setText(retrievedBoardName);
                    boardClass.setText("Class : "+retrievedBoardClass);
                    boardTeacher.setText("Teacher : "+retrievedBoardTeacher);

                    ManageGroupRequests();
                }else{
                    String retrievedBoardName = dataSnapshot.child("name").getValue().toString();
                    String retrievedBoardClass = dataSnapshot.child("boardClass").getValue().toString();
                    String retrievedBoardTeacher = dataSnapshot.child("creator").getValue().toString();
                    recieverUserID = dataSnapshot.child("creator_id").getValue().toString();

                    boardName.setText(retrievedBoardName);
                    boardClass.setText("Class : "+retrievedBoardClass);
                    boardTeacher.setText("Teacher : "+retrievedBoardTeacher);

                    ManageGroupRequests();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        joinRequestsRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(recieverUserID)){
                    String request_type=dataSnapshot.child(recieverUserID).child("request_type").getValue().toString();
                    String board=dataSnapshot.child(recieverUserID).child("board").getValue().toString();
                    if(request_type.equals("sent")&& board.equals(receiverBoardID)){
                        currentState="request_sent";
                        sendJoinRequestButton.setText("Cancel join Request");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ManageGroupRequests() {
        joinRequestsRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(recieverUserID)){
                    String request_type=dataSnapshot.child(recieverUserID).child("request_type").getValue().toString();
                    String board=dataSnapshot.child(recieverUserID).child("board").getValue().toString();
                    if(request_type.equals("sent")&& board.equals(receiverBoardID)){
                        currentState="request_sent";
                        sendJoinRequestButton.setText("Cancel join Request");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(BoardViewActivity.this, "Check 4:"+recieverUserID, Toast.LENGTH_SHORT).show();
        if(!senderUserID.equals(recieverUserID)){
            sendJoinRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendJoinRequestButton.setEnabled(false);
                    if(currentState.equals("new")){
                        sendJoinRequest();
                    }
                    if(currentState.equals("request_sent")){
                        cancelJoinRequest();
                    }
                }
            });
        }else{
            sendJoinRequestButton.setVisibility(View.INVISIBLE);
        }

    }

    private void sendJoinRequest() {
        Toast.makeText(this, receiverBoardID, Toast.LENGTH_SHORT).show();
        Map<String, String > requestValues = new HashMap<>();
        requestValues.put("request_type","sent");
        requestValues.put("board",receiverBoardID);
        Map<String, Object > requestUpdates = new HashMap<>();
        requestUpdates.put(recieverUserID, requestValues);

        joinRequestsRef.child(senderUserID).child(receiverBoardID).updateChildren(requestUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Map<String, String > requestValues = new HashMap<>();
                requestValues.put("request_type","received");
                requestValues.put("board",receiverBoardID);
                Map<String, Object > requestUpdates = new HashMap<>();
                requestUpdates.put(senderUserID, requestValues);


                joinRequestsRef.child(recieverUserID).child(receiverBoardID).updateChildren(requestUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        HashMap <String,String> BoardJoinNotificationMap=new HashMap<>();
                        BoardJoinNotificationMap.put("from",senderUserID);
                        BoardJoinNotificationMap.put("type","request");
                        NotificationRef.child(recieverUserID).push().setValue(BoardJoinNotificationMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                sendJoinRequestButton.setEnabled(true);
                                currentState="request_sent";
                                sendJoinRequestButton.setText("Cancel join Request");
                            }
                        });


                    }
                });
            }
        });
    }

    private void cancelJoinRequest() {
        joinRequestsRef.child(senderUserID).child(recieverUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                joinRequestsRef.child(recieverUserID).child(senderUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendJoinRequestButton.setEnabled(true);
                        currentState="new";
                        sendJoinRequestButton.setText("Join Board");
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(BoardViewActivity.this,FindBoardsActivity.class);
        this.startActivity(intent);
        finish();
    }
}
