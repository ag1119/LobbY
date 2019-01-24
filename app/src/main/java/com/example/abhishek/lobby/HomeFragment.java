package com.example.abhishek.lobby;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.data;


public class HomeFragment extends Fragment {

    View view;
    ArrayList<NewsFeed> newsFeeds=new ArrayList<>();
    Bundle b;
    Context context;
    ListView lv;
    myAdapter ma;
    String username;
    ProgressBar progressBar;
    HashMap<String,String> following=new HashMap<>();
    HashMap<String,String> usersProfilePic=new HashMap<>();
    String myNumber;
    TextView noFeed;
    ImageView chat;
    public static final String PREF="username";
    public static final String PREF1="myNumber";

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_home, container, false);
        b=savedInstanceState;
        context=getActivity();
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        lv=(ListView)view.findViewById(R.id.listView);
        ma=new myAdapter();
        noFeed=(TextView)view.findViewById(R.id.noFeed);
        chat=(ImageView)view.findViewById(R.id.chat);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,ChatHome.class));
            }
        });

        SharedPreferences sf=context.getSharedPreferences(PREF,Context.MODE_PRIVATE);
        username=sf.getString("username","null");

        SharedPreferences sf1=context.getSharedPreferences(PREF1,Context.MODE_PRIVATE);
        myNumber=sf1.getString("myNumber","null");

        lv.setAdapter(ma);
        ma.notifyDataSetChanged();

        FirebaseDatabase.getInstance().getReference("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       for(DataSnapshot child:dataSnapshot.getChildren())
                           usersProfilePic.put(child.getKey(),child.child("imageUri").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("Users").child(username).child("following")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child:dataSnapshot.getChildren())
                        {
                            following.put(child.getValue(String.class),"true");
                        }
                        FirebaseDatabase.getInstance().getReference("newsFeed")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        newsFeeds.clear();
                                        int i=0;
                                        for (DataSnapshot child: dataSnapshot.getChildren())
                                        {
                                            if(following.containsKey(child.child("lobby_handle").getValue(String.class))||
                                                    child.child("lobby_handle").getValue(String.class).equals(username))
                                            {
                                                newsFeeds.add(child.getValue(NewsFeed.class));
                                                newsFeeds.get(i).setKey(child.getKey());
                                                i++;
                                            }

                                        }
                                        ma.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                        if(newsFeeds.size()==0)
                        {
                            progressBar.setVisibility(View.INVISIBLE);
                            noFeed.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            noFeed.setVisibility(View.INVISIBLE);
                        }
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
                final TextView lobby_handle=(TextView)view.findViewById(R.id.lobby_handle);
                lobby_handle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        User_info info=new User_info();
                        info.setLobby_handle(newsFeeds.get(newsFeeds.size()-1-position).getLobby_handle());
                        info.setImageUri(newsFeeds.get(newsFeeds.size()-1-position).getUser_profile_image());
                        info.setContact(newsFeeds.get(newsFeeds.size()-1-position).getContact());
                        if(!newsFeeds.get(newsFeeds.size()-1-position).getLobby_handle().equals(username))
                        {
                            Intent intent=new Intent(context,FriendsProfile.class);
                            intent.putExtra("friendInfo",info);
                            startActivity(intent);
                        }
                    }
                });

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
                final TextView viewComments=(TextView)view.findViewById(R.id.viewComments);
                viewComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(context,Comments.class);
                        intent.putExtra("newsFeed",newsFeeds.get(newsFeeds.size()-1-position));
                        startActivity(intent);
                    }
                });
            }
        });



        return view;
    }

    public class myAdapter extends ArrayAdapter {
        myAdapter(){
            super(context,R.layout.cust_listview);
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

            LayoutInflater inflater = getLayoutInflater(b);
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
            TextView date=(TextView)v.findViewById(R.id.date);
            TextView time=(TextView)v.findViewById(R.id.time);

            NewsFeed feed=newsFeeds.get(newsFeeds.size()-1-position);
            if(feed!=null) {
                progressBar.setVisibility(View.INVISIBLE);
                noFeed.setVisibility(View.INVISIBLE);
                love.setColorFilter(Color.argb(255, 240, 240, 240));
                broken.setColorFilter(Color.argb(255, 240, 240, 240));
                pierced.setColorFilter(Color.argb(255, 240, 240, 240));

                if (feed.getReactions() != null)
                    if (feed.getReactions().containsKey(myNumber)) {
                        switch (feed.getReactions().get(myNumber).getType()) {
                            case "love":
                                love.setColorFilter(Color.argb(255, 214, 47, 125));
                                broken.setColorFilter(Color.argb(255, 240, 240, 240));
                                pierced.setColorFilter(Color.argb(255, 240, 240, 240));
                                break;
                            case "dislike":
                                love.setColorFilter(Color.argb(255, 240, 240, 240));
                                broken.setColorFilter(Color.argb(255, 0, 0, 0));
                                pierced.setColorFilter(Color.argb(255, 240, 240, 240));
                                break;
                            case "pierced":
                                love.setColorFilter(Color.argb(255, 240, 240, 240));
                                broken.setColorFilter(Color.argb(255, 240, 240, 240));
                                pierced.setColorFilter(Color.argb(255, 244, 67, 54));
                                break;
                        }
                    }


                if (feed.getLobby_handle() != null)
                    lobby_handle.setText(feed.getLobby_handle());
                if (feed.getAboutPic() != null)
                    aboutPic.setText(feed.getAboutPic());
                if (feed.getUser_profile_image() != null)
                    Glide.with(context).load(Uri.parse(usersProfilePic.get(feed.getLobby_handle()))).
                            override(80, 80).centerCrop().into(profilePic);
                if (feed.getImageUri() != null)
                    Glide.with(context).load(Uri.parse(feed.getImageUri())).override(400, 450).centerCrop().into(contentImage);
                if (feed.getLove_count() != null && !feed.getLove_count().equals("0"))
                    love_count.setText(feed.getLove_count());
                if (feed.getDislike_count() != null && !feed.getDislike_count().equals("0"))
                    broken_count.setText(feed.getDislike_count());
                if (feed.getPierced_count() != null && !feed.getPierced_count().equals("0"))
                    pierced_count.setText(feed.getPierced_count());
                if (feed.getTime() != null) {
                    time.setVisibility(View.VISIBLE);
                    time.setText(feed.getTime());
                }
                if (feed.getDate() != null)
                {
                    date.setVisibility(View.VISIBLE);
                    date.setText(feed.getDate());
                }
            }
            else
                progressBar.setVisibility(View.INVISIBLE);

            return v;

        }
    }
}
