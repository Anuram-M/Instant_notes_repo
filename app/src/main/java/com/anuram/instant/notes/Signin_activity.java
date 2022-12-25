package com.anuram.instant.notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Signin_activity extends AppCompatActivity
{

    EditText signin_mail, signin_password;
    Button signin_btn, signup_btn;
    TextView forgot_password;
    CheckBox checkBox;

    FirebaseAuth fireauth;
    FirebaseUser fireuser;
    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        signin_mail=findViewById(R.id.signin_mail_id);
        signin_password=findViewById(R.id.signin_password_id);
        signin_btn=findViewById(R.id.signin_btnid);
        signup_btn=findViewById(R.id.signup_btnid);
        forgot_password=findViewById(R.id.forgotpassword_id);

        checkBox=findViewById(R.id.show_passwordid);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    signin_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    signin_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        fireauth=FirebaseAuth.getInstance();
        fireuser=fireauth.getCurrentUser();
        firestore=FirebaseFirestore.getInstance();


        //signup button takes us to signup activity
        signup_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(Signin_activity.this,Signup_activity.class));
            }
        });


        signin_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String signin_mailstr=signin_mail.getText().toString();
                String signin_pwdstr=signin_password.getText().toString();
                if(signin_mailstr.isEmpty()||signin_pwdstr.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "some fields are empty", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    fireauth.signInWithEmailAndPassword(signin_mailstr,signin_pwdstr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                checkMailVerification();
                                //Toast.makeText(getApplicationContext(), "LOgged in successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "mail is not verified", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });



        forgot_password.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(Signin_activity.this,Forgotpassword_activity.class));
            }
        });


    }

    private void checkMailVerification()
    {

        fireuser=fireauth.getCurrentUser();
        if(fireuser.isEmailVerified()==true)
        {
            Toast.makeText(getApplicationContext(), "logged in successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Signin_activity.this,MainActivity.class));
        }
        else
        {
            Toast.makeText(getApplicationContext(), "account is not verified", Toast.LENGTH_SHORT).show();
            fireauth.signOut();
        }

    }

    private boolean exit=false;

    @Override
    public void onBackPressed()
    {
        Toast.makeText(getApplicationContext(), "exiting the app", Toast.LENGTH_SHORT).show();
        exit=true;
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent exit_intent=new Intent(Intent.ACTION_MAIN);
                exit_intent.addCategory(Intent.CATEGORY_HOME);
                exit_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(exit_intent);

            }
        },1000);
        //super.onBackPressed();
        //System.exit(0);
    }
}