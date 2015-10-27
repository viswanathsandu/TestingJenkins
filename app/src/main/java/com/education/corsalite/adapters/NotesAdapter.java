package com.education.corsalite.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.Chapters;
import com.education.corsalite.models.responsemodels.Message;
import com.education.corsalite.models.responsemodels.Note;
import com.education.corsalite.models.responsemodels.Notes;

import java.util.Collection;
import java.util.List;

/**
 * Created by ayush on 12/09/15.
 */
public class NotesAdapter extends AbstractRecycleViewAdapter {

    private List<Note> notesList;
    private LayoutInflater inflater;
    private Context context;

    public NotesAdapter(Context context, List<Note> notesList, LayoutInflater inflater) {
        this.context = context;
        this.notesList  = notesList;
        this.inflater = inflater;
    }

    public void updateNotesList(List<Note> notesList) {
        this.notesList.clear();
        this.notesList.addAll(notesList);
        notifyDataSetChanged();
    }

    @Override
    public NotesDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NotesDataHolder(inflater.inflate(R.layout.notes_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((NotesDataHolder)holder).bindData(notesList.get(position));
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public class NotesDataHolder extends RecyclerView.ViewHolder {

        public TextView notesHeading;
        public WebView notesContentWebview;

        public NotesDataHolder(View itemView) {
            super(itemView);
            notesHeading = (TextView) itemView.findViewById(R.id.chapter_name);
            notesContentWebview = (WebView) itemView.findViewById(R.id.notes_content_webview);
            notesContentWebview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            notesContentWebview.setScrollbarFadingEnabled(true);
            notesContentWebview.getSettings().setLoadsImagesAutomatically(true);
            notesContentWebview.getSettings().setJavaScriptEnabled(true);
            notesContentWebview.setBackgroundColor(Color.TRANSPARENT);
            notesContentWebview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                    // TODO : uncomment it after dicussing with client
                    /*if (Uri.parse(url).getHost().equals("staging.corsalite.com")) {
                        return false;
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intent);
                    return true;
                    */
                }
            });
        }

        public void bindData(final Note note) {
            notesContentWebview.loadData(note.noteHtml, "text/html", "UTF-8");
            notesHeading.setText(note.chapter);
        }
    }
}