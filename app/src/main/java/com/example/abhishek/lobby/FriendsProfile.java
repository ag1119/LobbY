package com.example.abhishek.lobby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsProfile extends AppCompatActivity {

    public static final String PREF="username";
    public static final String PREF1="myNumber";
    TextView nameText;
    CircleImageView profilepic;
    Button follow;
    String username,myNumber;
    User_info info;
    String mySR="nu",friendRR="ll";
    TextView no_of_follower,no_of_following,no_of_post;
    TextView bio;
     TextView blank;
    ArrayList<NewsFeed> newsFeeds=new ArrayList<>();
    ListView lv;
    myAdapter ma;
    ProgressBar progressBar;
    TextView noFeed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_profile);
        nameText=(TextView)findViewById(R.id.nameText);
        profilepic=(CircleImageView)findViewById(R.id.profilepic);
        follow=(Button)findViewById(R.id.followBtn);
        no_of_follower=(TextView)findViewById(R.id.noOfFollowers);
        no_of_following=(TextView)findViewById(R.id.noOfFollowing);
        no_of_post=(TextView)findViewById(R.id.noOfPosts);
        bio=(TextView)findViewById(R.id.bio);
        lv=(ListView)findViewById(R.id.listView);
        blank=(TextView)findViewById(R.id.blank);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        noFeed=(TextView)findViewById(R.id.noFeed);
        ma=new myAdapter();

        SharedPreferences sf=getSharedPreferences(PREF,MODE_PRIVATE);
        username=sf.getString("username","null");
        SharedPreferences sf1=getSharedPreferences(PREF1,MODE_PRIVATE);
        myNumber=sf1.getString("myNumber","null");
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                follow.setBackground(ContextCompat.getDrawable(FriendsProfile.this,R.drawable.cust_btn2));
                follow.setText("Request sent");
                follow.setTextColor(ContextCompat.getColor(FriendsProfile.this,R.color.hint_color));
                follow.setEnabled(false);
                FirebaseDatabase.getInstance().getReference("Users").child(username).child("sentRequestStatus").
                        child(info.getContact()).setValue("0");

                            Notification notification=new Notification();
                            notification.setLobby_handle(username);
                            notification.setContact(myNumber);
                            notification.setType("followRequest");
                        FirebaseDatabase.getInstance().getReference("Users").child(info.getLobby_handle())
                                .child("notification").push().setValue(notification);
                        FirebaseDatabase.getInstance().getReference("Users").child(info.lobby_handle).child("receivedRequestStatus").
                                child(myNumber).setValue("0");
                FirebaseDatabase.getInstance().getReference("Users").child(info.getLobby_handle()).child("unseenNotification")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                               int count;
                                if(!dataSnapshot.exists())
                                    FirebaseDatabase.getInstance().getReference("Users").child(info.getLobby_handle())
                                    .child("unseenNotification").setValue("1");
                                else
                                {
                                   count=Integer.parseInt(dataSnapshot.getValue(String.class));
                                    count++;
                                    FirebaseDatabase.getInstance().getReference("Users").child(info.getLobby_handle())
                                            .child("unseenNotification").setValue(count+"");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            }
        });
        info=(User_info) getIntent().getSerializableExtra("friendInfo");
        if(info.getName()!=null)
        nameText.setText(info.getName());
        if(info.getImageUri()!=null)
            Glide.with(this).load(Uri.parse(info.getImageUri())).override(80,80).centerCrop().into(profilepic);
        if(info.getLobby_handle()!=null)
        {
            FirebaseDatabase.getInstance().getReference("Users").child(info.getLobby_handle())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot child:dataSnapshot.getChildren())
                            {
                                switch (child.getKey())
                                {
                                    case "name":
                                        nameText.setText(child.getValue(String.class));
                                        break;
                                    case "imageUri":
                                        info.setImageUri(child.getValue(String.class));
                                        Glide.with(FriendsProfile.this).load(Uri.parse(child.
                                                getValue(String.class))).override(80,80).centerCrop().into(profilepic);
                                        break;
                                    case "receivedRequestStatus":
                                        friendRR=child.child(myNumber).getValue(String.class);
                                        break;
                                    case "followers":
                                        no_of_follower.setText(child.getChildrenCount()+"");
                                        break;
                                    case "following":
                                        no_of_following.setText(child.getChildrenCount()+"");
                                        break;
                                    case "newsFeed":
                                        no_of_post.setText(child.getChildrenCount()+"");
                                        break;
                                    case "bio":
                                        bio.setText(child.getValue(String.class));
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            FirebaseDatabase.getInstance().getReference("Users").child(username).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        for (DataSnapshot child:dataSnapshot.getChildren())
                        {
                            switch (child.getKey())
                            {
                                case "sentRequestStatus":
                                    mySR=child.child(info.getContact()).getValue(String.class);
                                    break;

                            }
                           // if(child.getKey().equals("sentRequestStatus"))
                              //  mySR=child.child(info.getContact()).getValue(String.class);

                            switch (mySR+friendRR)
                            {
                                case "00":follow.setBackground(ContextCompat.getDrawable(FriendsProfile.this,R.drawable.cust_btn2));
                                    follow.setText("Request sent");
                                    follow.setTextColor(ContextCompat.getColor(FriendsProfile.this,R.color.hint_color));
                                    follow.setEnabled(false);
                                    break;
                                case "11":follow.setBackground(ContextCompat.getDrawable(FriendsProfile.this,R.drawable.cust_btn2));
                                    follow.setText("Following");
                                    follow.setTextColor(ContextCompat.getColor(FriendsProfile.this,R.color.hint_color));
                                    follow.setEnabled(false);
                                    break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        FirebaseDatabase.getInstance().getReference("newsFeed")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        newsFeeds.clear();
                        int i=0;
                        for (DataSnapshot child: dataSnapshot.getChildren())
                        {
                            if(child.child("lobby_handle").getValue(String.class).equals(info.getLobby_handle()))
                            {
                                newsFeeds.add(child.getValue(NewsFeed.class));
                                newsFeeds.get(i).setKey(child.getKey());
                                i++;
                            }

                        }
                        if(newsFeeds.size()==0)
                        {
                            progressBar.setVisibility(View.INVISIBLE);
                            noFeed.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            noFeed.setVisibility(View.INVISIBLE);
                            no_of_post.setText(newsFeeds.size()+"");
                        }

                        ma.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("Users").child(info.getLobby_handle()).child("followers")
                .child(myNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()||newsFeeds.size()==0)
                        {
                            blank.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            lv.setVisibility(View.VISIBLE);
                            lv.setEnabled(true);
                            lv.setAdapter(ma);
                            ma.notifyDataSetChanged();
                        }
                        else
                            progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position=i;
                final ImageView love=(ImageView)view.findViewById(R.id.love);
                final ImageView broken=(ImageView)view.findViewById(R.id.broken);
                final ImageView piercedImg=(ImageView)view.findViewById(R.id.pierced_heart);
                final LinearLayout like=(LinearLayout) view.findViewById(R.id.like);
                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FirebaseDatabase.getInstance().getReference("newsFeed")
                                .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("reactions")
                                .child(myNumber)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()&&dataSnapshot.child("type").getValue(String.class).equals("love"))
                                        {
                                            love.setColorFilter(Color.argb(255,240,240,240));
                                            FirebaseDatabase.getInstance().getReference("newsFeed")
                                                    .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("reactions")
                                                    .child(myNumber).removeValue();
                                            int love_count=Integer.parseInt(newsFeeds.get(newsFeeds.size()-1-position).getLove_count());
                                            love_count--;
                                            FirebaseDatabase.getInstance().getReference("newsFeed")
                                                    .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("love_count")
                                                    .setValue(""+love_count);
                                        }
                                        else
                                        {
                                            love.setColorFilter(Color.argb(255,214,47,125));
                                            broken.setColorFilter(Color.argb(255,240,240,240));
                                            piercedImg.setColorFilter(Color.argb(255,240,240,240));
                                            Reaction r=new Reaction();
                                            r.setType("love");
                                            r.setLobby_handle(username);
                                            r.setContact(myNumber);
                                            FirebaseDatabase.getInstance().getReference("newsFeed")
                                                    .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("reactions")
                                                    .child(myNumber).setValue(r);
                                            int love_count=Integer.parseInt(newsFeeds.get(newsFeeds.size()-1-position).getLove_count());
                                            love_count++;
                                            FirebaseDatabase.getInstance().getReference("newsFeed")
                                                    .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("love_count")
                                                    .setValue(""+love_count);

                                            if(dataSnapshot.exists())
                                            {
                                                switch (dataSnapshot.child("type").getValue(String.class))
                                                {
                                                    case "dislike":
                                                        int dislike_count=Integer.parseInt(newsFeeds.get(newsFeeds.size()-1-position).getDislike_count());
                                                        dislike_count--;
                                                        FirebaseDatabase.getInstance().getReference("newsFeed")
                                                                .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("dislike_count")
                                                                .setValue(""+dislike_count);
                                                        break;
                                                    case "pierced":
                                                        int pierced_count=Integer.parseInt(newsFeeds.get(newsFeeds.size()-1-position).getPierced_count());
                                                        pierced_count--;
                                                        FirebaseDatabase.getInstance().getReference("newsFeed")
                                                                .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("pierced_count")
                                                                .setValue(""+pierced_count);
                                                        break;

                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                    }
                });

                final LinearLayout dislike=(LinearLayout)view.findViewById(R.id.dislke);
                dislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FirebaseDatabase.getInstance().getReference("newsFeed")
                                .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("reactions")
                                .child(myNumber)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()&&dataSnapshot.child("type").getValue(String.class).equals("dislike"))
                                        {
                                            broken.setColorFilter(Color.argb(255,240,240,240));
                                            FirebaseDatabase.getInstance().getReference("newsFeed")
                                                    .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("reactions")
                                                    .child(myNumber).removeValue();
                                            int dislike_count=Integer.parseInt(newsFeeds.get(newsFeeds.size()-1-position).getDislike_count());
                                            dislike_count--;
                                            FirebaseDatabase.getInstance().getReference("newsFeed")
                                                    .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("dislike_count")
                                                    .setValue(""+dislike_count);
                                        }
                                        else
                                        {
                                            love.setColorFilter(Color.argb(255,240,240,240));
                                            broken.setColorFilter(Color.argb(255,0,0,0));
                                            piercedImg.setColorFilter(Color.argb(255,240,240,240));
                                            Reaction r=new Reaction();
                                            r.setType("dislike");
                                            r.setLobby_handle(username);
                                            r.setContact(myNumber);
                                            FirebaseDatabase.getInstance().getReference("newsFeed")
                                                    .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("reactions")
                                                    .child(myNumber).setValue(r);
                                            int dislike_count=Integer.parseInt(newsFeeds.get(newsFeeds.size()-1-position).getDislike_count());
                                            dislike_count++;
                                            FirebaseDatabase.getInstance().getReference("newsFeed")
                                                    .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("dislike_count")
                                                    .setValue(""+dislike_count);
                                            if(dataSnapshot.exists())
                                            {
                                                switch (dataSnapshot.child("type").getValue(String.class))
                                                {
                                                    case "love":
                                                        int love_count=Integer.parseInt(newsFeeds.get(newsFeeds.size()-1-position).getLove_count());
                                                        love_count--;
                                                        FirebaseDatabase.getInstance().getReference("newsFeed")
                                                                .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("love_count")
                                                                .setValue(""+love_count);
                                                        break;
                                                    case "pierced":
                                                        int pierced_count=Integer.parseInt(newsFeeds.get(newsFeeds.size()-1-position).getPierced_count());
                                                        pierced_count--;
                                                        FirebaseDatabase.getInstance().getReference("newsFeed")
                                                                .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("pierced_count")
                                                                .setValue(""+pierced_count);
                                                        break;

                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                });

                final LinearLayout pierced=(LinearLayout)view.findViewById(R.id.pierced);
                pierced.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FirebaseDatabase.getInstance().getReference("newsFeed")
                                .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("reactions")
                                .child(myNumber)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()&&dataSnapshot.child("type").getValue(String.class).equals("pierced"))
                                        {
                                            piercedImg.setColorFilter(Color.argb(255,240,240,240));
                                            FirebaseDatabase.getInstance().getReference("newsFeed")
                                                    .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("reactions")
                                                    .child(myNumber).removeValue();
                                            int pierced_count=Integer.parseInt(newsFeeds.get(newsFeeds.size()-1-position).getPierced_count());
                                            pierced_count--;
                                            FirebaseDatabase.getInstance().getReference("newsFeed")
                                                    .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("pierced_count")
                                                    .setValue(""+pierced_count);
                                        }
                                        else
                                        {
                                            love.setColorFilter(Color.argb(255,240,240,240));
                                            broken.setColorFilter(Color.argb(255,240,240,240));
                                            piercedImg.setColorFilter(Color.argb(255,244,67,54));
                                            Reaction r=new Reaction();
                                            r.setType("pierced");
                                            r.setLobby_handle(username);
                                            r.setContact(myNumber);
                                            FirebaseDatabase.getInstance().getReference("newsFeed")
                                                    .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("reactions")
                                                    .child(myNumber).setValue(r);
                                            int pierced_count=Integer.parseInt(newsFeeds.get(newsFeeds.size()-1-position).getPierced_count());
                                            pierced_count++;
                                            FirebaseDatabase.getInstance().getReference("newsFeed")
                                                    .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("pierced_count")
                                                    .setValue(""+pierced_count);
                                            if(dataSnapshot.exists())
                                            {
                                                switch (dataSnapshot.child("type").getValue(String.class))
                                                {
                                                    case "love":
                                                        int love_count=Integer.parseInt(newsFeeds.get(newsFeeds.size()-1-position).getLove_count());
                                                        love_count--;
                                                        FirebaseDatabase.getInstance().getReference("newsFeed")
                                                                .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("love_count")
                                                                .setValue(""+love_count);
                                                        break;
                                                    case "dislike":
                                                        int dislike_count=Integer.parseInt(newsFeeds.get(newsFeeds.size()-1-position).getDislike_count());
                                                        dislike_count--;
                                                        FirebaseDatabase.getInstance().getReference("newsFeed")
                                                                .child(newsFeeds.get(newsFeeds.size()-1-position).getKey()).child("dislike_count")
                                                                .setValue(""+dislike_count);
                                                        break;

                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                });
            }
        });
    }

    public class myAdapter extends ArrayAdapter {
        myAdapter(){
            super(FriendsProfile.this,R.layout.cust_listview);
        }

        @Override
        public int getCount() {
            return newsFeeds.size();
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
            View v = inflater.inflate(R.layout.cust_listview,parent,false);
            TextView lobby_handle=(TextView)v.findViewById(R.id.lobby_handle);
            TextView love_count=(TextView)v.findViewById(R.id.love_count);
            TextView broken_count=(TextView)v.findViewById(R.id.broken_heart_count);
            TextView pierced_count=(TextView)v.findViewById(R.id.pierced_heart_count);
            TextView aboutPic=(TextView)v.findViewById(R.id.aboutPic);
            CircleImageView profilePic=(CircleImageView)v.findViewById(R.id.profilepic);
            ImageView contentImage=(ImageView)v.findViewById(R.id.contentImage);
            ImageView love=(ImageView)v.findViewById(R.id.love);
            ImageView broken=(ImageView)v.findViewById(R.id.broken);
            ImageView pierced=(ImageView)v.findViewById(R.id.pierced_heart);

            love.setColorFilter(Color.argb(255,240,240,240));
            broken.setColorFilter(Color.argb(255,240,240,240));
            pierced.setColorFilter(Color.argb(255,240,240,240));

            NewsFeed feed=newsFeeds.get(newsFeeds.size()-1-position);
            if(feed!=null)
            {
                progressBar.setVisibility(View.INVISIBLE);
                noFeed.setVisibility(View.INVISIBLE);
                love.setColorFilter(Color.argb(255,240,240,240));
                broken.setColorFilter(Color.argb(255,240,240,240));
                pierced.setColorFilter(Color.argb(255,240,240,240));

                if(feed.getReactions()!=null)
                    if(feed.getReactions().containsKey(myNumber))
                    {
                        switch (feed.getReactions().get(myNumber).getType())
                        {
                            case "love":
                                love.setColorFilter(Color.argb(255,214,47,125));
                                broken.setColorFilter(Color.argb(255,240,240,240));
                                pierced.setColorFilter(Color.argb(255,240,240,240));
                                break;
                            case "dislike":
                                love.setColorFilter(Color.argb(255,240,240,240));
                                broken.setColorFilter(Color.argb(255,0,0,0));
                                pierced.setColorFilter(Color.argb(255,240,240,240));
                                break;
                            case "pierced":
                                love.setColorFilter(Color.argb(255,240,240,240));
                                broken.setColorFilter(Color.argb(255,240,240,240));
                                pierced.setColorFilter(Color.argb(255,244,67,54));
                                break;
                        }
                    }

                lobby_handle.setText(feed.getLobby_handle());
                aboutPic.setText(feed.getAboutPic());
                if(info.getImageUri()!=null)
                Glide.with(FriendsProfile.this).load(Uri.parse(info.getImageUri()))
                        .override(80,80).centerCrop().into(profilePic);
                if(feed.getImageUri()!=null)
                Glide.with(FriendsProfile.this).load(Uri.parse(feed.getImageUri()))
                        .override(400,450).centerCrop().into(contentImage);
                if(feed.getLove_count()!=null&& !feed.getLove_count().equals("0"))
                    love_count.setText(feed.getLove_count());
                if(feed.getDislike_count()!=null&& !feed.getDislike_count().equals("0"))
                    broken_count.setText(feed.getDislike_count());
                if(feed.getPierced_count()!=null&& !feed.getPierced_count().equals("0"))
                    pierced_count.setText(feed.getPierced_count());
            }
            else
                progressBar.setVisibility(View.INVISIBLE);

            return v;

        }
    }
}
