package com.education.corsalite.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.ForumActivity;
import com.education.corsalite.activities.NotesActivity;
import com.education.corsalite.fragments.EditorDialogFragment;
import com.education.corsalite.models.responsemodels.Note;
import com.education.corsalite.services.ApiClientService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayush on 12/09/15.
 */
public class ForumAdapter extends AbstractRecycleViewAdapter {

    private List<ForumActivity.SubjectNameSection> notesList;
    private LayoutInflater inflater;
    private Context context;

    public ForumAdapter(Context context, ArrayList<ForumActivity.SubjectNameSection> notesList, LayoutInflater inflater) {
        this.context = context;
        this.notesList = notesList;
        this.inflater = inflater;
    }

    public void updateNotesList(List<ForumActivity.SubjectNameSection> notesList) {
        this.notesList.clear();
        this.notesList.addAll(notesList);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NotesActivity.SubjectNameSection.SECTION) {
            return new SectionHeaderViewHolder(inflater.inflate(R.layout.section_header, parent, false));
        } else if (viewType == NotesActivity.SubjectNameSection.ITEM) {
            return new NotesDataHolder(inflater.inflate(R.layout.notes_list_item, parent, false));
        } else {
            View v = inflater.inflate(R.layout.sub_section_header, parent, false);
            return new SubSectionHeaderViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (notesList.get(position).type == NotesActivity.SubjectNameSection.SECTION && holder instanceof SectionHeaderViewHolder) {
            SectionHeaderViewHolder headerViewHolder = (SectionHeaderViewHolder) holder;
            headerViewHolder.bindData(notesList.get(position), position);
        } else if (notesList.get(position).type == NotesActivity.SubjectNameSection.ITEM && holder instanceof NotesDataHolder) {
            NotesDataHolder itemViewHolder = (NotesDataHolder) holder;
            itemViewHolder.bindData(notesList.get(position), position);
        } else {
            SubSectionHeaderViewHolder itemViewHolder = (SubSectionHeaderViewHolder) holder;
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

        public void bindData(final ForumActivity.SubjectNameSection note, final int position) {
            chapterName.setText(((Note) note.tag).chapter);
        }
    }

    public static class SubSectionHeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView topicName;
        public View rootView;

        public SubSectionHeaderViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            this.rootView = itemLayoutView;
            topicName = (TextView) itemLayoutView.findViewById(R.id.topic_name);
        }

        public void bindData(final ForumActivity.SubjectNameSection note, final int position) {
            topicName.setText(((Note) note.tag).topic);
        }
    }

    public class NotesDataHolder extends RecyclerView.ViewHolder {

        public Button editBtn;
        public Button deleteBtn;
        public WebView notesContentWebview;

        public NotesDataHolder(View itemView) {
            super(itemView);
            editBtn = (Button) itemView.findViewById(R.id.edit_btn);
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
                }
            });
        }

        public void bindData(final ForumActivity.SubjectNameSection note, final int position) {
            String baseUrl = ApiClientService.getBaseUrl().replace("/v1", "");
            final String htmlContent = ((Note) note.tag).noteHtml.replace("\"", "");
            notesContentWebview.loadDataWithBaseURL(baseUrl, htmlContent, "text/html", "UTF-8", null);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(note.tag instanceof Note) {
                        Note noteObj = (Note) note.tag;
                        EditorDialogFragment fragment = new EditorDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "Notes");
                        bundle.putString("operation", "Edit");
                        bundle.putString("student_id", noteObj.studentId);
                        bundle.putString("topic_id", noteObj.topicId);
                        bundle.putString("content_id", noteObj.contentId);
                        bundle.putString("content", htmlContent);
                        fragment.setArguments(bundle);
                        fragment.show(((NotesActivity) context).getSupportFragmentManager(), "NotesEditorDialog");
                    }
                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAt(position);
                }
            });
        }

        private void removeAt(int itemPosition) {
            if(notesList.size() > itemPosition) {
                notesList.remove(itemPosition);
                notifyItemRemoved(itemPosition);
            }
        }
    }
}