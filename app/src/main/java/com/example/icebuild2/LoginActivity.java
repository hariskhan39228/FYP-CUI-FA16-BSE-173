package com.example.icebuild2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Button loginButton;
    private EditText userEmail,userPassword;
    private TextView forgetPasswordLink,signUpLink;
    String UserType="";
    private ProgressDialog loadingBar;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private FirebaseAuth mAuth;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ////////////////////////////////////////////////////////////////////////////////////////////
        initializeFields();
        ////////////////////////////////////////////////////////////////////////////////////////////
        mAuth=FirebaseAuth.getInstance();
        ////////////////////////////////////////////////////////////////////////////////////////////
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegisterActivity();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowUserToLogin();
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////
    }

    private void allowUserToLogin() {
        String email=userEmail.getText().toString().trim();
        String pass=userPassword.getText().toString().trim();

        if(email.isEmpty()){
            userEmail.setError("Email Required!");
            userEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            userEmail.setError("Valid Email Required!");
            userEmail.requestFocus();
            return;
        }

        if(pass.isEmpty() || pass.length() < 6){
            userPassword.setError("minimum 6 character password required !");
            userPassword.requestFocus();
            return;
        }

        if(email.toLowerCase().contains("-") && email.toLowerCase().contains("cuiatd.edu.pk")){
            UserType="student";
        }else if(email.toLowerCase().contains("cuiatd.edu.pk")||email.toLowerCase().contains("hariskhanjadoon@outlook.com")){
            UserType="teacher";
        }

        loadingBar.setTitle("Logging in.");
        loadingBar.setMessage("Please wait while we log you into your Account...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            if(mAuth.getCurrentUser().isEmailVerified()){
                                if(UserType=="teacher"){
                                    Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }else{
                                    Intent intent=new Intent(LoginActivity.this, StudentActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }

                            }else{
                                Toast.makeText(LoginActivity.this, "Please verify your Email first.", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    private void initializeFields() {
        loginButton=(Button)findViewById(R.id.login_button);
        userEmail=(EditText)findViewById(R.id.login_email);
        userPassword=(EditText)findViewById(R.id.login_password);
        forgetPasswordLink=(TextView)findViewById(R.id.forget_password_link);
        signUpLink=(TextView)findViewById(R.id.sign_up_link);
        loadingBar=new ProgressDialog(this);
    }
    private void sendUserToRegisterActivity() {
        Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
