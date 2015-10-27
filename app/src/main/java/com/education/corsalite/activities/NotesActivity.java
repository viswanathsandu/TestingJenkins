package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.education.corsalite.R;
import com.education.corsalite.adapters.GridRecyclerAdapter;
import com.education.corsalite.adapters.NotesAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Note;
import com.education.corsalite.models.responsemodels.Notes;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by ayush on 27/10/15.
 */
public class NotesActivity extends AbstractBaseActivity {

    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private LayoutInflater inflater;
    private String mSubjectId = "";
    private String mChapterId = "";
    private String mTopicId = "";
    private String mContentId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_notes, null);
        linearLayout = (LinearLayout) myView.findViewById(R.id.notes_layout);
        frameLayout.addView(myView);
//        setToolbarForNotes();
        getBundleData();
        initUI();
        setAdapter();
        getNotesData();
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(GridRecyclerAdapter.SUBJECT_ID) && bundle.getString(GridRecyclerAdapter.SUBJECT_ID) != null) {
                mSubjectId = bundle.getString("subjectId");
            }
            if (bundle.containsKey(GridRecyclerAdapter.CHAPTER_ID) && bundle.getString(GridRecyclerAdapter.CHAPTER_ID) != null) {
                mChapterId = bundle.getString("chapterId");
            }
        }
    }

    private void initUI() {
        recyclerView = (RecyclerView) findViewById(R.id.notes_list);
    }

    private void setAdapter() {
        mAdapter = new NotesAdapter(this, new ArrayList<Note>(), inflater);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(mAdapter);
    }

    private void getNotesData() {
        ApiManager.getInstance(this).getNotes(LoginUserCache.getInstance().loginResponse.studentId,mSubjectId, mChapterId, mTopicId,new ApiCallback<List<Note>>(this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                if (error != null && !TextUtils.isEmpty(error.message)) {
                    showToast(error.message);
                }
            }

            @Override
            public void success(List<Note> notesList, Response response) {
                super.success(notesList, response);
                if (notesList != null) {
                    ((NotesAdapter)mAdapter).updateNotesList(notesList);
                }
            }
        });
    }
}
