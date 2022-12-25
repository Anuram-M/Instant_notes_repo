package com.anuram.instant.notes;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteviewHolder extends RecyclerView.ViewHolder
{
    TextView note_title, note_content;
    ImageView popup_btn;
    LinearLayout cardView;
    public NoteviewHolder(@NonNull View itemView)
    {
        super(itemView);

        note_title=itemView.findViewById(R.id.note_title_id);
        note_content=itemView.findViewById(R.id.note_content_id);
        popup_btn=itemView.findViewById(R.id.popup_menu_id);
        cardView=itemView.findViewById(R.id.main_container_id);
    }
}
