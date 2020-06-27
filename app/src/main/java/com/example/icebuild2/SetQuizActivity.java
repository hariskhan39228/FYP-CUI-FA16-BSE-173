package com.example.icebuild2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class SetQuizActivity extends AppCompatActivity {
    private EditText Question,option1,option2,option3,option4;
    private RadioButton optnA,optnB,optnC,optnD;
    private Button SetQuestionBtn;
    private RadioGroup radioGroup;
    int noOfQuestions,counter;
    private String currentBoardName;
    private DatabaseReference QuizzesRef;
    private HashMap<String,Object> Questions;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_quiz);
        noOfQuestions=getIntent().getIntExtra("noOfQuestions",0);
        currentBoardName = getIntent().getStringExtra("BoardName");
        counter=1;
        ////////////////////////////////////////////////////////////////////////////////////////////
        mToolbar=(Toolbar)findViewById(R.id.SetQuiz_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Set Quiz for "+currentBoardName);
        ////////////////////////////////////////////////////////////////////////////////////////////
        QuizzesRef=FirebaseDatabase.getInstance().getReference().child("Board Quizzes").child(currentBoardName);
        initializeFields();
        SetQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAndAddQuestion();
            }
        });
    }
    private void initializeFields() {
        Questions=new HashMap<>();
        SetQuestionBtn=(Button)findViewById(R.id.setQuestionBtn);
        ////////////////////////////////////////////////////////////////////////////////////////////
        Question=(EditText)findViewById(R.id.QuestionEditText);
        option1=(EditText)findViewById(R.id.Option1EditText);
        option2=(EditText)findViewById(R.id.Option2EditText);
        option3=(EditText)findViewById(R.id.Option3EditText);
        option4=(EditText)findViewById(R.id.Option4EditText);
        ////////////////////////////////////////////////////////////////////////////////////////////
        radioGroup=(RadioGroup)findViewById(R.id.RadioGroupofSetting);
        ////////////////////////////////////////////////////////////////////////////////////////////
        optnA=(RadioButton) radioGroup.findViewById(R.id.optionARadioBtn);
        optnB=(RadioButton) radioGroup.findViewById(R.id.optionBRadioBtn);
        optnC=(RadioButton) radioGroup.findViewById(R.id.optionCRadioBtn);
        optnD=(RadioButton) radioGroup.findViewById(R.id.optionDRadioBtn);
        ////////////////////////////////////////////////////////////////////////////////////////////
    }

    private void verifyAndAddQuestion() {
        String question,optionA,optionB,optionC,optionD,correctAnswer;
        question=Question.getText().toString();
        if(question.isEmpty()){
            Question.setError("A valid Question required");
            Question.requestFocus();
            return;
        }

        optionA=option1.getText().toString();
        if(optionA.isEmpty()){
            option1.setError("A valid Option required");
            option1.requestFocus();
            return;
        }
        optionB=option2.getText().toString();
        if(optionB.isEmpty()){
            option2.setError("A valid Question required");
            option2.requestFocus();
            return;
        }
        optionC=option3.getText().toString();
        if(optionC.isEmpty()){
            option3.setError("A valid Question required");
            option3.requestFocus();
            return;
        }
        optionD=option4.getText().toString();
        if(optionD.isEmpty()){
            option4.setError("A valid Question required");
            option4.requestFocus();
            return;
        }

        int radioButtonID=radioGroup.getCheckedRadioButtonId();
        if(radioButtonID==R.id.optionARadioBtn){
            correctAnswer=optionA;
        }else if(radioButtonID==R.id.optionBRadioBtn){
            correctAnswer=optionB;
        }else if(radioButtonID==R.id.optionCRadioBtn){
            correctAnswer=optionC;
        }else if(radioButtonID==R.id.optionDRadioBtn){
            correctAnswer=optionD;
        }else{
            Toast.makeText(this, "Error: Correct option Required "+ radioButtonID+" "+R.id.optionARadioBtn, Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String,Object> QuestionInfoMap=new HashMap<>();
        QuestionInfoMap.put("question",question);
        QuestionInfoMap.put("option1",optionA);
        QuestionInfoMap.put("option2",optionB);
        QuestionInfoMap.put("option3",optionC);
        QuestionInfoMap.put("option4",optionD);
        QuestionInfoMap.put("answer",correctAnswer);

        addQuestion(QuestionInfoMap);
    }

    private void addQuestion(HashMap<String, Object> questionInfoMap) {


            Questions.put("Question "+counter,questionInfoMap);
            QuizzesRef.child("Question_"+counter).updateChildren(questionInfoMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(SetQuizActivity.this, "Question Added.", Toast.LENGTH_SHORT).show();
                    resetFields();
                    counter++;
                    if(counter>noOfQuestions){
                        QuizzesRef.child("Total_Questions").setValue(counter-1).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SetQuizActivity.this, "Quiz Successfully Added.", Toast.LENGTH_SHORT).show();
                                sendTeacherToTheDrawerActivity();
                            }
                        });

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetQuizActivity.this, "Error, Try Setting Again.", Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void sendTeacherToTheDrawerActivity() {
        Intent intent=new Intent(SetQuizActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void resetFields() {
        Question.setText("");
        option1.setText("");
        option2.setText("");
        option3.setText("");
        option4.setText("");
        radioGroup.clearCheck();
    }


}
