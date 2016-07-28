package com.education.corsalite.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.enums.OfflineContentStatus;
import com.education.corsalite.event.RefreshDownloadsEvent;
import com.education.corsalite.holders.CheckedItemViewHolder;
import com.education.corsalite.holders.IconTreeItemHolder;
import com.education.corsalite.models.ChapterModel;
import com.education.corsalite.models.ContentModel;
import com.education.corsalite.models.SubjectModel;
import com.education.corsalite.models.TopicModel;
import com.education.corsalite.models.db.ExerciseOfflineModel;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.services.ContentDownloadService;
import com.education.corsalite.services.TestDownloadService;
import com.education.corsalite.utils.AppPref;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.Gson;
import com.education.corsalite.utils.L;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.client.Response;

/**
 * Created by ayush on 05/10/15.
 */
public class SaveForOfflineActivity extends AbstractBaseActivity {

    public static final String COMMA_STRING = ",";
    public static final String SUBJECT_ID = "subjectId";
    public static final String CHAPTER_ID = "chapterId";
    public static final String TOPIC_ID = "topicId";
    public static final String CONTENT_ID = "contentId";
    public static final String SUBJECT = "subject";
    public static final String COURSE_ID = "courseId";
    public static final String CHAPTER_NAME = "chapterName";
    public static final String COURSE_NAME = "courseName";
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
    private String mCourseName = "";
    private List<ContentIndex> contentIndexList;
    private ArrayList<SubjectModel> subjectModelList;
    private ArrayList<ChapterModel> chapterModelList;
    private ArrayList<TopicModel> topicModelList;
    private HashMap<String, TopicModel> topicModelHashMap = new HashMap<String, TopicModel>();
    private LinearLayout downloadImage;
    private ProgressBar headerProgress;
    private Bundle savedInstanceState;
    private int chapterIndex = 0;
    private Dialog dialog;
    private List<View> topicList = new ArrayList<View>();
    private List<View> contentList = new ArrayList<View>();
    private List<View> allViews = new ArrayList<View>();
    private List<ExerciseOfflineModel> offlineExerciseModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_offline_subject, null);
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
        getContentIndex(mCourseId, LoginUserCache.getInstance().getStudentId());
        initNodes();
    }

    private void loopCheckedViews() {
        try {
            offlineExerciseModels = new ArrayList<>();
            String contentName = "";
            String videoContentId = "";
            String htmlContentId = "";
            if (dialog == null) {
                dialog = getDisplayDialog();
            }
            dialog.setTitle(getResources().getString(R.string.offline_dialog_title_text));
            LinearLayout subjectLayout = (LinearLayout) dialog.findViewById(R.id.subject_layout);
            subjectLayout.removeAllViews();
            subjectLayout.addView(getTextView(root.getChildren().get(0).getValue().toString()));
            LinearLayout topicLayout = (LinearLayout) dialog.findViewById(R.id.topic_layout);
            topicLayout.removeAllViews();
            List<OfflineContent> offlineContents = new ArrayList<>();
            for (TreeNode n : root.getChildren()) {
                int topicCount = 0;
                for (TreeNode innerNode : n.getChildren()) {
                    TopicModel topicModel = topicModelList.get(topicCount);
                    if (innerNode.isSelected()) {
                        String contentText = "";
                        int contentCount = 0;
                        for (TreeNode innerMostNode : innerNode.getChildren()) {
                            ContentModel contentModel = topicModel.contentMap.get(contentCount);
                            if (innerMostNode.isSelected()) {
                                contentText += "\t\t" + innerMostNode.getValue().toString() + "\n";
                                contentName = contentModel.contentName;
                                if (contentModel.type.equals(Constants.VIDEO_FILE)) {
                                    videoContentId += contentModel.idContent + ",";
                                } else if (contentModel.type.equals(Constants.HTML_FILE)) {
                                    htmlContentId += contentModel.idContent + ",";
                                } else {
                                    ExerciseOfflineModel model = new ExerciseOfflineModel(
                                            AbstractBaseActivity.getSelectedCourseId(), topicModel.idTopic);
                                    if (!offlineExerciseModels.contains(model)) {
                                        offlineExerciseModels.add(model);
                                    }
                                }
                                OfflineContent offlineContent = new OfflineContent(mCourseId, mCourseName,
                                        mSubjectId, mSubjectName,
                                        mChapterId, mChapterName,
                                        topicModel.idTopic, topicModel.topicName,
                                        contentModel.idContent, contentModel.contentName,
                                        contentModel.contentName
                                                + (contentModel.type.isEmpty() ? "" : ".")
                                                + contentModel.type);
                                offlineContent.status = OfflineContentStatus.WAITING;
                                offlineContents.add(offlineContent);
                            }
                            contentCount++;
                        }
                        String finalNodeValue = innerNode.getValue().toString() + "\n" + contentText;
                        topicLayout.addView(getTextView("\t" + finalNodeValue));
                    }
                    topicCount++;
                }
            }

            Collections.sort(offlineContents,
                    new Comparator<OfflineContent>() {
                        public int compare(OfflineContent ord1, OfflineContent ord2) {
                            return ord1.chapterName.compareToIgnoreCase(ord2.chapterName);
                        }
                    });
            setUpDialogLogic(offlineContents, offlineExerciseModels);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    // store the in-progress in db
    private void storeInProgressItemsInDb(List<OfflineContent> offlineContents, List<ExerciseOfflineModel> offlineExerciseModels) {
        if(offlineContents != null && !offlineContents.isEmpty()) {
            dbManager.saveOfflineContents(offlineContents);
        }
        if(offlineExerciseModels != null && !offlineExerciseModels.isEmpty()) {
            dbManager.saveOfflineExerciseTests(offlineExerciseModels);
        }
    }

    private void setUpDialogLogic(final List<OfflineContent> offlineContents, final List<ExerciseOfflineModel> offlineExerciseModels) {
        dialog.show();
        Button downloadBtn = (Button) dialog.findViewById(R.id.ok);
        Button cancelBtn = (Button) dialog.findViewById(R.id.cancel);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), ContentDownloadService.class));
                storeInProgressItemsInDb(offlineContents, offlineExerciseModels);
                EventBus.getDefault().post(new RefreshDownloadsEvent());
                startService(new Intent(getApplicationContext(), ContentDownloadService.class));
                dialog.dismiss();
                finish();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void startDownload(String htmlContentId, String videoContentId) {
        String finalContentIds = "";
        if (!htmlContentId.isEmpty()) {
            finalContentIds += htmlContentId;
        }
        if (!videoContentId.isEmpty()) {
            finalContentIds += COMMA_STRING + videoContentId;
        }

        if (offlineExerciseModels.size() > 0)
            downloadExercises();
        if (finalContentIds.isEmpty()) {
            Toast.makeText(SaveForOfflineActivity.this, getResources().getString(R.string.select_content_toast), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(SaveForOfflineActivity.this, getResources().getString(R.string.content_downloaded_toast), Toast.LENGTH_SHORT).show();
            if(!TextUtils.isEmpty(htmlContentId)) {
                getContent(htmlContentId, false);
            }
            if(!TextUtils.isEmpty(videoContentId)) {
                getContent(videoContentId, true);
            }
            dialog.dismiss();
            finish();
        }
    }

    private void downloadExercises() {
        Intent intent = new Intent(this, TestDownloadService.class);
        intent.putExtra("exercise_data", Gson.get().toJson(offlineExerciseModels));
        startService(intent);
    }

    public String method(String str) {
        if (str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private void getContent(String contentId, boolean isVideo) {
        storeDataInDb(contentId, isVideo);
        EventBus.getDefault().post(new RefreshDownloadsEvent());
    }

    private void storeDataInDb(String contentId, boolean isVideo) {
        List<OfflineContent> offlineContents = new ArrayList<>();
        OfflineContent offlineContent;
        offlineContent = new OfflineContent(mCourseId, mCourseName, mSubjectId, mSubjectName, mChapterId, mChapterName,
                null, null, contentId, null, null);
        offlineContent.status = OfflineContentStatus.WAITING;
        offlineContents.add(offlineContent);
        AppPref.getInstance(SaveForOfflineActivity.this).save("DATA_IN_PROGRESS", null);
        dbManager.saveOfflineContents(offlineContents);
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(SUBJECT_ID) && bundle.getString(SUBJECT_ID) != null) {
                mSubjectId = bundle.getString(SUBJECT_ID);
            }
            if (bundle.containsKey(CHAPTER_ID) && bundle.getString(CHAPTER_ID) != null) {
                mChapterId = bundle.getString(CHAPTER_ID);
            }
            if (bundle.containsKey(TOPIC_ID) && bundle.getString(TOPIC_ID) != null) {
                mTopicId = bundle.getString(TOPIC_ID);
            }
            if (bundle.containsKey(CONTENT_ID) && bundle.getString(CONTENT_ID) != null) {
                mContentId = bundle.getString(CONTENT_ID);
            }
            if (bundle.containsKey(SUBJECT) && bundle.getString(SUBJECT) != null) {
                mSubjectName = bundle.getString(SUBJECT);
            }
            if (bundle.containsKey(COURSE_ID) && bundle.getString(COURSE_ID) != null) {
                mCourseId = bundle.getString(COURSE_ID);
            }
            if (bundle.containsKey(CHAPTER_NAME) && bundle.getString(CHAPTER_NAME) != null) {
                mChapterName = bundle.getString(CHAPTER_NAME);
            }
            if (bundle.containsKey(COURSE_NAME) && bundle.getString(COURSE_NAME) != null) {
                mCourseName = bundle.getString(COURSE_NAME);
            }
        }
    }

    @Override
    protected void getContentIndex(String courseId, String studentId) {
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
                            initNodes();
                            prefillSubjects();
                        }
                    }
                });
    }

    private void prefillSubjects() {
        ContentIndex mContentIndex = contentIndexList.get(0);
        subjectModelList = new ArrayList<SubjectModel>(mContentIndex.subjectModelList);
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

    private Dialog getDisplayDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.offline_dialog);
        return dialog;
    }

    private TextView getTextView(String subject) {
        TextView newView = (TextView) View.inflate(this, R.layout.offline_content_text, null);
        newView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newView.setText(subject);
        return newView;
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
        tView.expandLevel(1);
    }

    private void setChapterNameAndChildren(ChapterModel chapters, int pos) {
        dialog = getDisplayDialog();
        headerProgress.setVisibility(View.GONE);
        TreeNode subjectName = new TreeNode(chapters.chapterName).setViewHolder(new CheckedItemViewHolder(this, false));
        topicModelList = (ArrayList<TopicModel>) chapters.topicMap;
        Collections.sort(topicModelList);

        for (int i = 0; i < chapters.topicMap.size(); i++) {
            addTopic(chapters.topicMap.get(i), subjectName, dialog);
        }
        root.addChild(subjectName);
    }

    private void addTopic(TopicModel topicModel, TreeNode subjectName, Dialog d) {
        TreeNode topicName = new TreeNode(topicModel.topicName).setViewHolder(new CheckedItemViewHolder(this, false));
        TreeNode file1 = null;

        List<ContentModel> contentModelArrayList = getSortedList(topicModel.contentMap);
        for (ContentModel contentModel : contentModelArrayList) {
            contentList.add(getTextView(contentModel.contentName + (contentModel.type.isEmpty() ? "" : ".") + contentModel.type));
            topicModelHashMap.put(contentModel.idContent, topicModel);
            file1 = new TreeNode(contentModel.contentName + (contentModel.type.isEmpty() ? "" : ".") + contentModel.type).setViewHolder(new CheckedItemViewHolder(this, true));
            topicName.addChildren(file1);
        }
        topicList.add(getTextView(topicModel.topicName));


        subjectName.addChild(topicName);
    }

    private List<ContentModel> getSortedList(List<ContentModel> contents) {
        List<ContentModel> exercises = new ArrayList<>();
        List<ContentModel> htmlContents = new ArrayList<>();
        List<ContentModel> videoContents = new ArrayList<>();
        for (ContentModel content : contents) {
            if (content.type.toLowerCase().endsWith("mpg")) {
                videoContents.add(content);
            } else if (content.type.toLowerCase().endsWith("html")) {
                htmlContents.add(content);
            } else {
                exercises.add(content);
            }
        }

        ContentModel contentModel = new ContentModel();
        contentModel.contentName = "Exercise";
        contentModel.type = "";
        exercises.add(contentModel);

        Collections.sort(videoContents,
                new Comparator<ContentModel>() {
                    public int compare(ContentModel content1, ContentModel content2) {
                        return content1.contentName.compareToIgnoreCase(content2.contentName);
                    }
                });
        contents.clear();
        contents.addAll(htmlContents);
        contents.addAll(videoContents);
        contents.addAll(exercises);
        return contents;
    }

    private void initNodes() {
        root = TreeNode.root();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState != null && tView != null) {
            outState.putString("tState", tView.getSaveState());
        }
    }
}
