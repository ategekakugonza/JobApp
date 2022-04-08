package com.kugonza.apps.jobapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity {
    private FirebaseAuth auth;
    private LinearLayout createNewAccount,login;
    private EditText email,password;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        auth = FirebaseAuth.getInstance();
        createNewAccount = findViewById(R.id.createNewAccount);
        login = findViewById(R.id.login);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        dialog = new ProgressDialog(LoginScreen.this);
        dialog.setMessage("Please wait");

        login.setOnClickListener(view -> {
            String mail=email.getText().toString().trim();
            String pass=password.getText().toString().trim();
           if(TextUtils.isEmpty(mail)){
               email.setError("Email is required");
           }
           else if(TextUtils.isEmpty(pass)){
               password.setError("Password is required");
            }
           else{
               dialog.show();
               auth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {

                       if(task.isSuccessful()){
                           dialog.dismiss();
                           Toast.makeText(LoginScreen.this,"Login is successfully",Toast.LENGTH_LONG).show();
                           startActivity( new Intent(LoginScreen.this,SplashActivity.class));
                           finish();
                       }
                       else{
                           dialog.dismiss();
                           Toast.makeText(LoginScreen.this,"Wrong Username and Password",Toast.LENGTH_LONG).show();
                       }

                   }
               }).addOnFailureListener(e ->
                       Toast.makeText(LoginScreen.this,"Error: "+e.getMessage(),Toast.LENGTH_LONG).show());
                         dialog.dismiss();
           }
        });
        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(LoginScreen.this,RegisterScreen.class));
            }
        });


    }
}