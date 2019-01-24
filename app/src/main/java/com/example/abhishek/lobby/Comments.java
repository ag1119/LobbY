package com.example.abhishek.lobby;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

public class Comments extends AppCompatActivity {

    NewsFeed newsFeed;
    ListView lv;
    myAdapter ma;
    EditText commentText;
    TextView post;
    ProgressBar progressBar;
    ArrayList<Comment> comments=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        newsFeed=(NewsFeed)getIntent().getSerializableExtra("newsFeed");
        lv=(ListView)findViewById(R.id.listView);
        post=(TextView)findViewById(R.id.post);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        ma=new myAdapter();
        lv.setAdapter(ma);
        ma.notifyDataSetChanged();
        commentText=(EditText)findViewById(R.id.commentText);
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(!TextUtils.isEmpty(commentText.getText().toString()))
                {
                    post.setTextColor(getResources().getColor(R.color.colorPrimary));
                    post.setEnabled(true);
                }
                else
                {
                    post.setTextColor(getResources().getColor(R.color.light_black));
                    post.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(commentText.getText().toString()))
                {
                    Comment cmnt=new Comment();
                    cmnt.setLobby_handle(newsFeed.getLobby_handle());
                    cmnt.setContact(newsFeed.getContact());
                    cmnt.setComment(commentText.getText().toString());
                    cmnt.setImageUri(newsFeed.getUser_profile_image());
                    FirebaseDatabase.getInstance().getReference("newsFeed")
                            .child(newsFeed.getKey()).child("Comment")
                            .push().setValue(cmnt);
                    commentText.setText("");
                }
            }
        });

        FirebaseDatabase.getInstance().getReference("newsFeed").child(newsFeed.getKey())
                .child("Comment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child:dataSnapshot.getChildren())
                    comments.add(child.getValue(Comment.class));
                ma.notifyDataSetChanged();
                if(comments.size()==0)
                    progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public class myAdapter extends ArrayAdapter {
        myAdapter(){
            super(Comments.this,R.layout.cust_search);
        }

        @Override
        public int getCount() {
            return comments.size();
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
            View v = inflater.inflate(R.layout.cust_comments,parent,false);
            TextView commentText=(TextView)v.findViewById(R.id.comment);
            TextView lobby_handle=(TextView)v.findViewById(R.id.lobby_handle);
            CircleImageView img=(CircleImageView)v.findViewById(R.id.profilepic);
            Comment object=comments.get(position);
            if(object.getComment()!=null)
                commentText.setText(object.getComment());
            if(object.getLobby_handle()!=null)
                lobby_handle.setText(object.getLobby_handle());
            if(object.getImageUri()!=null)
            {
                Glide.with(Comments.this).load(Uri.parse(object.getImageUri())).override(40,40).centerCrop().into(img);
                progressBar.setVisibility(View.INVISIBLE);
            }


            return v;

        }
    }
}
