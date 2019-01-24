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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    public static final String PREF="username";
    public static final String PREF1="myNumber";
    CircleImageView profilePic;
    TextView user_name;
    TextView no_of_post,no_of_follower,no_of_following;
    Button edit_profile;
    View view;
    ListView lv;
    Context context;
    String username,myNumber;
    ArrayList<NewsFeed> newsFeeds=new ArrayList<>();
    Bundle b;
    myAdapter ma;
    ProgressBar progressBar;
    RelativeLayout followers,following;
    TextView bio;
    String imageUri;
    TextView noFeed;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        context=getActivity();
        SharedPreferences sf=context.getSharedPreferences(PREF,Context.MODE_PRIVATE);
        username=sf.getString("username","null");
        SharedPreferences sf1=context.getSharedPreferences(PREF1,Context.MODE_PRIVATE);
        myNumber=sf1.getString("myNumber","null");
        profilePic=(CircleImageView)view.findViewById(R.id.profilepic);
        no_of_post=(TextView)view.findViewById(R.id.noOfPosts);
        no_of_follower=(TextView)view.findViewById(R.id.noOfFollowers);
        no_of_following=(TextView)view.findViewById(R.id.noOfFollowing);
        user_name=(TextView)view.findViewById(R.id.usernameText);
        edit_profile=(Button)view.findViewById(R.id.editProfile);
        bio=(TextView)view.findViewById(R.id.bio);
        lv=(ListView)view.findViewById(R.id.listView);
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        followers=(RelativeLayout)view.findViewById(R.id.followers);
        following=(RelativeLayout)view.findViewById(R.id.following);
        noFeed=(TextView)view.findViewById(R.id.noFeed);
        ma=new myAdapter();

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(context,Followers.class));
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             startActivity(new Intent(context,Following.class));
            }
        });

        user_name.setText(username);

        FirebaseDatabase.getInstance().getReference("Users").child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child:dataSnapshot.getChildren())
                {
                    switch (child.getKey())
                    {
                        case "imageUri":
                            imageUri=child.getValue(String.class);
                            Glide.with(context).load(Uri.parse(child.getValue(String.class)))
                                    .override(120,120).centerCrop().into(profilePic);
                            break;
                        case "followers":
                            no_of_follower.setText(child.getChildrenCount()+"");
                            break;
                        case "following":
                            no_of_following.setText(child.getChildrenCount()+"");
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

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,SetProfile.class);
                startActivity(intent);
            }
        });


        lv.setAdapter(ma);
        ma.notifyDataSetChanged();
        registerForContextMenu(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position=i;
                final LinearLayout like=(LinearLayout) view.findViewById(R.id.like);
                final LinearLayout dislike=(LinearLayout)view.findViewById(R.id.dislke);
                final LinearLayout pierced=(LinearLayout)view.findViewById(R.id.pierced);

                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(context,Reactions.class);
                        intent.putExtra("type","love");
                        intent.putExtra("key",newsFeeds.get(newsFeeds.size()-1-position).getKey());
                        startActivity(intent);
                    }
                });
                dislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(context,Reactions.class);
                        intent.putExtra("type","dislike");
                        intent.putExtra("key",newsFeeds.get(newsFeeds.size()-1-position).getKey());
                        startActivity(intent);
                    }
                });
                pierced.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(context,Reactions.class);
                        intent.putExtra("type","pierced");
                        intent.putExtra("key",newsFeeds.get(newsFeeds.size()-1-position).getKey());
                        startActivity(intent);
                    }
                });
            }
        });

        FirebaseDatabase.getInstance().getReference("newsFeed")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        newsFeeds.clear();
                        int i=0;
                        for (DataSnapshot child: dataSnapshot.getChildren())
                        {
                            if(child.child("lobby_handle").getValue(String.class).equals(username))
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

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater=getActivity().getMenuInflater();
        inflater.inflate(R.menu.long_press_options,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId())
        {
            case R.id.delete:
                FirebaseDatabase.getInstance().getReference("newsFeed")
                        .child(newsFeeds.get(newsFeeds.size()-1-info.position).getKey()).removeValue();
                return true;

        }
        return super.onContextItemSelected(item);
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
                if(feed.getLobby_handle()!=null)
                lobby_handle.setText(feed.getLobby_handle());
                if(feed.aboutPic!=null)
                aboutPic.setText(feed.getAboutPic());
                if(imageUri!=null)
                Glide.with(context).load(Uri.parse(imageUri))
                        .override(80,80).centerCrop().into(profilePic);
                if(feed.getImageUri()!=null)
                Glide.with(context).load(Uri.parse(feed.getImageUri())).
                        override(400,450).centerCrop().into(contentImage);
                if(feed.getLove_count()!=null&& !feed.getLove_count().equals("0"))
                    love_count.setText(feed.getLove_count());
                if(feed.getDislike_count()!=null&& !feed.getDislike_count().equals("0"))
                    broken_count.setText(feed.getDislike_count());
                if(feed.getPierced_count()!=null&& !feed.getPierced_count().equals("0"))
                    pierced_count.setText(feed.getPierced_count());
            }

            return v;

        }
    }

}
