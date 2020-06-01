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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText userEmail,userPassword;
    private TextView alreadyHaveAnAccountLink;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ////////////////////////////////////////////////////////////////////////////////////////////
        mAuth=FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();
        ////////////////////////////////////////////////////////////////////////////////////////////
        initializeFields();
        ////////////////////////////////////////////////////////////////////////////////////////////
        alreadyHaveAnAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLoginActivity();
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccout();
            }
        });

    }

    private void createNewAccout() {
        final String email=userEmail.getText().toString().trim();
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
        if(!(email.toLowerCase().contains("cuiatd.edu.pk")||email.toLowerCase().contains("hariskhanjadoon@outlook.com")) ){
            userEmail.setError("University issued Email Required! e.g. Fa16-BSE-106@cuiatd.edu.pk, Sanamali@cuiatd.edu.pk ");
            userEmail.requestFocus();
            return;
        }

        if(pass.isEmpty() || pass.length() < 6){
            userPassword.setError("minimum 6 character password required !");
            userPassword.requestFocus();
            return;
        }
        loadingBar.setTitle("Creating new Account");
        loadingBar.setMessage("Please wait while your new account is being created...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loadingBar.dismiss();
                    String currentUserID=mAuth.getCurrentUser().getUid();
                    rootRef.child("Users").child(currentUserID).child("email").setValue(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Registration Successful," +
                                                " Please check your email for verification.", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                } else {
                    // If sign in fails, display a message to the user.
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void initializeFields() {
        registerButton=(Button)findViewById(R.id.register_button);
        userEmail=(EditText)findViewById(R.id.register_email);
        userPassword=(EditText)findViewById(R.id.register_password);
        alreadyHaveAnAccountLink=(TextView)findViewById(R.id.login_link);
        loadingBar=new ProgressDialog(this);
    }

    private void sendUserToLoginActivity() {
        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}
