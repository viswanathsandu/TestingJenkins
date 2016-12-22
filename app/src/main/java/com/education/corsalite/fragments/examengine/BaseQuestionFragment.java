package com.education.corsalite.fragments.examengine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.enums.QuestionType;
import com.education.corsalite.fragments.BaseFragment;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.utils.ExamEngineWebViewClient;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.TimeUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vissu on 12/21/16.
 */

public abstract class BaseQuestionFragment extends BaseFragment {

    private static final String KEY_QUESTION = "QUESTION";
    private static final String KEY_QUESTION_NUMBER = "QUESTION_NUMBER";

    @Bind(R.id.webview_question) protected WebView webviewQuestion;
    @Bind(R.id.webview_paragraph) protected WebView webviewParagraph;
    @Bind(R.id.tv_verify) protected TextView btnVerify;
    @Bind(R.id.tv_clearanswer) protected TextView tvClearAnswer;
    @Bind(R.id.txtAnswerCount) protected TextView txtAnswerCount;
    @Bind(R.id.txtAnswerExp) protected WebView txtAnswerExp;
    @Bind(R.id.explanation_layout) protected LinearLayout explanationLayout;
    @Bind(R.id.layout_choice) protected LinearLayout layoutChoice;
    @Bind(R.id.tv_serial_no) protected TextView tvSerialNo;
    @Bind(R.id.answer_layout) protected LinearLayout answerLayout;

    protected long mStartedTime;
    protected boolean isFlagged;
    protected int questionNumber;
    protected ExamModel question;

    public static BaseQuestionFragment getInstance(ExamModel question, int questionNumber) {
        BaseQuestionFragment fragment;
        Bundle args = new Bundle();
        args.putString(KEY_QUESTION, Gson.get().toJson(question));
        args.putInt(KEY_QUESTION_NUMBER, questionNumber);

        switch (QuestionType.getQuestionType(question.idQuestionType)) {
            case SINGLE_SELECT_CHOICE:
                fragment = new SingleChoiceQuestionFragment();
                fragment.setArguments(args);
                return fragment;
            case MULTI_SELECT_CHOICE:
                fragment = new MultipleChoiceQuestionFragment();
                fragment.setArguments(args);
                return fragment;
            case ALPHANUMERIC:
                fragment = new AlphaNumericQuestionFragment();
                fragment.setArguments(args);
                return fragment;
            case NUMERIC:
                fragment = new NumericQuestionFragment();
                fragment.setArguments(args);
                return fragment;
            case FILL_IN_THE_BLANK:
                fragment = new FillInTheBlankQuestionFragment();
                fragment.setArguments(args);
                return fragment;
            case N_BLANK_SINGLE_SELECT:
            case N_BLANK_MULTI_SELECT:
            case GRID:
            case FRACTION:
            case PICK_A_SENTENCE:
            case WORD_PROPERTIES:
            default:
                break;
        }

        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_layout, container, false);
        ButterKnife.bind(this, view);
        initUi();
        initQuestion();
        loadQuestion();
        return view;
    }

    private void initUi() {
        initWebView();
    }

    private void initQuestion() {
        questionNumber = getArguments().getInt(KEY_QUESTION_NUMBER);
        String questionGson = getArguments().getString(KEY_QUESTION);
        question = Gson.get().fromJson(questionGson, ExamModel.class);
    }

    public void loadQuestion() {
        try {
            mStartedTime = TimeUtils.currentTimeInMillis();
            tvSerialNo.setText( String.format("Q%s)", questionNumber));
            if (TextUtils.isEmpty(question.paragraphHtml)) {
                webviewParagraph.setVisibility(View.GONE);
            } else {
                webviewParagraph.setVisibility(View.VISIBLE);
                webviewParagraph.loadDataWithBaseURL(null, question.paragraphHtml, "text/html", "UTF-8", null);
            }
            if (question.questionHtml != null) {
                webviewQuestion.setVisibility(View.VISIBLE);
                webviewQuestion.loadDataWithBaseURL(null, question.questionHtml, "text/html", "UTF-8", null);
            }
            loadAnswerLayout();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void initWebView() {
        webviewQuestion.getSettings().setSupportZoom(true);
        webviewQuestion.getSettings().setBuiltInZoomControls(false);
        webviewQuestion.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webviewQuestion.setScrollbarFadingEnabled(true);
        webviewQuestion.getSettings().setLoadsImagesAutomatically(true);
        webviewQuestion.getSettings().setJavaScriptEnabled(true);
        webviewQuestion.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if (getActivity().getExternalCacheDir() != null) {
            webviewQuestion.getSettings().setAppCachePath(getActivity().getExternalCacheDir().getAbsolutePath());
        } else {
            webviewQuestion.getSettings().setAppCachePath(getActivity().getCacheDir().getAbsolutePath());
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
        webviewQuestion.setWebViewClient(new ExamEngineWebViewClient(getActivity()));
    }

    public abstract void loadAnswerLayout();

    public boolean isAnswered() {
        return false;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public boolean isCorrect() {
        String correctAnswer = getCorrectAnswer();
        return !TextUtils.isEmpty(question.selectedAnswers)
                && !TextUtils.isEmpty(correctAnswer)
                && question.selectedAnswers.equalsIgnoreCase(getCorrectAnswer());
    }

    public abstract void updateAnswer();

    public abstract String getCorrectAnswer();

    public String getEnteredAnswer() {
        return question.selectedAnswers;
    }


}
