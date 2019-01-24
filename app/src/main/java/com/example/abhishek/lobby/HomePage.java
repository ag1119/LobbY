package com.example.abhishek.lobby;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity {

    ImageView cameraIcon,searchIcon,homeIcon,notificationIcon,profileIcon;
    SearchFragment searchFragment;
    HomeFragment homeFragment;
    NotificationFragment notificationFragment;
    ProfileFragment profileFragment;
    UploadPhoto uploadPhoto;
    String username;
    TextView notification;
    ArrayList<String> followRequest=new ArrayList<>();
    public static final String PREF="username";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        SharedPreferences sf=getSharedPreferences(PREF,MODE_PRIVATE);
        username=sf.getString("username","null");

        cameraIcon=(ImageView)findViewById(R.id.cameraIcon);
        searchIcon=(ImageView)findViewById(R.id.searchIcon);
        homeIcon=(ImageView)findViewById(R.id.homeIcon);
        notificationIcon=(ImageView)findViewById(R.id.notificationIcon);
        profileIcon=(ImageView)findViewById(R.id.profileIcon);
        notification=(TextView)findViewById(R.id.notificationText);

        searchFragment=new SearchFragment();
        homeFragment=new HomeFragment();
        notificationFragment=new NotificationFragment();
        profileFragment=new ProfileFragment();
        uploadPhoto=new UploadPhoto();

        cameraIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        searchIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        homeIcon.setColorFilter(Color.argb(255, 214, 47, 125));
        notificationIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        profileIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        setFragment(homeFragment);

        FirebaseDatabase.getInstance().getReference("Users").child(username).child("unseenNotification")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()&&Integer.parseInt(dataSnapshot.getValue(String.class))>0){
                            notification.setText(dataSnapshot.getValue(String.class));
                            notification.setVisibility(View.VISIBLE);}
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    public void onCamera(View view)
    {
        cameraIcon.setColorFilter(Color.argb(255, 214, 47, 125));
        searchIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        homeIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        notificationIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        profileIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        setFragment(uploadPhoto);
    }


    public void onSearch(View view )
    {
        cameraIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        searchIcon.setColorFilter(Color.argb(255, 214, 47, 125));
        homeIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        notificationIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        profileIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        setFragment(searchFragment);
    }

    public void onHome(View view)
    {
        cameraIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        searchIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        homeIcon.setColorFilter(Color.argb(255, 214, 47, 125));
        notificationIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        profileIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        setFragment(homeFragment);
    }

    public  void onNotification(View view)
    {
        cameraIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        searchIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        homeIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        notificationIcon.setColorFilter(Color.argb(255, 214, 47, 125));
        profileIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        setFragment(notificationFragment);

        notification.setVisibility(View.INVISIBLE);
        FirebaseDatabase.getInstance().getReference("Users").child(username).child("unseenNotification")
                .setValue("0");
    }
    public  void onProfile(View view)
    {  cameraIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        searchIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        homeIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        notificationIcon.setColorFilter(Color.argb(255, 94, 87, 87));
        profileIcon.setColorFilter(Color.argb(255, 214, 47, 125));
        setFragment(profileFragment);
    }

    public void setFragment(android.support.v4.app.Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }
}
