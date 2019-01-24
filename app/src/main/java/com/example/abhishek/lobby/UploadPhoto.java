package com.example.abhishek.lobby;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class UploadPhoto extends Fragment {

    public static final String PREF="username";
    public static final String PREF1="myNumber";
    private static final int GALLERY_INTENT=10;

    ImageView photo;
    Button uploadBtn;
    EditText aboutPic;
    View view;
    Context context;
    String username,myNumber;
    Uri uri,resultUri;
    private StorageReference mStorageRef;
    ProgressDialog progressDialog;
    public UploadPhoto() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_upload_photo, container, false);
        context=getActivity();
        photo=(ImageView)view.findViewById(R.id.photo);
        uploadBtn=(Button)view.findViewById(R.id.uploadBtn);
        aboutPic=(EditText)view.findViewById(R.id.aboutPic);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intn=new Intent(Intent.ACTION_PICK);
                intn.setType("image/*");
                startActivityForResult(intn,GALLERY_INTENT);
            }
        });
        if(resultUri==null)
        {
            uploadBtn.setEnabled(false);
            aboutPic.setEnabled(false);
            aboutPic.setVisibility(View.INVISIBLE);
        }
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if(resultUri!=null) {
                    final NewsFeed newsFeed = new NewsFeed();
                    newsFeed.setLobby_handle(username);
                    newsFeed.setContact(myNumber);
                    if(!TextUtils.isEmpty(aboutPic.getText().toString()))
                    newsFeed.setAboutPic(aboutPic.getText().toString());
                    FirebaseDatabase.getInstance().getReference("Users").child(username)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        switch (child.getKey()) {
                                            case "imageUri":
                                                newsFeed.setUser_profile_image(child.getValue(String.class));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                    if (resultUri != null) {
                        StorageReference images = mStorageRef.child("images").child(resultUri.getLastPathSegment());
                        progressDialog = new ProgressDialog(context);
                        progressDialog.show();

                        java.util.Calendar c= java.util.Calendar.getInstance();

                        String sDate = c.get(java.util.Calendar.YEAR) + "-"
                                + (c.get(java.util.Calendar.MONTH)+1)
                                + "-" + c.get(java.util.Calendar.DAY_OF_MONTH);
                        String sTime= c.get(java.util.Calendar.HOUR_OF_DAY)
                                + ":" + c.get(java.util.Calendar.MINUTE);
                        newsFeed.setDate(sDate);
                        newsFeed.setTime(sTime);

                        images.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Image Uploaded successfully", Toast.LENGTH_SHORT).show();
                                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                if (downloadUrl != null)
                                    newsFeed.setImageUri(downloadUrl.toString());
                                FirebaseDatabase.getInstance().getReference("newsFeed").push().setValue(newsFeed);
                                resultUri=null;
                                uploadBtn.setEnabled(false);
                                aboutPic.setText("");


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }
            }
        });
        SharedPreferences sf=context.getSharedPreferences(PREF,MODE_PRIVATE);
        username=sf.getString("username","null");
        SharedPreferences sf1=context.getSharedPreferences(PREF1,MODE_PRIVATE);
        myNumber=sf1.getString("myNumber","null");
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==GALLERY_INTENT&&resultCode==RESULT_OK)
        {
            uri=data.getData();
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(getContext(),this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                if(resultUri!=null)
                {
                    uploadBtn.setEnabled(true);
                    aboutPic.setEnabled(true);
                    aboutPic.setVisibility(View.VISIBLE);
                }
                Glide.with(context).load(resultUri).override(400,450).centerCrop().into(photo);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
