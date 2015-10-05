package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.ViewSwitcher;

import com.education.corsalite.R;
import com.education.corsalite.adapters.ChapterAdapter;
import com.education.corsalite.adapters.SubjectAdapter;
import com.education.corsalite.adapters.TopicAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.ChapterModel;
import com.education.corsalite.models.ContentModel;
import com.education.corsalite.models.SubjectModel;
import com.education.corsalite.models.TopicModel;
import com.education.corsalite.models.responsemodels.Content;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.utils.FileUtilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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
    @Bind(R.id.vs_container) ViewSwitcher mViewSwitcher;
    @Bind(R.id.footer_layout) RelativeLayout webFooter;
    @Bind(R.id.btn_next) Button btnNext;
    @Bind(R.id.btn_previous) Button btnPrevious;


    private List<ContentIndex> mContentIndexs;
    private HashSet<SubjectModel> mSubjectModels;

    List<ChapterModel> mChapterModelList = null;
    List<TopicModel> mTopicModelList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_web, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("clear_cookies")) {
                webviewContentReading.clearCache(true);
            }
            if (bundle.containsKey("contentData")) {
                mContentIndexs = (List<ContentIndex>) bundle.getSerializable("contentData");
            } else {
                getContentIndex("9", LoginUserCache.getInstance().loginResponse.studentId);
            }
        } else {
            getContentIndex("9", LoginUserCache.getInstance().loginResponse.studentId);
        }
        setListeners();
    }

    private void loadWeb(String htmlUrl) {
        // Initialize the WebView
        webviewContentReading.getSettings().setSupportZoom(true);
        webviewContentReading.getSettings().setBuiltInZoomControls(true);
        webviewContentReading.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webviewContentReading.setScrollbarFadingEnabled(true);
        webviewContentReading.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webviewContentReading.getSettings().setLoadsImagesAutomatically(true);
        webviewContentReading.getSettings().setJavaScriptEnabled(true);
        webviewContentReading.getSettings().setAppCachePath(getExternalCacheDir().getAbsolutePath());
        webviewContentReading.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        webviewContentReading.loadUrl(htmlUrl);

        // Load the URLs inside the WebView, not in the external web browser
        webviewContentReading.setWebViewClient(new MyWebViewClient());
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webviewContentReading.canGoBack()) {
            webviewContentReading.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void setListeners() {
        ivEditNotes.setOnClickListener(mClickListener);
        ivForum.setOnClickListener(mClickListener);
        btnNext.setOnClickListener(mClickListener);
        btnPrevious.setOnClickListener(mClickListener);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.iv_forum:
                    showToast("Forum button is clicked");
                    break;

                case R.id.iv_editnotes:
                    showToast("Add is clicked");
                    break;

                case R.id.btn_next:
                    showToast("Next is clicked");
                    break;

                case R.id.btn_previous:
                    showToast("Previous is clicked");
                    break;
            }
        }
    };

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

        if(mContentResponse.size() > 1) {
            webFooter.setVisibility(View.VISIBLE);
        } else {
            webFooter.setVisibility(View.GONE);
        }
        try {
            int count = 0;
            FileUtilities fileUtilities = new FileUtilities(this);
            for(Content content : mContentResponse) {
                String url = content.idContent.toString();
                String text = mContentResponse.get(0).contentHtml;
                String htmlUrl = fileUtilities.write(url + ".html", text);
                if (htmlUrl != null && count == 0) {
                    count = count + 1;
                    htmlUrl = "file:///" + htmlUrl;
                    loadWeb(htmlUrl);
                }
            }
        } catch (Exception e) {
            Log.e("corsalite", e.toString());
        }
        if(mViewSwitcher.getNextView() instanceof LinearLayout) {
            mViewSwitcher.showNext();
        }
    }

    private void getContentIndex(String courseId, String studentId) {
        // TODO : passing static data
        ApiManager.getInstance(this).getContentIndex(courseId, studentId,
                new ApiCallback<List<ContentIndex>>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            showToast(error.message);
                        }
                    }

                    @Override
                    public void success(List<ContentIndex> mContentIndexs, Response response) {
                        if (mContentIndexs != null) {
                            WebActivity.this.mContentIndexs = mContentIndexs;
                            setUpData();
                        }
                    }
                });
    }

    private void getContentData(String idSubject, String idChapter, String idTopic) {

        if (mViewSwitcher.indexOfChild(mViewSwitcher.getCurrentView()) == 1) {
            mViewSwitcher.showPrevious();
        }

        List<ChapterModel> chapterModelList = null;
        List<TopicModel> topicModelList = null;
        List<ContentModel> contentModels = null;
        for (SubjectModel subjectModel : mSubjectModels) {
            if (subjectModel.chapterMap.containsKey(idSubject)) {
                chapterModelList = new ArrayList<>(subjectModel.chapterMap.get(idSubject));
                break;
            }
        }

        for (ChapterModel chapterModel : chapterModelList) {
            if (chapterModel.topicMap.containsKey(idChapter)) {
                topicModelList = new ArrayList<>(chapterModel.topicMap.get(idChapter));
                break;
            }
        }

        for (TopicModel topicModel : topicModelList) {
            if (topicModel.contentMap.containsKey(idTopic)) {
                contentModels = new ArrayList<>(topicModel.contentMap.get(idTopic));
                break;
            }
        }

        String contentId = "";
        for(ContentModel contentModel : contentModels) {
            if(contentId.trim().length() > 0) {
                contentId = contentId + ",";
            }
            contentId = contentId + contentModel.idContent;
        }

        if(loadwebifFileExists(contentId)) {
            if(mViewSwitcher.getNextView() instanceof LinearLayout) {
                mViewSwitcher.showNext();
            }
            return;
        }

        ApiManager.getInstance(this).getContent(contentId, "", new ApiCallback<List<Content>>() {
            @Override
            public void failure(CorsaliteError error) {
                if (mViewSwitcher.getNextView() instanceof LinearLayout) {
                    mViewSwitcher.showNext();
                }
            }

            @Override
            public void success(List<Content> contents, Response response) {
                saveAndLoadWeb(contents);
                if (mViewSwitcher.getNextView() instanceof LinearLayout) {
                    mViewSwitcher.showNext();
                }
            }
        });
    }

    public boolean loadwebifFileExists(String contentId) {
        boolean isFileExist = false;
        String[] htmlFile;
        if(contentId.contains(",")) {
            htmlFile = contentId.split(",");
        } else {
            htmlFile = new String[]{contentId};
        }
        if(htmlFile.length > 1) {
            webFooter.setVisibility(View.VISIBLE);
        } else {
            webFooter.setVisibility(View.GONE);
        }
        File root = Environment.getExternalStorageDirectory();
        for(String file : htmlFile) {
            String fileName = file+".html";
            File f = new File(root.getAbsolutePath() + File.separator + "Corsalite" + File.separator + fileName);
            if(f.exists()) {
                loadWeb("file:///" + f.getAbsolutePath());
                return true;
            }
        }
        return isFileExist;
    }

    private void setUpData() {
        mSubjectModels = new HashSet<>();

        // add subjects to model
        for (ContentIndex contentIndex : mContentIndexs) {

            SubjectModel subjectModel = new SubjectModel();
            subjectModel.idSubject = contentIndex.idCourseSubject;
            subjectModel.subjectName = contentIndex.subjectName;
            subjectModel.subjectStatus = contentIndex.subjectStatus;
            subjectModel.scoreRed = contentIndex.scoreRed;
            subjectModel.scoreAmber = contentIndex.scoreAmber;
            subjectModel.scoreLevelPassing = contentIndex.scoreLevelPassing;
            subjectModel.chapterMap = new HashMap<>();
            subjectModel.chapterMap.put(subjectModel.idSubject, new TreeSet<ChapterModel>());
            mSubjectModels.add(subjectModel);
        }

        // add chapter to model
        for (ContentIndex contentIndex : mContentIndexs) {

            ChapterModel chapterModel = new ChapterModel();
            chapterModel.idChapter = contentIndex.idCourseSubjectChapter;
            chapterModel.chapterName = contentIndex.chapterName;
            chapterModel.chapterStatus = contentIndex.chapterStatus;
            chapterModel.chapterSortOrder = contentIndex.chapterSortOrder;
            chapterModel.topicMap = new HashMap<>();
            chapterModel.topicMap.put(chapterModel.idChapter, new TreeSet<TopicModel>());

            SortedSet<ChapterModel> chapterModels;
            for (SubjectModel subjectModel : mSubjectModels) {
                if (subjectModel.chapterMap.containsKey(contentIndex.idCourseSubject)) {
                    chapterModels = subjectModel.chapterMap.get(contentIndex.idCourseSubject);
                    chapterModels.add(chapterModel);
                    subjectModel.chapterMap.put(contentIndex.idCourseSubject, chapterModels);
                    break;
                }
            }
        }

        // add Topic to model
        for (ContentIndex contentIndex : mContentIndexs) {

            TopicModel topicModel = new TopicModel();
            topicModel.idTopic = contentIndex.idTopic;
            topicModel.topicName = contentIndex.topicName;
            topicModel.topicStatus = contentIndex.topicStatus;
            topicModel.topicSortOrder = contentIndex.topicSortOrder;
            topicModel.contentMap = new HashMap<>();
            topicModel.contentMap.put(topicModel.idTopic, new TreeSet<ContentModel>());

            SortedSet<ChapterModel> chapterModelSortedSet = null;
            SortedSet<TopicModel> topicModelSortedSet;
            for (SubjectModel subjectModel : mSubjectModels) {
                if (subjectModel.chapterMap.containsKey(contentIndex.idCourseSubject)) {

                    chapterModelSortedSet = subjectModel.chapterMap.get(contentIndex.idCourseSubject);
                    for (ChapterModel chapterModel : chapterModelSortedSet) {
                        if (chapterModel.topicMap.containsKey(contentIndex.idCourseSubjectChapter)) {

                            topicModelSortedSet = chapterModel.topicMap.get(contentIndex.idCourseSubjectChapter);
                            topicModelSortedSet.add(topicModel);
                            chapterModel.topicMap.put(contentIndex.idCourseSubjectChapter, topicModelSortedSet);
                            chapterModelSortedSet.add(chapterModel);
                            break;
                        }
                    }
                }
                //subjectModel.chapterMap.remove(contentIndex.idCourseSubject);
                if (chapterModelSortedSet != null) {
                    subjectModel.chapterMap.put(contentIndex.idCourseSubject, chapterModelSortedSet);
                }
            }
        }

        // add Content to model
        for (ContentIndex contentIndex : mContentIndexs) {

            ContentModel contentModel = new ContentModel();
            contentModel.idContent = contentIndex.idContent;
            contentModel.type = contentIndex.type;
            contentModel.contentName = contentIndex.contentName;
            contentModel.status = contentIndex.status;

            SortedSet<ChapterModel> chapterModelSortedSet = null;
            SortedSet<TopicModel> topicModelSortedSet =  null;
            SortedSet<ContentModel> contentModelSortedSet;

            for (SubjectModel subjectModel : mSubjectModels) {
                if (subjectModel.chapterMap.containsKey(contentIndex.idCourseSubject)) {

                    chapterModelSortedSet = subjectModel.chapterMap.get(contentIndex.idCourseSubject);
                    for (ChapterModel chapterModel : chapterModelSortedSet) {
                        if (chapterModel.topicMap.containsKey(contentIndex.idCourseSubjectChapter)) {

                            topicModelSortedSet = chapterModel.topicMap.get(contentIndex.idCourseSubjectChapter);
                            for (TopicModel topicModel : topicModelSortedSet) {
                                if(topicModel.contentMap.containsKey(contentIndex.idTopic)) {

                                    contentModelSortedSet = topicModel.contentMap.get(contentIndex.idTopic);
                                    contentModelSortedSet.add(contentModel);

                                    topicModel.contentMap.put(contentIndex.idTopic, contentModelSortedSet);
                                    topicModelSortedSet.add(topicModel);
                                }
                            }
                            break;
                        }

                        if (topicModelSortedSet != null) {
                            chapterModel.topicMap.put(contentIndex.idCourseSubjectChapter, topicModelSortedSet);
                        }
                    }
                }
                if (chapterModelSortedSet != null) {
                    subjectModel.chapterMap.put(contentIndex.idCourseSubject, chapterModelSortedSet);
                }
            }
        }

        showSubject();
    }

    private void showSubject() {

        if(mViewSwitcher.getNextView() instanceof LinearLayout) {
            mViewSwitcher.showNext();
        }
        List<SubjectModel> subjectModelList = new ArrayList<>(mSubjectModels);
        SubjectAdapter subjectAdapter = new SubjectAdapter(subjectModelList, this);

        spSubject.setAdapter(subjectAdapter);

        spSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<SubjectModel> subjectModelList = new ArrayList<>(mSubjectModels);
                showChapter(subjectModelList.get(position).idSubject);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showChapter(final String idSubject) {

        mChapterModelList = null;
        for (SubjectModel subjectModel : mSubjectModels) {
            if (subjectModel.chapterMap.containsKey(idSubject)) {
                mChapterModelList = new ArrayList<>(subjectModel.chapterMap.get(idSubject));
                break;
            }
        }
        ChapterAdapter chapterAdapter = new ChapterAdapter(mChapterModelList, this);
        spChapter.setAdapter(chapterAdapter);

        spChapter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showTopic(idSubject, mChapterModelList.get(position).idChapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showTopic(final String idSubject, final String idChapter) {

        List<ChapterModel> chapterModelList = null;
        for (SubjectModel subjectModel : mSubjectModels) {
            if (subjectModel.chapterMap.containsKey(idSubject)) {
                chapterModelList = new ArrayList<>(subjectModel.chapterMap.get(idSubject));
                break;
            }
        }

        for (ChapterModel chapterModel : chapterModelList) {
            if (chapterModel.topicMap.containsKey(idChapter)) {
                mTopicModelList = new ArrayList<>(chapterModel.topicMap.get(idChapter));
                break;
            }
        }
        TopicAdapter topicAdapter = new TopicAdapter(mTopicModelList, this);
        spTopic.setAdapter(topicAdapter);

        spTopic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getContentData(idSubject, idChapter, mTopicModelList.get(position).idTopic);
             }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}