package com.example.abhishek.lobby;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import static android.content.Context.MODE_PRIVATE;
import static com.example.abhishek.lobby.R.id.lobby_handle;
import static com.example.abhishek.lobby.R.id.myProfile;


public class NotificationFragment extends Fragment {


    public static final String PREF="username";
    public static final String PREF1="myNumber";
    View view;
    TextView confirmRequest;
    ListView lv;
    Context context;
    Bundle b;
    String username,myNumber;
    myAdapter ma;
   ArrayList<Notification> notifications=new ArrayList<>();
    HashMap<String,String> usersProfilePic=new HashMap<>();
    public NotificationFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_notification, container, false);
        b=savedInstanceState;
        context=getActivity();
        lv=(ListView)view.findViewById(R.id.listView);
        confirmRequest=(TextView)view.findViewById(R.id.confirmRequest);

        FirebaseDatabase.getInstance().getReference("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child:dataSnapshot.getChildren())
                            usersProfilePic.put(child.getKey(),child.child("imageUri").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              final TextView confirm=(TextView)view.findViewById(R.id.confirmRequest);
                final LinearLayout friendProfile=(LinearLayout)view.findViewById(R.id.friendProfile);
                final int position=i;
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        confirm.setBackground(ContextCompat.getDrawable(context,R.drawable.cust_btn2));
                        confirm.setText("Confirmed");
                        confirm.setTextColor(ContextCompat.getColor(context,R.color.hint_color));
                        confirm.setEnabled(false);

                        Notification notification=new Notification();
                        notification.setLobby_handle(username);
                        notification.setContact(myNumber);
                        notification.setType("requestAccepted");

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(username).child("followers").child(notifications.get(notifications.size()-1-position)
                                .getContact()).setValue(notifications.get(notifications.size()-1-position).getLobby_handle());

                        FirebaseDatabase.getInstance().getReference("Users").
                                child(notifications.get(notifications.size()-1-position).getLobby_handle())
                                .child("sentRequestStatus").child(myNumber).setValue("1");

                        FirebaseDatabase.getInstance().getReference("Users").
                                child(notifications.get(notifications.size()-1-position).getLobby_handle())
                                .child("notification").push().setValue(notification);

                        FirebaseDatabase.getInstance().getReference("Users").
                                child(notifications.get(notifications.size()-1-position).getLobby_handle())
                                .child("following").child(myNumber).setValue(username);

                        FirebaseDatabase.getInstance().getReference("Users").
                                child(username).child("receivedRequestStatus").
                                child(notifications.get(notifications.size()-1-position).getContact()).setValue("1");
                        FirebaseDatabase.getInstance().getReference("Users").child(username)
                                .child("notification").child(notifications.get(notifications.size()-1-position).getKey())
                                .removeValue();

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(notifications.get(notifications.size()-1-position).getLobby_handle())
                                .child("unseenNotification")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int count;
                                        if(!dataSnapshot.exists())
                                            FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(notifications.get(notifications.size()-1-position).getLobby_handle())
                                                    .child("unseenNotification").setValue("1");
                                        else
                                        {
                                            count=Integer.parseInt(dataSnapshot.getValue(String.class));
                                            count++;
                                            FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(notifications.get(notifications.size()-1-position).getLobby_handle())
                                                    .child("unseenNotification").setValue(count+"");
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                });

                friendProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        User_info info=new User_info();
                        info.setLobby_handle(notifications.get(notifications.size()-1-position).getLobby_handle());
                        info.setContact(notifications.get(notifications.size()-1-position).getContact());
                        Intent intent=new Intent(context,FriendsProfile.class);
                        intent.putExtra("friendInfo",info);
                        startActivity(intent);
                    }
                });
            }
        });

        SharedPreferences sf=context.getSharedPreferences(PREF,MODE_PRIVATE);
        username=sf.getString("username","null");
        SharedPreferences sf1=context.getSharedPreferences(PREF1,MODE_PRIVATE);
        myNumber=sf1.getString("myNumber","null");

        ma=new myAdapter();
        lv.setAdapter(ma);

        FirebaseDatabase.getInstance().getReference("Users").child(username)
                .child("notification").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              notifications.clear();
                int i=0;
                for(DataSnapshot child: dataSnapshot.getChildren())
              {
                  notifications.add(child.getValue(Notification.class));
                  notifications.get(i).setKey(child.getKey());
                  i++;
              }
                ma.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    public class myAdapter extends ArrayAdapter {
        myAdapter(){
            super(context,R.layout.cust_notification);
        }

        @Override
        public int getCount() {
            return notifications.size();
        }

        @Nullable
        @Override
        public Object getItem(int position) {
            return super.getItem(position);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

           Notification nf;
            LayoutInflater inflater = getLayoutInflater(b);
            View v = inflater.inflate(R.layout.cust_notification,parent,false);
            TextView lobby_handle=(TextView)v.findViewById(R.id.lobby_handle);
            TextView confirm=(TextView)v.findViewById(R.id.confirmRequest);
            TextView tv1=(TextView)v.findViewById(R.id.tv1);
            CircleImageView profilePic=(CircleImageView)v.findViewById(R.id.profilepic);

            if(notifications.size()-1-position>=0)
            {
                nf=notifications.get(notifications.size()-1-position);
                switch (nf.getType())
                {
                    case "followRequest":
                        lobby_handle.setText(nf.getLobby_handle())
                        ;
                        break;
                    case "requestAccepted":
                        confirm.setEnabled(false);
                        confirm.setVisibility(View.INVISIBLE);
                        lobby_handle.setText(nf.getLobby_handle());
                        tv1.setText("accepted your follow request");
                        break;
                }
                if(usersProfilePic.get(nf.getLobby_handle())!=null)
                Glide.with(context).load(Uri.parse(usersProfilePic.get(nf.getLobby_handle())))
                        .override(80,80).centerCrop().into(profilePic);
            }

            return v;

        }
    }

}
