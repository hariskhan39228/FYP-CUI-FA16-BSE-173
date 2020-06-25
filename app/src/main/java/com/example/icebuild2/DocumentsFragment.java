package com.example.icebuild2;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

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
    private ImageButton sendMessageButton, sendFilesButton;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessages;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String currentBoardName, currentUserID, currentUserName, currentDate, currentTime, checker="", myUrl="";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef, boardNameRef, boardMessageKeyRef, rootRef;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView usermessageList;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Uri fileUri;
    private StorageTask uploadTask;
    private ProgressDialog loadingBar;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public DocumentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DocumentsFragmentView=inflater.inflate(R.layout.fragment_documents, container, false);
        initializeFields();
        ////////////////////////////////////////////////////////////////////////////////////////////
        currentBoardName=getActivity().getIntent().getStringExtra("BoardName");
        mAuth= FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        currentUserID=mAuth.getCurrentUser().getUid();
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        boardNameRef=FirebaseDatabase.getInstance().getReference().child("Board Documents").child(currentBoardName);
        ////////////////////////////////////////////////////////////////////////////////////////////

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

        sendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]
                        {
                            "Images",
                            "PDF files",
                            "MS Word Files"
                        };
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("Select file Type:");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            checker="image";
                            Intent intent=new Intent();
                            intent.setAction(intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Select Image"),438);
                        }
                        if(which==1){
                            checker="pdf";
                            Intent intent=new Intent();
                            intent.setAction(intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent, "Select Pdf File"),438);
                        }
                        if(which==2){
                            checker="docx";
                            Intent intent=new Intent();
                            intent.setAction(intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(intent.createChooser(intent, "Select Word File"),438);
                        }
                    }
                });
                builder.show();
            }
        });
        return DocumentsFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        rootRef.child("Board Documents").child(currentBoardName).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
                usermessageList.smoothScrollToPosition(usermessageList.getAdapter().getItemCount());
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
        sendFilesButton=(ImageButton)DocumentsFragmentView.findViewById(R.id.send_files_btn);
        userMessageInput=(EditText)DocumentsFragmentView.findViewById(R.id.input_message);
        displayTextMessages=(TextView)DocumentsFragmentView.findViewById(R.id.boards_docs_text_display);
        mScrollView=(ScrollView)DocumentsFragmentView.findViewById(R.id.docs_scroll_view);

        usermessageList = (RecyclerView)DocumentsFragmentView.findViewById(R.id.board_messages_list);
        messageAdapter = new MessageAdapter(messagesList);
        linearLayoutManager = new LinearLayoutManager(getContext());
        usermessageList.setLayoutManager(linearLayoutManager);
        usermessageList.setAdapter(messageAdapter);

        loadingBar=new ProgressDialog(getContext());

        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat currentDataFormat= new SimpleDateFormat("MMM dd, yyyy");
        currentDate=currentDataFormat.format(calForDate.getTime());

        Calendar calForTime=Calendar.getInstance();
        SimpleDateFormat currentTimeFormat= new SimpleDateFormat("hh:mm a");
        currentTime=currentTimeFormat.format(calForTime.getTime());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==438 && resultCode== Activity.RESULT_OK && data!=null && data.getData()!= null){
            loadingBar.setTitle("Sending File.");
            loadingBar.setMessage("Please wait while the file is being sent..");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            fileUri=data.getData();
            if(!checker.equals("image")){
                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Document Files");

                final String messageKEY=boardNameRef.child("messages").push().getKey();
                boardMessageKeyRef=boardNameRef.child("messages").child(messageKEY);
                final StorageReference filePath = storageReference.child(messageKEY + "." + checker);

                filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();
                                    HashMap<String ,Object> boardsMessageKey=new HashMap<>();
                                    boardNameRef.child("messages").updateChildren(boardsMessageKey);
                                    boardMessageKeyRef=boardNameRef.child("messages").child(messageKEY);

                                    HashMap<String ,Object> messageInfoMap=new HashMap<>();
                                    messageInfoMap.put("name",fileUri.getLastPathSegment());
                                    messageInfoMap.put("message",downloadUrl);
                                    messageInfoMap.put("date",currentDate);
                                    messageInfoMap.put("time",currentTime);
                                    messageInfoMap.put("type",checker);
                                    messageInfoMap.put("from",currentUserID);
                                    messageInfoMap.put("messageID",messageKEY);

                                    boardMessageKeyRef.updateChildren(messageInfoMap);
                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingBar.dismiss();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        loadingBar.setMessage((int)progress+ " % uploaded...");

                        if(progress==100){
                            loadingBar.dismiss();
                        }
                    }
                });

            }else if(checker.equals("image")){
                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Image Files");

                final String messageKEY=boardNameRef.child("messages").push().getKey();
                boardMessageKeyRef=boardNameRef.child("messages").child(messageKEY);
                final StorageReference filePath = storageReference.child(messageKEY + "." + "jpg");
                uploadTask = filePath.putFile(fileUri);

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task <Uri>task) {
                        if(task.isSuccessful()){
                            Uri downloadUrl=task.getResult();
                            myUrl=downloadUrl.toString();

                            HashMap<String ,Object> boardsMessageKey=new HashMap<>();
                            boardNameRef.child("messages").updateChildren(boardsMessageKey);
                            boardMessageKeyRef=boardNameRef.child("messages").child(messageKEY);

                            HashMap<String ,Object> messageInfoMap=new HashMap<>();
                            messageInfoMap.put("name",fileUri.getLastPathSegment());
                            messageInfoMap.put("message",myUrl);
                            messageInfoMap.put("date",currentDate);
                            messageInfoMap.put("time",currentTime);
                            messageInfoMap.put("type",checker);
                            messageInfoMap.put("from",currentUserID);
                            messageInfoMap.put("messageID",messageKEY);

                            boardMessageKeyRef.updateChildren(messageInfoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        loadingBar.dismiss();
                                        Toast.makeText(getContext(), "File sent.", Toast.LENGTH_SHORT).show();
                                    }else {
                                        loadingBar.dismiss();
                                        Toast.makeText(getContext(), "Some error Occurred.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            userMessageInput.setText("");
                        }
                    }
                });

            }else{
                loadingBar.dismiss();
                Toast.makeText(getContext(), "Error : Nothing Selected", Toast.LENGTH_SHORT).show();
            }
        }
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
            messageInfoMap.put("messageID",messageKEY);

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
