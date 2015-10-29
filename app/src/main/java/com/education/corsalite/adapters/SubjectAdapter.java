package com.education.corsalite.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.SubjectModel;

import java.util.List;

/**
 * Created by Girish on 03/10/15.
 */
public class SubjectAdapter extends BaseAdapter {

    List<SubjectModel> subjectModelList;
    Context mContext;
    LayoutInflater inflater;

    public SubjectAdapter(List<SubjectModel> subjectModelList, Context mContext) {
        this.subjectModelList = subjectModelList;
        this.mContext = mContext;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(subjectModelList != null) {
            return subjectModelList.size();
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
        tv.setText(subjectModelList.get(position).subjectName);
        return convertView;
    }
}
