package com.education.corsalite.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.education.corsalite.R;
import com.education.corsalite.activities.ExamEngineActivity;
import com.education.corsalite.models.responsemodels.AnswerChoiceModel;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.utils.Constants;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mt0060 on 05/12/15.
 */
public class FullQuestionDialog extends DialogFragment {

    @Bind(R.id.vs_container) ViewSwitcher mViewSwitcher;
    @Bind(R.id.tv_close) TextView tvClose;
    @Bind(R.id.tv_module) TextView tvModule;

    @Bind(R.id.layout_full_question) LinearLayout layoutFullQuestion;

    private List<ExamModel> localExamModelList;
    Context context;
    int serialNumber = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog=new Dialog(getActivity(),android.R.style.Theme_Light_NoTitleBar);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fullquestion, container, false);
        ButterKnife.bind(this, v);
        context = getActivity();
        getAvailableArguments();
        if(localExamModelList != null) {
            for(ExamModel examModel : localExamModelList) {
                if (examModel.idQuestionType.equalsIgnoreCase("1") ||
                        examModel.idQuestionType.equalsIgnoreCase("2")) {
                    setQuestionLayout(examModel);
                }
            }
        }
        if (mViewSwitcher.indexOfChild(mViewSwitcher.getCurrentView()) == 0) {
            mViewSwitcher.showNext();
        }
        return v;
    }

    private void getAvailableArguments() {
        if(getArguments().getString(Constants.SELECTED_TOPIC) != null) {
            tvModule.setText(getArguments().getString(Constants.SELECTED_TOPIC));
        }
        localExamModelList = ((ExamEngineActivity)getActivity()).getLocalExamModelList();
        if(localExamModelList != null && localExamModelList.size() > 0) {
            Collections.sort(localExamModelList);
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

    private void setQuestionLayout(ExamModel examModel) {

        LinearLayout questionLayout = new LinearLayout(context);
        questionLayout.setOrientation(LinearLayout.VERTICAL);

        TextView tvComment = new TextView(context);
        tvComment.setPadding(10,5,5,5);
        tvComment.setText(examModel.comment);
        tvComment.setBackgroundColor(getResources().getColor(R.color.golden_yellow));
        questionLayout.addView(tvComment);

        // set paragraph html
        if(examModel.paragraphHtml != null && examModel.paragraphHtml.length() > 0) {
            WebView optionWebView = new WebView(context);
            optionWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            optionWebView.setScrollbarFadingEnabled(true);
            optionWebView.getSettings().setLoadsImagesAutomatically(true);
            optionWebView.getSettings().setJavaScriptEnabled(true);
            optionWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            optionWebView.setWebChromeClient(new WebChromeClient());
            optionWebView.setWebViewClient(new MyWebViewClient());

            optionWebView.loadData(examModel.paragraphHtml, "text/html; charset=UTF-8", null);

            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            questionLayout.setLayoutParams(p);
            questionLayout.addView(optionWebView);
        }

        // set question html
        if(examModel.questionHtml != null && examModel.questionHtml.length() > 0) {

            serialNumber = serialNumber + 1;
            LinearLayout llQuestionNumber = new LinearLayout(context);
            llQuestionNumber.setOrientation(LinearLayout.HORIZONTAL);

            TextView questionNumber = new TextView(context);
            questionNumber.setText("Q" + serialNumber + ")");
            questionNumber.setTextColor(Color.BLACK);
            questionNumber.setGravity(Gravity.TOP);
            questionNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            questionNumber.setPadding(0, 10, 0, 0);
            llQuestionNumber.addView(questionNumber);

            WebView optionWebView = new WebView(context);
            optionWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            optionWebView.setScrollbarFadingEnabled(true);
            optionWebView.getSettings().setLoadsImagesAutomatically(true);
            optionWebView.getSettings().setJavaScriptEnabled(true);
            optionWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            optionWebView.setWebChromeClient(new WebChromeClient());
            optionWebView.setWebViewClient(new MyWebViewClient());

            optionWebView.loadData(examModel.questionHtml, "text/html; charset=UTF-8", null);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            questionLayout.setLayoutParams(p);
            llQuestionNumber.addView(optionWebView);
            questionLayout.addView(llQuestionNumber);
        }

        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        questionLayout.setLayoutParams(p1);

        if (examModel.idQuestionType.equalsIgnoreCase("1")) {
            questionLayout.addView(loadAnswers(examModel.answerChoice));
        } else if (examModel.idQuestionType.equalsIgnoreCase("2")) {
            questionLayout.addView(loadChexkbox(examModel.answerChoice));
        }
        layoutFullQuestion.addView(questionLayout);
    }

    private LinearLayout loadChexkbox(List<AnswerChoiceModel> answerChoiceModels) {

        LinearLayout answerLayout = new LinearLayout(context);
        answerLayout.setOrientation(LinearLayout.VERTICAL);
        final int size = answerChoiceModels.size();
        final CheckBox[] checkBoxes = new CheckBox[size];
        final TextView[] tvSerial = new TextView[size];
        final LinearLayout[] rowLayout = new LinearLayout[size];

        for (int i = 0; i < size; i++) {

            final AnswerChoiceModel answerChoiceModel = answerChoiceModels.get(i);

            rowLayout[i] = new LinearLayout(context);
            rowLayout[i].setOrientation(LinearLayout.HORIZONTAL);
            rowLayout[i].setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            tvSerial[i] = new TextView(context);
            tvSerial[i].setText((i + 1) + ")");
            tvSerial[i].setTextColor(Color.BLACK);
            tvSerial[i].setGravity(Gravity.TOP);
            tvSerial[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            p.setMargins(0, 0, 2, 0);
            tvSerial[i].setLayoutParams(p);

            checkBoxes[i] = new CheckBox(context);
            checkBoxes[i].setId(Integer.valueOf(answerChoiceModel.idAnswerKey));
            checkBoxes[i].setTag(answerChoiceModel);
            checkBoxes[i].setBackgroundResource(R.drawable.selector_checkbox);
            checkBoxes[i].setEnabled(false);
            checkBoxes[i].setClickable(false);

            rowLayout[i] = new LinearLayout(context);
            rowLayout[i].setOrientation(LinearLayout.HORIZONTAL);
            rowLayout[i].setGravity(Gravity.CENTER_VERTICAL);
            checkBoxes[i].setLayoutParams(p);

            rowLayout[i].addView(checkBoxes[i]);

            WebView optionWebView = new WebView(context);
            optionWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            optionWebView.setScrollbarFadingEnabled(true);
            optionWebView.getSettings().setLoadsImagesAutomatically(true);
            optionWebView.getSettings().setJavaScriptEnabled(true);
            optionWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            optionWebView.setWebChromeClient(new WebChromeClient());
            optionWebView.setWebViewClient(new MyWebViewClient());

            optionWebView.loadData(answerChoiceModel.answerChoiceTextHtml, "text/html; charset=UTF-8", null);
            p = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            optionWebView.setLayoutParams(p);
            rowLayout[i].addView(optionWebView);
            answerLayout.addView(rowLayout[i]);
        }
        return answerLayout;
    }


    private LinearLayout loadAnswers(List<AnswerChoiceModel> answerChoiceModels) {

        LinearLayout answerLayout = new LinearLayout(context);
        answerLayout.setOrientation(LinearLayout.VERTICAL);
        final int size = answerChoiceModels.size();
        final RadioButton[] radioButton = new RadioButton[size];
        final TextView[] tvSerial = new TextView[size];
        final LinearLayout[] rowLayout = new LinearLayout[size];

        for (int i = 0; i < size; i++) {

            final AnswerChoiceModel answerChoiceModel = answerChoiceModels.get(i);

            rowLayout[i] = new LinearLayout(context);
            rowLayout[i].setOrientation(LinearLayout.HORIZONTAL);
            rowLayout[i].setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            tvSerial[i] = new TextView(context);
            tvSerial[i].setText((i + 1) + ")");
            tvSerial[i].setTextColor(Color.BLACK);
            tvSerial[i].setGravity(Gravity.TOP);
            tvSerial[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            p.setMargins(0, 0, 2, 0);
            tvSerial[i].setLayoutParams(p);

            radioButton[i] = new RadioButton(context);
            radioButton[i].setId(Integer.valueOf(answerChoiceModel.idAnswerKey));
            radioButton[i].setTag(answerChoiceModel);
            radioButton[i].setBackgroundResource(R.drawable.selector_radio);
            radioButton[i].setEnabled(false);
            radioButton[i].setClickable(false);

            radioButton[i].setLayoutParams(p);

            rowLayout[i].addView(tvSerial[i]);
            rowLayout[i].addView(radioButton[i]);

            WebView optionWebView = new WebView(context);
            optionWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            optionWebView.setScrollbarFadingEnabled(true);
            optionWebView.getSettings().setLoadsImagesAutomatically(true);
            optionWebView.getSettings().setJavaScriptEnabled(true);
            optionWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            optionWebView.setWebChromeClient(new WebChromeClient());
            optionWebView.setWebViewClient(new MyWebViewClient());

            optionWebView.loadData(answerChoiceModel.answerChoiceTextHtml, "text/html; charset=UTF-8", null);
            p = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            optionWebView.setLayoutParams(p);
            rowLayout[i].addView(optionWebView);
            answerLayout.addView(rowLayout[i]);
        }
        return  answerLayout;
    }
}
