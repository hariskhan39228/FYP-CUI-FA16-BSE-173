package com.example.icebuild2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AttemptQuizActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private String currentBoardName;

    TextView questionTxt, TimerTextView;
    Button b1,b2,b3,b4;
    int correct=0,wrong=0, total=0, computerCount=0;
    int maxCounter=0;
    DatabaseReference QuizQuestionRef;
    DatabaseReference QuizRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempt_quiz);
        ////////////////////////////////////////////////////////////////////////////////////////////
        currentBoardName = getIntent().getStringExtra("BoardName");
        ////////////////////////////////////////////////////////////////////////////////////////////
        QuizRef=FirebaseDatabase.getInstance().getReference().child("Board Quizzes").child(currentBoardName);
        ////////////////////////////////////////////////////////////////////////////////////////////
        initializeFields();
        ////////////////////////////////////////////////////////////////////////////////////////////
        mToolbar=(Toolbar)findViewById(R.id.Attempt_Quiz_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Attempt Quiz of "+currentBoardName);
        ////////////////////////////////////////////////////////////////////////////////////////////
        QuizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String maxString=dataSnapshot.child("Total_Questions").getValue().toString();
                int max=Integer.parseInt(maxString);
                Toast.makeText(AttemptQuizActivity.this, "Check Counter: "+ max, Toast.LENGTH_SHORT).show();
                maxCounter=max;
                updateQuestion(maxCounter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

    }


    private void initializeFields() {
        b1 = (Button) findViewById(R.id.OptionA);
        b2 = (Button) findViewById(R.id.OptionB);
        b3 = (Button) findViewById(R.id.OptionC);
        b4 = (Button) findViewById(R.id.OptionD);
        questionTxt = (TextView) findViewById(R.id.Question_TextView);
        TimerTextView =(TextView)findViewById(R.id.Timer_TextView);
    }
    private void updateQuestion(final int maxCounter) {
        reverseTimer(60, TimerTextView);
        computerCount++;
        if(computerCount> maxCounter)
        {
            Toast.makeText(getApplicationContext(),"Game Over "+ this.maxCounter,Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(AttemptQuizActivity.this,ResultActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            myIntent.putExtra("total",String.valueOf(total));
            myIntent.putExtra("correct",String.valueOf(correct));
            myIntent.putExtra("incorrect",String.valueOf(wrong));
            myIntent.putExtra("BoardName",currentBoardName);
            startActivity(myIntent);
            this.finish();
        }else{
            ////////////////////////////////////////////////////////////////////////////////////////
            QuizQuestionRef = FirebaseDatabase.getInstance().getReference().child("Board Quizzes").
                    child(currentBoardName).child("Question_"+computerCount);
            ////////////////////////////////////////////////////////////////////////////////////////
            total++;
            ////////////////////////////////////////////////////////////////////////////////////////
            QuizQuestionRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    final Question question = dataSnapshot.getValue(Question.class);
                    questionTxt.setText(question.getQuestion());
                    b1.setText(question.getOption1());
                    b2.setText(question.getOption2());
                    b3.setText(question.getOption3());
                    b4.setText(question.getOption4());
                    ////////////////////////////////////////////////////////////////////////////////
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(b1.getText().toString().equals(question.getAnswer())) {
                                ////////////////////////////////////////////////////////////////////
                                Toast.makeText(getApplicationContext(),"Correct answer",Toast.LENGTH_SHORT).show();
                                b1.setBackgroundColor(Color.GREEN);
                                correct = correct +1;
                                ////////////////////////////////////////////////////////////////////
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        b1.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        updateQuestion(maxCounter);
                                    }
                                }, 1500);
                                ////////////////////////////////////////////////////////////////////
                            }else{
                                ////////////////////////////////////////////////////////////////////
                                Toast.makeText(getApplicationContext(),"Incorrect",Toast.LENGTH_SHORT).show();
                                wrong = wrong+1;
                                b1.setBackgroundColor(Color.RED);
                                ////////////////////////////////////////////////////////////////////
                                if(b2.getText().toString().equals(question.getAnswer())){
                                    b2.setBackgroundColor(Color.GREEN);
                                }else if(b3.getText().toString().equals(question.getAnswer())){
                                    b3.setBackgroundColor(Color.GREEN);
                                }else if(b4.getText().toString().equals(question.getAnswer())){
                                    b4.setBackgroundColor(Color.GREEN);
                                }
                                ////////////////////////////////////////////////////////////////////
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        b1.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        b2.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        b3.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        b4.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        updateQuestion(maxCounter);
                                    }
                                }, 1500);
                                ////////////////////////////////////////////////////////////////////
                            }
                        }
                    });
                    ////////////////////////////////////////////////////////////////////////////////
                    b2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(b2.getText().toString().equals(question.getAnswer())) {
                                ////////////////////////////////////////////////////////////////////
                                Toast.makeText(getApplicationContext(),"Correct answer",Toast.LENGTH_SHORT).show();
                                b2.setBackgroundColor(Color.GREEN);
                                correct = correct +1;
                                ////////////////////////////////////////////////////////////////////
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        b2.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        updateQuestion(maxCounter);
                                    }
                                }, 1500);
                                ////////////////////////////////////////////////////////////////////
                            }else{
                                ////////////////////////////////////////////////////////////////////
                                Toast.makeText(getApplicationContext(),"Incorrect",Toast.LENGTH_SHORT).show();
                                wrong = wrong+1;
                                b2.setBackgroundColor(Color.RED);
                                ////////////////////////////////////////////////////////////////////
                                if(b1.getText().toString().equals(question.getAnswer())){
                                    b1.setBackgroundColor(Color.GREEN);
                                }else if(b3.getText().toString().equals(question.getAnswer())){
                                    b3.setBackgroundColor(Color.GREEN);
                                }else if(b4.getText().toString().equals(question.getAnswer())){
                                    b4.setBackgroundColor(Color.GREEN);
                                }
                                ////////////////////////////////////////////////////////////////////
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        b1.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        b2.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        b3.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        b4.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        updateQuestion(maxCounter);
                                    }
                                }, 1500);
                                ////////////////////////////////////////////////////////////////////
                            }
                        }
                    });
                    ////////////////////////////////////////////////////////////////////////////////
                    b3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(b3.getText().toString().equals(question.getAnswer())) {
                                ////////////////////////////////////////////////////////////////////
                                Toast.makeText(getApplicationContext(),"Correct answer",Toast.LENGTH_SHORT).show();
                                b3.setBackgroundColor(Color.GREEN);
                                correct = correct +1;
                                ////////////////////////////////////////////////////////////////////
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        b3.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        updateQuestion(maxCounter);
                                    }
                                }, 1500);
                                ////////////////////////////////////////////////////////////////////
                            }else{
                                ////////////////////////////////////////////////////////////////////
                                Toast.makeText(getApplicationContext(),"Incorrect",Toast.LENGTH_SHORT).show();
                                wrong = wrong+1;
                                b3.setBackgroundColor(Color.RED);
                                ////////////////////////////////////////////////////////////////////
                                if(b1.getText().toString().equals(question.getAnswer())){
                                    b1.setBackgroundColor(Color.GREEN);
                                }else if(b2.getText().toString().equals(question.getAnswer())){
                                    b2.setBackgroundColor(Color.GREEN);
                                }else if(b4.getText().toString().equals(question.getAnswer())){
                                    b4.setBackgroundColor(Color.GREEN);
                                }
                                ////////////////////////////////////////////////////////////////////
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        b1.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        b2.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        b3.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        b4.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        updateQuestion(maxCounter);
                                    }
                                }, 1500);
                                ////////////////////////////////////////////////////////////////////
                            }
                        }
                    });
                    ////////////////////////////////////////////////////////////////////////////////
                    b4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(b4.getText().toString().equals(question.getAnswer())) {
                                ////////////////////////////////////////////////////////////////////
                                Toast.makeText(getApplicationContext(),"Correct answer",Toast.LENGTH_SHORT).show();
                                b4.setBackgroundColor(Color.GREEN);
                                correct = correct +1;
                                ////////////////////////////////////////////////////////////////////
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        b4.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        updateQuestion(maxCounter);
                                    }
                                }, 1500);
                                ////////////////////////////////////////////////////////////////////
                            }else{
                                ////////////////////////////////////////////////////////////////////
                                Toast.makeText(getApplicationContext(),"Incorrect",Toast.LENGTH_SHORT).show();
                                wrong = wrong+1;
                                b4.setBackgroundColor(Color.RED);
                                ////////////////////////////////////////////////////////////////////
                                if(b1.getText().toString().equals(question.getAnswer())){
                                    b1.setBackgroundColor(Color.GREEN);
                                }else if(b2.getText().toString().equals(question.getAnswer())){
                                    b2.setBackgroundColor(Color.GREEN);
                                }else if(b3.getText().toString().equals(question.getAnswer())){
                                    b3.setBackgroundColor(Color.GREEN);
                                }
                                ////////////////////////////////////////////////////////////////////
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        b1.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        b2.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        b3.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        b4.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        updateQuestion(maxCounter);
                                    }
                                }, 1500);
                                ////////////////////////////////////////////////////////////////////
                            }
                        }
                    });
                    ////////////////////////////////////////////////////////////////////////////////
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void reverseTimer(int Seconds,final TextView tv){



        final CountDownTimer time = new CountDownTimer(Seconds* 1000+1000, 1000) {
            int seconds=0;
            int minutes=0;

            public void onTick(long millisUntilFinished) {
                seconds = (int) (millisUntilFinished / 1000);
                minutes = seconds / 60;
                seconds = seconds % 60;
                tv.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            public void onFinish() {
                if(!(computerCount>maxCounter)) {
                    tv.setText("Completed");
                    Intent myIntent = new Intent(AttemptQuizActivity.this, ResultActivity.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    myIntent.putExtra("total", String.valueOf(total));
                    myIntent.putExtra("correct", String.valueOf(correct));
                    myIntent.putExtra("incorrect", String.valueOf(wrong));
                    myIntent.putExtra("BoardName",currentBoardName);
                    startActivity(myIntent);
                }
            }
        };
        time.cancel();
        time.start();
        if(computerCount>maxCounter){
            time.cancel();
        }
    }
}
