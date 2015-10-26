package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.Chapters;
import com.education.corsalite.models.responsemodels.Note;
import com.education.corsalite.models.responsemodels.Notes;

import java.util.List;

/**
 * Created by ayush on 12/09/15.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesDataHolder> {

    private List<Note> notesList;

    public NotesAdapter(List<Note> notesList) {
        this.notesList  = notesList;
    }

    @Override
    public NotesDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_list_item, parent, false);
        return new NotesDataHolder(view);
    }


    @Override
    public void onBindViewHolder(NotesDataHolder holder, int position) {
        final Note note = notesList.get(position);
        holder.notesText.setText(note.noteHtml);
        holder.notesHeading.setText(note.chapter);
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public class NotesDataHolder extends RecyclerView.ViewHolder {

        public TextView notesHeading;
        public TextView notesText;

        public NotesDataHolder(View itemView) {
            super(itemView);
            notesHeading = (TextView) itemView.findViewById(R.id.chapter_name);
            notesText = (TextView) itemView.findViewById(R.id.notes_text);
        }
    }
}