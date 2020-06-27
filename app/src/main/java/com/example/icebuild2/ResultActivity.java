package com.example.icebuild2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResultActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private String currentBoardName;

    private TextView TotalQuestionsAttempted, CorrectlyAnsweredQuestions, IncorrectlyAnsweredQuestions;
    private String total,correct,incorrect;
    private Button TakeBackToHomeBtn;
    private DatabaseReference QuizResultRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ////////////////////////////////////////////////////////////////////////////////////////////
        Intent intent = getIntent();
        total = intent.getStringExtra("total");
        correct = intent.getStringExtra("correct");
        incorrect = intent.getStringExtra("incorrect");
        currentBoardName = intent.getStringExtra("BoardName");
        ////////////////////////////////////////////////////////////////////////////////////////////
        mAuth=FirebaseAuth.getInstance();
        QuizResultRef= FirebaseDatabase.getInstance().getReference().child("Quiz Results").child(currentBoardName);
        ////////////////////////////////////////////////////////////////////////////////////////////
        initializeFields();
        ////////////////////////////////////////////////////////////////////////////////////////////
        mToolbar=(Toolbar)findViewById(R.id.ResultActivity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Quiz Result of "+currentBoardName);
        ////////////////////////////////////////////////////////////////////////////////////////////
        sendValuesToDatabase();
        setValues();
        ////////////////////////////////////////////////////////////////////////////////////////////
        TakeBackToHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToStudentActivity();
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////
    }

    private void sendValuesToDatabase() {
        String CurrentUserID=mAuth.getUid();
        QuizResultRef.child(CurrentUserID).child("Marks: ").setValue(correct);
    }

    private void initializeFields() {
        TotalQuestionsAttempted = (TextView)findViewById(R.id.attempted);
        CorrectlyAnsweredQuestions = (TextView)findViewById(R.id.correct);
        IncorrectlyAnsweredQuestions = (TextView)findViewById(R.id.incorrect);
        TakeBackToHomeBtn=(Button)findViewById(R.id.TakeToHomeFromResult);
    }
    private void setValues() {
        TotalQuestionsAttempted.setText(total);
        CorrectlyAnsweredQuestions.setText(correct);
        IncorrectlyAnsweredQuestions.setText(incorrect);
    }
    private void sendUserToStudentActivity() {
        Intent studentIntent=new Intent(ResultActivity.this, StudentActivity.class);
        studentIntent.putExtra("flag",1);
        startActivity(studentIntent);
    }

}
