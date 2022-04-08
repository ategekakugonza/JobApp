package com.kugonza.apps.jobapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import cn.pedant.SweetAlert.SweetAlertDialog;
import kotlinx.coroutines.Job;

public class JobDetailsActivity extends AppCompatActivity {
private DatabaseReference reference;
    ImageView jobImage, pickImage;
    private TextView jobTitle, jobDetails,jobRequirements,uploadDate,jobPoster;
String key,poster,from,message;
    private FirebaseAuth auth;
    private  FirebaseUser user;
    private  String user_email;
    private LinearLayout send_email,delete_post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);
        jobImage = findViewById(R.id.jobImageHolder);
        jobDetails=findViewById(R.id.jobDetails);
        jobTitle=findViewById(R.id.jobTitle);
        jobPoster=findViewById(R.id.jobPoster);
        uploadDate=findViewById(R.id.uploadDate);
        jobRequirements = findViewById(R.id.jobRequirements);
        send_email = findViewById(R.id.send_email);
        delete_post = findViewById(R.id.delete_post);

        send_email.setVisibility(View.GONE);
        delete_post.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        user_email=user.getEmail().toString();

        reference = FirebaseDatabase.getInstance().getReference().child("Jobs");
        key = getIntent().getExtras().getString("key");
        loadJobInfo(key);
    }

    private void  loadJobInfo(String key){
        reference.keepSynced(true);
       reference.child(key).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {

               if (user_email.equalsIgnoreCase(snapshot.child("postedBy").getValue(String.class))){
                   send_email.setVisibility(View.GONE);
                   delete_post.setVisibility(View.VISIBLE);
               }
               else{
                   send_email.setVisibility(View.VISIBLE);
                   delete_post.setVisibility(View.GONE);
               }
               poster=snapshot.child("postedBy").getValue(String.class);
               message=snapshot.child("title").getValue(String.class);
               jobPoster.setText(snapshot.child("postedBy").getValue(String.class));
               jobTitle.setText(snapshot.child("title").getValue(String.class));
               jobDetails.setText(snapshot.child("details").getValue(String.class));
               jobRequirements.setText(snapshot.child("requirements").getValue(String.class));
               uploadDate.setText(snapshot.child("uploadDate").getValue(String.class)+" | "+snapshot.child("UploadTime").getValue(String.class));
               Picasso.with(JobDetailsActivity.this)
                       .load(snapshot.child("image").getValue(String.class))
                       .placeholder(R.drawable.img_kin)
                       .error(R.drawable.img_kin)
                       .into(jobImage ,new Callback(){
                           @Override
                           public void onSuccess() {

                           }
                           @Override
                           public void onError() {

                           }
                       });

           }
           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               Toast.makeText(JobDetailsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

           }
       });
    }

    protected void sendEmail(String to,String subject,String body) {
        ProgressDialog dialog = new ProgressDialog(JobDetailsActivity.this);
        dialog.setMessage("Sending .....");
        dialog.show();
        String[] TO = { to};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(JobDetailsActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }

    public void send_email(View view) {
        String body="Dear Employer, \n \n I hereby express my interest in the job subjected above. For  further information send me an email. \n\n Other Email: "+user_email+" \n\n\n Thanks ";
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("By tapping send it, an email will be send to the employer contacting your email. ")
                .setConfirmText("Yes,send it!")
                .setCancelText("No,don't!")
                .showCancelButton(true)
                .setConfirmClickListener(sDialog ->

                        sendEmail(poster,"Applying for "+message+" Job", body)
                )
                .setCancelClickListener(sDialog -> sDialog.cancel())
                .show();
    }

    public void delete_pots(View view) {

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("By tapping delete it, this job post will be deleted ")
                .setConfirmText("Yes,delete it!")
                .setCancelText("No,don't!")
                .showCancelButton(true)
                .setConfirmClickListener(sDialog ->

                        reference.child(key).removeValue().addOnSuccessListener(unused -> {
                            Toast.makeText(JobDetailsActivity.this,
                                    "Job Deleted", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(JobDetailsActivity.this,ListJobList.class));
                            finish();
                        }).addOnFailureListener(e -> Toast.makeText(JobDetailsActivity.this,
                                ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                )
                .setCancelClickListener(sDialog -> sDialog.cancel())
                .show();
    }
}