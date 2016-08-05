package com.education.corsalite.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.enums.QuestionType;
import com.education.corsalite.models.responsemodels.AnswerChoiceModel;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.utils.L;
import com.education.corsalite.views.CorsaliteWebViewClient;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Viswanath
 */
public class FullQuestionsAdapter extends AbstractRecycleViewAdapter {

    private LayoutInflater inflater;
    private Context mContext;

    public FullQuestionsAdapter(List<ExamModel> questions) {
        addAll(questions);
    }

    public FullQuestionsAdapter(Context context, List<ExamModel> questions, LayoutInflater inflater) {
        this(questions);
        this.mContext = context;
        this.inflater = inflater;
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.question_recyclerview_item, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            ((QuestionViewHolder)holder).loadQuestion(position);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {

        public View parent;
        @Bind(R.id.tv_serial_no) TextView questionNumberTxt;
        @Bind(R.id.question_webview) WebView questionWebview;
        @Bind(R.id.paragraph_webview) WebView paragraphWebview;
        @Bind(R.id.answer_layout) LinearLayout answersContainer;

        public QuestionViewHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void loadQuestion(int position) {
            try {
                ExamModel question = (ExamModel) getItem(position);
                questionNumberTxt.setText("Q" + (position + 1) + ")");
                if (TextUtils.isEmpty(question.paragraphHtml)) {
                    paragraphWebview.setVisibility(View.GONE);
                } else {
                    paragraphWebview.setVisibility(View.VISIBLE);
                    paragraphWebview.loadData(question.paragraphHtml, "text/html; charset=UTF-8", null);
                }
                questionWebview.setVisibility(View.GONE);
                if (question.questionHtml != null) {
                    questionWebview.loadData(question.questionHtml, "text/html; charset=UTF-8", null);
                    questionWebview.setVisibility(View.VISIBLE);
                }
                switch (QuestionType.getQuestionType(question.idQuestionType)) {
                    case SINGLE_SELECT_CHOICE:
                        loadSingleSelectChoiceAnswers(question);
                        break;
                    case MULTI_SELECT_CHOICE:
//                        loadMultiSelectChoiceAnswers(position);
                        break;
                    case ALPHANUMERIC:
//                        loadEditTextAnswer(QuestionType.ALPHANUMERIC, position);
                        break;
                    case NUMERIC:
//                        loadEditTextAnswer(QuestionType.NUMERIC, position);
                        break;
                    case FILL_IN_THE_BLANK:
//                        loadEditTextAnswer(QuestionType.FILL_IN_THE_BLANK, position);
                        break;
                    case N_BLANK_SINGLE_SELECT:
//                        loadNBlankAnswer(QuestionType.N_BLANK_SINGLE_SELECT, position);
                        break;
                    case N_BLANK_MULTI_SELECT:
//                        loadNBlankAnswer(QuestionType.N_BLANK_MULTI_SELECT, position);
                        break;
                    case GRID:
//                        loadGridTypeAnswer(position);
                        break;
                    case FRACTION:
                    case PICK_A_SENTENCE:
                    case WORD_PROPERTIES:
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                L.error(e.getMessage(), e);
            }
        }

        private void loadSingleSelectChoiceAnswers(ExamModel question) {

            answersContainer.removeAllViews();
            answersContainer.setOrientation(LinearLayout.VERTICAL);
            List<AnswerChoiceModel> answerChoiceModels = question.answerChoice;
            final int size = answerChoiceModels.size();
            final RadioButton[] optionRadioButtons = new RadioButton[size];
            for (int i = 0; i < size; i++) {
                final AnswerChoiceModel answerChoiceModel = answerChoiceModels.get(i);
                LinearLayout container = (LinearLayout) inflater.inflate(R.layout.exam_engine_radio_btn, null);
                TextView optionNumberTxt = (TextView) container.findViewById(R.id.option_number_txt);
                optionNumberTxt.setText((i + 1) + "");
                final RadioButton optionRBtn = (RadioButton) container.findViewById(R.id.option_radio_button);
                optionRBtn.setId(Integer.valueOf(answerChoiceModel.idAnswerKey));
                optionRBtn.setTag(answerChoiceModel);
                optionRadioButtons[i] = optionRBtn;
                WebView webview = (WebView) container.findViewById(R.id.webview);
                webview.setScrollbarFadingEnabled(true);
                webview.getSettings().setLoadsImagesAutomatically(true);
                webview.getSettings().setJavaScriptEnabled(true);
                webview.setWebChromeClient(new WebChromeClient());
                webview.setWebViewClient(new MyWebViewClient(mContext));
                webview.loadData(answerChoiceModel.answerChoiceTextHtml, "text/html; charset=UTF-8", null);
                answersContainer.addView(container);
            }
        }
    }

    private class MyWebViewClient extends CorsaliteWebViewClient {
        public MyWebViewClient(Context context) {
            super(context);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (checkNetconnection(view, url)) {
                return true;
            }
            if (Uri.parse(url).getHost().contains("corsalite.com")) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mContext.startActivity(intent);
            return true;
        }
    }

}