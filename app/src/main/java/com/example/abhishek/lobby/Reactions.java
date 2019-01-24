package com.example.abhishek.lobby;

import android.content.Context;
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

public class Reactions extends AppCompatActivity {

    TextView reaction_type;
    ListView lv;
    myAdapter ma;
    ProgressBar progressBar;
    String type,key;
    String username,myNumber;
    ArrayList<Reaction> reactions=new ArrayList<>();
    HashMap<String,String> usersProfilePic=new HashMap<>();
    HashMap<String,String> usersName=new HashMap<>();
    public static final String PREF="username";
    public static final String PREF1="myNumber";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reactions);

        reaction_type=(TextView)findViewById(R.id.reaction_type);
        lv=(ListView)findViewById(R.id.listView);
        ma=new myAdapter();
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        SharedPreferences sf=getSharedPreferences(PREF,MODE_PRIVATE);
        username=sf.getString("username","null");

        SharedPreferences sf1=getSharedPreferences(PREF1,MODE_PRIVATE);
        myNumber=sf1.getString("myNumber","null");

        type=getIntent().getExtras().getString("type");
        key=getIntent().getExtras().getString("key");

        if(type!=null)
        {
            switch (type)
            {
                case "love":
                    reaction_type.setText("Love");
                    break;
                case "dislike":
                    reaction_type.setText("Dislike");
                    break;
                case "pierced":
                    reaction_type.setText("Pierced");
                    break;
            }
        }

        FirebaseDatabase.getInstance().getReference("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child:dataSnapshot.getChildren())
                        {
                            usersProfilePic.put(child.getKey(),child.child("imageUri").getValue(String.class));
                            usersName.put(child.getKey(),child.child("name").getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        if(key!=null&&type!=null)
        {
            FirebaseDatabase.getInstance().getReference("newsFeed").child(key).child("reactions")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            reactions.clear();
                            for(DataSnapshot child:dataSnapshot.getChildren())
                            {
                                if(child.child("type").getValue(String.class).equals(type)&&
                                        !child.child("lobby_handle").getValue(String.class).equals(username))
                                    reactions.add(child.getValue(Reaction.class));
                            }
                            ma.notifyDataSetChanged();
                            if(reactions.size()==0)
                                progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        lv.setAdapter(ma);
        ma.notifyDataSetChanged();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User_info info=new User_info();
                info.setContact(reactions.get(i).getContact());
                info.setLobby_handle(reactions.get(i).getLobby_handle());
                Intent intent=new Intent(Reactions.this,FriendsProfile.class);
                intent.putExtra("friendInfo",info);
                startActivity(intent);
            }
        });

    }


    public class myAdapter extends ArrayAdapter {
        myAdapter(){
            super(Reactions.this,R.layout.cust_search);
        }

        @Override
        public int getCount() {
            return reactions.size();
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
            Reaction object=reactions.get(position);
            if(object!=null)
            {
                if(usersName.get(object.getLobby_handle())!=null)
                    name.setText(usersName.get(object.getLobby_handle()));
                if(object.getLobby_handle()!=null)
                    lobby_handle.setText(object.getLobby_handle());
                if(usersProfilePic.get(object.getLobby_handle())!=null)
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    Glide.with(Reactions.this).load(Uri.parse(usersProfilePic.get(object.getLobby_handle()))).override(80,80).centerCrop().into(img);
                }
            }
            else
                progressBar.setVisibility(View.INVISIBLE);


            return v;

        }
    }
}
