package com.anuram.instant.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

public class Newnote_activity extends AppCompatActivity
{
    Toolbar title_toolbar;
    EditText new_note_title,new_note_content;
    FloatingActionButton save_note_fab;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newnote);

        title_toolbar=findViewById(R.id.toolbar_newnoteid);
        setSupportActionBar(title_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        new_note_title=findViewById(R.id.new_note_title_id);
        new_note_content=findViewById(R.id.new_note_content_id);

        save_note_fab=findViewById(R.id.save_note_fabid);
        save_note_fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String title_str=new_note_title.getText().toString();
                String content_str=new_note_content.getText().toString();
                if(title_str.isEmpty()||content_str.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "some fiedls are empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DocumentReference documentReference=firestore.collection("notes")
                                                                 .document(firebaseUser.getUid())
                                                                 .collection("mynotes")
                                                                 .document();
                    Map<String, Object> note=new HashMap<>();
                    note.put("title",title_str);
                    note.put("content",content_str);
                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void unused)
                        {
                            Toast.makeText(getApplicationContext(), "note is created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Newnote_activity.this,MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(getApplicationContext(), "failed to create note", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                new_note_title.setText("");
                new_note_content.setText("");

            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {

        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }


}