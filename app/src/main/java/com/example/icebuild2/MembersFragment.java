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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MembersFragment extends Fragment {
    private View membersView;
    private RecyclerView mMembersList;
    private DatabaseReference membersRef, usersRef;
    private String currentBoardName;


    public MembersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        membersView =inflater.inflate(R.layout.fragment_members, container, false);
        mMembersList=(RecyclerView)membersView.findViewById(R.id.members_list);
        mMembersList.setLayoutManager(new LinearLayoutManager(getContext()));
        currentBoardName = getActivity().getIntent().getStringExtra("BoardName");
        membersRef= FirebaseDatabase.getInstance().getReference().child("Board Members").child(currentBoardName).child("members");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        return membersView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(membersRef,Users.class)
                .build();

        FirebaseRecyclerAdapter<Users, MembersViewHolder> adapter=new FirebaseRecyclerAdapter<Users, MembersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MembersViewHolder holder, int position, @NonNull Users users) {
                String userIDs=getRef(position).getKey();
                { /*usersRef.child(userIDs).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        final String memberUserName = dataSnapshot.child("name").getValue().toString();
                        final String memberUserEmail = dataSnapshot.child("email").getValue().toString();
                        final String memberUserReg = memberUserEmail.substring(0, 12);

                        holder.memberName.setText(memberUserName);
                        holder.memberReg.setText(memberUserReg);
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
                });*/}
                usersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("image")) {
                            final String memberUserImage = dataSnapshot.child("image").getValue().toString();
                            Picasso.with(getContext()).load(memberUserImage).into(holder.memberProfileImage);
                        }
                        final String memberUserName = dataSnapshot.child("name").getValue().toString();
                        final String memberUserEmail = dataSnapshot.child("email").getValue().toString();
                        final String memberUserReg = memberUserEmail.substring(0, 12);

                        holder.memberName.setText(memberUserName);
                        holder.memberReg.setText(memberUserReg);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_request_display_layout, parent, false);
                MembersViewHolder holder = new MembersViewHolder(view);
                return holder;
            }
        };
        mMembersList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder {

        TextView memberName, memberReg;
        CircleImageView memberProfileImage;


        public MembersViewHolder(@NonNull View itemView) {
            super(itemView);

            memberName = itemView.findViewById(R.id.requesting_user_profile_name);
            memberReg = itemView.findViewById(R.id.requesting_user_registration_number);
            memberProfileImage = itemView.findViewById(R.id.requesting_user_profile_image);

        }
    }
}
