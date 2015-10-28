package com.education.corsalite.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.NotesActivity;
import com.education.corsalite.models.responsemodels.Note;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayush on 12/09/15.
 */
public class NotesAdapter extends AbstractRecycleViewAdapter {

    private List<NotesActivity.SubjectNameSection> notesList;
    private LayoutInflater inflater;
    private Context context;

    public NotesAdapter(Context context, ArrayList<NotesActivity.SubjectNameSection> notesList, LayoutInflater inflater) {
        this.context = context;
        this.notesList = notesList;
        this.inflater = inflater;
    }

    public void updateNotesList(List<NotesActivity.SubjectNameSection> notesList) {
        this.notesList.clear();
        this.notesList.addAll(notesList);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView;
        if (viewType == NotesActivity.SubjectNameSection.SECTION) {
            itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_header, parent, false);
            SectionHeaderViewHolder headerViewHolder = new SectionHeaderViewHolder(itemLayoutView);
            return headerViewHolder;
        } else if (viewType == NotesActivity.SubjectNameSection.ITEM) {
            return new NotesDataHolder(inflater.inflate(R.layout.notes_list_item, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (notesList.get(position).type == NotesActivity.SubjectNameSection.SECTION && holder instanceof SectionHeaderViewHolder) {
            SectionHeaderViewHolder headerViewHolder = (SectionHeaderViewHolder) holder;
            headerViewHolder.bindData(notesList.get(position), position);
        } else if (notesList.get(position).type == NotesActivity.SubjectNameSection.ITEM && holder instanceof NotesDataHolder) {
            NotesDataHolder itemViewHolder = (NotesDataHolder) holder;
            itemViewHolder.bindData(notesList.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return notesList.get(position).type;
    }

    public static class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView chapterName;
        public View rootView;

        public SectionHeaderViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            this.rootView = itemLayoutView;
            chapterName = (TextView) itemLayoutView.findViewById(R.id.chapter_name);
        }

        public void bindData(final NotesActivity.SubjectNameSection note, final int position) {
            chapterName.setText(((Note) note.tag).chapter);
        }
    }

    public class NotesDataHolder extends RecyclerView.ViewHolder {

        public Button deleteBtn;
        public WebView notesContentWebview;

        public NotesDataHolder(View itemView) {
            super(itemView);
            deleteBtn = (Button) itemView.findViewById(R.id.delete_btn);
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

        public void bindData(final NotesActivity.SubjectNameSection note, final int position) {
            String baseUrl = ApiClientService.getBaseUrl().replace("/v1", "");
            String htmlContent = ((Note) note.tag).noteHtml.replace("\"", "");
            notesContentWebview.loadDataWithBaseURL(baseUrl, htmlContent, "text/html", "UTF-8", null);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAt(position);
                }
            });
        }

        private void removeAt(int itemPosition) {
            notesList.remove(itemPosition);
            notifyItemRemoved(itemPosition);
        }
    }
}