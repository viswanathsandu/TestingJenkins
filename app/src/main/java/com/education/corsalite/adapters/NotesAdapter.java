package com.education.corsalite.adapters;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.EditorActivity;
import com.education.corsalite.activities.NotesActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.requestmodels.UpdateNoteRequest;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.DefaultNoteResponse;
import com.education.corsalite.models.responsemodels.Note;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.L;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by ayush on 12/09/15.
 */
public class NotesAdapter extends AbstractRecycleViewAdapter {

    private List<NotesActivity.SubjectNameSection> notesList;
    private LayoutInflater inflater;
    private Activity context;

    public NotesAdapter(Activity context, ArrayList<NotesActivity.SubjectNameSection> notesList, LayoutInflater inflater) {
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

        public void bindData(final NotesActivity.SubjectNameSection note, final int position) {
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

        public void bindData(final NotesActivity.SubjectNameSection note, final int position) {
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

        public void bindData(final NotesActivity.SubjectNameSection note, final int position) {
            String baseUrl = ApiClientService.getBaseUrl().replace("/v1", "");
            final String htmlContent = ((Note) note.tag).noteHtml;
            try {
                notesContentWebview.loadDataWithBaseURL(baseUrl, htmlContent, "text/html", "UTF-8", null);
            } catch (Exception e) {
                L.error(e.getMessage(), e);
            }
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (note.tag instanceof Note) {
                        Note noteObj = (Note) note.tag;
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "Note");
                        bundle.putString("operation", "Edit");
                        bundle.putString("student_id", LoginUserCache.getInstance().loginResponse.studentId);
                        bundle.putString("topic_id", noteObj.topicId);
                        bundle.putString("content_id", noteObj.contentId);
                        bundle.putString("notes_id", noteObj.idNotes);
                        bundle.putString("content", htmlContent);
                        Intent intent = new Intent(context, EditorActivity.class);
                        intent.putExtras(bundle);
                        context.startActivity(intent);

                    }
                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteNote((Note) note.tag);
                }
            });
        }
    }

    private void deleteNote(Note note) {
        UpdateNoteRequest request = new UpdateNoteRequest(note.studentId, note.idNotes, null);
        ApiManager.getInstance(context).deleteNote(new Gson().toJson(request), new ApiCallback<DefaultNoteResponse>(context) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                Toast.makeText(context, "Failed to delete Note", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void success(DefaultNoteResponse defaultNoteResponse, Response response) {
                super.success(defaultNoteResponse, response);
                Toast.makeText(context, "Deleted Note successfully", Toast.LENGTH_SHORT).show();
                if(context instanceof NotesActivity) {
                    ((NotesActivity) context).refreshNotes();
                }
            }
        });
    }
}