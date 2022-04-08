package com.kugonza.apps.jobapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeScreen extends AppCompatActivity {
    java.util.List<Joblist> List;
    private JobAdapter adapter;
    private RecyclerView recyclerView;
    private EditText search_input;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        reference = FirebaseDatabase.getInstance().getReference().child("Jobs");
        List = new ArrayList<>();
        adapter = new JobAdapter(HomeScreen.this, List);

        recyclerView =findViewById(R.id.contactRecycle);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(HomeScreen.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);




    }

    @Override
    protected void onResume() {
        super.onResume();
        loadJobs();
    }

    public void open_new_app(View view) {
        startActivity(new Intent(HomeScreen.this,PostJobScreen.class));
    }

    public  void loadJobs(){
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List.clear();
                int x=0;
                for(DataSnapshot ds: snapshot.getChildren()){
                    x++;
                    if(x<=2){
                        List.add(new Joblist(
                                ds.getKey(),
                                ds.child("postedBy").getValue().toString(),
                                ds.child("title").getValue().toString(),
                                ds.child("details").getValue().toString(),
                                ds.child("requirements").getValue().toString(),
                                ds.child("uploadDate").getValue().toString(),
                                ds.child("UploadTime").getValue().toString(),
                                ds.child("image").getValue().toString()
                        ));
                    }

                }

                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeScreen.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void open_view_list(View view) {
        startActivity(new Intent(HomeScreen.this,ListJobList.class));

    }


    public void open_logout(View view) {


        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure you want to logout?")
                .setConfirmText("Yes,logout!")
                .setCancelText("No, don't!")
                .showCancelButton(true)
                .setConfirmClickListener(sDialog -> {
                    auth.signOut();
                    startActivity(new Intent(HomeScreen.this,ListJobList.class));
                    finish();
                })
                .showCancelButton(true)
                .setCancelClickListener(sDialog -> sDialog.cancel())
                .show();
    }
}