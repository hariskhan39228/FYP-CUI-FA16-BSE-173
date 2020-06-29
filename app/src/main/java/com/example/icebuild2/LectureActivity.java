package com.example.icebuild2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class LectureActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    Button startLectureButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);
        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        startLectureButton=(Button)findViewById(R.id.Start_Lecture);
        ////////////////////////////////////////////////////////////////////////////////////////////
        mToolbar=(Toolbar)findViewById(R.id.Lecture_Activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Lecture");
        ////////////////////////////////////////////////////////////////////////////////////////////
        startLectureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(intent.ACTION_GET_CONTENT);
                intent.setType("*/pptx");
                startActivityForResult(intent.createChooser(intent, "Select PowerPoint File"),410);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==410 && resultCode== Activity.RESULT_OK && data!=null && data.getData()!= null){
            String path=data.getData().getPath();
        }
    }
}
