package com.example.abhishek.lobby;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    public static final String PREF="username";
    private String chatUser;
    private android.support.v7.widget.Toolbar mChatToolbar;
    private DatabaseReference mrootref;
    private FirebaseAuth mAuth;
    private String mCurrentuser;
    private DatabaseReference mCurrentuserRef;

    private EditText chatmessageView;
    private ImageButton chatSendbtn;
    static String mCurrentusername;
    private RecyclerView mMessagesList;

    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager mlinearLayout;
    private MessageAdapter mAdapter;
    User_info user_info;
    CircleImageView friend_img;
    TextView friend_lobbyHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        SharedPreferences sf=getSharedPreferences(PREF,MODE_PRIVATE);
        mCurrentusername=sf.getString("username","null");
        user_info=(User_info)getIntent().getSerializableExtra("friendInfo");
        if(user_info!=null)
            chatUser=user_info.getLobby_handle();


        mChatToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();


        chatmessageView=(EditText)findViewById(R.id.chatEditText);
        chatSendbtn=(ImageButton)findViewById(R.id.send_btn);

        friend_img=(CircleImageView)findViewById(R.id.friend_img);
        friend_lobbyHandle=(TextView)findViewById(R.id.friend_lobbyHandle);
        friend_lobbyHandle.setText(user_info.getName());
        if(user_info.getImageUri()!=null)
            Glide.with(ChatActivity.this).load(Uri.parse(user_info.getImageUri())).
                    override(80,80).centerCrop().into(friend_img);


        mAdapter=new MessageAdapter(messagesList);


        mMessagesList=(RecyclerView)findViewById(R.id.messages_list);

        mlinearLayout=new LinearLayoutManager(this);
        //mlinearLayout.setReverseLayout(true);
        mMessagesList.setLayoutManager(mlinearLayout);
        mMessagesList.setAdapter(mAdapter);
        //mMessagesList.scrollToPosition(mAdapter.getItemCount()-1);





        mrootref = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        mCurrentuser = mAuth.getCurrentUser().getUid();
        mCurrentuserRef = mrootref.child("users").child(mCurrentuser);

        loadMessages();


        chatSendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                chatmessageView.setText("");

            }
        });


    }

    private void loadMessages() {
        mrootref.child("messages").child(mCurrentusername).child(chatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message=dataSnapshot.getValue(Messages.class);

                //Log.d("Valuemessage",dataSnapshot.getChildren().toString());

                if(message==null) {
                    return;
                }
                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    private void sendMessage(){
        String message=chatmessageView.getText().toString();
        if(!TextUtils.isEmpty(message)){
            Messages msg=new Messages();
            msg.setMessage(message);
            msg.setFrom(mCurrentusername);
            FirebaseDatabase.getInstance().getReference("messages").child(mCurrentusername)
                    .child(chatUser).push().setValue(msg);
            FirebaseDatabase.getInstance().getReference("messages").child(user_info.getLobby_handle())
                    .child(mCurrentusername).push().setValue(msg);


        }

    }
}
