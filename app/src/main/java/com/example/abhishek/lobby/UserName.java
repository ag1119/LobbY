package com.example.abhishek.lobby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserName extends AppCompatActivity {

    public static final String PREF="username";
    Button Continue;
    String userNumber;
    TextView register;
    EditText usernameEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_name);
        userNumber=getIntent().getExtras().getString("mobileNo");

        Continue=(Button)findViewById(R.id.continueBtn);
        register=(TextView)findViewById(R.id.register);
        usernameEt=(EditText)findViewById(R.id.usernameEt);

    }

    public void onContinue(View view)
    {
        if(!TextUtils.isEmpty(usernameEt.getText().toString())){
        FirebaseDatabase.getInstance().getReference("Users").child(usernameEt.getText().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.child("contact").getValue(String.class).equals(userNumber))
                    {
                        SharedPreferences username=getSharedPreferences(PREF,MODE_PRIVATE);
                        SharedPreferences.Editor editor=username.edit();
                        editor.putString("username",usernameEt.getText().toString());
                        editor.commit();
                        finish();
                        startActivity(new Intent(UserName.this,HomePage.class));
                    }
                    else
                        Toast.makeText(UserName.this,"This LobbY Handle is registered with another number",Toast.LENGTH_SHORT).show();

                }
                else
                    Toast.makeText(UserName.this,"This account doesn't exist",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });}
    }

    public void onRegister(View view)
    {
        Intent intent=new Intent(UserName.this,SetProfile.class);
        intent.putExtra("mobileNo",userNumber);
        finish();
        startActivity(intent);
    }
}
