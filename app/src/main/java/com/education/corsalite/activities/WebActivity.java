package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import com.education.corsalite.adapters.SubjectAdapter;
import com.education.corsalite.adapters.TopicAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.DbManager;
import com.education.corsalite.fragments.EditorDialogFragment;
import com.education.corsalite.fragments.VideoListDialog;
import com.education.corsalite.models.ChapterModel;
import com.education.corsalite.models.ContentModel;
import com.education.corsalite.models.SubjectModel;
import com.education.corsalite.models.TopicModel;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.responsemodels.Content;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.ExerciseModel;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.FileUtilities;
import com.education.corsalite.utils.FileUtils;
import com.education.corsalite.utils.L;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Girish on 28/09/15.
 */
public class WebActivity extends AbstractBaseActivity {

    @Bind(R.id.iv_editnotes) ImageView ivEditNotes;
    @Bind(R.id.iv_forum) ImageView ivForum;
    @Bind(R.id.webView_content_reading) WebView webviewContentReading;
    @Bind(R.id.sp_subject) Spinner spSubject;
    @Bind(R.id.sp_chapter) Spinner spChapter;
    @Bind(R.id.sp_topic) Spinner spTopic;
    @Bind(R.id.tv_exercise) TextView tvExercise;
    @Bind(R.id.layout_exercise) RelativeLayout layoutExercise;
    @Bind(R.id.vs_container) ViewSwitcher mViewSwitcher;
    @Bind(R.id.footer_layout) RelativeLayout webFooter;
    @Bind(R.id.btn_next) Button btnNext;
    @Bind(R.id.btn_previous) Button btnPrevious;
    @Bind(R.id.tv_video) TextView tvVideo;

    private List<ContentIndex> contentIndexList;
    private List<SubjectModel> subjectModelList;
    private List<ChapterModel> chapterModelList;
    private List<TopicModel> topicModelList;
    private List<ContentModel> contentModelList;
    private List<ContentModel> videoModelList;
    public static List<ExerciseModel> exerciseModelList;

    private String mSubjectId = "";
    private String mChapterId = "";
    private String mTopicId = "";
    private String mContentId = "";
    private int mContentIdPosition;

    private String selectedText = "";
    private String studentId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        studentId = LoginUserCache.getInstance().loginResponse.studentId;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_web, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        setToolbarForContentReading();
        initWebView();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("clear_cookies")) {
                webviewContentReading.clearCache(true);
            }
            if(bundle.containsKey("subjectId") && bundle.getString("subjectId") != null) {
                mSubjectId = bundle.getString("subjectId");
            }
            if(bundle.containsKey("chapterId") && bundle.getString("chapterId") != null) {
                mChapterId = bundle.getString("chapterId");
            }
            if(bundle.containsKey("topicId") && bundle.getString("topicId") != null) {
                mTopicId = bundle.getString("topicId");
            }
            if(bundle.containsKey("contentId") && bundle.getString("contentId") != null) {
                mContentId = bundle.getString("contentId");
            }
        }
        setListeners();
    }

    @Override
    public void onEvent(Course course) {
        super.onEvent(course);
        getContentIndex(course.courseId.toString(), studentId);
    }

    private void initWebView() {
        webviewContentReading.getSettings().setSupportZoom(true);
        webviewContentReading.getSettings().setBuiltInZoomControls(false);
        webviewContentReading.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webviewContentReading.setScrollbarFadingEnabled(true);
        webviewContentReading.getSettings().setLoadsImagesAutomatically(true);
        webviewContentReading.getSettings().setJavaScriptEnabled(true);

        if(getExternalCacheDir() != null) {
            webviewContentReading.getSettings().setAppCachePath(getExternalCacheDir().getAbsolutePath());
        } else {
            webviewContentReading.getSettings().setAppCachePath(getCacheDir().getAbsolutePath());
        }
        webviewContentReading.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);

        webviewContentReading.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                selectedText = message;
                addToNote(selectedText);
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
        webviewContentReading.setWebViewClient(new MyWebViewClient());

    }

    private void addToNote(String htmlText) {
        EditorDialogFragment fragment = new EditorDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", "Notes");
        bundle.putString("operation", "Add");
        bundle.putString("student_id", LoginUserCache.getInstance().getLongResponse().studentId);
        bundle.putString("topic_id", mTopicId);
        bundle.putString("content_id", mContentId);
        bundle.putString("content", htmlText);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "NotesEditorDialog");
    }

    private void loadWeb(String htmlUrl) {
        // Initialize the WebView
        mContentId = "";
        if(htmlUrl.endsWith("html")) {
            webviewContentReading.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } else {
            webviewContentReading.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        webviewContentReading.loadUrl(htmlUrl);
        navigateButtonEnabled();


    }

    public void navigateButtonEnabled() {
        if(spSubject.getSelectedItemPosition() == 0 && spChapter.getSelectedItemPosition() == 0 &&
                spTopic.getSelectedItemPosition() == 0 && mContentIdPosition == 0) {
            btnPrevious.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            return ;
        }

        if(spSubject.getSelectedItemPosition() == subjectModelList.size() -1 &&
                spChapter.getSelectedItemPosition() == chapterModelList.size() -1 &&
                spTopic.getSelectedItemPosition() == topicModelList.size() -1 &&
                mContentIdPosition == contentIndexList.size() -1 ) {
            btnPrevious.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
            return ;
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
                showToast("Read Offline");
                return true;

            case R.id.action_download_pdf:
                showToast("Download PDF");
                return true;

            case R.id.action_view_notes:
                showToast("View Notes");
                return true;

            case R.id.action_rate_it:
                showToast("Rate It");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Removing as previous and next is there for each content if
    // there are more than 1 content for each topic
    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webviewContentReading.canGoBack()) {
            webviewContentReading.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/


    private void setListeners() {
        ivEditNotes.setOnClickListener(mClickListener);
        ivForum.setOnClickListener(mClickListener);
        btnNext.setOnClickListener(mClickListener);
        btnPrevious.setOnClickListener(mClickListener);
        tvVideo.setOnClickListener(mClickListener);
        tvExercise.setOnClickListener(mClickListener);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.iv_forum:
                    showToast("Forum button is clicked");
                    break;

                case R.id.iv_editnotes:
                    webviewContentReading.loadUrl("javascript:alert(copy())");
                    break;

                case R.id.btn_next:
                    mContentIdPosition = mContentIdPosition + 1;
                    loadNext();
                    break;

                case R.id.btn_previous:
                    mContentIdPosition = mContentIdPosition - 1;
                    loadPrevious();
                    break;

                case R.id.tv_video:
                    showVideoDialog();
                    break;

                case R.id.tv_exercise:
                    Intent intent = new Intent(WebActivity.this, ExerciseActivity.class);
                    intent.putExtra(Constants.SELECTED_TOPIC, topicModelList.get(spTopic.getSelectedItemPosition()).topicName);
                    intent.putExtra(Constants.TEST_TITLE, "Exercise Test");
                    intent.putExtra(Constants.SELECTED_POSITION, 0);
                    startActivity(intent);
                    break;
            }
        }
    };

    private void loadNext() {
        if(mContentIdPosition >= contentModelList.size()) {
            int nextTopicPosition = spTopic.getSelectedItemPosition() + 1;
            if(nextTopicPosition >= topicModelList.size()) {
                int nextChapterPosition = spChapter.getSelectedItemPosition() + 1;
                if(nextChapterPosition >= chapterModelList.size()) {
                    int nextSubjectPosition = spSubject.getSelectedItemPosition() + 1;
                    if(nextSubjectPosition >= subjectModelList.size()) {
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
        } else {
            loadwebifFileExists(contentModelList.get(mContentIdPosition).idContent + "." +
                    contentModelList.get(mContentIdPosition).type);
        }
    }

    private void loadPrevious() {
        if(mContentIdPosition < 0) {
            int previousTopicPosition = spTopic.getSelectedItemPosition() - 1;
            if(previousTopicPosition < 0) {
                int previousChapterPosition = spChapter.getSelectedItemPosition() - 1;
                if(previousChapterPosition < 0) {
                    int previousSubjectPosition = spSubject.getSelectedItemPosition() - 1;
                    if(previousSubjectPosition < 0) {
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
        } else {
            loadwebifFileExists(contentModelList.get(mContentIdPosition).idContent + "." +
                    contentModelList.get(mContentIdPosition).type);
        }
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("staging.corsalite.com")) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    private void saveAndLoadWeb(List<Content> mContentResponse) {

        try {
            int count = 0;
            FileUtilities fileUtilities = new FileUtilities(this);
            int listSize = mContentResponse.size();
            String folderStructure =  selectedCourse.name + File.separator +
                    subjectModelList.get(spSubject.getSelectedItemPosition()).subjectName + File.separator +
                    chapterModelList.get(spChapter.getSelectedItemPosition()).chapterName + File.separator +
                    topicModelList.get(spTopic.getSelectedItemPosition()).topicName;
            for(int i = 0; i < listSize; i++) {
                String contentId = mContentResponse.get(i).idContent;
                String htmlUrl;
                String contentType = mContentResponse.get(i).type + "";
                String text = contentType.equalsIgnoreCase(Constants.VIDEO_FILE) ?
                        mContentResponse.get(i).url :
                        "<script type='text/javascript'>" +
                                "function copy() {" +
                                "    var t = (document.all) ? document.selection.createRange().text : document.getSelection();" +
                                "    return t;" +
                                "}" +
                                "</script>" + mContentResponse.get(i).contentHtml;
                L.info("Content : "+text);
                if(!contentType.isEmpty()) {
                    htmlUrl = fileUtilities.write(contentId + "." + contentType.trim(), text, folderStructure);
                } else {
                    htmlUrl = fileUtilities.write(contentId + "." + Constants.HTML_FILE, text, folderStructure);
                }
                if(mContentId.isEmpty()) {
                    if (!htmlUrl.isEmpty() && count == 0) {
                        count = count + 1;
                        mContentIdPosition = i;
                        if(mContentResponse.get(i).type.equalsIgnoreCase(Constants.VIDEO_FILE)) {
                            htmlUrl = Constants.VIDEO_PREFIX_URL + mContentResponse.get(i).url.replace("./", "");
                        } else {
                            htmlUrl = Constants.HTML_PREFIX_URL + htmlUrl;
                        }
                        loadWeb(htmlUrl);
                    }
                } else {
                    if(!htmlUrl.isEmpty() && contentId.equalsIgnoreCase(mContentId)) {
                        mContentIdPosition = i;
                        if(mContentResponse.get(i).type.equalsIgnoreCase(Constants.VIDEO_FILE)) {
                            htmlUrl = Constants.VIDEO_PREFIX_URL + mContentResponse.get(i).url.replace("./", "");
                        } else {
                            htmlUrl = Constants.HTML_PREFIX_URL + htmlUrl;
                        }
                        loadWeb(htmlUrl);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("corsalite", e.toString());
        }
        if(mViewSwitcher.getNextView() instanceof RelativeLayout) {
            mViewSwitcher.showNext();
        }
    }

    private void getContentIndex(String courseId, String studentId) {
        if (mViewSwitcher.indexOfChild(mViewSwitcher.getCurrentView()) == 1) {
            mViewSwitcher.showPrevious();
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
        layoutExercise.setVisibility(View.INVISIBLE);
        ApiManager.getInstance(this).getExercise(topicModelList.get(topicPosition).idTopic, selectedCourse.courseId.toString(),
                studentId, "", new ApiCallback<List<ExerciseModel>>(this) {
                    @Override
                    public void success(List<ExerciseModel> exerciseModels, Response response) {
                        super.success(exerciseModels, response);
                        exerciseModelList = exerciseModels;
                        showExercise();
                    }
                });
    }

    private void getContentData(int topicPosition) {

        if (mViewSwitcher.indexOfChild(mViewSwitcher.getCurrentView()) == 1) {
            mViewSwitcher.showPrevious();
        }

        contentModelList = new ArrayList<>();
        videoModelList = new ArrayList<>();
        for(ContentModel contentModel : topicModelList.get(topicPosition).contentMap) {
            if(contentModel.type.endsWith(Constants.VIDEO_FILE)) {
                videoModelList.add(contentModel);
            } else {
                contentModelList.add(contentModel);
            }
        }

        if(videoModelList.size() > 0) {
            tvVideo.setVisibility(View.VISIBLE);
        } else {
            tvVideo.setVisibility(View.GONE);
        }

        String contentId = "";
        String contentIds = "" ;
        for(ContentModel contentModel : contentModelList) {
            if(contentId.trim().length() > 0) {
                contentId = contentId + ",";
                contentIds = contentIds + ",";
            }
            contentId = contentId + contentModel.idContent + "." +contentModel.type;
            contentIds = contentIds + contentModel.idContent;
        }

        if(loadwebifFileExists(contentId)) {
            if(mViewSwitcher.getNextView() instanceof RelativeLayout) {
                mViewSwitcher.showNext();
            }
            return;
        }
        getContent(contentIds);
    }

    private void getContent(final String contentId) {
        ApiManager.getInstance(this).getContent(contentId, "", new ApiCallback<List<Content>>(this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                if (mViewSwitcher.getNextView() instanceof RelativeLayout) {
                    mViewSwitcher.showNext();
                }
            }

            @Override
            public void success(List<Content> contents, Response response) {
                super.success(contents, response);
                saveAndLoadWeb(contents);

                String courseId = String.valueOf(selectedCourse.courseId);
                String courseName = selectedCourse.name;
                String subjectId = subjectModelList.get(spSubject.getSelectedItemPosition()).idSubject;
                String subjectName = subjectModelList.get(spSubject.getSelectedItemPosition()).subjectName;
                String chapterId = chapterModelList.get(spChapter.getSelectedItemPosition()).idChapter;
                String chapterName = chapterModelList.get(spChapter.getSelectedItemPosition()).chapterName;
                String topicId = topicModelList.get(spTopic.getSelectedItemPosition()).idTopic;
                String topicName = topicModelList.get(spTopic.getSelectedItemPosition()).topicName;
                String fileName;

                List<OfflineContent> offlineContents = new ArrayList<>(contents.size());
                OfflineContent offlineContent;
                for(Content content : contents) {
                    if(TextUtils.isEmpty(content.type)) {
                        fileName = content.idContent + ".html";
                    } else {
                        fileName = content.idContent + "." + content.type;
                    }
                    offlineContent = new OfflineContent(courseId, courseName,
                            subjectId, subjectName, chapterId, chapterName, topicId, topicName,
                            content.idContent, content.name, fileName);
                    offlineContents.add(offlineContent);
                }
                DbManager.getInstance(WebActivity.this).saveOfflineContent(offlineContents);
                if (mViewSwitcher.getNextView() instanceof RelativeLayout) {
                    mViewSwitcher.showNext();
                }
            }
        });
    }

    public boolean loadwebifFileExists(String contentId) {
        String[] htmlFile;
        if (contentId.contains(",")) {
            htmlFile = contentId.split(",");
        } else {
            htmlFile = new String[]{contentId};
        }
        if (subjectModelList.size() > 0 || chapterModelList.size() > 0 ||
                topicModelList.size() > 0 || contentModelList.size() > 0)  {
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
        String folderStructure =  selectedCourse.name + File.separator +
                subjectModelList.get(spSubject.getSelectedItemPosition()).subjectName + File.separator +
                chapterModelList.get(spChapter.getSelectedItemPosition()).chapterName + File.separator +
                topicModelList.get(spTopic.getSelectedItemPosition()).topicName;
        if(fileName.endsWith(Constants.VIDEO_FILE)) {
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
        for(int i = 0; i < htmlFile.length; i++) {
            fileName = htmlFile[i];
            f = getgetFile(fileName);
            if(f.exists()) {
                if(htmlFile.length == contentModelList.size()) {
                    mContentIdPosition = i;
                }
                if(f.getName().endsWith(Constants.VIDEO_FILE)) {
                    String videoUrl = FileUtils.getUrlFromFile(f);
                    if(videoUrl.length() > 0) {
                        loadWeb(Constants.VIDEO_PREFIX_URL + videoUrl.replace("./", ""));
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
                if (!mContentId.isEmpty() && mContentId.equalsIgnoreCase(htmlFile[i].split(".")[0])) {
                    if(htmlFile.length == contentModelList.size()) {
                        mContentIdPosition = i;
                    }
                    if(f.getName().endsWith(Constants.VIDEO_FILE)) {
                        String videoUrl = FileUtils.getUrlFromFile(f);
                        if(videoUrl.length() > 0) {
                            loadWeb(Constants.VIDEO_PREFIX_URL + videoUrl.replace("./", ""));
                        }
                    } else {
                        loadWeb(Constants.HTML_PREFIX_URL + f.getAbsolutePath());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void showSubject() {

        if(mViewSwitcher.getNextView() instanceof RelativeLayout) {
            mViewSwitcher.showNext();
        }
        ContentIndex mContentIndex = contentIndexList.get(0);
        subjectModelList = new ArrayList<>(mContentIndex.subjectModelList);
        final SubjectAdapter subjectAdapter = new SubjectAdapter(subjectModelList, this);
        spSubject.setAdapter(subjectAdapter);

        int listSize = subjectModelList.size();
        if(!mSubjectId.isEmpty()) {
            for(int i = 0; i < listSize; i++) {
                if(subjectModelList.get(i).idSubject.equalsIgnoreCase(mSubjectId)) {
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
        final ChapterAdapter chapterAdapter = new ChapterAdapter(chapterModelList, this);
        spChapter.setAdapter(chapterAdapter);

        if(!mChapterId.isEmpty()) {
            int listSize = chapterModelList.size();
            for(int i = 0; i < listSize; i++) {
                if(chapterModelList.get(i).idChapter.equalsIgnoreCase(mChapterId)) {
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
        if(exerciseModelList != null && exerciseModelList.size() > 0) {
            layoutExercise.setVisibility(View.VISIBLE);
        } else {
            layoutExercise.setVisibility(View.INVISIBLE);
        }
    }

    private void showTopic(final int chapterPosition) {
        topicModelList = new ArrayList<>(chapterModelList.get(chapterPosition).topicMap);
        if(topicModelList != null) {
            final TopicAdapter topicAdapter = new TopicAdapter(topicModelList, this);
            spTopic.setAdapter(topicAdapter);

            if(!mTopicId.isEmpty()) {
                int listSize = topicModelList.size();
                for(int i = 0; i < listSize; i++) {
                    if(topicModelList.get(i).idTopic.equalsIgnoreCase(mTopicId)) {
                        spTopic.setSelection(i);
                        break;
                    }
                }
            }

            spTopic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    getExercise(position);
                    getContentData(position);
                    topicAdapter.setSelectedPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void showVideoDialog() {
        VideoListDialog videoListDialog = new VideoListDialog();
        Bundle bundle = new Bundle();
        bundle.putString("title", topicModelList.get(spTopic.getSelectedItemPosition()).topicName);
        bundle.putSerializable("videolist", (Serializable) videoModelList);
        videoListDialog.setArguments(bundle);
        videoListDialog.show(getFragmentManager(), "videoListDialog");
    }

}