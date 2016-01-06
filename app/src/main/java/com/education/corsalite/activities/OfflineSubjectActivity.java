package com.education.corsalite.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.DbManager;
import com.education.corsalite.event.OfflineEventClass;
import com.education.corsalite.holders.CheckedItemViewHolder;
import com.education.corsalite.holders.IconTreeItemHolder;
import com.education.corsalite.models.ChapterModel;
import com.education.corsalite.models.ContentModel;
import com.education.corsalite.models.SubjectModel;
import com.education.corsalite.models.TopicModel;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.responsemodels.Content;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.utils.AppPref;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.FileUtilities;
import com.google.gson.Gson;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by ayush on 05/10/15.
 */
public class OfflineSubjectActivity extends AbstractBaseActivity {

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
    private HashMap<String,TopicModel> topicModelHashMap = new HashMap<String, TopicModel>();
    private LinearLayout downloadImage;
    private ProgressBar headerProgress;
    Bundle savedInstanceState;
    private int chapterIndex = 0;
    private Dialog d;
    List<View> topicList = new ArrayList<View>();
    List<View> contentList = new ArrayList<View>();
    List<View> allViews = new ArrayList<View>();

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
        getContentIndex(mCourseId, LoginUserCache.getInstance().loginResponse.studentId);
        initNodes();
    }

    private void loopCheckedViews() {
        String videoContentId = "";
        String htmlContentId = "";
        d.setTitle(getResources().getString(R.string.offline_dialog_title_text));
        LinearLayout subjectLayout = (LinearLayout) d.findViewById(R.id.subject_layout);
        subjectLayout.removeAllViews();
        subjectLayout.addView(getTextView(root.getChildren().get(0).getValue().toString() + "\n"));
        LinearLayout topicLayout = (LinearLayout) d.findViewById(R.id.topic_layout);
        topicLayout.removeAllViews();
        List<OfflineContent> offlineContents = new ArrayList<>();
        for (TreeNode n : root.getChildren()) {
            int topicCount = 0;
            for (TreeNode innerNode : n.getChildren()) {
                TopicModel topicModel = topicModelList.get(topicCount);
                if (((CheckBox) innerNode.getViewHolder().getNodeView().findViewById(R.id.node_selector)).isChecked()) {
                    String contentText = "";
                    int contentCount = 0;
                    for (TreeNode innerMostNode : innerNode.getChildren()) {
                        ContentModel contentModel = topicModel.contentMap.get(contentCount);
                        if (((CheckBox) innerMostNode.getViewHolder().getNodeView().findViewById(R.id.node_selector)).isChecked()) {
                            contentText += "\t\t" + innerMostNode.getValue().toString() + "\n";
                            if (contentModel.type.equals("mpg")) {
                                videoContentId += contentModel.idContent + ",";
                            } else {
                                htmlContentId += contentModel.idContent + ",";
                            }
                            OfflineContent offlineContent = new OfflineContent(mCourseId,mCourseName,mSubjectId,mSubjectName,
                                    mChapterId,mChapterName,topicModel.idTopic,topicModel.topicName,
                                    contentModel.idContent,contentModel.contentName,contentModel.contentName + "." + contentModel.type);
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
        AppPref.getInstance(this).save("DATA_IN_PROGRESS", new Gson().toJson(offlineContents));
        setUpDialogLogic(method(videoContentId), method(htmlContentId));
    }

    private void setUpDialogLogic(String videoContentId, String htmlContentId) {
        d.show();
        final CheckBox videoContent = (CheckBox) d.findViewById(R.id.download_video);
        final CheckBox htmlContent = (CheckBox) d.findViewById(R.id.download_html);
        if (videoContentId.isEmpty()) {
            videoContent.setEnabled(false);
        }
        if (htmlContentId.isEmpty()) {
            htmlContent.setEnabled(false);
        }
        Button okButton = (Button) d.findViewById(R.id.ok);
        Button okCancel = (Button) d.findViewById(R.id.cancel);

        final String finalHtmlContentId = htmlContentId.trim();
        final String finalVideoContentId = videoContentId.trim();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String finalContentIds = "";
                if (!finalHtmlContentId.isEmpty() && htmlContent.isChecked()) {
                    finalContentIds += finalHtmlContentId;
                }
                if (!finalVideoContentId.isEmpty() && videoContent.isChecked()) {
                    finalContentIds += COMMA_STRING + finalVideoContentId;
                }
                if (finalContentIds.isEmpty()) {
                    Toast.makeText(OfflineSubjectActivity.this, getResources().getString(R.string.select_content_toast), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OfflineSubjectActivity.this, getResources().getString(R.string.content_downloaded_toast), Toast.LENGTH_SHORT).show();
                    getContent(finalContentIds);
                    finish();
                }
            }
        });

        okCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
    }

    public String method(String str) {
        if (str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
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
                ApiCacheHolder.getInstance().setContentResponse(contents);
                storeDataInDb(contents);
            }
        });
    }

    private void saveFileToDisk(String htmlText, Content content) {
        TopicModel topicModel = topicModelHashMap.get(content.idContent);
        FileUtilities fileUtilities = new FileUtilities(this);
        String folderStructure =  selectedCourse.name + File.separator +mSubjectName + File.separator +
                mChapterName + File.separator + topicModel.topicName;

        if(TextUtils.isEmpty(htmlText) || htmlText.endsWith(Constants.HTML_FILE)) {
            showToast(getString(R.string.file_exists));
        } else {
            String htmlUrl = fileUtilities.write(content.name + "." + Constants.HTML_FILE, htmlText, folderStructure);
            if(htmlUrl != null) {
                getEventbus().post(new OfflineEventClass(content.idContent));
                showToast(getString(R.string.file_saved));
            } else {
                showToast(getString(R.string.file_save_failed));
            }
        }
    }

    private void storeDataInDb(List<Content> contents){
        String fileName;
        List<OfflineContent> offlineContents = new ArrayList<OfflineContent>(contents.size());
        OfflineContent offlineContent;
        for(Content content : contents) {
            if(TextUtils.isEmpty(content.type)) {
                fileName = content.name + ".html";
            } else {
                fileName = content.name + "." + content.type;
            }
            TopicModel topicModel = topicModelHashMap.get(content.idContent);
            offlineContent = new OfflineContent(mCourseId, mCourseName, mSubjectId, mSubjectName, mChapterId, mChapterName, topicModel.idTopic, topicModel.topicName, content.idContent, content.name, fileName);
            offlineContents.add(offlineContent);
            saveFileToDisk(getHtmlText(content),content);
        }
        AppPref.getInstance(OfflineSubjectActivity.this).save("DATA_IN_PROGRESS",null);
        getEventbus().post(true);
        DbManager.getInstance(this).saveOfflineContent(offlineContents);
    }

    private String getHtmlText(Content content){
        String text = content.type.equalsIgnoreCase(Constants.VIDEO_FILE) ?
                content.url :
                "<script type='text/javascript'>" +
                        "function copy() {" +
                        "    var t = (document.all) ? document.selection.createRange().text : document.getSelection();" +
                        "    return t;" +
                        "}" +
                        "</script>" + content.contentHtml;
        return  text;
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
    }

    private void setChapterNameAndChildren(ChapterModel chapters, int pos) {
        d = getDisplayDialog();
        headerProgress.setVisibility(View.GONE);
        TreeNode subjectName = new TreeNode(chapters.chapterName).setViewHolder(new CheckedItemViewHolder(this, false));
        topicModelList = (ArrayList<TopicModel>) chapters.topicMap;
        for (int i = 0; i < chapters.topicMap.size(); i++) {
            addTopic(chapters.topicMap.get(i), subjectName, d);
        }
        root.addChild(subjectName);
    }

    private void addTopic(TopicModel topicModel, TreeNode subjectName, Dialog d) {
        TreeNode topicName = new TreeNode(topicModel.topicName).setViewHolder(new CheckedItemViewHolder(this, false));
        int size = topicModel.contentMap.size();
        TreeNode file1 = null;
        for (ContentModel contentModel : topicModel.contentMap) {
            contentList.add(getTextView(contentModel.contentName + "." + contentModel.type));
            topicModelHashMap.put(contentModel.idContent,topicModel);
            file1 = new TreeNode(contentModel.contentName + "." + contentModel.type).setViewHolder(new CheckedItemViewHolder(this, true));
            topicName.addChildren(file1);
        }
        topicList.add(getTextView(topicModel.topicName));
        subjectName.addChild(topicName);
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
