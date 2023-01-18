package com.anuram.instant.notes;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Signup_activity extends AppCompatActivity
{

    private String SITEKEY="your site key";
    private String SECRETKEY="your secret key";

    RequestQueue queue;
    EditText signup_mail, signup_password;
    Button create_account_btn, goback_to_signinbtn;
    CheckBox checkBoxsignup,recaptcha_checkbox;


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

        queue= Volley.newRequestQueue(getApplicationContext());

        recaptcha_checkbox=findViewById(R.id.recaptcha_verifyid);
        recaptcha_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                Recaptcha_verifymethod();
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

    private void Recaptcha_verifymethod() {
       SafetyNet.getClient(this).verifyWithRecaptcha(SITEKEY).addOnSuccessListener(new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
           @Override
           public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {
               if(!recaptchaTokenResponse.getTokenResult().isEmpty())
               {
                   handleVerificationMethod(recaptchaTokenResponse.getTokenResult());
               }
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(Signup_activity.this, "verification failed", Toast.LENGTH_SHORT).show();
           }
       });
    }

    private void handleVerificationMethod(final String tokenResult)
    {
        String url="https://www.google.com/recaptcha/api/siteverify";
        StringRequest str_request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
              try
              {
                  JSONObject jsonObject=new JSONObject(response);
                  if(jsonObject.getBoolean("success"))
                  {
                      Toast.makeText(Signup_activity.this, "Successfully verified", Toast.LENGTH_SHORT).show();
                  }
                  else
                  {
                      Toast.makeText(Signup_activity.this, "not verified", Toast.LENGTH_SHORT).show();
                  }
              }
              catch(Exception e)
              {
                  Log.d("tag","error in "+e.getMessage());
              }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
               Log.d("tag",error.toString());
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params=new HashMap<>();
                params.put("secret",SECRETKEY);
                params.put("response",tokenResult);

                return params;

            }
        };

        str_request.setRetryPolicy(new DefaultRetryPolicy(4999,
                                                                       DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                                       DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(str_request);


    };

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