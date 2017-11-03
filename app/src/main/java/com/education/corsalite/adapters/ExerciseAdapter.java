package com.education.corsalite.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.ExamModel;

import java.util.List;

/**
 * Created by Girish on 03/11/15.
 */
public class ExerciseAdapter extends BaseAdapter {

    List<ExamModel> examModelList;
    Context mContext;
    LayoutInflater inflater;

    public ExerciseAdapter(List<ExamModel> examModelList, Context mContext) {
        this.examModelList = examModelList;
        this.mContext = mContext;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (examModelList != null) {
            return examModelList.size() + 1;
        }
        return 0;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.row_spinner_text, parent, false);
        TextView tv = (TextView) convertView.findViewById(R.id.tv_spn);
        tv.setText(position == 0 ? "Exercise" : examModelList.get(position - 1).displayName);
        return convertView;
    }
}

