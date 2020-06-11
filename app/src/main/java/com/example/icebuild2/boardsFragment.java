package com.example.icebuild2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class boardsFragment extends Fragment {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private View boardsFragmentView;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_Boards= new ArrayList<>();
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private DatabaseReference boardRef;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private DatabaseReference boardMembersRef;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    String UserType="";
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boardsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ////////////////////////////////////////////////////////////////////////////////////////////
        Toast.makeText(getContext(), "Check 0 : "+UserType, Toast.LENGTH_SHORT).show();
        ////////////////////////////////////////////////////////////////////////////////////////////
        boardsFragmentView= inflater.inflate(R.layout.fragment_boards, container, false);
        ////////////////////////////////////////////////////////////////////////////////////////////
        boardRef= FirebaseDatabase.getInstance().getReference().child("Boards");
        mAuth=FirebaseAuth.getInstance();
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        boardMembersRef= FirebaseDatabase.getInstance().getReference().child("Board Members");
        ////////////////////////////////////////////////////////////////////////////////////////////
        String email=mAuth.getCurrentUser().getEmail();
        if(email.contains("-")){
            UserType="Student";
        }else {
            UserType="Teacher";
        }
        ////////////////////////////////////////////////////////////////////////////////////////////
        usersRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String checkName=dataSnapshot.child("name").getValue().toString();
                Toast.makeText(getContext(), "Check 1 : "+checkName, Toast.LENGTH_SHORT).show();
                retrieveAndDisplayBoards(checkName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        initializeFields();
        ////////////////////////////////////////////////////////////////////////////////////////////
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentBoardName=parent.getItemAtPosition(position).toString();
                ////////////////////////////////////////////////////////////////////////////////////
                Intent BoardDocumentIntent=new Intent(getContext(),MainDrawerActivity.class);
                BoardDocumentIntent.putExtra("BoardName",currentBoardName);
                startActivity(BoardDocumentIntent);
                ////////////////////////////////////////////////////////////////////////////////////
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////
        return boardsFragmentView;
    }

    private void initializeFields() {
        listView = (ListView)boardsFragmentView.findViewById(R.id.list_view);
        arrayAdapter=new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,list_of_Boards);
        listView.setAdapter(arrayAdapter);
    }

    private void retrieveAndDisplayBoards(final String checkName) {

        Toast.makeText(getContext(), "Check 2 : "+checkName, Toast.LENGTH_SHORT).show();
        if (UserType.equals("Teacher")){
            boardRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    checkBoardsForCreator(dataSnapshot, checkName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            boardMembersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    checkBoardsForMembers(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    private void checkBoardsForMembers(DataSnapshot dataSnapshot) {
        Set<String> set=new HashSet<>();
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            if(ds.child("members").hasChild(mAuth.getCurrentUser().getUid())){
                set.add(ds.getKey());
            }
            list_of_Boards.clear();
            list_of_Boards.addAll(set);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    private void checkBoardsForCreator(DataSnapshot dataSnapshot, String checkName) {
        Set<String> set=new HashSet<>();
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            String creatorCheck=ds.child("creator").getValue().toString();
            if(creatorCheck.equals(checkName)){
                set.add(ds.getKey());
            }
            list_of_Boards.clear();
            list_of_Boards.addAll(set);
            arrayAdapter.notifyDataSetChanged();
        }
    }

}