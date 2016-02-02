package com.education.corsalite.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.education.corsalite.R;
import com.education.corsalite.adapters.ExamEngineGridAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.enums.QuestionType;
import com.education.corsalite.event.ExerciseAnsEvent;
import com.education.corsalite.fragments.FullQuestionDialog;
import com.education.corsalite.models.requestmodels.ExamTemplateChapter;
import com.education.corsalite.models.requestmodels.ExamTemplateConfig;
import com.education.corsalite.models.requestmodels.FlaggedQuestionModel;
import com.education.corsalite.models.requestmodels.PostCustomExamTemplate;
import com.education.corsalite.models.requestmodels.PostExerciseRequestModel;
import com.education.corsalite.models.requestmodels.PostQuestionPaperRequest;
import com.education.corsalite.models.responsemodels.AnswerChoiceModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.models.responsemodels.ExamModels;
import com.education.corsalite.models.responsemodels.PostExamTemplate;
import com.education.corsalite.models.responsemodels.PostExercise;
import com.education.corsalite.models.responsemodels.PostFlaggedQuestions;
import com.education.corsalite.models.responsemodels.PostQuestionPaper;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.TimeUtils;
import com.education.corsalite.views.GridViewInScrollView;
import com.google.gson.Gson;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Girish on 04/11/15.
 */
public class ExamEngineActivity extends AbstractBaseActivity {

    @Bind(R.id.vs_container) ViewSwitcher mViewSwitcher;
    @Bind(R.id.footer_layout) RelativeLayout webFooter;
    @Bind(R.id.answer_layout) LinearLayout answerLayout;
    @Bind(R.id.btn_next) Button btnNext;
    @Bind(R.id.btn_previous) Button btnPrevious;
    @Bind(R.id.webview_question) WebView webviewQuestion;
    @Bind(R.id.webview_paragraph) WebView webviewParagraph;
    @Bind(R.id.tv_comment) TextView tvComment;
    @Bind(R.id.tv_level) TextView tvLevel;
    @Bind(R.id.tv_nav_title) TextView tvNavTitle;
    @Bind(R.id.tv_pagetitle) TextView tvPageTitle;
    @Bind(R.id.tv_timer) TextView tv_timer;
    @Bind(R.id.btn_view_full_question) Button btnViewFullQuestion;
    @Bind(R.id.btn_verify) Button btnVerify;
    @Bind(R.id.tv_clearanswer) TextView tvClearAnswer;
    @Bind(R.id.btn_submit) Button btnSubmit;
    @Bind(R.id.txtAnswerCount) TextView txtAnswerCount;
    @Bind(R.id.txtAnswerExp) WebView txtAnswerExp;
    @Bind(R.id.tv_serial_no) TextView tvSerialNo;
    @Bind(R.id.layout_timer) LinearLayout timerLayout;
    @Bind(R.id.explanation_layout) LinearLayout explanationLayout;
    @Bind(R.id.layout_choice) LinearLayout layoutChoice;
    @Bind(R.id.imv_refresh) ImageView imvRefresh;
    @Bind(R.id.btn_slider_test) Button slider;
    @Bind(R.id.ll_test_navigator) LinearLayout testNavLayout;
    @Bind(R.id.shadow_view) View shadowView;
    @Bind(R.id.gv_test) GridViewInScrollView gvTest;
    @Bind(R.id.test_nav_footer) LinearLayout testNavFooter;
    @Bind(R.id.navigator_layout) RelativeLayout navigatorLayout;
    @Bind(R.id.header_progress) ProgressBar headerProgress;
    @Bind(R.id.tv_empty_layout) TextView tvEmptyLayout;

    //Flagged Answer View ID
    @Bind(R.id.flagged_explanation) LinearLayout flaggedLayout;
    @Bind(R.id.flagged_answer) WebView webViewFlaggedAnswer;
    @Bind(R.id.tv_question_status) TextView tvQuestionStatus;
    @Bind(R.id.tv_recommended_time) TextView tvRecommendedTime;
    @Bind(R.id.tv_max_marks) TextView tvMaxMarks;
    @Bind(R.id.tv_time_taken) TextView tvTimeTaken;
    @Bind(R.id.tv_positive_max_marks) TextView tvPositiveMaxMarks;
    @Bind(R.id.tv_average_time) TextView tvAverageTime;
    @Bind(R.id.tv_negative_max_marks) TextView tvNegativeMaxMarks;
    @Bind(R.id.tv_peer_average) TextView tvPeerAverage;
    @Bind(R.id.tv_percentile) TextView tvPercentile;
    @Bind(R.id.imv_flag) ImageView imvFlag;

    public int selectedPosition = 0;
    public int previousQuestionPosition = -1;
    private String webQuestion = "";
    private int selectedAnswerPosition = -1;
    private String enteredAnswer = ""; // for alphanumeric
    private String title = "";
    private String topic = "";
    private ExamEngineGridAdapter gridAdapter;
    private List<ExamModel> localExamModelList;
    private String subjectId = null;
    private String chapterId = null;
    private String topicIds = null;
    private String questionsCount = null;
    private boolean isFlagged = false;
    private long examDurationInSeconds = 0;
    private long examDurationTakenInSeconds = 0;

    public static Intent getMyIntent(Context context, @Nullable Bundle extras) {
        Intent intent = new Intent(context, ExamEngineActivity.class);
        if (extras != null) {
            intent.putExtras(extras);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_exercise, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        toggleSlider();
        initWebView();
        initWebView1();
        initFlaggedWebView();
        initSuggestionWebView();
        setListener();
        getIntentData();
        sendAnalytics(getString(R.string.screen_exercise));
    }

    public List<ExamModel> getLocalExamModelList() {
        return this.localExamModelList;
    }

    private void getIntentData() {
        if (getIntent().hasExtra(Constants.TEST_TITLE)) {
            title = getIntent().getExtras().getString(Constants.TEST_TITLE);
        }
        tvNavTitle.setText(title);
        setToolbarForExercise(title);

        if (getIntent().hasExtra(Constants.SELECTED_TOPIC)) {
            topic = getIntent().getExtras().getString(Constants.SELECTED_TOPIC);
            tvPageTitle.setText(topic);
        }
        if (getIntent().hasExtra(Constants.QUESTIONS_COUNT)) {
            questionsCount = getIntent().getExtras().getString(Constants.QUESTIONS_COUNT, "");
        }
        // set selected position
        if (getIntent().hasExtra(Constants.SELECTED_POSITION)) {
            selectedPosition = getIntent().getExtras().getInt(Constants.SELECTED_POSITION);
        }
        if(getIntent().hasExtra(Constants.SELECTED_SUBJECTID)) {
            subjectId = getIntent().getExtras().getString(Constants.SELECTED_SUBJECTID);
        }
        if(getIntent().hasExtra(Constants.SELECTED_CHAPTERID)) {
            chapterId = getIntent().getExtras().getString(Constants.SELECTED_CHAPTERID);
        }
        if(getIntent().hasExtra(Constants.SELECTED_TOPICID)) {
            topicIds = getIntent().getExtras().getString(Constants.SELECTED_TOPICID);
        }

        if(title.equalsIgnoreCase("Flagged Questions")) {
            imvFlag.setVisibility(View.VISIBLE);
            tvPageTitle.setText(title);
            getFlaggedQuestion();
            navigatorLayout.setVisibility(View.GONE);
            tvClearAnswer.setVisibility(View.GONE);
            btnVerify.setVisibility(View.GONE);
            imvRefresh.setVisibility(View.GONE);
            timerLayout.setVisibility(View.GONE);
        } else if(title.equalsIgnoreCase("Exercise Test")) {
            imvFlag.setVisibility(View.INVISIBLE);
            localExamModelList = ContentReadingActivity.examModelList;
            webFooter.setVisibility(localExamModelList.isEmpty() ? View.GONE : View.VISIBLE);
            btnVerify.setVisibility(View.VISIBLE);
            imvRefresh.setVisibility(View.GONE);
            timerLayout.setVisibility(View.GONE);
            testNavFooter.setVisibility(View.GONE);
            renderQuestionLayout();
        } else {
            imvFlag.setVisibility(View.VISIBLE);
            if (getIntent().hasExtra(Constants.SELECTED_SUBJECT)) {
                topic = getIntent().getExtras().getString(Constants.SELECTED_SUBJECT);
                tvPageTitle.setText(topic);
            }
            // get Exam Id
            getStandardExamByCourse();

            imvRefresh.setVisibility(View.VISIBLE);
            timerLayout.setVisibility(View.VISIBLE);
            testNavFooter.setVisibility(View.VISIBLE);
            btnVerify.setVisibility(View.GONE);
        }
    }

    public void renderQuestionLayout() {
        if (selectedPosition >= 0) {
            inflateUI(selectedPosition);
        }
        gridAdapter = new ExamEngineGridAdapter(this, localExamModelList);
        gvTest.setAdapter(gridAdapter);
        gvTest.setExpanded(true);
    }

    private void setListener() {
        btnNext.setOnClickListener(mClickListener);
        btnPrevious.setOnClickListener(mClickListener);
        btnVerify.setOnClickListener(mClickListener);
        tvClearAnswer.setOnClickListener(mClickListener);
        shadowView.setOnClickListener(mClickListener);
        slider.setOnClickListener(mClickListener);
        testNavLayout.setOnClickListener(mClickListener);
        btnViewFullQuestion.setOnClickListener(mClickListener);
        btnSubmit.setOnClickListener(mClickListener);
        imvFlag.setOnClickListener(mClickListener);
    }

    private void initSuggestionWebView() {
        txtAnswerExp.getSettings().setSupportZoom(true);
        txtAnswerExp.getSettings().setBuiltInZoomControls(false);
        txtAnswerExp.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        txtAnswerExp.setScrollbarFadingEnabled(true);
        txtAnswerExp.getSettings().setLoadsImagesAutomatically(true);
        txtAnswerExp.getSettings().setJavaScriptEnabled(true);
        txtAnswerExp.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        webviewQuestion.setWebChromeClient(new WebChromeClient());
        webviewParagraph.setWebChromeClient(new WebChromeClient());
        // Load the URLs inside the WebView, not in the external web browser
        txtAnswerExp.setWebViewClient(new MyWebViewClient());
    }

    private void initWebView1() {
        webviewParagraph.getSettings().setSupportZoom(true);
        webviewParagraph.getSettings().setBuiltInZoomControls(false);
        webviewParagraph.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webviewParagraph.setScrollbarFadingEnabled(true);
        webviewParagraph.getSettings().setLoadsImagesAutomatically(true);
        webviewParagraph.getSettings().setJavaScriptEnabled(true);
        webviewParagraph.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if (getExternalCacheDir() != null) {
            webviewParagraph.getSettings().setAppCachePath(getExternalCacheDir().getAbsolutePath());
        } else {
            webviewParagraph.getSettings().setAppCachePath(getCacheDir().getAbsolutePath());
        }
        webviewParagraph.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);

        webviewParagraph.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                L.info("JS return value " + message);
                showToast("Adding '" + message + "' to the notes");
                result.confirm();
                return true;
            }
        });
        webviewParagraph.addJavascriptInterface(new Object() {
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
        webviewParagraph.setWebViewClient(new MyWebViewClient());
    }

    private void initWebView() {
        webviewQuestion.getSettings().setSupportZoom(true);
        webviewQuestion.getSettings().setBuiltInZoomControls(false);
        webviewQuestion.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webviewQuestion.setScrollbarFadingEnabled(true);
        webviewQuestion.getSettings().setLoadsImagesAutomatically(true);
        webviewQuestion.getSettings().setJavaScriptEnabled(true);
        webviewQuestion.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if (getExternalCacheDir() != null) {
            webviewQuestion.getSettings().setAppCachePath(getExternalCacheDir().getAbsolutePath());
        } else {
            webviewQuestion.getSettings().setAppCachePath(getCacheDir().getAbsolutePath());
        }
        webviewQuestion.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);

        webviewQuestion.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                L.info("JS return value " + message);
                showToast("Adding '" + message + "' to the notes");
                result.confirm();
                return true;
            }
        });
        webviewQuestion.addJavascriptInterface(new Object() {
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
        webviewQuestion.setWebViewClient(new MyWebViewClient());
    }

    private void initFlaggedWebView() {
        webViewFlaggedAnswer.getSettings().setSupportZoom(true);
        webViewFlaggedAnswer.getSettings().setBuiltInZoomControls(false);
        webViewFlaggedAnswer.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webViewFlaggedAnswer.setScrollbarFadingEnabled(true);
        webViewFlaggedAnswer.getSettings().setLoadsImagesAutomatically(true);
        webViewFlaggedAnswer.getSettings().setJavaScriptEnabled(true);
        webViewFlaggedAnswer.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if (getExternalCacheDir() != null) {
            webViewFlaggedAnswer.getSettings().setAppCachePath(getExternalCacheDir().getAbsolutePath());
        } else {
            webViewFlaggedAnswer.getSettings().setAppCachePath(getCacheDir().getAbsolutePath());
        }
        webViewFlaggedAnswer.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);

        webViewFlaggedAnswer.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                L.info("JS return value " + message);
                showToast("Adding '" + message + "' to the notes");
                result.confirm();
                return true;
            }
        });
        webViewFlaggedAnswer.addJavascriptInterface(new Object() {
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
        webViewFlaggedAnswer.setWebViewClient(new MyWebViewClient());
    }

    public void inflateUI(int position) {
        if (previousQuestionPosition >= 0 && !title.equalsIgnoreCase("Exercise Test")) {
            setAnswerState();
        }
        selectedPosition = position;
        resetExplanation();
        if (localExamModelList != null && localExamModelList.size() > 0) {
            loadQuestion(position);
        }
    }

    private void navigateButtonEnabled() {
        if (selectedPosition == 0) {
            btnPrevious.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            return;
        }
        if (selectedPosition == localExamModelList.size() - 1) {
            btnPrevious.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
            return;
        }
        btnPrevious.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.VISIBLE);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_next:
                    previousQuestionPosition = selectedPosition;
                    inflateUI(selectedPosition + 1);
                    break;
                case R.id.btn_previous:
                    previousQuestionPosition = selectedPosition;
                    inflateUI(selectedPosition - 1);
                    break;
                case R.id.btn_verify:
                    postAnswer(localExamModelList.get(selectedPosition).answerChoice.get(selectedAnswerPosition),
                            localExamModelList.get(selectedPosition).idQuestion);
                    break;
                case R.id.tv_clearanswer:
                    clearAnswers();
                    break;
                case R.id.btn_slider_test:
                case R.id.shadow_view:
                    toggleSlider();
                    break;
                case R.id.ll_test_navigator:
                    break;
                case R.id.btn_view_full_question:
                    showFullQuestionDialog();
                    break;
                case R.id.btn_submit:
                    showSubmitTestAlert();
                    break;
                case R.id.imv_flag:
                    postFlaggedQuestion();
                    break;
            }
        }
    };

    private void showSubmitTestAlert() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirm");
        alert.setMessage("Do you want to submit the exam?");
        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                submitTest();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private void submitTest() {
        if(localExamModelList != null && !localExamModelList.isEmpty()) {
            int score = 0;
            int success = 0;
            int failure = 0;
            for(ExamModel model : localExamModelList) {
                if(!TextUtils.isEmpty(model.selectedAnswers)) {
                    int selectedOption = -1;
                    try {
                        selectedOption = Integer.parseInt(model.selectedAnswers);
                        if(model.answerChoice.get(selectedOption).isCorrectAnswer.equals("Y")) {
                            success++;
                        } else {
                            failure++;
                        }
                    } catch (NumberFormatException e) {
                        L.error(e.getMessage(), e);
                        failure++;
                    }
                } else {
                    failure++;
                }
            }
            postExerciseAnsEvent();
            navigateToExamResultActivity(localExamModelList.size(), success, failure);
        }
    }

    private void postExerciseAnsEvent(){
        ExerciseAnsEvent event = new ExerciseAnsEvent();
        event.id = chapterId;
        event.pageView = "";
        getEventbus().post(event);
    }

    private void navigateToExamResultActivity(int totalQuestions, int correct, int wrong) {
        Intent intent = new Intent(this, ExamResultActivity.class);
        intent.putExtra("exam", "Chapter");
        intent.putExtra("type", "Custom");
        intent.putExtra("recommended_time", TimeUtils.getSecondsInTimeFormat(examDurationInSeconds));
        intent.putExtra("time_taken", TimeUtils.getSecondsInTimeFormat(examDurationTakenInSeconds));
        intent.putExtra("total_questions", totalQuestions);
        intent.putExtra("correct", correct);
        intent.putExtra("wrong", wrong);
        startActivity(intent);
        finish();
    }

    private void loadQuestion(int position) {
        isFlagged = false;
        tvSerialNo.setText("Q" + (position + 1) + ")");
        if(!TextUtils.isEmpty(localExamModelList.get(position).displayName) && !title.equalsIgnoreCase("Flagged Questions")) {
            tvLevel.setText(localExamModelList.get(position).displayName.split("\\s+")[0].toUpperCase(Locale.ENGLISH));
        }
        if (TextUtils.isEmpty(localExamModelList.get(position).paragraphHtml)) {
            webviewParagraph.setVisibility(View.GONE);
        } else {
            webviewParagraph.setVisibility(View.VISIBLE);
            webQuestion = localExamModelList.get(position).paragraphHtml;
            webviewParagraph.loadData(webQuestion, "text/html; charset=UTF-8", null);
        }
        webviewQuestion.setVisibility(View.GONE);
        if (localExamModelList.get(position).questionHtml != null) {
            webQuestion = localExamModelList.get(position).questionHtml;
            webviewQuestion.loadData(webQuestion, "text/html; charset=UTF-8", null);
            webviewQuestion.setVisibility(View.VISIBLE);
        }
        if (localExamModelList.get(position).comment != null) {
            tvComment.setText(localExamModelList.get(position).comment);
            tvComment.setVisibility(View.VISIBLE);
        } else {
            tvComment.setVisibility(View.GONE);
        }
        navigateButtonEnabled();

        switch (QuestionType.getQuestionType(localExamModelList.get(position).idQuestionType)) {
            case SINGLE_SELECT_CHOICE:
                loadSingleSelectChoiceAnswers(position);
                break;
            case MULTI_SELECT_CHOICE:
                loadMultiSelectChoiceAnswers(position);
                break;
            case ALPHANUMERIC:
                loadEditTextAnswer(QuestionType.ALPHANUMERIC, position);
                break;
            case NUMERIC:
                loadEditTextAnswer(QuestionType.NUMERIC, position);
                break;
            case FILL_IN_THE_BLANK:
                loadEditTextAnswer(QuestionType.FILL_IN_THE_BLANK, position);
                break;
            case N_BLANK_SINGLE_SELECT:
                loadNBlankAnswer(QuestionType.N_BLANK_SINGLE_SELECT, position);
                break;
            case N_BLANK_MULTI_SELECT:
                loadNBlankAnswer(QuestionType.N_BLANK_MULTI_SELECT, position);
                break;
            default:
                if (localExamModelList.size() - 1 == 0) {
                    return;
                }
                if (localExamModelList.size() - 1 > selectedPosition) {
                    localExamModelList.remove(selectedPosition);
                    inflateUI(selectedPosition);
                } else {
                    localExamModelList.remove(selectedPosition);
                    inflateUI(selectedPosition - 1);
                }
                break;
        }
        if (mViewSwitcher.indexOfChild(mViewSwitcher.getCurrentView()) == 0) {
            mViewSwitcher.showNext();
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

    private void loadNBlankAnswer(QuestionType type, int position) {
        resetExplanation();
        answerLayout.removeAllViews();
        List<AnswerChoiceModel> answerChoiceModels = localExamModelList.get(position).answerChoice;
        if(answerChoiceModels == null || answerChoiceModels.isEmpty()) {
            return;
        }
        AnswerChoiceModel answerModel = answerChoiceModels.get(0);
        String [] lists = answerModel.answerChoiceTextHtml.split(":");
        final ListView[] listViews = new ListView[lists.length];
        answerLayout.setOrientation(LinearLayout.HORIZONTAL);
        for(int i=0; i<lists.length; i++) {
            String list = lists[i];
            String [] data = list.split("~");
            String header = data[0];
            String[] items = data[1].split(",");
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout container = (LinearLayout) inflater.inflate(R.layout.exam_engine_n_blank_answer, null);
            TextView headerTxt = (TextView) container.findViewById(R.id.header_txt);
            headerTxt.setText(header);
            ListView optionsListView = (ListView) container.findViewById(R.id.options_listview);
            optionsListView.setTag(header);
            listViews[i] = optionsListView;
            optionsListView.setChoiceMode(type == QuestionType.N_BLANK_SINGLE_SELECT
                                            ? AbsListView.CHOICE_MODE_SINGLE
                                            : AbsListView.CHOICE_MODE_MULTIPLE);
            optionsListView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, items));
            optionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    localExamModelList.get(selectedPosition).selectedAnswers = getAnswerForNBlankType(listViews);
                }
            });

            answerLayout.addView(container);
        }
        loadAnswersInToNBlankType(listViews, localExamModelList.get(selectedPosition).selectedAnswers);
        setExplanationLayout();
    }

    private void clearNBlankAnswers(ListView[] listviews) {
        for(ListView listview : listviews) {
            for(int i=0; i<listview.getAdapter().getCount(); i++) {
                listview.setItemChecked(i, false);
            }
        }
        localExamModelList.get(selectedPosition).selectedAnswers = "";
    }

    private void loadAnswersInToNBlankType(ListView[] listviews, String answer) {
        if(listviews == null || TextUtils.isEmpty(answer)) {
            return;
        }
        String [] lists = answer.split(":");
        for(int i=0; i<lists.length; i++) {
            String [] data = lists[i].split("~");
            String header = data[0];
            List<String> items = Arrays.asList(data[1].split(","));
            for(ListView listView : listviews) {
                if(listView.getTag().equals(header)) {
                    for(int j=0; j<listView.getAdapter().getCount(); j++) {
                        for(String item : items) {
                            if (item.equals(listView.getAdapter().getItem(j))) {
                                listView.setItemChecked(j, true);
                            }
                        }
                    }
                }
            }
        }
    }

    private String getAnswerForNBlankType(ListView[] listviews) {
        String answer = "";
        for(int i=0; i<listviews.length; i++) {
            String blankAnswer = "";
            SparseBooleanArray checkedItems = listviews[i].getCheckedItemPositions();
            for(int j=0; j< checkedItems.size(); j++) {
                int key = checkedItems.keyAt(j);
                if(checkedItems.get(key)) {
                    blankAnswer += blankAnswer.isEmpty() ? "" : ",";
                    blankAnswer += listviews[i].getItemAtPosition(key);
                }
            }
            if(!blankAnswer.isEmpty()) {
                answer += answer.isEmpty() ? "" : ":";
                answer += listviews[i].getTag() + "~" + blankAnswer;
            }
        }
        return answer;
    }

    private void loadEditTextAnswer(QuestionType type, int position) {
        resetExplanation();
        answerLayout.removeAllViews();
        List<AnswerChoiceModel> answerChoiceModels = localExamModelList.get(position).answerChoice;
        String previousAnswer = "";
        if (!title.equalsIgnoreCase("Exercise Test") && !TextUtils.isEmpty(localExamModelList.get(selectedPosition).selectedAnswers)) {
            previousAnswer = localExamModelList.get(selectedPosition).selectedAnswers;
        }
        if(!answerChoiceModels.isEmpty()) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout container = (LinearLayout) inflater.inflate(R.layout.exam_engine_alphanumeric, null);
            EditText answerTxt = (EditText) container.findViewById(R.id.answer_txt);
            answerTxt.setText(previousAnswer);
            switch (type) {
                case ALPHANUMERIC:
                case FILL_IN_THE_BLANK:
                    answerTxt.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case NUMERIC:
                    answerTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
            }
            answerTxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    localExamModelList.get(selectedPosition).selectedAnswers = s.toString();
                }
            });
            answerLayout.addView(container);
        }
        setExplanationLayout();
    }

    private void loadMultiSelectChoiceAnswers(int position) {
        resetExplanation();
        answerLayout.removeAllViews();

        List<AnswerChoiceModel> answerChoiceModels = localExamModelList.get(position).answerChoice;
        final int size = answerChoiceModels.size();
        final CheckBox[] checkBoxes = new CheckBox[size];
        final TextView[] tvSerial = new TextView[size];
        final LinearLayout[] rowLayout = new LinearLayout[size];

        String[] preselectedAnswers = null;
        if (!title.equalsIgnoreCase("Exercise Test") &&
                !TextUtils.isEmpty(localExamModelList.get(selectedPosition).selectedAnswers)) {
            preselectedAnswers = localExamModelList.get(selectedPosition).selectedAnswers.split(",");
        }

        for (int i = 0; i < size; i++) {

            final AnswerChoiceModel answerChoiceModel = answerChoiceModels.get(i);

            rowLayout[i] = new LinearLayout(this);
            rowLayout[i].setOrientation(LinearLayout.HORIZONTAL);
            rowLayout[i].setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            tvSerial[i] = new TextView(this);
            tvSerial[i].setText((i + 1) + ")");
            tvSerial[i].setTextColor(Color.BLACK);
            tvSerial[i].setGravity(Gravity.TOP);
            tvSerial[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            p.setMargins(0, 0, 10, 0);
            tvSerial[i].setLayoutParams(p);


            checkBoxes[i] = new CheckBox(this);
            checkBoxes[i].setId(Integer.valueOf(answerChoiceModel.idAnswerKey));
            checkBoxes[i].setTag(answerChoiceModel);
            checkBoxes[i].setBackgroundResource(R.drawable.selector_checkbox);

            if(title.equalsIgnoreCase("Flagged Questions")) {
                checkBoxes[i].setEnabled(false);
                checkBoxes[i].setClickable(false);
            }

            rowLayout[i] = new LinearLayout(this);
            rowLayout[i].setOrientation(LinearLayout.HORIZONTAL);
            rowLayout[i].setGravity(Gravity.CENTER_VERTICAL);
            checkBoxes[i].setLayoutParams(p);

            rowLayout[i].addView(checkBoxes[i]);

            WebView optionWebView = new WebView(getApplicationContext());
            optionWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            optionWebView.setScrollbarFadingEnabled(true);
            optionWebView.getSettings().setLoadsImagesAutomatically(true);
            optionWebView.getSettings().setJavaScriptEnabled(true);
            optionWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            optionWebView.setWebChromeClient(new WebChromeClient());
            optionWebView.setWebViewClient(new MyWebViewClient());
            if(answerChoiceModel.answerChoiceTextHtml.startsWith("./") && answerChoiceModel.answerChoiceTextHtml.endsWith(".html")) {
                answerChoiceModel.answerChoiceTextHtml = answerChoiceModel.answerChoiceTextHtml.replace("./", ApiClientService.getBaseUrl());
                optionWebView.loadUrl(answerChoiceModel.answerChoiceTextHtml);
            } else {
                optionWebView.loadData(answerChoiceModel.answerChoiceTextHtml, "text/html; charset=UTF-8", null);
            }
            p = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
            optionWebView.setLayoutParams(p);
            rowLayout[i].addView(optionWebView);
            answerLayout.addView(rowLayout[i]);

            try {
                checkBoxes[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isCheckedAtLeastOnce = false;
                        CheckBox chkBox = (CheckBox) v;
                        if (chkBox.isChecked()) {
                            isCheckedAtLeastOnce = true;
                            for (int x = 0; x < size; x++) {
                                if (checkBoxes[x].getId() == Integer.valueOf(answerChoiceModel.idAnswerKey)) {
                                    selectedAnswerPosition = x + 1;
                                    if (TextUtils.isEmpty(localExamModelList.get(selectedPosition).selectedAnswers)) {
                                        localExamModelList.get(selectedPosition).selectedAnswers = String.valueOf(x);
                                    } else {
                                        localExamModelList.get(selectedPosition).selectedAnswers = localExamModelList.get(selectedPosition).selectedAnswers + "," + x;
                                    }
                                }
                            }
                        } else {
                            for (int x = 0; x < size; x++) {
                                if (checkBoxes[x].isChecked()) {
                                    selectedAnswerPosition = x + 1;
                                    if (TextUtils.isEmpty(localExamModelList.get(selectedPosition).selectedAnswers)) {
                                        localExamModelList.get(selectedPosition).selectedAnswers = String.valueOf(x);
                                    } else {
                                        localExamModelList.get(selectedPosition).selectedAnswers = localExamModelList.get(selectedPosition).selectedAnswers + "," + x;
                                    }
                                    isCheckedAtLeastOnce = true;
                                    break;
                                }
                            }
                        }
                        if (!isCheckedAtLeastOnce) {
                            selectedAnswerPosition = -1;
                            localExamModelList.get(selectedPosition).selectedAnswers = null;
                        }
                        btnVerify.setEnabled(isCheckedAtLeastOnce);
                    }
                });
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        //set checkBox check
        if (preselectedAnswers != null && preselectedAnswers.length > 0) {
            for (String selectedChoice : preselectedAnswers) {
                checkBoxes[Integer.valueOf(selectedChoice)].setChecked(true);
                selectedAnswerPosition = Integer.valueOf(selectedChoice) + 1;
            }
        }
        setExplanationLayout();
    }


    private void loadSingleSelectChoiceAnswers(int position) {
        resetExplanation();
        answerLayout.removeAllViews();
        List<AnswerChoiceModel> answerChoiceModels = localExamModelList.get(position).answerChoice;
        final int size = answerChoiceModels.size();
        final RadioButton[] optionRadioButtons = new RadioButton[size];
        String preselectedAnswers = null;
        if (!title.equalsIgnoreCase("Exercise Test") && !TextUtils.isEmpty(localExamModelList.get(selectedPosition).selectedAnswers)) {
            preselectedAnswers = localExamModelList.get(selectedPosition).selectedAnswers;
        }
        for (int i = 0; i < size; i++) {
            final AnswerChoiceModel answerChoiceModel = answerChoiceModels.get(i);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout container = (LinearLayout) inflater.inflate(R.layout.exam_engine_radio_btn, null);
            TextView optionNumberTxt = (TextView) container.findViewById(R.id.option_number_txt);
            optionNumberTxt.setText((i+1)+"");
            final RadioButton optionRBtn =  (RadioButton) container.findViewById(R.id.option_radio_button);
            optionRBtn.setId(Integer.valueOf(answerChoiceModel.idAnswerKey));
            optionRBtn.setTag(answerChoiceModel);
            if(title.equalsIgnoreCase("Flagged Questions")) {
                optionRBtn.setEnabled(false);
                optionRBtn.setClickable(false);
            }
            if (!TextUtils.isEmpty(preselectedAnswers) && i == Integer.valueOf(preselectedAnswers)) {
                optionRBtn.setChecked(true);
                selectedAnswerPosition = Integer.valueOf(preselectedAnswers) + 1;
            }
            optionRadioButtons[i] = optionRBtn;
            WebView webview = (WebView) container.findViewById(R.id.webview);
            webview.setScrollbarFadingEnabled(true);
            webview.getSettings().setLoadsImagesAutomatically(true);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setWebChromeClient(new WebChromeClient());
            webview.setWebViewClient(new MyWebViewClient());
            webview.loadData(answerChoiceModel.answerChoiceTextHtml, "text/html; charset=UTF-8", null);
            answerLayout.addView(container);

            try {
                optionRBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int j = 0; j < size; j++) {
                            optionRadioButtons[j].setChecked(false);
                            if (optionRadioButtons[j].getId() == Integer.valueOf(answerChoiceModel.idAnswerKey)) {
                                selectedAnswerPosition = j + 1;
                                localExamModelList.get(selectedPosition).selectedAnswers = String.valueOf(j);
                            }
                        }
                        ((RadioButton) v).setChecked(true);
                        btnVerify.setEnabled(true);
                    }
                });
            } catch (Exception e) {
                L.error(e.getMessage(), e);
            }
        }
        setExplanationLayout();
    }

    private void clearAnswers() {
        List<AnswerChoiceModel> answerChoiceModels = localExamModelList.get(selectedPosition).answerChoice;
        int size = answerChoiceModels.size();
        boolean isCompound;
        if (localExamModelList.get(selectedPosition).idQuestionType.equalsIgnoreCase("1") ||
                localExamModelList.get(selectedPosition).idQuestionType.equalsIgnoreCase("2")) {
            isCompound = true;
        } else {
            isCompound = false;
        }
        if (!isCompound) {
            return;
        }
        for (int i = 0; i < size; i++) {
            ((CompoundButton) findViewById(Integer.valueOf(answerChoiceModels.get(i).idAnswerKey))).setChecked(false);
        }
        localExamModelList.get(selectedPosition).selectedAnswers = null;
        resetExplanation();
    }

    private void setExplanationLayout() {
        String webText = "";
        String correctAnswer = "";
        String correctAnswerText = "";
        List<AnswerChoiceModel> answerChoiceModels = localExamModelList.get(selectedPosition).answerChoice;
        int counter = 0;
        for (AnswerChoiceModel answerChoiceModel : answerChoiceModels) {
            counter = counter + 1;
            if (answerChoiceModel.isCorrectAnswer.equalsIgnoreCase("Y")) {
                correctAnswer = String.valueOf(counter);
                correctAnswerText = answerChoiceModel.answerChoiceTextHtml;
                webText = answerChoiceModel.answerChoiceExplanationHtml;
                break;
            }
        }
        txtAnswerCount.setText(correctAnswer);
        txtAnswerExp.loadData(webText, "text/html; charset=UTF-8", null);

        if (gridAdapter != null) {
            gridAdapter.notifyDataSetChanged();
        }
        setFlaggedQuestionLayout(correctAnswerText);
    }

    private void setFlaggedQuestionLayout(String correctAnswers) {
        if(title.equalsIgnoreCase("Flagged Questions")) {
            flaggedLayout.setVisibility(View.VISIBLE);
        } else {
            flaggedLayout.setVisibility(View.GONE);
        }
        webViewFlaggedAnswer.loadData(correctAnswers, "text/html; charset=UTF-8", null);
        webViewFlaggedAnswer.setBackgroundColor(0);
    }

    private void resetExplanation() {
        selectedAnswerPosition = -1;
        btnVerify.setEnabled(false);
        explanationLayout.setVisibility(View.GONE);
        flaggedLayout.setVisibility(View.GONE);
        layoutChoice.setVisibility(View.GONE);
    }

    private void postAnswer(AnswerChoiceModel answerChoiceModel, String questionId) {
        PostExerciseRequestModel postExerciseRequestModel = new PostExerciseRequestModel();
        postExerciseRequestModel.updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        postExerciseRequestModel.idStudent = LoginUserCache.getInstance().loginResponse.studentId;
        postExerciseRequestModel.idQuestion = questionId;
        postExerciseRequestModel.studentAnswerChoice = selectedAnswerPosition + "";
        postExerciseRequestModel.score = answerChoiceModel.isCorrectAnswer.equalsIgnoreCase("Y") ? "1" : "0";

        ApiManager.getInstance(this).postExerciseAnswer(new Gson().toJson(postExerciseRequestModel),
                new ApiCallback<PostExercise>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        btnVerify.setEnabled(true);
                        String message = "Unknown error occured.Please try again.";
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            message = error.message;
                        }
                        explanationLayout.setVisibility(View.VISIBLE);
                        layoutChoice.setVisibility(View.VISIBLE);
                        showToast(message);
                    }

                    @Override
                    public void success(PostExercise postExercise, Response response) {
                        super.success(postExercise, response);
                        if (postExercise.isSuccessful()) {
                            btnVerify.setEnabled(false);
                            explanationLayout.setVisibility(View.VISIBLE);
                            layoutChoice.setVisibility(View.VISIBLE);
                        } else {
                            String message = "Unknown error occured. Please try again.";
                            if (postExercise != null && !TextUtils.isEmpty(postExercise.message)) {
                                message = postExercise.message;
                            }
                            showToast(message);
                        }

                    }
                });
    }

    private void toggleSlider() {
        if (testNavLayout.getVisibility() == View.GONE) {
            testNavLayout.setVisibility(View.VISIBLE);
            shadowView.setVisibility(View.VISIBLE);
            slider.setBackground(getResources().getDrawable(R.drawable.btn_right_slider_white));
        } else {
            testNavLayout.setVisibility(View.GONE);
            shadowView.setVisibility(View.GONE);
            slider.setBackground(getResources().getDrawable(R.drawable.btn_right_slider));
        }
    }



    private void setAnswerState() {
        if (selectedAnswerPosition != -1) {
            localExamModelList.get(previousQuestionPosition).answerColorSelection = Constants.AnswerState.ANSWERED;
        } else {
            localExamModelList.get(previousQuestionPosition).answerColorSelection = Constants.AnswerState.SKIPPED;
        }
    }

    public class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            tv_timer.setText("TIME OVER");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            examDurationTakenInSeconds = examDurationInSeconds - millisUntilFinished/1000;
            String hms = TimeUtils.getSecondsInTimeFormat(millisUntilFinished/1000);
            tv_timer.setText(hms);
        }
    }

    private void getFlaggedQuestion() {
        ApiManager.getInstance(this).getFlaggedQuestions(LoginUserCache.getInstance().loginResponse.studentId,
                subjectId,
                chapterId, "", new ApiCallback<List<ExamModel>>(this) {
                    @Override
                    public void success(List<ExamModel> examModels, Response response) {
                        super.success(examModels, response);
                        localExamModelList = examModels;
                        if (localExamModelList != null && localExamModelList.size() > 0) {
                            if (localExamModelList.size() > 1) {
                                webFooter.setVisibility(View.VISIBLE);
                            } else {
                                webFooter.setVisibility(View.GONE);
                            }
                            tvLevel.setText("TOTAL NUMBER OF QUESTIONS : " + examModels.size());
                            renderQuestionLayout();
                        } else {
                            headerProgress.setVisibility(View.GONE);
                            tvEmptyLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void getStandardExamByCourse() {
        String entityId;
        String selectedCourseId;
        entityId = LoginUserCache.getInstance().loginResponse.entitiyId;

        if(getIntent().hasExtra(Constants.SELECTED_COURSE)) {
            selectedCourseId = getIntent().getExtras().getString(Constants.SELECTED_COURSE);
        } else {
            selectedCourseId = selectedCourse.courseId.toString();
        }
        ApiManager.getInstance(this).getStandardExamsByCourse(selectedCourseId, entityId,
                new ApiCallback<List<ExamModels>>(this) {
                    @Override
                    public void success(List<ExamModels> examModelses, Response response) {
                        super.success(examModelses, response);
                        if (examModelses != null && !examModelses.isEmpty()) {
                            postCustomExamTemplate(examModelses);
                        } else {
                            showToast("No data found.");
                            headerProgress.setVisibility(View.GONE);
                            tvEmptyLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void postCustomExamTemplate(List<ExamModels> examModelsList){

        PostCustomExamTemplate postCustomExamTemplate = new PostCustomExamTemplate();
        postCustomExamTemplate.examId = examModelsList.get(0).examId;
        postCustomExamTemplate.examName = examModelsList.get(0).examName;
        postCustomExamTemplate.examTemplateConfig = new ArrayList<>();

        ExamTemplateConfig examTemplateConfig = new ExamTemplateConfig();
        examTemplateConfig.subjectId = subjectId;
        examTemplateConfig.examTemplateChapter = new ArrayList<>();

        ExamTemplateChapter examTemplateChapter = new ExamTemplateChapter();
        examTemplateChapter.chapterID = chapterId;
        examTemplateChapter.topicIDs = topicIds;
        examTemplateChapter.questionCount = questionsCount;
        examTemplateConfig.examTemplateChapter.add(examTemplateChapter);
        postCustomExamTemplate.examTemplateConfig.add(examTemplateConfig);

        ApiManager.getInstance(this).postCustomExamTemplate(new Gson().toJson(postCustomExamTemplate),
                new ApiCallback<PostExamTemplate>(this) {
                    @Override
                    public void success(PostExamTemplate postExamTemplate, Response response) {
                        super.success(postExamTemplate, response);
                        if (postExamTemplate != null && !TextUtils.isEmpty(postExamTemplate.idExamTemplate)) {
                            postQuestionPaper(LoginUserCache.getInstance().loginResponse.entitiyId, postExamTemplate.idExamTemplate,
                                    LoginUserCache.getInstance().loginResponse.studentId);
                        }
                    }
                });
    }

    private void postFlaggedQuestion() {
        FlaggedQuestionModel flaggedQuestionModel = new FlaggedQuestionModel();
        if(isFlagged) {
            flaggedQuestionModel.flaggedYN = "N";
        } else {
            flaggedQuestionModel.flaggedYN = "Y";
        }
        flaggedQuestionModel.idTestAnswerPaper = "";
        flaggedQuestionModel.idTestQuestion = localExamModelList.get(selectedPosition).idTestQuestion+"";
        flaggedQuestionModel.updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        ApiManager.getInstance(this).postFlaggedQuestions(new Gson().toJson(flaggedQuestionModel),
                new ApiCallback<PostFlaggedQuestions>(this) {
                    @Override
                    public void success(PostFlaggedQuestions postFlaggedQuestions, Response response) {
                        super.success(postFlaggedQuestions, response);
                        isFlagged = !isFlagged;
                        if(isFlagged) {
                            imvFlag.setImageResource(R.drawable.ico_offline_info_white);
                        } else {
                            imvFlag.setImageResource(R.drawable.ico_offline_info);
                        }

                    }
                });
    }

    private void postQuestionPaper(String entityId, String examTemplateId, String studentId) {
        PostQuestionPaperRequest postQuestionPaper = new PostQuestionPaperRequest();
        postQuestionPaper.idCollegeBatch = "";
        postQuestionPaper.idEntity = entityId;
        postQuestionPaper.idExamTemplate = examTemplateId;
        postQuestionPaper.idSubject = subjectId;
        postQuestionPaper.idStudent = studentId;

        ApiManager.getInstance(this).postQuestionPaper(new Gson().toJson(postQuestionPaper),
                new ApiCallback<PostQuestionPaper>(this) {
                    @Override
                    public void success(PostQuestionPaper postQuestionPaper, Response response) {
                        super.success(postQuestionPaper, response);
                        if (postQuestionPaper != null && !TextUtils.isEmpty(postQuestionPaper.idTestQuestionPaper)) {
                            getTestQuestionPaper(postQuestionPaper.idTestQuestionPaper, null);
                        } else {
                            headerProgress.setVisibility(View.GONE);
                            tvEmptyLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void getTestQuestionPaper(String testQuestionPaperId, String testAnswerPaperId) {
        ApiManager.getInstance(this).getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId,
                new ApiCallback<List<ExamModel>>(this) {
                    @Override
                    public void success(List<ExamModel> examModels, Response response) {
                        super.success(examModels, response);
                        localExamModelList = examModels;
                        if(localExamModelList != null ) {
                            if (localExamModelList.size() > 1) {
                                webFooter.setVisibility(View.VISIBLE);
                            } else {
                                webFooter.setVisibility(View.GONE);
                            }
                            renderQuestionLayout();
                            examDurationInSeconds = getExamDurationInSeconds(examModels);
                            //dummy timer.. need to fetch time and interval from service
                            tv_timer.setText(TimeUtils.getSecondsInTimeFormat(examDurationInSeconds));
                            final CounterClass timer = new CounterClass(examDurationInSeconds * 1000, 1000);
                            timer.start();
                        } else {
                            headerProgress.setVisibility(View.GONE);
                            tvEmptyLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private long getExamDurationInSeconds(List<ExamModel> models) {
        long examDuration = 0;
        for(ExamModel model : models) {
            long duration = 0;
            try {
                duration = Integer.valueOf(model.recommendedTime);
            } catch (Exception e) {
                L.error(e.getMessage(), e);
                duration = 0;
            }
            examDuration += duration;
        }
        return examDuration;
    }

    private void showFullQuestionDialog() {
        FullQuestionDialog fullQuestionDialog = new FullQuestionDialog();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SELECTED_TOPIC, topic);
        fullQuestionDialog.setArguments(bundle);
        fullQuestionDialog.show(getFragmentManager(), "fullQuestionDialog");
    }
}