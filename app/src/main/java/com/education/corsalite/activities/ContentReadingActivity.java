package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.education.corsalite.R;
import com.education.corsalite.adapters.ChapterAdapter;
import com.education.corsalite.adapters.GridRecyclerAdapter;
import com.education.corsalite.adapters.SubjectAdapter;
import com.education.corsalite.adapters.TopicAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.event.ContentReadingEvent;
import com.education.corsalite.fragments.VideoListDialog;
import com.education.corsalite.models.ChapterModel;
import com.education.corsalite.models.ContentModel;
import com.education.corsalite.models.SubjectModel;
import com.education.corsalite.models.TopicModel;
import com.education.corsalite.models.db.ExerciseOfflineModel;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.responsemodels.Content;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.FileUtils;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.SystemUtils;
import com.education.corsalite.utils.TimeUtils;
import com.education.corsalite.views.CorsaliteWebViewClient;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Girish on 28/09/15.
 */
public class ContentReadingActivity extends AbstractBaseActivity {

    @Bind(R.id.iv_editnotes)
    ImageView ivEditNotes;
    @Bind(R.id.iv_forum)
    ImageView ivForum;
    @Bind(R.id.webView_content_reading)
    WebView webviewContentReading;
    @Bind(R.id.sp_subject)
    Spinner spSubject;
    @Bind(R.id.sp_chapter)
    Spinner spChapter;
    @Bind(R.id.sp_topic)
    Spinner spTopic;
    @Bind(R.id.topic_icon)
    ImageView topicSpinnerArrow;
    @Bind(R.id.iv_exercise)
    ImageView ivExercise;
    @Bind(R.id.vs_container)
    ViewSwitcher mViewSwitcher;
    @Bind(R.id.footer_layout)
    RelativeLayout webFooter;
    @Bind(R.id.btn_next)
    Button btnNext;
    @Bind(R.id.btn_previous)
    Button btnPrevious;
    @Bind(R.id.tv_video)
    TextView tvVideo;
    @Bind(R.id.not_available_offline_txt)
    TextView notAvailableForOfflineTxt;

    private List<ContentIndex> contentIndexList;
    private List<SubjectModel> subjectModelList;
    private List<ChapterModel> chapterModelList;
    private List<TopicModel> topicModelList;
    private List<ContentModel> contentModelList;
    private List<ContentModel> videoModelList;
    private List<Content> contentList;
    private List<ExerciseOfflineModel> offlineExercises;

    private String mSubjectId = "";
    private String mChapterId = "";
    private String mTopicId = "";
    private String mContentId = "";
    private String mContentName = "";
    private int mContentIdPosition;

    private String selectedText = "";
    private String studentId = "";
    private String htmlFileText = "";
    private long eventStartTime;
    private long eventEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        studentId = LoginUserCache.getInstance().getStudentId();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_web, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        setToolbarForContentReading();


         toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        initWebView();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("clear_cookies")) {
                webviewContentReading.clearCache(true);
            }
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

                if (bundle.containsKey("contentName") && bundle.getString("contentName") != null) {
                    mContentName = bundle.getString("contentName");
                } else {
                    throw new NullPointerException("You must have to pass content name");
                }
            }
        }
        setListeners();

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {}

            @Override
            public void onDrawerOpened(View drawerView) {}

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {}
        });

    }

    private void loadOfflineExercises() {
        offlineExercises = dbManager.getOfflineExerciseModels(AbstractBaseActivity.getSelectedCourseId());
        for (ExerciseOfflineModel model : offlineExercises) {
            if (model.topicId.equals(mTopicId) && model.courseId.equals(getSelectedCourseId())) {
                AbstractBaseActivity.setSharedExamModels(model.questions);
                showExercise();
            }
        }
    }
    
    @Override
    public void onEvent(Course course) {
        super.onEvent(course);
        getContentIndex(course.courseId.toString(), studentId);
    }

    private void postContentReadingEvent() {
        try {
            eventEndDate = TimeUtils.currentTimeInMillis();
            ContentReadingEvent event = new ContentReadingEvent();
            event.idContent = contentModelList.get(0).idContent;
            event.idStudent = LoginUserCache.getInstance().getStudentId();
            event.eventStartTime = TimeUtils.getDateString(eventStartTime);
            event.eventEndTime= TimeUtils.getDateString(eventEndDate);
            event.updatetime = TimeUtils.getDateString(TimeUtils.currentTimeInMillis());
            getEventbus().post(event);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        eventStartTime = 0;
        eventEndDate = 0;
    }

    private void initWebView() {
        webviewContentReading.getSettings().setSupportZoom(true);
        webviewContentReading.getSettings().setBuiltInZoomControls(true);
        webviewContentReading.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webviewContentReading.setScrollbarFadingEnabled(true);
        webviewContentReading.getSettings().setLoadsImagesAutomatically(true);
        webviewContentReading.getSettings().setJavaScriptEnabled(true);

        if (getExternalCacheDir() != null) {
            webviewContentReading.getSettings().setAppCachePath(getExternalCacheDir().getAbsolutePath());
        } else {
            webviewContentReading.getSettings().setAppCachePath(getCacheDir().getAbsolutePath());
        }
        webviewContentReading.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);

        webviewContentReading.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                selectedText = message;
                selectedText = selectedText.replace("'", "&#39;");
                if (operation.equals("Note")) {
                    addToNote(selectedText);
                } else if (operation.equals("Forum")) {
                    addToForum(selectedText);
                }
                result.confirm();
                return true;
            }
        });
        webviewContentReading.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void test() {
                L.debug("JS", "test");
            }

            @JavascriptInterface
            public void onData(String value) {
                L.info("JS data" + value);
            }
        }, "android");
        // Load the URLs inside the WebView, not in the external web browser
        webviewContentReading.setWebViewClient(new MyWebViewClient(this));
    }

    private void addToNote(String htmlText) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "Note");
        bundle.putString("operation", "Add");
        bundle.putString("student_id", LoginUserCache.getInstance().getLongResponse().studentId);
        bundle.putString("topic_id", topicModelList.get(spTopic.getSelectedItemPosition()).idTopic);
        bundle.putString("content_id", contentModelList.get(mContentIdPosition).idContent);
        bundle.putString("content", htmlText);
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void addToForum(String htmlText) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "Forum");
        bundle.putString("operation", "Add");
        bundle.putString("student_id", LoginUserCache.getInstance().getLongResponse().studentId);
        bundle.putString("subject_id", subjectModelList.get(spSubject.getSelectedItemPosition()).idSubject);
        bundle.putString("chapter_id", chapterModelList.get(spChapter.getSelectedItemPosition()).idChapter);
        bundle.putString("topic_id", topicModelList.get(spTopic.getSelectedItemPosition()).idTopic);
        bundle.putString("content_id", contentModelList.get(mContentIdPosition).idContent);
        bundle.putString("content", htmlText);
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void loadWeb(String htmlUrl) {
        // Initialize the WebView
        // start the timer
        eventStartTime = TimeUtils.currentTimeInMillis();
        mContentId = "";
        webviewContentReading.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        htmlFileText = htmlUrl;
        if (htmlUrl.endsWith(Constants.HTML_FILE)) {
            webviewContentReading.loadUrl(htmlUrl);
        } else {
            webviewContentReading.loadDataWithBaseURL("file:///android_asset/", htmlUrl, "text/html; charset=UTF-8", null, "");
        }
        navigateButtonEnabled();
    }

    public void navigateButtonEnabled() {
        if (spSubject.getSelectedItemPosition() == 0 && spChapter.getSelectedItemPosition() == 0 &&
                spTopic.getSelectedItemPosition() == 0 && mContentIdPosition == 0) {
            btnPrevious.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            return;
        }

        if (spSubject.getSelectedItemPosition() == subjectModelList.size() - 1 &&
                spChapter.getSelectedItemPosition() == chapterModelList.size() - 1 &&
                spTopic.getSelectedItemPosition() == topicModelList.size() - 1 &&
                mContentIdPosition == contentIndexList.size() - 1) {
            btnPrevious.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
            return;
        }
        btnPrevious.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content_reading, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_read_offline:
                saveFileToDisk();
                return true;
            case R.id.action_download_pdf:
                showToast("Download PDF");
                return true;
            case R.id.action_view_notes:
                startNotesActivity();
                return true;
            case R.id.action_rate_it:
                showToast("Rate Content");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startNotesActivity() {
        try {
            Intent intent = new Intent(this, NotesActivity.class);
            intent.putExtra(GridRecyclerAdapter.COURSE_ID, AbstractBaseActivity.getSelectedCourseId().toString());
            intent.putExtra(GridRecyclerAdapter.SUBJECT_ID, subjectModelList.get(spSubject.getSelectedItemPosition()).idSubject);
            intent.putExtra(GridRecyclerAdapter.CHAPTER_ID, chapterModelList.get(spChapter.getSelectedItemPosition()).idChapter);
            intent.putExtra(GridRecyclerAdapter.TOPIC_ID, topicModelList.get(spTopic.getSelectedItemPosition()).idTopic);
            startActivity(intent);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void setListeners() {
        ivEditNotes.setOnClickListener(mClickListener);
        ivForum.setOnClickListener(mClickListener);
        btnNext.setOnClickListener(mClickListener);
        btnPrevious.setOnClickListener(mClickListener);
        tvVideo.setOnClickListener(mClickListener);
        ivExercise.setOnClickListener(mClickListener);
        topicSpinnerArrow.setOnClickListener(mClickListener);

    }

    private String operation = "";

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_forum:
                    operation = "Forum";
                    webviewContentReading.loadUrl("javascript:alert(copy())");
                    break;
                case R.id.iv_editnotes:
                    operation = "Note";
                    webviewContentReading.loadUrl("javascript:alert(copy())");
                    break;
                case R.id.btn_next:
                    mContentIdPosition += 1;
                    loadNext();
                    break;
                case R.id.btn_previous:
                    mContentIdPosition -= 1;
                    loadPrevious();
                    break;
                case R.id.tv_video:
                    showVideoDialog();
                    break;
                case R.id.topic_icon:
                    spTopic.performClick();
                    break;
                case R.id.iv_exercise:
                    Intent intent = new Intent(ContentReadingActivity.this, ExamEngineActivity.class);
                    intent.putExtra(Constants.SELECTED_TOPIC_NAME, topicModelList.get(spTopic.getSelectedItemPosition()).topicName);
                    intent.putExtra(Constants.TEST_TITLE, "Exercises");
                    intent.putExtra(Constants.SELECTED_POSITION, 0);
                    startActivity(intent);
                    break;
            }
        }
    };

    private void saveFileToDisk() {
        FileUtils fileUtils = new FileUtils(this);
        String folderStructure = getSelectedCourseName() + File.separator +
                subjectModelList.get(spSubject.getSelectedItemPosition()).subjectName + File.separator +
                chapterModelList.get(spChapter.getSelectedItemPosition()).chapterName + File.separator +
                topicModelList.get(spTopic.getSelectedItemPosition()).topicName;

        if (TextUtils.isEmpty(htmlFileText) || htmlFileText.endsWith(Constants.HTML_FILE)) {
            showToast("File already exists.");
        } else {
            String htmlUrl = fileUtils.write(contentModelList.get(mContentIdPosition).contentName + "." +
                    Constants.HTML_FILE, htmlFileText, folderStructure);
            if (htmlUrl != null) {
                showToast("File saved");
                htmlFileText = "";
            } else {
                showToast("Unable to save file.");
            }
        }
    }

    private void loadNext() {
        postContentReadingEvent();
        int nextTopicPosition = spTopic.getSelectedItemPosition() + 1;
        if (nextTopicPosition >= topicModelList.size()) {
            int nextChapterPosition = spChapter.getSelectedItemPosition() + 1;
            if (nextChapterPosition >= chapterModelList.size()) {
                int nextSubjectPosition = spSubject.getSelectedItemPosition() + 1;
                if (nextSubjectPosition >= subjectModelList.size()) {
                    btnNext.setVisibility(View.GONE);
                } else {
                    spSubject.setSelection(nextSubjectPosition);
                }
            } else {
                spChapter.setSelection(nextChapterPosition);
            }
        } else {
            spTopic.setSelection(nextTopicPosition);
        }
    }

    private String getHtmlcontent(String content) {
        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<script type='text/javascript' src='file:///android_asset/jquery/jquery-latest.js'></script>" +
                "<script type='text/javascript' src='file:///android_asset/jquery/jquery.selection.js'></script>" +
                "<script>" +
                "function copy() {" +
                "return $.selection('html');" +
                "}" +
                "</script>" +
                "</head>" +
                "<body>"
                + content +
                "</body>" +
                "</html>";
        return htmlContent;
    }

    private void loadPrevious() {
        postContentReadingEvent();
        int previousTopicPosition = spTopic.getSelectedItemPosition() - 1;
        if (previousTopicPosition < 0) {
            int previousChapterPosition = spChapter.getSelectedItemPosition() - 1;
            if (previousChapterPosition < 0) {
                int previousSubjectPosition = spSubject.getSelectedItemPosition() - 1;
                if (previousSubjectPosition < 0) {
                    btnPrevious.setVisibility(View.GONE);
                } else {
                    spSubject.setSelection(previousSubjectPosition);
                }
            } else {
                spChapter.setSelection(previousChapterPosition);
            }
        } else {
            spTopic.setSelection(previousTopicPosition);
        }
    }

    private class MyWebViewClient extends CorsaliteWebViewClient {

        public MyWebViewClient(Context context) {
            super(context);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (checkNetconnection(view, url)) {
                return true;
            }
            // TODO : need to handle it for production
            if (Uri.parse(url).getHost().contains("corsalite.com")) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    private void getWebData(List<Content> mContentResponse, boolean updatePosition) {
        try {
            int count = 0;
            int listSize = mContentResponse.size();
            for (int i = 0; i < listSize; i++) {
                String contentId = mContentResponse.get(i).idContent;
                String contentType = mContentResponse.get(i).type + "";
                String text = contentType.equalsIgnoreCase(Constants.VIDEO_FILE) ?
                        mContentResponse.get(i).url : getHtmlcontent(mContentResponse.get(i).contentHtml);
                if (mContentId.isEmpty()) {
                    if (!TextUtils.isEmpty(text) && count == 0) {
                        count = count + 1;
                        if (updatePosition) {
                            mContentIdPosition = i;
                        }
                        loadWeb(text);
                    }
                } else {
                    if (!TextUtils.isEmpty(text) && contentId.equalsIgnoreCase(mContentId)) {
                        if (updatePosition) {
                            mContentIdPosition = i;
                        }
                        loadWeb(text);
                    }
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        if (mViewSwitcher.getNextView() instanceof RelativeLayout) {
            mViewSwitcher.showNext();
        }
    }

    @Override
    protected void getContentIndex(String courseId, String studentId) {
        if (mViewSwitcher.indexOfChild(mViewSwitcher.getCurrentView()) == 1) {
            mViewSwitcher.showPrevious();
            notAvailableForOfflineTxt.setVisibility(View.GONE);
        }

        ApiManager.getInstance(this).getContentIndex(courseId, studentId,
                new ApiCallback<List<ContentIndex>>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            showToast(error.message);
                            if (mViewSwitcher.getNextView() instanceof RelativeLayout) {
                                mViewSwitcher.showNext();
                            }
                        }
                    }

                    @Override
                    public void success(List<ContentIndex> mContentIndexs, Response response) {
                        super.success(mContentIndexs, response);
                        if (mContentIndexs != null) {
                            contentIndexList = mContentIndexs;
                            ApiCacheHolder.getInstance().setcontentIndexResponse(mContentIndexs);
                            dbManager.saveReqRes(ApiCacheHolder.getInstance().contentIndex);
                            showSubject();
                        }
                    }
                });
    }

    private void getExercise(int topicPosition) {
        if (SystemUtils.isNetworkConnected(this)) {
            ivExercise.setVisibility(View.GONE);
            String topicId = topicModelList.get(topicPosition).idTopic;
            if (offlineExercises != null && !offlineExercises.isEmpty() && offlineExercises.contains(new ExerciseOfflineModel(getSelectedCourseId(), topicId))) {
                for (ExerciseOfflineModel model : offlineExercises) {
                    if (model.topicId.equals(topicId) && model.courseId.equals(getSelectedCourseId())) {
                        AbstractBaseActivity.setSharedExamModels(model.questions);
                        showExercise();
                    }
                }
            } else {
                ApiManager.getInstance(this).getExercise(topicModelList.get(topicPosition).idTopic, getSelectedCourseId(),
                        studentId, "", new ApiCallback<List<ExamModel>>(this) {
                            @Override
                            public void success(List<ExamModel> examModels, Response response) {
                                super.success(examModels, response);
                                AbstractBaseActivity.setSharedExamModels(examModels);
                                showExercise();
                            }

                            @Override
                            public void failure(CorsaliteError error) {
                                super.failure(error);
                                showExercise();
                            }
                        });
            }
        }
    }

    private String contentIds = "";

    /**
     * called when sptopic is selected
     */
    private void getContentData(int topicPosition) {
        updateToolbarTitle();
        if (mViewSwitcher.indexOfChild(mViewSwitcher.getCurrentView()) == 1) {
            mViewSwitcher.showPrevious();
            notAvailableForOfflineTxt.setVisibility(View.GONE);
        }

        contentModelList = new ArrayList<>();
        videoModelList = new ArrayList<>();
        for (ContentModel contentModel : topicModelList.get(topicPosition).contentMap) {
            if (contentModel.type.endsWith(Constants.VIDEO_FILE)) {
                videoModelList.add(contentModel);
            } else {
                contentModelList.add(contentModel);
            }
        }

        if (videoModelList.size() > 0) {
            tvVideo.setVisibility(View.VISIBLE);
        } else {
            tvVideo.setVisibility(View.GONE);
        }

        contentIds = "";
        String contentNames = "";
        for (ContentModel contentModel : contentModelList) {
            if (contentIds.trim().length() > 0) {
                contentIds = contentIds + ",";
                contentNames = contentNames + ",";
            }
            contentNames = contentNames + contentModel.contentName + "." + contentModel.type;
            contentIds = contentIds + contentModel.idContent;
        }

        if (loadwebifFileExists(contentNames)) {
            if (mViewSwitcher.getNextView() instanceof RelativeLayout) {
                mViewSwitcher.showNext();
            }
            return;
        }
        if (SystemUtils.isNetworkConnected(this)) {
            getContent(contentIds, true);
        } else {
            notAvailableForOfflineTxt.setVisibility(View.VISIBLE);
        }
    }

    private void getContent(final String contentId, final boolean updatePosition) {
        mContentId = contentId;
        ApiManager.getInstance(this).getContent(contentId, "", new ApiCallback<List<Content>>(this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                try {
                    if (TextUtils.isEmpty(mContentId) || !mContentId.equalsIgnoreCase(contentId)) {
                        return;
                    }
                } catch (Exception e) {
                    L.error(e.getMessage(), e);
                    return;
                }
                showToast(error.message);
                if (mViewSwitcher.getNextView() instanceof RelativeLayout) {
                    mViewSwitcher.showNext();
                }
            }

            @Override
            public void success(List<Content> contents, Response response) {
                super.success(contents, response);
                try {
                    if (TextUtils.isEmpty(mContentId) || !mContentId.equalsIgnoreCase(contents.get(0).idContent)) {
                        return;
                    }
                } catch (Exception e) {
                    L.error(e.getMessage(), e);
                    return;
                }
                contentList = contents;
                getWebData(contents, updatePosition);
                if (mViewSwitcher.getNextView() instanceof RelativeLayout) {
                    mViewSwitcher.showNext();
                }
            }
        });
    }

    public boolean loadwebifFileExists(String contentNames) {
        String[] htmlFile;
        if (contentNames.contains(",")) {
            htmlFile = contentNames.split(",");
        } else {
            htmlFile = new String[]{contentNames};
        }
        if (subjectModelList.size() > 0 || chapterModelList.size() > 0 ||
                topicModelList.size() > 0 || contentModelList.size() > 0) {
            webFooter.setVisibility(View.VISIBLE);
        } else {
            webFooter.setVisibility(View.GONE);
        }

        // load specific content
        if (loadSpecificContent(htmlFile)) {
            return true;
        }
        // load first available content
        return loadFirstContent(htmlFile) || false;
    }

    File root;

    private File getgetFile(String fileName) {
        root = Environment.getExternalStorageDirectory();
        File file;
        String folderStructure = getSelectedCourseName() + File.separator +
                subjectModelList.get(spSubject.getSelectedItemPosition()).subjectName + File.separator +
                chapterModelList.get(spChapter.getSelectedItemPosition()).chapterName + File.separator +
                topicModelList.get(spTopic.getSelectedItemPosition()).topicName;
        if (fileName.endsWith(Constants.VIDEO_FILE)) {
            file = new File(root.getAbsolutePath() + File.separator + Constants.PARENT_FOLDER +
                    File.separator + folderStructure + File.separator + Constants.VIDEO_FOLDER +
                    File.separator + fileName);
        } else {
            file = new File(root.getAbsolutePath() + File.separator + Constants.PARENT_FOLDER +
                    File.separator + folderStructure + File.separator + Constants.HTML_FOLDER +
                    File.separator + fileName);
        }
        return file;
    }

    private boolean loadFirstContent(String[] htmlFile) {
        File f;
        String fileName;
        for (int i = 0; i < htmlFile.length; i++) {
            fileName = htmlFile[i];
            f = getgetFile(fileName);
            if (f.exists()) {
                if (htmlFile.length == contentModelList.size()) {
                    mContentIdPosition = i;
                }
                if (f.getName().endsWith(Constants.VIDEO_FILE)) {
                    String videoUrl = new FileUtils(this).getUrlFromFile(f);
                    if (videoUrl.length() > 0) {
                        loadWeb(ApiClientService.getBaseUrl() + videoUrl.replace("./", ""));
                    }
                } else {
                    loadWeb(Constants.HTML_PREFIX_URL + f.getAbsolutePath());
                }
                return true;
            }
        }
        return false;
    }

    private boolean loadSpecificContent(String[] htmlFile) {
        File f;
        String fileName;
        for (int i = 0; i < htmlFile.length; i++) {
            fileName = htmlFile[i];
            f = getgetFile(fileName);
            if (f.exists()) {
                if (!TextUtils.isEmpty(mContentName)) {
                    String[] data = htmlFile[i].split(".");
                    if (data.length > 0 && mContentName.equalsIgnoreCase(data[0])) {
                        if (htmlFile.length == contentModelList.size()) {
                            mContentIdPosition = i;
                        }
                        if (f.getName().endsWith(Constants.VIDEO_FILE)) {
                            String videoUrl = new FileUtils(this).getUrlFromFile(f);
                            if (videoUrl.length() > 0) {
                                loadWeb(ApiClientService.getBaseUrl() + videoUrl.replace("./", ""));
                            }
                        } else {
                            loadWeb(Constants.HTML_PREFIX_URL + f.getAbsolutePath());
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void showSubject() {

        if (mViewSwitcher.getNextView() instanceof RelativeLayout) {
            mViewSwitcher.showNext();
        }
        ContentIndex mContentIndex = contentIndexList.get(0);
        subjectModelList = new ArrayList<>(mContentIndex.subjectModelList);
        final SubjectAdapter subjectAdapter = new SubjectAdapter(subjectModelList, this);
        spSubject.setAdapter(subjectAdapter);

        int listSize = subjectModelList.size();
        if (!mSubjectId.isEmpty()) {
            for (int i = 0; i < listSize; i++) {
                if (subjectModelList.get(i).idSubject.equalsIgnoreCase(mSubjectId)) {
                    spSubject.setSelection(i);
                    mSubjectId = "";
                    break;
                }
            }
        }

        spSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showChapter(position);
                mChapterId = "";
                subjectAdapter.setSelectedPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showChapter(int subjectPosition) {

        chapterModelList = new ArrayList<>(subjectModelList.get(subjectPosition).chapters);
        Collections.sort(chapterModelList); // sort the chapters based on the chapterSortOrder
        final ChapterAdapter chapterAdapter = new ChapterAdapter(chapterModelList, this);
        spChapter.setAdapter(chapterAdapter);

        if (!mChapterId.isEmpty()) {
            int listSize = chapterModelList.size();
            for (int i = 0; i < listSize; i++) {
                if (chapterModelList.get(i).idChapter.equalsIgnoreCase(mChapterId)) {
                    spChapter.setSelection(i);
                    break;
                }
            }
        }

        spChapter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showTopic(position);
                mTopicId = "";
                chapterAdapter.setSelectedPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void showExercise() {
        if (AbstractBaseActivity.getSharedExamModels() != null && !AbstractBaseActivity.getSharedExamModels().isEmpty()) {
            ivExercise.setVisibility(View.VISIBLE);
        } else {
            ivExercise.setVisibility(View.GONE);
        }
    }

    private void showTopic(final int chapterPosition) {
        topicModelList = new ArrayList<>(chapterModelList.get(chapterPosition).topicMap);
        Collections.sort(topicModelList);
        if (topicModelList != null) {
            List<OfflineContent> offlineCList = dbManager.getOfflineContents(AbstractBaseActivity.getSelectedCourseId());
            if(offlineCList!=null){
                for(TopicModel t: topicModelList){
                    for(OfflineContent c: offlineCList){
                        if(c.topicId.equalsIgnoreCase(t.idTopic)){
                            t.isAvailableForOffline=true;
                        }
                    }
                }
            }

            final TopicAdapter topicAdapter = new TopicAdapter(topicModelList, this);
            spTopic.setAdapter(topicAdapter);

            if (!mTopicId.isEmpty()) {
                int listSize = topicModelList.size();
                for (int i = 0; i < listSize; i++) {
                    if (topicModelList.get(i).idTopic.equalsIgnoreCase(mTopicId)) {
                        spTopic.setSelection(i);
                        break;
                    }
                }
            }

            spTopic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    getExercise(position);
                    loadOfflineExercises();
                    getContentData(position);
                    topicAdapter.setSelectedPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void updateToolbarTitle() {
        try {
            String subjectName = ((SubjectModel) spSubject.getSelectedItem()).subjectName;
            String chapterName = ((ChapterModel) spChapter.getSelectedItem()).chapterName;
            setToolbarTitle(subjectName + "  -  " + chapterName);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void showVideoDialog() {
        try {
            VideoListDialog videoListDialog = new VideoListDialog();
            Bundle bundle = new Bundle();
            bundle.putString("title", topicModelList.get(spTopic.getSelectedItemPosition()).topicName);
            bundle.putSerializable("videolist", (Serializable) videoModelList);
            videoListDialog.setArguments(bundle);
            videoListDialog.show(getFragmentManager(), "videoListDialog");
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    @Override
    public void onStop() {
        postContentReadingEvent();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        postContentReadingEvent();
        super.onBackPressed();
    }
}