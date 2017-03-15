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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.enums.QuestionType;
import com.education.corsalite.event.FlagEvent;
import com.education.corsalite.event.FlagUpdatedEvent;
import com.education.corsalite.fragments.BaseFragment;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.responsemodels.AnswerChoiceModel;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.ExamEngineWebViewClient;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.TimeUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by vissu on 12/21/16.
 */

public abstract class BaseQuestionFragment extends BaseFragment {

    private static final String KEY_QUESTION = "QUESTION";
    private static final String KEY_QUESTION_NUMBER = "QUESTION_NUMBER";
    private static final String KEY_ENABLE_VERIFY = "ENABLE_VERIFY";
    private static final String KEY_IS_FLAGGED = "IS_FLAGGED";

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
    @Bind(R.id.imv_flag) protected ImageView flaggedImg;

    protected long mStartedTime;
    protected boolean isFlagged;
    protected boolean isVerifyEnabled;
    protected boolean isExplanationShown;
    protected boolean isFlaggedQuestionShown;
    protected int questionNumber;
    protected ExamModel question;

    public void enableVerify() {
        getArguments().putBoolean(KEY_ENABLE_VERIFY, true);
    }

    public void showExplanation() {
        isExplanationShown = true;
    }

    public void showFlaggedQuestionShown() {
        isFlaggedQuestionShown = true;
    }

    public void setFlagged(boolean isFlagged) {
        getArguments().putBoolean(KEY_IS_FLAGGED, isFlagged);
    }

    public static BaseQuestionFragment getInstance(ExamModel question, int questionNumber) {
        BaseQuestionFragment fragment = null;
        Bundle args = new Bundle();
        args.putString(KEY_QUESTION, Gson.get().toJson(question));
        args.putInt(KEY_QUESTION_NUMBER, questionNumber);
        switch (QuestionType.getQuestionType(question.idQuestionType)) {
            case SINGLE_SELECT_CHOICE:
                fragment = new SingleChoiceQuestionFragment();
                break;
            case MULTI_SELECT_CHOICE:
                fragment = new MultipleChoiceQuestionFragment();
                break;
            case ALPHANUMERIC:
                fragment = new AlphaNumericQuestionFragment();
                break;
            case NUMERIC:
                fragment = new NumericQuestionFragment();
                break;
            case FILL_IN_THE_BLANK:
                fragment = new FillInTheBlankQuestionFragment();
                break;
            case N_BLANK_SINGLE_SELECT:
            case N_BLANK_MULTI_SELECT:
                fragment = new NBlankQuestionFragment();
                break;
            case GRID:
                fragment = new GridQuestionFragment();
                break;
            case FRACTION:
            case PICK_A_SENTENCE:
            case WORD_PROPERTIES:
            default:
                break;
        }
        if(fragment != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_layout, container, false);
        ButterKnife.bind(this, view);
        initData();
        initUi();
        loadQuestion();
        return view;
    }

    private void initUi() {
        initWebView();
        setExplanationLayout();
        btnVerify.setVisibility(isVerifyEnabled ? View.VISIBLE : View.GONE);
        updateFlagStatus();
    }

    private void controlViewAnswers(ViewGroup viewGroup) {
        for (int i=0; i<viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(false);
            if(view instanceof ViewGroup) {
                controlViewAnswers((ViewGroup) view);
            }
        }
    }

    private void updateFlagStatus() {
        flaggedImg.setImageResource(isFlagged ? R.drawable.btn_flag_select : R.drawable.btn_flag_unselect);
    }

    private void initData() {
        questionNumber = getArguments().getInt(KEY_QUESTION_NUMBER);
        String questionGson = getArguments().getString(KEY_QUESTION);
        question = Gson.get().fromJson(questionGson, ExamModel.class);
        isFlagged = getArguments().getBoolean(KEY_IS_FLAGGED);
        isVerifyEnabled = getArguments().getBoolean(KEY_ENABLE_VERIFY);
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
            if(isExplanationShown) {
                onVerifyClicked(null);
            }
            if(isExplanationShown || isFlaggedQuestionShown) {
                controlViewAnswers(answerLayout);
                tvClearAnswer.setVisibility(View.INVISIBLE);
            }
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

    public String getSelectedAnswer() {
        return question.selectedAnswers;
    }

    public String getSelectedAnswerKeyIds() {
        return question.selectedAnswerKeyIds;
    }

    public String getQuestionState() {
        if(question != null && question.isFlagged) {
            return Constants.AnswerState.FLAGGED.getValue();
        } else {
            return getAnswerState();
        }
    }

    public String getAnswerState() {
        return TextUtils.isEmpty(question.selectedAnswers)
                ? Constants.AnswerState.SKIPPED.getValue()
                : Constants.AnswerState.ANSWERED.getValue();
    }

    protected void setExplanationLayout() {
        String webText = "";
        List<AnswerChoiceModel> answerChoiceModels = question.answerChoice;
        for (AnswerChoiceModel answerChoiceModel : answerChoiceModels) {
            if (answerChoiceModel.isCorrectAnswer.equalsIgnoreCase("Y")) {
                webText = answerChoiceModel.answerChoiceExplanationHtml;
                break;
            }
        }
        txtAnswerCount.setText(getDisplayedCorrectAnswer());
        txtAnswerExp.loadDataWithBaseURL(null, webText, "text/html", "UTF-8", null);
    }


    @OnClick(R.id.tv_clearanswer)
    public void onClearAnswer(View view) {
        if(isAnswered()) {
            clearAnswer();
        } else {
            showToast("Please select an option");
        }
    }

    @OnClick(R.id.tv_verify)
    public void onVerifyClicked(View view) {
        if(isAnswered()) {
            btnVerify.setEnabled(false);
            explanationLayout.setVisibility(View.VISIBLE);
            layoutChoice.setVisibility(View.VISIBLE);
        } else {
            showToast("Please select an option");
        }
    }

    @OnClick(R.id.imv_flag)
    public void onFlagClicked(View view) {
        EventBus.getDefault().post(new FlagEvent(!isFlagged));
    }

    public abstract void loadAnswerLayout();

    public abstract void clearAnswer();

    public abstract void updateAnswer();

    public abstract String getCorrectAnswer();

    public abstract String getDisplayedCorrectAnswer();

    public boolean isAnswered() {
        return !TextUtils.isEmpty(question.selectedAnswers);
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

    public void onEventMainThread(FlagUpdatedEvent event) {
        if(isAdded()) {
            isFlagged = !isFlagged;
            question.isFlagged = isFlagged;
            updateFlagStatus();
        }
    }
}
