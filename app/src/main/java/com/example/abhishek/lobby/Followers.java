package com.example.abhishek.lobby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Followers extends AppCompatActivity {

    public static final String PREF="username";
    String username;
    ListView lv;
    myAdapter ma;
    ProgressBar progressBar;
    HashMap<String,String> followers=new HashMap<>();
    ArrayList<User_info> user_info=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        lv=(ListView)findViewById(R.id.listView);
        ma=new myAdapter();
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        SharedPreferences sf=getSharedPreferences(PREF,MODE_PRIVATE);
        username=sf.getString("username","null");

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Followers.this,FriendsProfile.class);
                intent.putExtra("friendInfo",user_info.get(i));
                startActivity(intent);
            }
        });

        lv.setAdapter(ma);
        ma.notifyDataSetChanged();

        FirebaseDatabase.getInstance().getReference("Users").child(username).child("followers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child:dataSnapshot.getChildren())
                        {
                            followers.put(child.getValue(String.class),"true");
                        }
                        if(followers.size()==0)
                            progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        FirebaseDatabase.getInstance().getReference("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int i=0;
                        for(DataSnapshot child:dataSnapshot.getChildren())
                        {
                            if(followers.containsKey(child.getKey()))
                            {
                                user_info.add(child.getValue(User_info.class));
                                user_info.get(i).setLobby_handle(child.getKey());
                                i++;
                            }
                        }
                        ma.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public class myAdapter extends ArrayAdapter {
        myAdapter(){
            super(Followers.this,R.layout.cust_search);
        }

        @Override
        public int getCount() {
            return user_info.size();
        }

        @Nullable
        @Override
        public Object getItem(int position) {
            return super.getItem(position);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View v = inflater.inflate(R.layout.cust_search,parent,false);
            TextView name=(TextView)v.findViewById(R.id.name);
            TextView lobby_handle=(TextView)v.findViewById(R.id.lobby_handle);
            CircleImageView img=(CircleImageView)v.findViewById(R.id.profilepic);
            User_info object=user_info.get(position);
            if(object!=null)
            {
            if(object.getName()!=null)
                name.setText(object.getName());
            if(object.getLobby_handle()!=null)
                lobby_handle.setText(object.getLobby_handle());
            if(object.getImageUri()!=null)
              {
                progressBar.setVisibility(View.INVISIBLE);
                Glide.with(Followers.this).load(Uri.parse(object.getImageUri())).override(80,80).centerCrop().into(img);
              }
            }
            else
             progressBar.setVisibility(View.INVISIBLE);


            return v;

        }
    }
}
