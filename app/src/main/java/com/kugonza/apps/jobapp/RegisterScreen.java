package com.kugonza.apps.jobapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterScreen extends AppCompatActivity {
    private FirebaseAuth auth;
    private LinearLayout createNewAccount,login;
    private EditText email,password,confirm;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confrmPassword);
        auth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(RegisterScreen.this);
        dialog.setMessage("Please wait");



    }

    public void open_login(View view) {
        startActivity(new Intent(RegisterScreen.this,LoginScreen.class));
        finish();

    }

    public void open_create_account(View view) {
        String mail=email.getText().toString().trim();
        String pass=password.getText().toString().trim();
        String passConfirm=confirm.getText().toString().trim();

        if(TextUtils.isEmpty(mail)){
            email.setError("Email is required");
        }
        else if(TextUtils.isEmpty(mail) || pass.length() < 6){
            password.setError("Password at least 6 characters");
        }
        else if(!pass.equals(passConfirm)){
            confirm.setError("Password confirmation failed");
        }
        else{
            dialog.show();
            auth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(task -> {
                dialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(RegisterScreen.this,"Account Created Successfully",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterScreen.this,LoginScreen.class));
                    finish();
                }
                else{
                    Toast.makeText(RegisterScreen.this,"Account not created",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(RegisterScreen.this,"Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                dialog.dismiss();
            });
        }

    }
}