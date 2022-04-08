package com.kugonza.apps.jobapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListJobList extends AppCompatActivity {
    java.util.List<Joblist> List;
    private JobAdapter adapter;
    private RecyclerView recyclerView;
    private EditText search_input;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_job_list);
        reference = FirebaseDatabase.getInstance().getReference().child("Jobs");
        List = new ArrayList<>();
        adapter = new JobAdapter(ListJobList.this, List);

        recyclerView =findViewById(R.id.contactRecycle);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(ListJobList.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }
    public void goBack(View view) {
        onBackPressed();
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadJobs();
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
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListJobList.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void open_new_job(View view) {
        startActivity(new Intent(ListJobList.this,PostJobScreen.class));

    }
}