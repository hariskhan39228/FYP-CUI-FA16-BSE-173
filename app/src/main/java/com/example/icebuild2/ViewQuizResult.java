package com.example.icebuild2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewQuizResult extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView QuizResultsList;
    private DatabaseReference QuizzesRef, usersRef;
    private String currentBoardName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_quiz_result);
        ////////////////////////////////////////////////////////////////////////////////////////////
        currentBoardName=getIntent().getStringExtra("BoardName");
        ////////////////////////////////////////////////////////////////////////////////////////////
        QuizzesRef= FirebaseDatabase.getInstance().getReference().child("Quiz Results").child(currentBoardName);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ////////////////////////////////////////////////////////////////////////////////////////////
        QuizResultsList =(RecyclerView)findViewById(R.id.Quiz_Results_recycle_list);
        QuizResultsList.setLayoutManager(new LinearLayoutManager(this));
        ////////////////////////////////////////////////////////////////////////////////////////////
        mToolbar=(Toolbar)findViewById(R.id.Quiz_Results_List_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Quiz results for "+currentBoardName);
        ////////////////////////////////////////////////////////////////////////////////////////////
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(QuizzesRef, Users.class)
                .build();
        FirebaseRecyclerAdapter<Users, QuizResultViewHolder> adapter = new FirebaseRecyclerAdapter<Users, QuizResultViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final QuizResultViewHolder holder, int position, @NonNull Users users) {
                final String userIDs = getRef(position).getKey();

                usersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("image")) {
                            final String memberUserImage = dataSnapshot.child("image").getValue().toString();
                            Picasso.with(ViewQuizResult.this).load(memberUserImage).into(holder.memberProfileImage);
                        }
                        final String memberUserName = dataSnapshot.child("name").getValue().toString();
                        final String memberUserEmail = dataSnapshot.child("email").getValue().toString();
                        final String memberUserReg = memberUserEmail.substring(0, 12);
                        getMarks(userIDs,holder);
                        holder.memberReg.setText(memberUserReg);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @NonNull
            @Override
            public QuizResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_request_display_layout, parent, false);
                QuizResultViewHolder holder= new QuizResultViewHolder(view);
                return holder;
            }
        };
        QuizResultsList.setAdapter(adapter);
        adapter.startListening();
    }

    private void getMarks(final String userIDs, final QuizResultViewHolder holder) {
        QuizzesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String marks="dummy marks";
                if(dataSnapshot.child(userIDs).child("Marks: ").exists()){

                    marks=dataSnapshot.child(userIDs).child("Marks: ").getValue().toString();
                }


                holder.memberName.setText(marks+" marks");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static class QuizResultViewHolder extends RecyclerView.ViewHolder {
        TextView memberName, memberReg;
        CircleImageView memberProfileImage;


        public QuizResultViewHolder(@NonNull View itemView) {
            super(itemView);

            memberName = itemView.findViewById(R.id.requesting_user_profile_name);
            memberReg = itemView.findViewById(R.id.requesting_user_registration_number);
            memberProfileImage = itemView.findViewById(R.id.requesting_user_profile_image);

        }
    }
}





