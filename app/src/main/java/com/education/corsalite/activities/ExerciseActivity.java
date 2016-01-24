package com.education.corsalite.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.fragments.FullQuestionDialog;
import com.education.corsalite.models.requestmodels.ExamTemplateChapter;
import com.education.corsalite.models.requestmodels.ExamTemplateConfig;
import com.education.corsalite.models.requestmodels.FlaggedQuestionModel;
import com.education.corsalite.models.requestmodels.PostCustomExamTemplate;
import com.education.corsalite.models.requestmodels.PostExerciseRequestModel;
import com.education.corsalite.models.requestmodels.PostQuestionPaperRequest;
import com.education.corsalite.models.responsemodels.AnswerChoiceModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ExamModels;
import com.education.corsalite.models.responsemodels.ExerciseModel;
import com.education.corsalite.models.responsemodels.PostExamTemplate;
import com.education.corsalite.models.responsemodels.PostExercise;
import com.education.corsalite.models.responsemodels.PostFlaggedQuestions;
import com.education.corsalite.models.responsemodels.PostQuestionPaper;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;
import com.education.corsalite.views.GridViewInScrollView;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class ExerciseActivity extends AbstractBaseActivity {

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

    private String webQuestion = "";
    private int selectedPosition = 0;
    private int selectedAnswerPosition = -1;
    private String title = "";
    private String topic = "";
    private GridAdapter gridAdapter;
    private List<ExerciseModel> localExerciseModelList;
    private int previousQuestionPosition = -1;
    private String subjectId = null;
    private String chapterId = null;
    private String topicIds = null;
    private String questionsCount = null;
    private boolean isFlagged = false;

    public static Intent getMyIntent(Context context, @Nullable Bundle extras) {
        Intent intent = new Intent(context, ExerciseActivity.class);
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

    public List<ExerciseModel> getLocalExerciseModelList() {
        return this.localExerciseModelList;
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
            localExerciseModelList = WebActivity.exerciseModelList;
            webFooter.setVisibility(localExerciseModelList.isEmpty() ? View.GONE : View.VISIBLE);
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
        gridAdapter = new GridAdapter();
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

    private void inflateUI(int position) {
        if (previousQuestionPosition >= 0 && !title.equalsIgnoreCase("Exercise Test")) {
            setAnswerState();
        }
        selectedPosition = position;
        resetExplanation();
        if (localExerciseModelList != null && localExerciseModelList.size() > 0) {
            loadQuestion(position);
        }
    }

    private void navigateButtonEnabled() {
        if (selectedPosition == 0) {
            btnPrevious.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            return;
        }
        if (selectedPosition == localExerciseModelList.size() - 1) {
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
                    postAnswer(localExerciseModelList.get(selectedPosition).answerChoice.get(selectedAnswerPosition),
                            localExerciseModelList.get(selectedPosition).idQuestion);
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
        if(localExerciseModelList != null && !localExerciseModelList.isEmpty()) {
            int score = 0;
            int success = 0;
            int failure = 0;
            for(ExerciseModel model : localExerciseModelList) {
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
            navigateToExamResultActivity(localExerciseModelList.size(), success, failure);
        }
    }

    private void navigateToExamResultActivity(int totalQuestions, int correct, int wrong) {
        Intent intent = new Intent(this, ExamResultActivity.class);
        intent.putExtra("exam", "Chapter");
        intent.putExtra("type", "Custom");
        intent.putExtra("time_taken", "00:00:30");
        intent.putExtra("total_questions", totalQuestions);
        intent.putExtra("correct", correct);
        intent.putExtra("wrong", wrong);
        startActivity(intent);
        finish();
    }

    private void loadQuestion(int position) {

        isFlagged = false;
        tvSerialNo.setText("Q" + (position + 1) + ")");
        if(!TextUtils.isEmpty(localExerciseModelList.get(position).displayName) && !title.equalsIgnoreCase("Flagged Questions")) {
            tvLevel.setText(localExerciseModelList.get(position).displayName.split("\\s+")[0].toUpperCase(Locale.ENGLISH));
        }

        if (TextUtils.isEmpty(localExerciseModelList.get(position).paragraphHtml)) {
            webviewParagraph.setVisibility(View.GONE);
        } else {
            webviewParagraph.setVisibility(View.VISIBLE);
            webQuestion = localExerciseModelList.get(position).paragraphHtml;
            webviewParagraph.loadData(webQuestion, "text/html; charset=UTF-8", null);
        }

        webviewQuestion.setVisibility(View.GONE);
        if (localExerciseModelList.get(position).questionHtml != null) {
            webQuestion = localExerciseModelList.get(position).questionHtml;
            webviewQuestion.loadData(webQuestion, "text/html; charset=UTF-8", null);
            webviewQuestion.setVisibility(View.VISIBLE);
        }

        if (localExerciseModelList.get(position).comment != null) {
            tvComment.setText(localExerciseModelList.get(position).comment);
            tvComment.setVisibility(View.VISIBLE);
        } else {
            tvComment.setVisibility(View.GONE);
        }

        navigateButtonEnabled();
        if (localExerciseModelList.get(position).idQuestionType.equalsIgnoreCase("1")) {
            loadAnswers(position);
        } else if (localExerciseModelList.get(position).idQuestionType.equalsIgnoreCase("2")) {
            loadChexkbox(position);
        } else {
            if (localExerciseModelList.size() - 1 == 0) {
                return;
            }
            if (localExerciseModelList.size() - 1 > selectedPosition) {
                localExerciseModelList.remove(selectedPosition);
                inflateUI(selectedPosition);
            } else {
                localExerciseModelList.remove(selectedPosition);
                inflateUI(selectedPosition - 1);
            }
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

    private void loadChexkbox(int position) {
        resetExplanation();
        answerLayout.removeAllViews();

        List<AnswerChoiceModel> answerChoiceModels = localExerciseModelList.get(position).answerChoice;
        final int size = answerChoiceModels.size();
        final CheckBox[] checkBoxes = new CheckBox[size];
        final TextView[] tvSerial = new TextView[size];
        final LinearLayout[] rowLayout = new LinearLayout[size];

        String[] preselectedAnswers = null;
        if (!title.equalsIgnoreCase("Exercise Test") &&
                !TextUtils.isEmpty(localExerciseModelList.get(selectedPosition).selectedAnswers)) {
            preselectedAnswers = localExerciseModelList.get(selectedPosition).selectedAnswers.split(",");
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

            optionWebView.loadData(answerChoiceModel.answerChoiceTextHtml, "text/html; charset=UTF-8", null);
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
                                    if (TextUtils.isEmpty(localExerciseModelList.get(selectedPosition).selectedAnswers)) {
                                        localExerciseModelList.get(selectedPosition).selectedAnswers = String.valueOf(x);
                                    } else {
                                        localExerciseModelList.get(selectedPosition).selectedAnswers = localExerciseModelList.get(selectedPosition).selectedAnswers + "," + x;
                                    }
                                }
                            }
                        } else {
                            for (int x = 0; x < size; x++) {
                                if (checkBoxes[x].isChecked()) {
                                    selectedAnswerPosition = x + 1;
                                    if (TextUtils.isEmpty(localExerciseModelList.get(selectedPosition).selectedAnswers)) {
                                        localExerciseModelList.get(selectedPosition).selectedAnswers = String.valueOf(x);
                                    } else {
                                        localExerciseModelList.get(selectedPosition).selectedAnswers = localExerciseModelList.get(selectedPosition).selectedAnswers + "," + x;
                                    }
                                    isCheckedAtLeastOnce = true;
                                    break;
                                }
                            }
                        }
                        if (!isCheckedAtLeastOnce) {
                            selectedAnswerPosition = -1;
                            localExerciseModelList.get(selectedPosition).selectedAnswers = null;
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


    private void loadAnswers(int position) {
        resetExplanation();
        answerLayout.removeAllViews();

        List<AnswerChoiceModel> answerChoiceModels = localExerciseModelList.get(position).answerChoice;
        final int size = answerChoiceModels.size();
        final RadioButton[] radioButton = new RadioButton[size];
        final TextView[] tvSerial = new TextView[size];
        final LinearLayout[] rowLayout = new LinearLayout[size];

        String preselectedAnswers = null;
        if (!title.equalsIgnoreCase("Exercise Test") &&
                !TextUtils.isEmpty(localExerciseModelList.get(selectedPosition).selectedAnswers)) {
            preselectedAnswers = localExerciseModelList.get(selectedPosition).selectedAnswers;
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

            radioButton[i] = new RadioButton(this);
            radioButton[i].setId(Integer.valueOf(answerChoiceModel.idAnswerKey));
            radioButton[i].setTag(answerChoiceModel);
            radioButton[i].setBackgroundResource(R.drawable.selector_radio);

            if(title.equalsIgnoreCase("Flagged Questions")) {
                radioButton[i].setEnabled(false);
                radioButton[i].setClickable(false);
            }

            radioButton[i].setLayoutParams(p);

            rowLayout[i].addView(tvSerial[i]);
            rowLayout[i].addView(radioButton[i]);

            WebView optionWebView = new WebView(getApplicationContext());
            optionWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            optionWebView.setScrollbarFadingEnabled(true);
            optionWebView.getSettings().setLoadsImagesAutomatically(true);
            optionWebView.getSettings().setJavaScriptEnabled(true);
            optionWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            optionWebView.setWebChromeClient(new WebChromeClient());
            optionWebView.setWebViewClient(new MyWebViewClient());

            optionWebView.loadData(answerChoiceModel.answerChoiceTextHtml, "text/html; charset=UTF-8", null);
            p = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
            optionWebView.setLayoutParams(p);
            rowLayout[i].addView(optionWebView);
            answerLayout.addView(rowLayout[i]);

            try {
                radioButton[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int x = 0; x < size; x++) {
                            radioButton[x].setChecked(false);
                            if (radioButton[x].getId() == Integer.valueOf(answerChoiceModel.idAnswerKey)) {
                                selectedAnswerPosition = x + 1;
                                localExerciseModelList.get(selectedPosition).selectedAnswers = String.valueOf(x);
                            }
                        }
                        ((RadioButton) v).setChecked(true);
                        btnVerify.setEnabled(true);
                    }
                });
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        //set radiobutton check
        if (!TextUtils.isEmpty(preselectedAnswers)) {
            radioButton[Integer.valueOf(preselectedAnswers)].setChecked(true);
            selectedAnswerPosition = Integer.valueOf(preselectedAnswers) + 1;
        }
        setExplanationLayout();
    }

    private void clearAnswers() {
        List<AnswerChoiceModel> answerChoiceModels = localExerciseModelList.get(selectedPosition).answerChoice;
        int size = answerChoiceModels.size();
        boolean isCompound;
        if (localExerciseModelList.get(selectedPosition).idQuestionType.equalsIgnoreCase("1") ||
                localExerciseModelList.get(selectedPosition).idQuestionType.equalsIgnoreCase("2")) {
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
        localExerciseModelList.get(selectedPosition).selectedAnswers = null;
        resetExplanation();
    }

    private void setExplanationLayout() {
        String webText = "";
        String correctAnswer = "";
        String correctAnswerText = "";
        List<AnswerChoiceModel> answerChoiceModels = localExerciseModelList.get(selectedPosition).answerChoice;
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
                            String message = "Unknown error occured.Please try again.";
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

    public class GridAdapter extends BaseAdapter {

        View grid;
        LayoutInflater inflater;

        public GridAdapter() {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return (localExerciseModelList == null) ? 0 : localExerciseModelList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v;
            TextView btnCounter;
            if (convertView == null) {
                v = inflater.inflate(R.layout.grid_text, null, false);
            } else {
                v = convertView;
            }
            btnCounter = (TextView) v.findViewById(R.id.btnNumber);
            if (position < 9) {
                btnCounter.setText("0" + (position + 1));
            } else {
                btnCounter.setText(String.valueOf(position + 1));
            }
            switch (localExerciseModelList.get(position).answerColorSelection) {
                case ANSWERED:
                    btnCounter.setBackgroundResource(R.drawable.rounded_corners_green);
                    btnCounter.setTextColor(getResources().getColor(R.color.white));
                    break;

                case SKIPPED:
                    btnCounter.setBackgroundResource(R.drawable.rounded_corners_red);
                    btnCounter.setTextColor(getResources().getColor(R.color.white));
                    break;

                case FLAGGED:
                    btnCounter.setBackgroundResource(R.drawable.rounded_corners_yellow);
                    btnCounter.setTextColor(getResources().getColor(R.color.black));
                    break;

                case UNATTEMPTED:
                    btnCounter.setBackgroundResource(R.drawable.rounded_corners_gray);
                    btnCounter.setTextColor(getResources().getColor(R.color.black));
                    break;
            }

            if (position == selectedPosition) {
                btnCounter.setPaintFlags(btnCounter.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                previousQuestionPosition = position;
            } else {
                if ((btnCounter.getPaintFlags() & Paint.UNDERLINE_TEXT_FLAG) > 0) {
                    btnCounter.setPaintFlags(btnCounter.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                }
            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inflateUI(position);
                }
            });
            return v;
        }
    }

    private void setAnswerState() {
        if (selectedAnswerPosition != -1) {
            localExerciseModelList.get(previousQuestionPosition).answerColorSelection = Constants.AnswerState.ANSWERED;
        } else {
            localExerciseModelList.get(previousQuestionPosition).answerColorSelection = Constants.AnswerState.SKIPPED;
        }
    }

    public class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            tv_timer.setText("Completed.");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            tv_timer.setText(hms);
        }
    }

    private void getFlaggedQuestion() {
        ApiManager.getInstance(this).getFlaggedQuestions(LoginUserCache.getInstance().loginResponse.studentId,
                subjectId,
                chapterId, "", new ApiCallback<List<ExerciseModel>>(this) {
                    @Override
                    public void success(List<ExerciseModel> exerciseModels, Response response) {
                        super.success(exerciseModels, response);
                        localExerciseModelList = exerciseModels;
                        if (localExerciseModelList != null && localExerciseModelList.size() > 0) {
                            if (localExerciseModelList.size() > 1) {
                                webFooter.setVisibility(View.VISIBLE);
                            } else {
                                webFooter.setVisibility(View.GONE);
                            }
                            tvLevel.setText("TOTAL NUMBER OF QUESTIONS : " + exerciseModels.size());
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
        flaggedQuestionModel.idTestQuestion = localExerciseModelList.get(selectedPosition).idTestQuestion+"";
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
                new ApiCallback<List<ExerciseModel>>(this) {
                    @Override
                    public void success(List<ExerciseModel> exerciseModels, Response response) {
                        super.success(exerciseModels, response);
                        localExerciseModelList = exerciseModels;
                        if(localExerciseModelList != null ) {
                            if (localExerciseModelList.size() > 1) {
                                webFooter.setVisibility(View.VISIBLE);
                            } else {
                                webFooter.setVisibility(View.GONE);
                            }
                            renderQuestionLayout();
                            //dummy timer.. need to fetch time and interval from service
                            tv_timer.setText("00:30:00");
                            final CounterClass timer = new CounterClass(1800000, 1000);
                            timer.start();
                        } else {
                            headerProgress.setVisibility(View.GONE);
                            tvEmptyLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void showFullQuestionDialog() {
        FullQuestionDialog fullQuestionDialog = new FullQuestionDialog();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SELECTED_TOPIC, topic);
        fullQuestionDialog.setArguments(bundle);
        fullQuestionDialog.show(getFragmentManager(), "fullQuestionDialog");
    }

}