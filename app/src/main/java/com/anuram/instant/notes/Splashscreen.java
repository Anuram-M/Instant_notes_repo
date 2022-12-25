package com.anuram.instant.notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splashscreen extends AppCompatActivity
{
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();


        new Handler(getMainLooper()).postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if(firebaseUser!=null)
                {
                    startActivity(new Intent(Splashscreen.this,MainActivity.class));
                }
                else
                {
                    startActivity(new Intent(Splashscreen.this,Signin_activity.class));
                }
                finish();

            }
        },4000);

    }
}