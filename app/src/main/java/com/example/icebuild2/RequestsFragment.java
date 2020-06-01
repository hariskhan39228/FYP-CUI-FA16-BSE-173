package com.example.icebuild2;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {
    private View requestFragmentView;
    private RecyclerView mJoinRequests;
    private DatabaseReference joinRequestsRef, usersRef, boardsRef, boardMembersRef;
    private FirebaseAuth mAuth;
    private String currentUserID, currentBoardName,currentDate, currentTime;;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requestFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        currentBoardName = getActivity().getIntent().getStringExtra("BoardName");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        boardsRef = FirebaseDatabase.getInstance().getReference().child("Boards");
        boardMembersRef= FirebaseDatabase.getInstance().getReference().child("Board Members");
        joinRequestsRef = FirebaseDatabase.getInstance().getReference().child("Join Requests");
        mJoinRequests = (RecyclerView) requestFragmentView.findViewById(R.id.join_requests_list);
        mJoinRequests.setLayoutManager(new LinearLayoutManager(getContext()));

        return requestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(joinRequestsRef.child(currentUserID).child(currentBoardName), Users.class)
                .build();

        FirebaseRecyclerAdapter<Users, RequestsViewHolder> adapter = new FirebaseRecyclerAdapter<Users, RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull Users users) {
                final String listUserID = getRef(position).getKey();
                DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String requestType = dataSnapshot.getValue().toString();
                            if (requestType.equals("received")) {
                                usersRef.child(listUserID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("image")) {
                                            final String requestUserImage = dataSnapshot.child("image").getValue().toString();
                                            Picasso.get().load(requestUserImage).into(holder.profileImage);
                                        }
                                        final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                        final String requestUserEmail = dataSnapshot.child("email").getValue().toString();
                                        final String requestUserReg = requestUserEmail.substring(0, 12);

                                        holder.userName.setText(requestUserName);
                                        holder.userReg.setText(requestUserReg);
                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence options[] = new CharSequence[]{
                                                        "Accept",
                                                        "Decline"
                                                };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle(currentBoardName + " Join Request by" + requestUserReg);
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (which == 0) {
                                                            Calendar calForDate=Calendar.getInstance();
                                                            SimpleDateFormat currentDataFormat= new SimpleDateFormat("MMM dd, yyyy");
                                                            currentDate=currentDataFormat.format(calForDate.getTime());

                                                            Calendar calForTime=Calendar.getInstance();
                                                            SimpleDateFormat currentTimeFormat= new SimpleDateFormat("hh:mm a");
                                                            currentTime=currentTimeFormat.format(calForTime.getTime());
                                                            HashMap<String ,Object> boardMemberJoinData=new HashMap<>();
                                                            boardMemberJoinData.put("date joined",currentDate);
                                                            boardMemberJoinData.put("time",currentTime);

                                                            boardMembersRef.child(currentBoardName).child("members").child(listUserID).updateChildren(boardMemberJoinData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    joinRequestsRef.child(currentUserID).child(currentBoardName).child(listUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            joinRequestsRef.child(listUserID).child(currentBoardName).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Toast.makeText(getContext(), "Member added to " + currentBoardName, Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                        if (which == 1) {
                                                            joinRequestsRef.child(currentUserID).child(currentBoardName).child(listUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    joinRequestsRef.child(listUserID).child(currentBoardName).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(getContext(), "Request Deleted!", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_request_display_layout, parent, false);
                RequestsViewHolder holder = new RequestsViewHolder(view);
                return holder;
            }
        };
        mJoinRequests.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userReg;
        CircleImageView profileImage;


        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.requesting_user_profile_name);
            userReg = itemView.findViewById(R.id.requesting_user_registration_number);
            profileImage = itemView.findViewById(R.id.requesting_user_profile_image);

        }
    }
}
