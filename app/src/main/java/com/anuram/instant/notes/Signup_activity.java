package com.anuram.instant.notes;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Signup_activity extends AppCompatActivity
{
    EditText signup_mail, signup_password;
    Button create_account_btn, goback_to_signinbtn;
    CheckBox checkBoxsignup;


    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signup_mail=findViewById(R.id.signup_mail_id);
        signup_password=findViewById(R.id.signup_password_id);
        create_account_btn=findViewById(R.id.signup_create_btnid);
        goback_to_signinbtn=findViewById(R.id.goback_signin_btnid);

        checkBoxsignup=findViewById(R.id.show_password2id);
        checkBoxsignup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if(b)
                {
                    signup_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    signup_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

            }
        });

        firebaseAuth=FirebaseAuth.getInstance();
        //firebaseUser=firebaseAuth.getCurrentUser();
        firestore=FirebaseFirestore.getInstance();



        goback_to_signinbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(Signup_activity.this,Signin_activity.class));
            }
        });



        create_account_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String signup_mailstr=signup_mail.getText().toString();
                String signup_pwdstr=signup_password.getText().toString();
                if(signup_mailstr.isEmpty()||signup_pwdstr.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
                else if(signup_pwdstr.length()<8)
                {
                    Toast.makeText(getApplicationContext(), "Password is short", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    firebaseAuth.createUserWithEmailAndPassword(signup_mailstr,signup_pwdstr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(), "mail is sent", Toast.LENGTH_SHORT).show();
                                sendmailverificationMethod();
                            }

                            else
                            {
                                Toast.makeText(getApplicationContext(), "failed to create a new use", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

                }
            }
        });

    }

    private void sendmailverificationMethod()
    {
        firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    Toast.makeText(getApplicationContext(), "mail is sent to verify", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(Signup_activity.this,Signin_activity.class));
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(getApplicationContext(), "failed to send mail", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}