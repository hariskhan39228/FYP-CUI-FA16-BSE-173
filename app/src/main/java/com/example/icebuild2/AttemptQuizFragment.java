package com.example.icebuild2;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class AttemptQuizFragment extends Fragment {

    private Button AttemptQuizBtn;
    private TextView QuizStatusTextView;
    private DatabaseReference quizRef,quizResultRef;
    private String currentBoardName;
    private FirebaseAuth mAuth;


    public AttemptQuizFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View AttemptQuizFragment = inflater.inflate(R.layout.fragment_attempt_quiz, container, false);
        ////////////////////////////////////////////////////////////////////////////////////////////
        currentBoardName = getActivity().getIntent().getStringExtra("BoardName");
        ////////////////////////////////////////////////////////////////////////////////////////////
        AttemptQuizBtn = (Button)AttemptQuizFragment.findViewById(R.id.attemptQuizBtn);
        QuizStatusTextView=(TextView)AttemptQuizFragment.findViewById(R.id.quiz_Status_TextView);
        ////////////////////////////////////////////////////////////////////////////////////////////
        quizRef=FirebaseDatabase.getInstance().getReference().child("Board Quizzes");
        quizResultRef= FirebaseDatabase.getInstance().getReference().child("Quiz Results").child(currentBoardName);
        mAuth=FirebaseAuth.getInstance();
        ////////////////////////////////////////////////////////////////////////////////////////////
        checkQuizStatus();
        ////////////////////////////////////////////////////////////////////////////////////////////
        return AttemptQuizFragment;
    }

    private void checkQuizStatus() {
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(currentBoardName)){
                    setQuizEnabledOptions();
                }else{
                    setQuizDisabledOptions();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setQuizDisabledOptions() {
        QuizStatusTextView.setText("No Quiz Available at the Moment!");
        AttemptQuizBtn.setVisibility(View.INVISIBLE);
        AttemptQuizBtn.setEnabled(false);
    }

    private void setQuizEnabledOptions() {
        QuizStatusTextView.setText("Quiz Available!");
        AttemptQuizBtn.setVisibility(View.VISIBLE);
        AttemptQuizBtn.setEnabled(true);
        checkAttemptStatus();
        AttemptQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), AttemptQuizActivity.class);
                intent.putExtra("BoardName",currentBoardName);
                startActivity(intent);
            }
        });
    }

    private void checkAttemptStatus() {
        final String currentUserID=mAuth.getCurrentUser().getUid();
        quizResultRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getContext(), currentUserID, Toast.LENGTH_SHORT).show();
                if(dataSnapshot.child(currentUserID).exists()){

                    disableAttemptButton();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void disableAttemptButton() {
        QuizStatusTextView.setText("Quiz Performed, No new Quiz Available at the Moment!");
        AttemptQuizBtn.setVisibility(View.INVISIBLE);
        AttemptQuizBtn.setEnabled(false);
    }

}
