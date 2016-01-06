package com.education.corsalite.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.ExerciseModel;

import java.util.List;

/**
 * Created by Girish on 03/11/15.
 */
public class ExerciseAdapter extends BaseAdapter {

    List<ExerciseModel> exerciseModelList;
    Context mContext;
    LayoutInflater inflater;

    public ExerciseAdapter(List<ExerciseModel> exerciseModelList, Context mContext) {
        this.exerciseModelList = exerciseModelList;
        this.mContext = mContext;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(exerciseModelList != null) {
            return exerciseModelList.size() + 1;
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
        TextView tv = (TextView)convertView.findViewById(R.id.tv_spn);
        tv.setText(position == 0 ? "Exercise" : exerciseModelList.get(position - 1).displayName);
        return convertView;
    }
}

