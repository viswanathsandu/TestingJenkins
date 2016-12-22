package com.education.corsalite.fragments.examengine;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.event.UpdateAnswerEvent;
import com.education.corsalite.models.responsemodels.AnswerChoiceModel;
import com.education.corsalite.utils.ExamEngineWebViewClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * Created by vissu on 12/21/16.
 */

public abstract class ChoiceQuestionFragment extends BaseQuestionFragment {

    protected CompoundButton[] options;

    protected Set<Integer> selectedAnswers = new HashSet<>();

    protected abstract CompoundButton[] createOptions(int size);

    protected abstract CompoundButton getOption(View container, AnswerChoiceModel model);

    protected abstract int getOptionsLayoutId();

    protected abstract void loadSelectedAnswers();

    @Override
    public void loadAnswerLayout() {
        options = createOptions(question.answerChoice.size());
        answerLayout.setOrientation(LinearLayout.VERTICAL);
        List<AnswerChoiceModel> answerChoiceModels = question.answerChoice;
        String preselectedAnswers = question.selectedAnswers;

        for (int i = 0; i < options.length; i++) {
            AnswerChoiceModel answerChoiceModel = answerChoiceModels.get(i);
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout container = (LinearLayout) inflater.inflate(getOptionsLayoutId(), null);
            WebView webview = (WebView) container.findViewById(R.id.webview);
            TextView optionNumberTxt = (TextView) container.findViewById(R.id.option_number_txt);
            optionNumberTxt.setText(String.format("%s", i + 1));
            options[i] = getOption(container, answerChoiceModel);
            options[i].setOnClickListener(getOptionClickListener());
            loadChoice(webview, answerChoiceModel);
            setTouchListener(webview, options[i]);
            options[i].setChecked(false);
            answerLayout.addView(container);
        }
        loadSelectedAnswers();
    }

    private void loadChoice(WebView webview, AnswerChoiceModel choice) {
        webview.setScrollbarFadingEnabled(true);
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new ExamEngineWebViewClient(getActivity()));
        webview.loadDataWithBaseURL(null, choice.answerChoiceTextHtml, "text/html", "UTF-8", null);
    }

    private void setTouchListener(WebView webview, final CompoundButton option) {
        webview.setOnTouchListener(new View.OnTouchListener() {
            private static final int MAX_CLICK_DURATION = 200;
            private long startClickTime;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if(clickDuration < MAX_CLICK_DURATION) {
                            option.performClick();
                        }
                    }
                }
                return true;
            }
        });
    }

    private View.OnClickListener getOptionClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if(tag != null && tag instanceof AnswerChoiceModel) {
                    updateAnswer((AnswerChoiceModel) tag);
                }
            }
        };
    }

    @Override
    public void updateAnswer() {
        for(int i=0; i<options.length; i++) {
            options[i].setChecked(selectedAnswers.contains(i));
        }
    }

    public void updateAnswer(AnswerChoiceModel model) {
        selectedAnswers.add(question.answerChoice.indexOf(model));
        formatSelectedAnswers();
        updateAnswer();
        EventBus.getDefault().post(new UpdateAnswerEvent());
    }

    private void formatSelectedAnswers() {
        question.selectedAnswers = null;
        List<Integer> answerList = new ArrayList<Integer>(selectedAnswers);
        Collections.sort(answerList);
        for(int i=0; i<answerList.size(); i++) {
            if(TextUtils.isEmpty(question.selectedAnswers)) {
                question.selectedAnswers = String.valueOf(answerList.get(i));
                question.selectedAnswerKeyIds = question.answerChoice.get(answerList.get(i)).idAnswerKey;
            } else {
                question.selectedAnswers += String.format(",%s", answerList.get(i));
                question.selectedAnswerKeyIds += String.format(",%s", question.answerChoice.get(answerList.get(i)).idAnswerKey);
            }
        }
    }

    @Override
    public void clearAnswer() {
        selectedAnswers.clear();
        question.selectedAnswers = null;
        question.selectedAnswerKeyIds = null;
        updateAnswer();
        EventBus.getDefault().post(new UpdateAnswerEvent());
    }
}
