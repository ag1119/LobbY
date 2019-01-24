package com.example.abhishek.lobby;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class SearchFragment extends Fragment {

    public static final String PREF="username";
    View view;
    Context context;
    Bundle b;
    EditText search;
    ListView lv;
    myAdapter ma;
    String username;
    ArrayList<User_info> user_info=new ArrayList<>();
    ArrayList<User_info> srchList=new ArrayList<>();
    int count=0;
    public SearchFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);
        context=getActivity();
        b=savedInstanceState;
        SharedPreferences sf=context.getSharedPreferences(PREF,Context.MODE_PRIVATE);
        username=sf.getString("username","null");


        lv=(ListView)view.findViewById(R.id.listView);
        search=(EditText)view.findViewById(R.id.search);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(context,FriendsProfile.class);
                intent.putExtra("friendInfo",srchList.get(i));
                startActivity(intent);
            }
        });

        ma=new myAdapter();
        lv.setAdapter(ma);

        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_info.clear();
                for(DataSnapshot child:dataSnapshot.getChildren())
                {
                    User_info i=new User_info();
                    if(!child.getKey().equals(username))
                    {
                        i.setLobby_handle(child.getKey());
                        i.setName(child.child("name").getValue(String.class));
                        i.setImageUri(child.child("imageUri").getValue(String.class));
                        i.setContact(child.child("contact").getValue(String.class));
                        user_info.add(i);
                    }
                }
                ma.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                srchList.clear();
                for(int j=0;j<user_info.size();j++)
                {
                    if ((user_info.get(j).getName().toLowerCase()).indexOf(search.getText().toString().toLowerCase())>-1
                    ||(user_info.get(j).getLobby_handle().toLowerCase()).indexOf(search.getText().toString().toLowerCase())>-1)

                        if(search.getText().toString().length()>0)
                            srchList.add(user_info.get(j));
                        else
                            srchList.clear();
                }
                ma.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return view;
    }


    public class myAdapter extends ArrayAdapter {
        myAdapter(){
            super(context,R.layout.cust_search);
        }

        @Override
        public int getCount() {
            return srchList.size();
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
            View v = inflater.inflate(R.layout.cust_search,parent,false);
            TextView name=(TextView)v.findViewById(R.id.name);
            TextView lobby_handle=(TextView)v.findViewById(R.id.lobby_handle);
            CircleImageView img=(CircleImageView)v.findViewById(R.id.profilepic);
            User_info object=srchList.get(position);
            if(object.getName()!=null)
            name.setText(object.getName());
            if(object.getLobby_handle()!=null)
            lobby_handle.setText(object.getLobby_handle());
            if(object.getImageUri()!=null)
                Glide.with(context).load(Uri.parse(object.getImageUri())).override(80,80).centerCrop().into(img);


            return v;

        }
    }

}
