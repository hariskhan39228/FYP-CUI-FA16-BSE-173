package com.example.icebuild2;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DocumentsFragment extends Fragment {
    private View DocumentsFragmentView;
    private ImageButton sendMessageButton;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessages;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String currentBoardName, currentUserID, currentUserName, currentDate, currentTime;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef, boardNameRef, boardMessageKeyRef, rootRef;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView usermessageList;
    ////////////////////////////////////////////////////////////////////////////////////////////////


    public DocumentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DocumentsFragmentView=inflater.inflate(R.layout.fragment_documents, container, false);
        ////////////////////////////////////////////////////////////////////////////////////////////
        currentBoardName=getActivity().getIntent().getStringExtra("BoardName");
        mAuth= FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        currentUserID=mAuth.getCurrentUser().getUid();
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        boardNameRef=FirebaseDatabase.getInstance().getReference().child("Board Documents").child(currentBoardName);
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
                //mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        return DocumentsFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(getContext(), "check 1 into onStart", Toast.LENGTH_SHORT).show();
        rootRef.child("Board Documents").child(currentBoardName).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Toast.makeText(getContext(), "check 2 into onStart", Toast.LENGTH_SHORT).show();
                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);

                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
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
        sendMessageButton=(ImageButton)DocumentsFragmentView.findViewById(R.id.send_message_btn);
        userMessageInput=(EditText)DocumentsFragmentView.findViewById(R.id.input_message);
        displayTextMessages=(TextView)DocumentsFragmentView.findViewById(R.id.boards_docs_text_display);
        mScrollView=(ScrollView)DocumentsFragmentView.findViewById(R.id.docs_scroll_view);

        messageAdapter = new MessageAdapter(messagesList);
        usermessageList = (RecyclerView)DocumentsFragmentView.findViewById(R.id.board_messages_list);
        linearLayoutManager = new LinearLayoutManager(getContext());
        usermessageList.setAdapter(messageAdapter);

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
        String messageKEY=boardNameRef.child("messages").push().getKey();
        if(message.length()==0){
            userMessageInput.setError("The message cannot be empty!");
            userMessageInput.requestFocus();
            return;
        }else{
            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat currentDataFormat= new SimpleDateFormat("MMM dd, yyyy");
            currentDate=currentDataFormat.format(calForDate.getTime());

            Calendar calForTime=Calendar.getInstance();
            SimpleDateFormat currentTimeFormat= new SimpleDateFormat("hh:mm a");
            currentTime=currentTimeFormat.format(calForTime.getTime());

            HashMap<String ,Object> boardsMessageKey=new HashMap<>();
            boardNameRef.child("messages").updateChildren(boardsMessageKey);
            boardMessageKeyRef=boardNameRef.child("messages").child(messageKEY);

            HashMap<String ,Object> messageInfoMap=new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);
            messageInfoMap.put("type","text");
            messageInfoMap.put("from",currentUserID);
            boardMessageKeyRef.updateChildren(messageInfoMap);
            userMessageInput.setText("");
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
