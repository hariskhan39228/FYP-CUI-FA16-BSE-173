package com.example.icebuild2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SetQuizFragment extends Fragment {
    private View SetQuizFragmentView;
    private int numberOfQuestions=0;
    private EditText noOfQuestions;
    private TextView alreadySetText;
    private Button setNoOfQuestions, deleteQuiz, ViewQuizResult;
    private String currentBoardName;
    private DatabaseReference quizRef,quizResultRef;


    public SetQuizFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SetQuizFragmentView=inflater.inflate(R.layout.fragment_set_quiz, container, false);
        currentBoardName = getActivity().getIntent().getStringExtra("BoardName");

        Toast.makeText(getContext(), currentBoardName, Toast.LENGTH_SHORT).show();
        quizRef= FirebaseDatabase.getInstance().getReference().child("Board Quizzes");
        quizResultRef= FirebaseDatabase.getInstance().getReference().child("Quiz Results");
        initializeFields();
        disableFields();
        checkQuizStatus();



        return SetQuizFragmentView;
    }



    private void initializeFields() {
        noOfQuestions=(EditText)SetQuizFragmentView.findViewById(R.id.editTextNoOfQuestions);
        setNoOfQuestions=(Button)SetQuizFragmentView.findViewById(R.id.StartSetQuizActivityBtn);
        deleteQuiz=(Button)SetQuizFragmentView.findViewById(R.id.deleteQuizBtn);
        ViewQuizResult=(Button)SetQuizFragmentView.findViewById(R.id.ViewQuizResultBtn);
        alreadySetText=(TextView) SetQuizFragmentView.findViewById(R.id.alreadySetText);
    }

    private void disableFields() {
        noOfQuestions.setVisibility(View.INVISIBLE);
        noOfQuestions.setEnabled(false);
        setNoOfQuestions.setVisibility(View.INVISIBLE);
        setNoOfQuestions.setEnabled(false);

        ViewQuizResult.setVisibility(View.INVISIBLE);
        ViewQuizResult.setEnabled(false);

        deleteQuiz.setVisibility(View.INVISIBLE);
        alreadySetText.setVisibility(View.INVISIBLE);
        deleteQuiz.setEnabled(false);
        alreadySetText.setEnabled(false);
    }

    private void checkQuizStatus() {
        quizRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(currentBoardName)){
                    setQuizDisabledOptions();
                }else{
                    setQuizEnabledOptions();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setQuizDisabledOptions() {
        noOfQuestions.setVisibility(View.INVISIBLE);
        noOfQuestions.setEnabled(false);
        setNoOfQuestions.setVisibility(View.INVISIBLE);
        setNoOfQuestions.setEnabled(false);

        deleteQuiz.setVisibility(View.VISIBLE);
        alreadySetText.setVisibility(View.VISIBLE);
        deleteQuiz.setEnabled(true);
        alreadySetText.setEnabled(true);

        CheckForResults();


        deleteQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quizRef.child(currentBoardName).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Quiz Deleted!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void CheckForResults() {
        quizResultRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(currentBoardName)){
                    enableViewResultBtn();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void enableViewResultBtn() {
        ViewQuizResult.setVisibility(View.VISIBLE);
        ViewQuizResult.setEnabled(true);
        ViewQuizResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), ViewQuizResult.class);
                intent.putExtra("BoardName",currentBoardName);
                startActivity(intent);
            }
        });
    }

    private void setQuizEnabledOptions() {
        noOfQuestions.setVisibility(View.VISIBLE);
        noOfQuestions.setEnabled(true);
        setNoOfQuestions.setVisibility(View.VISIBLE);
        setNoOfQuestions.setEnabled(true);

        deleteQuiz.setVisibility(View.INVISIBLE);
        alreadySetText.setVisibility(View.INVISIBLE);
        deleteQuiz.setEnabled(false);
        alreadySetText.setEnabled(false);

        setNoOfQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOfQuestions=Integer.parseInt(noOfQuestions.getText().toString());
                if(numberOfQuestions==0 || numberOfQuestions<3){
                    noOfQuestions.setError("At-least 3 Questions required.");
                    noOfQuestions.requestFocus();
                    return;
                }
                if(numberOfQuestions>50){
                    noOfQuestions.setError("Number of Questions can't be more then 50.");
                    noOfQuestions.requestFocus();
                    return;
                }
                Intent intent=new Intent(getContext(), SetQuizActivity.class);
                intent.putExtra("noOfQuestions",numberOfQuestions);
                intent.putExtra("BoardName",currentBoardName);
                startActivity(intent);

            }
        });

    }
}