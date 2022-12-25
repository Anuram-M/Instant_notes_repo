package com.anuram.instant.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Notedetail_activity extends AppCompatActivity
{
    TextView notedetail_title, notedetail_content;
    FloatingActionButton edit_note_fab;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notedetail);

        toolbar=findViewById(R.id.toolbar_notedetailid);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        Intent notedata=getIntent();


        notedetail_title=findViewById(R.id.notedetail_title_id);
        notedetail_content=findViewById(R.id.notedetail_content_id);
        edit_note_fab=findViewById(R.id.edit_note_fabid);

        notedetail_title.setText(notedata.getStringExtra("title"));
        notedetail_content.setText(notedata.getStringExtra("content"));

        edit_note_fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(Notedetail_activity.this,Editnote_activity.class)
                        .putExtra("title",notedata.getStringExtra("title"))
                        .putExtra("content",notedata.getStringExtra("content"))
                        .putExtra("noteID",notedata.getStringExtra("noteID")));
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

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(Notedetail_activity.this,MainActivity.class));

    }
}