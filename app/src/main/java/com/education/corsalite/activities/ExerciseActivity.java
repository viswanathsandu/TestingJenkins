package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.requestmodels.PostExerciseRequestModel;
import com.education.corsalite.models.responsemodels.AnswerChoiceModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ExerciseModel;
import com.education.corsalite.models.responsemodels.PostExercise;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;
import com.education.corsalite.views.GridViewInScrollView;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    @Bind(R.id.btn_verify) Button btnVerify;
    @Bind(R.id.tv_clearanswer) TextView tvClearAnswer;

    @Bind(R.id.txtAnswerCount) TextView txtAnswerCount;
    @Bind(R.id.txtAnswerExp) WebView txtAnswerExp;
    @Bind(R.id.tv_serial_no) TextView tvSerialNo;


    @Bind(R.id.explanation_layout) LinearLayout explanationLayout;
    @Bind(R.id.layout_choice) LinearLayout layoutChoice;

    @Bind(R.id.btn_slider_test)Button slider;
    @Bind(R.id.ll_test_navigator)LinearLayout testNavLayout;
    @Bind(R.id.shadow_view) View shadowView;

    @Bind(R.id.gv_test) GridViewInScrollView gvTest;
    @Bind(R.id.test_nav_footer) LinearLayout testNavFooter;

    String webQuestion = "";
    private int selectedPosition = 0;
    private int selectedAnswerPosition = -1;
    String title = "";
    GridAdapter gridAdapter;
    private List<ExerciseModel> localExerciseModelList;
    private int previousQuestionPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_exercise, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        localExerciseModelList = WebActivity.exerciseModelList;
        toggleSlider();
        initWebView();
        initWebView1();
        initSuggestionWebView();
        setListener();
        getIntentData();
    }

    private void getIntentData() {
        if(getIntent().hasExtra(Constants.TEST_TITLE)) {
            title = getIntent().getExtras().getString(Constants.TEST_TITLE);
        }
        tvNavTitle.setText(title);
        setToolbarForExercise(title);

        if(getIntent().hasExtra(Constants.SELECTED_TOPIC)) {
            tvPageTitle.setText(getIntent().getExtras().getString(Constants.SELECTED_TOPIC));
        }

        // set selected position
        if(getIntent().hasExtra(Constants.SELECTED_POSITION)) {
            selectedPosition = getIntent().getExtras().getInt(Constants.SELECTED_POSITION);
        }

        if(selectedPosition >= 0) {
            inflateUI(selectedPosition);
        }

        if(localExerciseModelList.size() > 1) {
            webFooter.setVisibility(View.VISIBLE);
        } else {
            webFooter.setVisibility(View.GONE);
        }

        gridAdapter = new GridAdapter();
        gvTest.setAdapter(gridAdapter);
        gvTest.setExpanded(true);

        if(title.equalsIgnoreCase("Exercise Test")) {
            testNavFooter.setVisibility(View.GONE);
        } else {
            testNavFooter.setVisibility(View.VISIBLE);
            btnVerify.setVisibility(View.GONE);
        }
    }

    private void setListener() {
        btnNext.setOnClickListener(mClickListener);
        btnPrevious.setOnClickListener(mClickListener);
        btnVerify.setOnClickListener(mClickListener);
        tvClearAnswer.setOnClickListener(mClickListener);
        shadowView.setOnClickListener(mClickListener);
        slider.setOnClickListener(mClickListener);
        testNavLayout.setOnClickListener(mClickListener);
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

        if(getExternalCacheDir() != null) {
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

        if(getExternalCacheDir() != null) {
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

    private void inflateUI(int position) {
        if(previousQuestionPosition >= 0) {
            setAnswerState();
        }
        selectedPosition = position;
        resetExplanation();
        if(localExerciseModelList  != null && localExerciseModelList.size() > 0) {
            loadQuestion(position);
        }
    }

    private void navigateButtonEnabled() {
        if(selectedPosition == 0) {
            btnPrevious.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            return;
        }
        if(selectedPosition == localExerciseModelList.size() - 1) {
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
            }
        }
    };

    private void loadQuestion(int position) {

        tvSerialNo.setText("Q" + (position + 1) + ")");

        /*String header = "Exercise: " + WebActivity.exe)rciseModelList.get(position).displayName.split("\\s+")[0];
        SpannableString headerText = new SpannableString(header);
        headerText.setSpan(new UnderlineSpan(), 0, header.length(), 0);*/
        tvLevel.setText(localExerciseModelList.get(position).displayName.split("\\s+")[0].toUpperCase(Locale.ENGLISH));


        if(TextUtils.isEmpty(localExerciseModelList.get(position).paragraphHtml)) {
            webviewParagraph.setVisibility(View.GONE);
        } else {
            webviewParagraph.setVisibility(View.VISIBLE);
            webQuestion = localExerciseModelList.get(position).paragraphHtml;
            webviewParagraph.loadData(webQuestion, "text/html; charset=UTF-8", null);
        }

        webviewQuestion.setVisibility(View.GONE);
        if(localExerciseModelList.get(position).questionHtml != null) {
            webQuestion =  localExerciseModelList.get(position).questionHtml;
            webviewQuestion.loadData(webQuestion, "text/html; charset=UTF-8", null);
            webviewQuestion.setVisibility(View.VISIBLE);
        }

        if(localExerciseModelList.get(position).comment != null) {
            tvComment.setText(localExerciseModelList.get(position).comment);
            tvComment.setVisibility(View.VISIBLE);
        } else {
            tvComment.setVisibility(View.GONE);
        }

        navigateButtonEnabled();
        if (mViewSwitcher.indexOfChild(mViewSwitcher.getCurrentView()) == 0) {
            mViewSwitcher.showNext();
        }

        if(localExerciseModelList.get(position).idQuestionType.equalsIgnoreCase("1")) {
            loadAnswers(position);
        } else if (localExerciseModelList.get(position).idQuestionType.equalsIgnoreCase("2")) {
            loadChexkbox(position);
        } else {
            if(localExerciseModelList.size() - 1 == 0) {
                return;
            }
            if(localExerciseModelList.size() - 1 > selectedPosition) {
                localExerciseModelList.remove(selectedPosition);
                inflateUI(selectedPosition);
            } else {
                localExerciseModelList.remove(selectedPosition);
                inflateUI(selectedPosition - 1);
            }
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
        final LinearLayout[] rowLayout = new LinearLayout[size];

        String[] preselectedAnswers = null;
        if(/*!title.equalsIgnoreCase("Exercise Test") &&*/
                !TextUtils.isEmpty(localExerciseModelList.get(selectedPosition).selectedAnswers)) {
            preselectedAnswers = localExerciseModelList.get(selectedPosition).selectedAnswers.split(",");
        }

        for(int i = 0; i < size; i++) {

            final AnswerChoiceModel answerChoiceModel = answerChoiceModels.get(i);
            checkBoxes[i] = new CheckBox(this);
            checkBoxes[i].setId(Integer.valueOf(answerChoiceModel.idAnswerKey));
            checkBoxes[i].setTag(answerChoiceModel);
            checkBoxes[i].setBackgroundResource(R.drawable.selector_checkbox);

            rowLayout[i] = new LinearLayout(this);
            rowLayout[i].setOrientation(LinearLayout.HORIZONTAL);
            rowLayout[i].setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
                        if(chkBox.isChecked()) {
                            isCheckedAtLeastOnce = true;
                            for (int x = 0; x < size; x++) {
                                if(checkBoxes[x].getId() == Integer.valueOf(answerChoiceModel.idAnswerKey)) {
                                    selectedAnswerPosition = x + 1;
                                    if(TextUtils.isEmpty(localExerciseModelList.get(selectedPosition).selectedAnswers)) {
                                        localExerciseModelList.get(selectedPosition).selectedAnswers = String.valueOf(x);
                                    } else {
                                        localExerciseModelList.get(selectedPosition).selectedAnswers = localExerciseModelList.get(selectedPosition).selectedAnswers + ","+x;
                                    }
                                }
                            }
                        } else {
                            for (int x = 0; x < size; x++) {
                                if(checkBoxes[x].isChecked()) {
                                    selectedAnswerPosition = x + 1;
                                    if(TextUtils.isEmpty(localExerciseModelList.get(selectedPosition).selectedAnswers)) {
                                        localExerciseModelList.get(selectedPosition).selectedAnswers = String.valueOf(x);
                                    } else {
                                        localExerciseModelList.get(selectedPosition).selectedAnswers = localExerciseModelList.get(selectedPosition).selectedAnswers + ","+x;
                                    }
                                    isCheckedAtLeastOnce = true;
                                     break;
                                }
                            }
                        }
                        if(!isCheckedAtLeastOnce) {
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
        if(preselectedAnswers != null && preselectedAnswers.length > 0) {
            for(String selectedChoice : preselectedAnswers) {
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
        if(/*!title.equalsIgnoreCase("Exercise Test") &&*/
                !TextUtils.isEmpty(localExerciseModelList.get(selectedPosition).selectedAnswers)) {
            preselectedAnswers = localExerciseModelList.get(selectedPosition).selectedAnswers;
        }

        for(int i = 0; i < size; i++) {

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
                            if(radioButton[x].getId() == Integer.valueOf(answerChoiceModel.idAnswerKey)) {
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
        if(!TextUtils.isEmpty(preselectedAnswers)) {
            radioButton[Integer.valueOf(preselectedAnswers)].setChecked(true);
            selectedAnswerPosition = Integer.valueOf(preselectedAnswers) + 1;
        }
        setExplanationLayout();
    }

    private void clearAnswers() {
        List<AnswerChoiceModel> answerChoiceModels = localExerciseModelList.get(selectedPosition).answerChoice;
        int size = answerChoiceModels.size();
        boolean isCompound;
        if(localExerciseModelList.get(selectedPosition).idQuestionType.equalsIgnoreCase("1")) {
            isCompound = true;
        } else {
            isCompound = false;
        }
        if(!isCompound) {
            return;
        }
        for(int i = 0; i < size; i++) {
            ((CompoundButton)findViewById(Integer.valueOf(answerChoiceModels.get(i).idAnswerKey))).setChecked(false);
        }
        localExerciseModelList.get(selectedPosition).selectedAnswers = null;
        resetExplanation();
    }

    private void setExplanationLayout() {
        String webText = "";
        String correctAnswer = "";
        List<AnswerChoiceModel> answerChoiceModels = localExerciseModelList.get(selectedPosition).answerChoice;
        int counter = 0;
        for(AnswerChoiceModel answerChoiceModel : answerChoiceModels) {
            counter = counter + 1 ;
            if(answerChoiceModel.isCorrectAnswer.equalsIgnoreCase("Y")) {
                correctAnswer = String.valueOf(counter);
                webText = answerChoiceModel.answerChoiceExplanationHtml;
                break;
            }
        }
        txtAnswerCount.setText(correctAnswer);
        txtAnswerExp.loadData(webText, "text/html; charset=UTF-8", null);

        if(gridAdapter != null) {
            gridAdapter.notifyDataSetChanged();
        }
    }

    private void resetExplanation() {
        selectedAnswerPosition = -1;
        btnVerify.setEnabled(false);
        explanationLayout.setVisibility(View.GONE);
        layoutChoice.setVisibility(View.GONE);
    }

    private void postAnswer(AnswerChoiceModel answerChoiceModel, String questionId) {
        PostExerciseRequestModel postExerciseRequestModel = new PostExerciseRequestModel();
        postExerciseRequestModel.updateTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        postExerciseRequestModel.idStudent = LoginUserCache.getInstance().loginResponse.studentId;
        postExerciseRequestModel.idQuestion = questionId;
        postExerciseRequestModel.studentAnswerChoice = selectedAnswerPosition+"";
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
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return localExerciseModelList.size();
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
            btnCounter = (TextView)v.findViewById(R.id.btnNumber);
            if(position < 9) {
                btnCounter.setText("0" + (position + 1));
            } else {
                btnCounter.setText(String.valueOf(position + 1));
            }

            Log.e("tteesstt", localExerciseModelList.get(position).answerColorSelection+"::"+position);

            switch (localExerciseModelList.get(position).answerColorSelection) {
                case ANSWERED:
                    btnCounter.setBackgroundResource(R.drawable.rounded_corners_green);
                    break;

                case SKIPPED:
                    btnCounter.setBackgroundResource(R.drawable.rounded_corners_red);
                    break;

                case FLAGGED:
                    btnCounter.setBackgroundResource(R.drawable.rounded_corners_yellow);
                    break;

                case UNATTEMPTED:
                    btnCounter.setBackgroundResource(R.drawable.rounded_corners_gray);
                    break;
            }

            if(position == selectedPosition) {
                btnCounter.setPaintFlags(btnCounter.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
                previousQuestionPosition = position;
            } else {
                if ((btnCounter.getPaintFlags() & Paint.UNDERLINE_TEXT_FLAG) > 0) {
                    btnCounter.setPaintFlags( btnCounter.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
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
        if(selectedAnswerPosition != -1) {
            localExerciseModelList.get(previousQuestionPosition).answerColorSelection = Constants.AnswerState.ANSWERED;
        } else {
            localExerciseModelList.get(previousQuestionPosition).answerColorSelection = Constants.AnswerState.SKIPPED;
        }
    }
}