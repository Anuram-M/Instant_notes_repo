package com.anuram.instant.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forgotpassword_activity extends AppCompatActivity
{

    FirebaseAuth firebaseAuth;
    EditText forgotpassword_mail;
    Button send_mail_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        firebaseAuth=FirebaseAuth.getInstance();
        forgotpassword_mail=findViewById(R.id.forgotpassword_mail_id);
        send_mail_btn=findViewById(R.id.signup_create_btnid);
        send_mail_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String password_resetmailstr=forgotpassword_mail.getText().toString();
                if(password_resetmailstr.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "mail is empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    firebaseAuth.sendPasswordResetEmail(password_resetmailstr).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(), "reset password link is sent", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Forgotpassword_activity.this,Signin_activity.class));
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "failed to send email to resent password!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}