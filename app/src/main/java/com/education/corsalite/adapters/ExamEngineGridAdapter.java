package com.education.corsalite.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.ExamEngineActivity;
import com.education.corsalite.models.responsemodels.ExamModel;

import java.util.List;

/**
 * Created by vissu on 1/24/16.
 */
public class ExamEngineGridAdapter extends BaseAdapter {

    private ExamEngineActivity mActivity;
    private View grid;
    private LayoutInflater inflater;
    private List<ExamModel> mModelList;
    private String selectedSectionName;

    public ExamEngineGridAdapter(ExamEngineActivity activity, List<ExamModel> modelList) {
        this.mActivity = activity;
        this.mModelList = modelList;
        inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setSelectedSectionName(String sectionName) {
        this.selectedSectionName = sectionName;
    }

    @Override
    public int getCount() {
        return (mModelList == null) ? 0 : mModelList.size();
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
        if(!mModelList.get(position).sectionName.equals(selectedSectionName)) {
            btnCounter.setEnabled(false);
            btnCounter.setClickable(false);
            btnCounter.setBackgroundResource(R.drawable.rounded_corners_gray);
            btnCounter.setTextColor(mActivity.getResources().getColor(R.color.dark_gray));
        } else {
            switch (mModelList.get(position).answerColorSelection) {
                case ANSWERED:
                    btnCounter.setBackgroundResource(R.drawable.rounded_corners_green);
                    btnCounter.setTextColor(mActivity.getResources().getColor(R.color.white));
                    break;

                case SKIPPED:
                    btnCounter.setBackgroundResource(R.drawable.rounded_corners_red);
                    btnCounter.setTextColor(mActivity.getResources().getColor(R.color.white));
                    break;

                case FLAGGED:
                    btnCounter.setBackgroundResource(R.drawable.rounded_corners_yellow);
                    btnCounter.setTextColor(mActivity.getResources().getColor(R.color.black));
                    break;

                case UNATTEMPTED:
                    btnCounter.setBackgroundResource(R.drawable.rounded_corners_gray);
                    btnCounter.setTextColor(mActivity.getResources().getColor(R.color.black));
                    break;
            }

            if (position == mActivity.selectedPosition) {
                btnCounter.setPaintFlags(btnCounter.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                mActivity.previousQuestionPosition = position;
            } else if ((btnCounter.getPaintFlags() & Paint.UNDERLINE_TEXT_FLAG) > 0) {
                btnCounter.setPaintFlags(btnCounter.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.inflateUI(position);
                }
            });
        }
        return v;
    }
}
