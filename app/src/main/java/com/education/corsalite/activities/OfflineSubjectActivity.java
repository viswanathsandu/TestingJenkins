package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.holders.IconTreeItemHolder;
import com.education.corsalite.holders.CheckedItemViewHolder;
import com.education.corsalite.models.ChapterModel;
import com.education.corsalite.models.ContentModel;
import com.education.corsalite.models.SubjectModel;
import com.education.corsalite.models.db.ContentIndexResponse;
import com.education.corsalite.models.responsemodels.Content;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by ayush on 05/10/15.
 */
public class OfflineSubjectActivity extends AbstractBaseActivity {

    private RelativeLayout mainNodeLayout;
    private AndroidTreeView tView;
    private TreeNode root;
    private TreeNode contentRoot;
    private String mSubjectId = "";
    private String mChapterId = "";
    private String mTopicId = "";
    private String mContentId = "";
    private String mSubjectName = "";
    private String mCourseId = "";
    private String mChapterName = "";
    private List<ContentIndex> contentIndexList;
    private ArrayList<SubjectModel> subjectModelList;
    private ArrayList<ChapterModel> chapterModelList;
    private LinearLayout downloadImage;
    private ProgressBar headerProgress;
    Bundle savedInstanceState;
    private int chapterIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_offline_content, null);
        frameLayout.addView(myView);
        mainNodeLayout = (RelativeLayout) findViewById(R.id.main_node);
        downloadImage = (LinearLayout) findViewById(R.id.download);
        headerProgress = (ProgressBar) findViewById(R.id.headerProgress);
        downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loopCheckedViews();
            }
        });
        setToolbarForOfflineContentReading();
        getBundleData();
        getContentIndex(mCourseId, LoginUserCache.getInstance().loginResponse.studentId);
        initNodes();
    }

    private void loopCheckedViews() {
        int i = 0;
        for (TreeNode n : root.getChildren().get(0).getChildren()) {
            if (((CheckBox) n.getViewHolder().getNodeView().findViewById(R.id.node_selector)).isChecked()) {
                if (i == 0) {
                    chapterModelList.get(chapterIndex).htmlChecked = true;
                } else {
                    chapterModelList.get(chapterIndex).videoChecked = true;
                }
            }
            i++;
        }
        getContent(getContentIdsForOtherChapters(chapterModelList));
    }

    private void getContent(String contentId) {
        ApiManager.getInstance(this).getContent(contentId, "", new ApiCallback<List<Content>>(this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);

            }

            @Override
            public void success(List<Content> contents, Response response) {
                super.success(contents, response);
            }
        });
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("subjectId") && bundle.getString("subjectId") != null) {
                mSubjectId = bundle.getString("subjectId");
            }
            if (bundle.containsKey("chapterId") && bundle.getString("chapterId") != null) {
                mChapterId = bundle.getString("chapterId");
            }
            if (bundle.containsKey("topicId") && bundle.getString("topicId") != null) {
                mTopicId = bundle.getString("topicId");
            }
            if (bundle.containsKey("contentId") && bundle.getString("contentId") != null) {
                mContentId = bundle.getString("contentId");
            }
            if (bundle.containsKey("subject") && bundle.getString("subject") != null) {
                mSubjectName = bundle.getString("subject");
            }
            if (bundle.containsKey("courseId") && bundle.getString("courseId") != null) {
                mCourseId = bundle.getString("courseId");
            }
            if (bundle.containsKey("chapterName") && bundle.getString("chapterName") != null) {
                mChapterName = bundle.getString("chapterName");
            }
        }
    }

    private void getContentIndex(String courseId, String studentId) {
        ApiManager.getInstance(this).getContentIndex(courseId, studentId,
                new ApiCallback<List<ContentIndex>>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            showToast(error.message);
                        }
                    }

                    @Override
                    public void success(List<ContentIndex> mContentIndexs, Response response) {
                        super.success(mContentIndexs, response);
                        if (mContentIndexs != null) {
                            contentIndexList = mContentIndexs;
                            ContentIndexResponse mContentIndexResponse = new ContentIndexResponse();
//                            mContentIndexResponse.contentIndexes = contentIndexList;
                            initNodes();
                            prefillSubjects();
                        }
                    }
                });
    }

    private String getContentIds(List<ChapterModel> chapterModelList) {
        String contentId = "";
        String contentIds = "";

        for (ChapterModel chapterModel : chapterModelList) {
            int i = 0;
            for (ContentModel contentModel : chapterModel.topicMap.get(i).contentMap) {
                if (contentId.trim().length() > 0) {
                    contentId = contentId + ",";
                    contentIds = contentIds + ",";
                }
                contentId = contentId + contentModel.idContent + "." + contentModel.type;
                contentIds = contentIds + contentModel.idContent;
            }
            i++;
        }
        return contentIds;
    }

    private String getContentIdsForOtherChapters(List<ChapterModel> chapterModelList) {
        String contentId = "";
        String contentIds = "";

        for (ChapterModel chapterModel : chapterModelList) {
            int i = 0;
            for (ContentModel contentModel : chapterModel.topicMap.get(i).contentMap) {
                if (chapterModel.htmlChecked) {
                    if (contentId.trim().length() > 0) {
                        contentId = contentId + ",";
                        contentIds = contentIds + ",";
                    }
                    contentId = contentId + contentModel.idContent + "." + contentModel.type;
                    contentIds = contentIds + contentModel.idContent;
                }
                if (chapterModel.videoChecked) {
                    if (contentId.trim().length() > 0) {
                        contentId = contentId + ",";
                        contentIds = contentIds + ",";
                    }
                    contentId = contentId + contentModel.idContent + "." + contentModel.type;
                    contentIds = contentIds + contentModel.idContent;
                }
            }
            i++;
        }
        return contentIds;
    }

    private void prefillSubjects() {
        ContentIndex mContentIndex = contentIndexList.get(0);
        subjectModelList = new ArrayList<>(mContentIndex.subjectModelList);
        int listSize = subjectModelList.size();
        if (!mSubjectId.isEmpty()) {
            for (int i = 0; i < listSize; i++) {
                if (subjectModelList.get(i).subjectName.equals(mSubjectName)) {
                    chapterModelList = (ArrayList<ChapterModel>) subjectModelList.get(i).chapters;
                    showChapters();
                }
            }
            addRootAndItsView();
        }
    }

    private void showChapters() {
        if (!mChapterId.isEmpty()) {
            int listSize = chapterModelList.size();
            for (int i = 0; i < listSize; i++) {
                if (chapterModelList.get(i).chapterName.equals(mChapterName)) {
                    chapterIndex = i;
                    setChapterNameAndChildren(chapterModelList.get(i), i);
                    break;
                }
            }
        }
    }

    private void addRootAndItsView() {
        tView = new AndroidTreeView(this, root);
        tView.setDefaultAnimation(true);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setSelectionModeEnabled(true);
        mainNodeLayout.addView(tView.getView());
        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }
    }

    private void setChapterNameAndChildren(ChapterModel chapters, int pos) {
        headerProgress.setVisibility(View.GONE);
        TreeNode subjectName = new TreeNode(chapters.chapterName).setViewHolder(new CheckedItemViewHolder(this, false));
        TreeNode file1 = new TreeNode(chapters.chapterName.toString() + ".html").setViewHolder(new CheckedItemViewHolder(this, true));
        TreeNode file2 = new TreeNode(chapters.chapterName.toString() + "_video.mpg").setViewHolder(new CheckedItemViewHolder(this, true));
        subjectName.addChildren(file1, file2);
        root.addChild(subjectName);
    }

    private void initNodes() {
        root = TreeNode.root();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", tView.getSaveState());
    }
}
