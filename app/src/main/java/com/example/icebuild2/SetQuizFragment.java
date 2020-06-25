package com.example.icebuild2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class SetQuizFragment extends Fragment {
    private View SetQuizFragmentView;
    private int numberOfQuestions=0;
    private EditText noOfQuestions;
    private Button setNoOfQuestions;
    private String currentBoardName;


    public SetQuizFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SetQuizFragmentView=inflater.inflate(R.layout.fragment_set_quiz, container, false);
        currentBoardName = getActivity().getIntent().getStringExtra("BoardName");
        noOfQuestions=(EditText)SetQuizFragmentView.findViewById(R.id.editTextNoOfQuestions);
        setNoOfQuestions=(Button)SetQuizFragmentView.findViewById(R.id.StartSetQuizActivityBtn);


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


        return SetQuizFragmentView;
    }
}