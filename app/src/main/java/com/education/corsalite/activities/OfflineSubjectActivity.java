package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.holders.IconTreeItemHolder;
import com.education.corsalite.models.ChapterModel;
import com.education.corsalite.models.SubjectModel;
import com.education.corsalite.models.db.ContentIndexResponse;
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

    private int counter = 0;
    private LinearLayout mainNodeLayout;
    private AndroidTreeView tView;
    private TreeNode root;
    private TreeNode contentRoot;
    private String mSubjectId = "";
    private String mChapterId = "";
    private String mTopicId = "";
    private String mContentId = "";
    private String mSubjectName = "";
    private String mCourseId = "";
    private List<ContentIndex> contentIndexList;
    private ArrayList<SubjectModel> subjectModelList;
    private ArrayList<ChapterModel> chapterModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_offline_content, null);
        frameLayout.addView(myView);
        mainNodeLayout = (LinearLayout) findViewById(R.id.main_node);
        setToolbarTitle("Offline Content");
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
        }
        getContentIndex(mCourseId, LoginUserCache.getInstance().loginResponse.studentId);
        initNodes();
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
                            mContentIndexResponse.contentIndexes = contentIndexList;
                            initNodes();
                            prefillSubjects();
                        }
                    }
                });
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
                setChapterNameAndChilds(chapterModelList.get(i).chapterName,i);
            }
        }
    }

    private void addRootAndItsView() {
        root.addChild(contentRoot);
        tView = new AndroidTreeView(this, root);
        tView.setDefaultAnimation(true);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);
        tView.setDefaultNodeClickListener(nodeClickListener);
        tView.setDefaultNodeLongClickListener(nodeLongClickListener);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        mainNodeLayout.addView(tView.getView());
    }

    private void setChapterNameAndChilds(String subName,int pos) {
        TreeNode subjectName = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, subName));
        TreeNode file1 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, subName+".html"));
        TreeNode file2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, subName+"_video.mpg"));
        subjectName.addChildren(file1, file2);
        contentRoot.addChildren(subjectName);
    }

    private void initNodes() {
        root = TreeNode.root();
        contentRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, mSubjectName));
    }

    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
        }
    };

    private TreeNode.TreeNodeLongClickListener nodeLongClickListener = new TreeNode.TreeNodeLongClickListener() {
        @Override
        public boolean onLongClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            Toast.makeText(OfflineSubjectActivity.this, "Long click: " + item.text, Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putString("tState", tView.getSaveState());
    }
}
