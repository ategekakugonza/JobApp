package com.kugonza.apps.jobapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class PostJobScreen extends AppCompatActivity {
    private static final int MY_CAMERA_PERMISSION_CODE = 12;
    private Uri imageUri;
    ImageView jobImage, pickImage;
    private EditText jobTitle, jobDetails,jobRequirements;
    ProgressDialog dialog;
    private FirebaseAuth auth;
    private  FirebaseUser user;
    private  String user_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job_screen);
        jobImage =findViewById(R.id.jobImageHolder);
        pickImage=findViewById(R.id.pickImage);

        jobDetails=findViewById(R.id.jobDetails);
        jobTitle=findViewById(R.id.jobTitle);
        jobRequirements = findViewById(R.id.jobRequirements);
        dialog = new ProgressDialog(PostJobScreen.this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        user_email=user.getEmail().toString();
        pickImage.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getProfileImage();
            }

        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getProfileImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(PostJobScreen.this);
    }

    public void goBack(View view) {
        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK ) {
                Uri resultUri = result.getUri();
                jobImage.setImageURI(resultUri);
                imageUri = resultUri;
            }
            else{
                Toast.makeText(this, "Could not load Image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void post_job(View view) {
        String title=jobTitle.getText().toString().trim();
        String details=jobDetails.getText().toString().trim();
        String requirements=jobRequirements.getText().toString().trim();
        if (imageUri == null){
            Snackbar.make(view,"Select Job Image",Snackbar.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(title) || title.length() <5){
            Snackbar.make(view,"Title should not be less than 5 characters",Snackbar.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(details) || details.length() <10){
            Snackbar.make(view,"Details should not be less than 10 characters",Snackbar.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(requirements) || requirements.length() <10){
            Snackbar.make(view,"Requirements should not be less than 10 characters",Snackbar.LENGTH_LONG).show();
        }
        else{
            dialog.show();
            StorageReference reference = FirebaseStorage.getInstance().getReference().child("Images");
            StorageReference file_path=reference.child("jobs").child(""+imageUri.getLastPathSegment());
            file_path.putFile(imageUri).addOnSuccessListener(taskSnapshot -> file_path.getDownloadUrl().addOnSuccessListener(uri -> {
                dialog.setMessage("Image Uploaded. Saving.....");
                DatabaseReference database= FirebaseDatabase.getInstance().getReference().child("Jobs");
                final DatabaseReference  ref = database.push();
                Calendar calendar = Calendar.getInstance();
                String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
                Date date=new Date();
                calendar.setTime(date);
                int hour=calendar.get(Calendar.HOUR_OF_DAY);
                int minute=calendar.get(Calendar.MINUTE);
                int second=calendar.get(Calendar.SECOND);
                String time=String.valueOf(hour+":"+minute+":"+second);
                HashMap map = new HashMap();
                map.put("UploadTime",time);
                map.put("uploadDate",currentDate);
                map.put("requirements",requirements);
                map.put("details",details);
                map.put("image", String.valueOf(uri));
                map.put("title",title);
                map.put("postedBy", user_email);
                ref.updateChildren(map).addOnSuccessListener(new OnSuccessListener() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onSuccess(Object o) {
                        dialog.dismiss();
                        imageUri= null;
                        jobImage.setImageDrawable(getResources().getDrawable(R.drawable.img_kin));
                        jobDetails.setText("");
                        jobTitle.setText("");
                        jobRequirements.setText("");
                        Snackbar.make(view,"Job Uploaded Successfully",Snackbar.LENGTH_LONG).setAction("View In Job List", view1 -> {
                            startActivity( new Intent(PostJobScreen.this,ListJobList.class));
                        }).show();
                    }
                }).addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(PostJobScreen.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                });
            }).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(PostJobScreen.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
            })).addOnProgressListener(snapshot -> {
                int progess = (int)((snapshot.getBytesTransferred()/snapshot.getTotalByteCount())*100);
                dialog.setMessage("Upload at "+progess+"%");
            }).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(PostJobScreen.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
            });
        }
    }
}