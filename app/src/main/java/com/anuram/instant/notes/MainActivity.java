package com.anuram.instant.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity
{

    EditText searchtext;
    ImageView search_image, close_image;
    LinearLayout searchbox_Layout;


    FirebaseAuth fireauth;
    FirebaseUser fireuser;
    FirebaseFirestore firestore;

    Toolbar my_toolbar;
    FloatingActionButton new_note_fab;

    RecyclerView recyclerView;

    FirestoreRecyclerAdapter<Firebasedata,NoteviewHolder> adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        my_toolbar=findViewById(R.id.toolbar_id);
        setSupportActionBar(my_toolbar);



        searchtext=findViewById(R.id.searchtextid);
        search_image=findViewById(R.id.searchimageid);
        close_image=findViewById(R.id.close_imageid);
        searchbox_Layout=findViewById(R.id.searchbox_layout_id);

        search_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_image.setBackgroundResource(0);
                searchbox_Layout.setBackgroundResource(R.drawable.outline);
                searchtext.setVisibility(View.VISIBLE);
                close_image.setVisibility(View.VISIBLE);
            }
        });

        close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchtext.setText("");
                search_image.setBackgroundResource(R.drawable.outline);
                searchbox_Layout.setBackgroundResource(0);
                searchtext.setVisibility(View.INVISIBLE);
                close_image.setVisibility(View.INVISIBLE);
            }
        });
        searchtext.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                {
                    Query query;
                    if(editable.toString().isEmpty())
                    {

                        query=firestore.collection("notes")
                                .document(fireuser.getUid())
                                .collection("mynotes")
                                .orderBy("content",Query.Direction.ASCENDING);
                        //empty_state.setVisibility(View.GONE);
                        //close_icon.setVisibility(View.GONE);
                    }
                    else
                    {
                        query=firestore.collection("notes")
                                .document(fireuser.getUid())
                                .collection("mynotes")
                                .whereEqualTo("title",editable.toString())
                                .orderBy("title",Query.Direction.ASCENDING);


                        //if(query)
                    }

                    FirestoreRecyclerOptions<Firebasedata> usernotes_query=new FirestoreRecyclerOptions.Builder<Firebasedata>().setQuery(query,Firebasedata.class).build();


                    adapter.updateOptions(usernotes_query);
                    adapter.startListening();
                    recyclerView.setAdapter(adapter);



                }
            }
        });






        new_note_fab=findViewById(R.id.new_note_fabid);
        new_note_fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainActivity.this,Newnote_activity.class));
            }
        });
        fireauth=FirebaseAuth.getInstance();
        fireuser=fireauth.getCurrentUser();
        firestore=FirebaseFirestore.getInstance();


        Query query=firestore.collection("notes")
                             .document(fireuser.getUid())
                             .collection("mynotes")
                             . orderBy("title",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Firebasedata> usernotes=new FirestoreRecyclerOptions.Builder<Firebasedata>()
                                                                                     .setQuery(query,Firebasedata.class).build();
        adapter=new FirestoreRecyclerAdapter<Firebasedata, NoteviewHolder>(usernotes)
        {
            @Override
            protected void onBindViewHolder(@NonNull NoteviewHolder holder, int position, @NonNull Firebasedata model)
            {

                int backgroundcolor=getRandomcolor();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    holder.cardView.setBackgroundColor(holder.itemView.getResources()
                                   .getColor(backgroundcolor,null));
                }

                holder.note_title.setText(model.getTitle());
                holder.note_content.setText(model.getContent());
                String docID=adapter.getSnapshots().getSnapshot(position).getId();
                holder.cardView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        startActivity(new Intent(MainActivity.this,Notedetail_activity.class)
                                .putExtra("title",model.getTitle())
                                .putExtra("content",model.getContent())
                                .putExtra("noteID",docID));
                    }
                });

                ImageView popup_menu_btn=holder.itemView.findViewById(R.id.popup_menu_id);
                popup_menu_btn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        PopupMenu popupMenu=new PopupMenu(view.getContext(),view);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem)
                            {
                                startActivity(new Intent(MainActivity.this,Editnote_activity.class)
                                        .putExtra("title",model.getTitle())
                                        .putExtra("content", model.getContent())
                                        .putExtra("noteID",docID));
                                return true;
                            }
                        });
                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem)
                            {
                                DocumentReference documentReference=firestore.collection("notes")
                                        .document(fireuser.getUid())
                                        .collection("mynotes")
                                        .document(docID);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>()
                                {
                                    @Override
                                    public void onSuccess(Void unused)
                                    {
                                        Toast.makeText(getApplicationContext(), "successfully deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener()
                                {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        Toast.makeText(getApplicationContext(), "failed to delete", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return true;
                            }
                        });

                        popupMenu.show();

                    }
                });


            }

            @NonNull
            @Override
            public NoteviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                return new NoteviewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout,parent,false));

            }
        };



        recyclerView=findViewById(R.id.recycler_main_id);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(adapter);
        /*if(usernotes.getSnapshots().size()==0)
        {
            empty_state.setVisibility(View.VISIBLE);
        }
        else
        {
            empty_state.setVisibility(View.GONE);
        }

         */

    }



    private int getRandomcolor()
    {

        List<Integer> colorcode=new ArrayList<>();
        colorcode.add(R.color.dull_orange);
        colorcode.add(R.color.grey_with_blue_hue);
        colorcode.add(R.color.pastel_blue);
        colorcode.add(R.color.pastel_green);
        colorcode.add(R.color.pink_100);
        colorcode.add(R.color.light_red);
        colorcode.add(R.color.purple_200);
        colorcode.add(R.color.yellowish_white);
        colorcode.add(R.color.teal_200);
        //colorcode.add(R.color.dull_orange);


        Random number=new Random();
        int randomcode=number.nextInt(colorcode.size());
        return colorcode.get(randomcode);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.optionsmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.logout_id:fireauth.signOut();
                finish();
                startActivity(new Intent(MainActivity.this,Signin_activity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onStart()
    {
        super.onStart();
        adapter.startListening();

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