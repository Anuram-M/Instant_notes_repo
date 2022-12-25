package com.anuram.instant.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Editnote_activity extends AppCompatActivity
{
    EditText update_note_title, update_note_content;
    FloatingActionButton update_note_fab;

    FirebaseFirestore firestore;
    FirebaseAuth fireauth;
    FirebaseUser fireuser;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnote);

        toolbar=findViewById(R.id.toolbar_editnoteid);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        update_note_title=findViewById(R.id.update_note_title_id);
        update_note_content=findViewById(R.id.update_note_content_id);
        update_note_fab=findViewById(R.id.update_note_fabid);


        fireauth=FirebaseAuth.getInstance();
        fireuser=fireauth.getCurrentUser();
        firestore=FirebaseFirestore.getInstance();

        Intent noteupdate=getIntent();
        update_note_title.setText(noteupdate.getStringExtra("title"));
        update_note_content.setText(noteupdate.getStringExtra("content"));

        update_note_fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String newtitle_str=update_note_title.getText().toString();
                String newcontent_str=update_note_content.getText().toString();
                if(newtitle_str.isEmpty()||newcontent_str.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "fields are empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DocumentReference documentReference=firestore.collection("notes")
                                                                    .document(fireuser.getUid())
                                                                    .collection("mynotes")
                                                                    .document(noteupdate.getStringExtra("noteID"));
                    Map<String, Object> updatednote=new HashMap<>();
                    updatednote.put("title",newtitle_str);
                    updatednote.put("content",newcontent_str);
                    documentReference.set(updatednote).addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void unused)
                        {
                            Toast.makeText(getApplicationContext(), "successfully updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Editnote_activity.this, MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(getApplicationContext(), "failed to update the note", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(Editnote_activity.this,MainActivity.class));
    }
}