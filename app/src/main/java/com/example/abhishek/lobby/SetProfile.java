package com.example.abhishek.lobby;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetProfile extends AppCompatActivity {

    public static final String PREF="username";
    public static final String PREF1="myNumber";
    private static final int GALLERY_INTENT=10;
    String userNumber,username,myNumber;
    CircleImageView profilepic;
    TextView changepic;
    EditText nameEt,usernameEt,bioEt;
    TextView phone;
    Uri uri,resultUri;
    ImageView done;
    DatabaseReference myRef;
    private StorageReference mStorageRef;
    ProgressDialog progressDialog;
    String REQUEST_CODE="null";
    HashMap<String,String> users=new HashMap<>();
    boolean validUserName=false,validNumber=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);
        SharedPreferences sf=getSharedPreferences(PREF,MODE_PRIVATE);
        username=sf.getString("username","null");
        SharedPreferences sf1=getSharedPreferences(PREF,MODE_PRIVATE);
        myNumber=sf1.getString("myNumber","null");
        if(getIntent().getExtras()!=null)
       userNumber=getIntent().getExtras().getString("mobileNo");

        profilepic=(CircleImageView)findViewById(R.id.profilepic);
        nameEt=(EditText)findViewById(R.id.nameEt);
        usernameEt=(EditText)findViewById(R.id.usernameEt);
        bioEt=(EditText)findViewById(R.id.bioEt);
        changepic=(TextView)findViewById(R.id.changepic);
        phone=(TextView)findViewById(R.id.phone);
        done=(ImageView)findViewById(R.id.done);

        FirebaseDatabase.getInstance().getReference("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        users.clear();
                        for (DataSnapshot child:dataSnapshot.getChildren())
                        {
                            users.put(child.getKey(),"1");
                            if(!myNumber.equals("null")&&child.child("contact").getValue(String.class)!=null)
                            if(child.child("contact").getValue(String.class).equals(myNumber))
                                validNumber=false;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        if(username.equals("null")){
        usernameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if(users.containsKey(usernameEt.getText().toString())){
                   Toast.makeText(SetProfile.this,"This user name already exist.Please choose another user name",Toast.LENGTH_SHORT).show();
               }
               else
                    validUserName =true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });}
        else
            validUserName=true;



        if(!username.equals("null"))
        {
            FirebaseDatabase.getInstance().getReference("Users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    usernameEt.setText(username);
                    usernameEt.setEnabled(false);
                    nameEt.setText(dataSnapshot.child("name").getValue(String.class));
                    bioEt.setText(dataSnapshot.child("bio").getValue(String.class));
                    phone.setText(dataSnapshot.child("contact").getValue(String.class));
                    if(dataSnapshot.child("imageUri").getValue(String.class)!=null)
                    Glide.with(SetProfile.this).load(Uri.parse(dataSnapshot.child("imageUri").getValue(String.class))).override(120,120).centerCrop().into(profilepic);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        mStorageRef = FirebaseStorage.getInstance().getReference();

        if(userNumber!=null)
        phone.setText(userNumber);
    }
    public void onChangePhoto(View view)
    {
        Intent intn=new Intent(Intent.ACTION_PICK);
        intn.setType("image/*");
        startActivityForResult(intn,GALLERY_INTENT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==GALLERY_INTENT&&resultCode==RESULT_OK)
        {
            uri=data.getData();
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                Glide.with(SetProfile.this).load(resultUri).override(120,120).centerCrop().into(profilepic);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void onDone(View view)
    {
        if(validUserName&&validNumber&& !TextUtils.isEmpty(nameEt.getText().toString())
        &&!TextUtils.isEmpty(usernameEt.getText().toString())){

            SharedPreferences username=getSharedPreferences(PREF,MODE_PRIVATE);
            SharedPreferences.Editor editor=username.edit();
            editor.putString("username",usernameEt.getText().toString());
            editor.commit();

        myRef=FirebaseDatabase.getInstance().getReference("Users").child(usernameEt.getText().toString());
         myRef.child("name").setValue(nameEt.getText().toString());
         myRef.child("bio").setValue(bioEt.getText().toString());
        if(userNumber!=null)
         myRef.child("contact").setValue(userNumber);
        else if(phone.getText()!=null)
            myRef.child("contact").setValue(phone.getText());
        if(resultUri!=null){
        StorageReference images=mStorageRef.child("images").child(resultUri.getLastPathSegment());
        progressDialog=new ProgressDialog(SetProfile.this);
        progressDialog.show();
        images.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(SetProfile.this,"Profile created successfully",Toast.LENGTH_SHORT).show();
                @SuppressWarnings("VisibleForTests") Uri downloadUrl=taskSnapshot.getDownloadUrl();
                if(downloadUrl!=null)
                myRef.child("imageUri").setValue(downloadUrl.toString());
                if(userNumber!=null)
                {
                    finish();
                    startActivity(new Intent(SetProfile.this,HomePage.class));
                }
                else
                    finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });}
        if(resultUri==null)
        finish();
      }
      else
          Toast.makeText(SetProfile.this,"Your number is already registered or some required fields are empty",Toast.LENGTH_SHORT).show();
    }

}
