package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
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
import com.education.corsalite.models.responsemodels.PostExercise;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by mt0060 on 04/11/15.
 */
public class ExerciseActivity extends AbstractBaseActivity {

    @Bind(R.id.vs_container) ViewSwitcher mViewSwitcher;
    @Bind(R.id.footer_layout) RelativeLayout webFooter;
    @Bind(R.id.answer_layout) LinearLayout answerLayout;
    @Bind(R.id.btn_next) Button btnNext;
    @Bind(R.id.btn_previous) Button btnPrevious;
    @Bind(R.id.webview_question) WebView webviewQuestion;
    @Bind(R.id.tv_comment) TextView tvComment;
    @Bind(R.id.tv_header) TextView tvHeader;
    @Bind(R.id.btn_verify) Button btnVerify;

    @Bind(R.id.txtAnswerCount) TextView txtAnswerCount;
    @Bind(R.id.txtAnswerExp) WebView txtAnswerExp;

    String webQuestion = "";
    private int selectedPosition = 0;
    private int selectedAnswerPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_exercise, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        initWebView();
        initSuggestionWebView();
        setListener();
        if(getIntent().hasExtra(Constants.SELECTED_POSITION)) {
            selectedPosition = getIntent().getExtras().getInt(Constants.SELECTED_POSITION);
        }

        if(selectedPosition >= 0) {
            setToolbarForExercise(WebActivity.exerciseModelList, selectedPosition);
        }
        if(WebActivity.exerciseModelList.size() > 1) {
            webFooter.setVisibility(View.VISIBLE);
        } else {
            webFooter.setVisibility(View.GONE);
        }
    }

    private void setListener() {
        btnNext.setOnClickListener(mClickListener);
        btnPrevious.setOnClickListener(mClickListener);
        btnVerify.setOnClickListener(mClickListener);
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
        // Load the URLs inside the WebView, not in the external web browser
        txtAnswerExp.setWebViewClient(new MyWebViewClient());
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
                showToast("Adding '"+message+"' to the notes");
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

    @Override
    public void onEvent(Integer position) {
        super.onEvent(position);
        selectedPosition = position;
        resetExplanation();
        if(WebActivity.exerciseModelList  != null && WebActivity.exerciseModelList.size() > 0) {
            loadQuestion(position);
        }
    }

    private void navigateButtonEnabled() {
        if(selectedPosition == 0) {
            btnPrevious.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            return;
        }
        if(selectedPosition == WebActivity.exerciseModelList.size() - 1) {
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
                    showExerciseToolbar(null, selectedPosition + 1, true);
                    break;
                case R.id.btn_previous:
                    showExerciseToolbar(null, selectedPosition - 1, true);
                    break;
                case R.id.btn_verify:
                    postAnswer(WebActivity.exerciseModelList.get(selectedPosition).answerChoice.get(selectedAnswerPosition),
                            WebActivity.exerciseModelList.get(selectedPosition).idQuestion);
                    break;
            }
        }
    };

    private void loadQuestion(int position) {

        String header = "Exercise: " + WebActivity.exerciseModelList.get(position).displayName.split("\\s+")[0];
        SpannableString headerText = new SpannableString(header);
        headerText.setSpan(new UnderlineSpan(), 0, header.length(), 0);
        tvHeader.setText(headerText);


        if(WebActivity.exerciseModelList.get(position).paragraphHtml != null) {
            webQuestion = WebActivity.exerciseModelList.get(position).paragraphHtml;
        }
        if(WebActivity.exerciseModelList.get(position).questionHtml != null) {
            webQuestion = webQuestion + WebActivity.exerciseModelList.get(position).questionHtml;
            webviewQuestion.loadData(webQuestion, "text/html; charset=UTF-8", null);
        }
        if(WebActivity.exerciseModelList.get(position).comment != null) {
            tvComment.setText(WebActivity.exerciseModelList.get(position).comment);
            tvComment.setVisibility(View.VISIBLE);
        } else {
            tvComment.setVisibility(View.GONE);
        }

        navigateButtonEnabled();
        if (mViewSwitcher.indexOfChild(mViewSwitcher.getCurrentView()) == 0) {
            mViewSwitcher.showNext();
        }
        loadAnswers(position);
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

    private void loadAnswers(int position) {
        resetExplanation();
        answerLayout.removeAllViews();

        List<AnswerChoiceModel> answerChoiceModels = WebActivity.exerciseModelList.get(position).answerChoice;
        final int size = answerChoiceModels.size();
        final RadioButton[] radioButton = new RadioButton[size];
        final LinearLayout[] rowLayout = new LinearLayout[size];

        for(int i = 0; i < size; i++) {

            final AnswerChoiceModel answerChoiceModel = answerChoiceModels.get(i);
            radioButton[i] = new RadioButton(this);
            radioButton[i].setId(Integer.valueOf(answerChoiceModel.idAnswerKey));
            radioButton[i].setTag(answerChoiceModel);

            rowLayout[i] = new LinearLayout(this);
            rowLayout[i].setOrientation(LinearLayout.HORIZONTAL);
            rowLayout[i].setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            radioButton[i].setLayoutParams(p);

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
        setExplanationLayout();
    }

    private void setExplanationLayout() {
        String webText = "";
        String correctAnswer = "";
        List<AnswerChoiceModel> answerChoiceModels = WebActivity.exerciseModelList.get(selectedPosition).answerChoice;
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


    }

    private void resetExplanation() {
        selectedAnswerPosition = -1;
        btnVerify.setEnabled(false);
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
            }

            @Override
            public void success(PostExercise postExercise, Response response) {
                super.success(postExercise, response);
                if (postExercise.isSuccessful()) {
                    btnVerify.setEnabled(false);

                }
            }
        });
    }
}