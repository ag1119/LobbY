package com.example.abhishek.lobby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FlashPage extends AppCompatActivity implements Runnable{

    public static final String PREF="username";
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_page);
        SharedPreferences sf=getSharedPreferences(PREF,MODE_PRIVATE);
        username=sf.getString("username","null");
        Thread main = new Thread(this);
        main.start();
    }

    @Override
    public void run()
    {
        try{
            Thread.sleep(3000);
        }
        catch (Exception e){}
        finish();
        if(username.equals("null"))
        {
        Intent intn = new Intent(this,AuthActivity.class);
        startActivity(intn);
        }
        else
            startActivity(new Intent(FlashPage.this,HomePage.class));
    }
}
