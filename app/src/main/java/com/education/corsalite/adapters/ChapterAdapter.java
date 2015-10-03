package com.education.corsalite.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.ChapterModel;

import java.util.List;

/**
 * Created by Girish on 03/10/15.
 */
public class ChapterAdapter extends BaseAdapter {

    List<ChapterModel> chapterModelList;
    Context mContext;
    LayoutInflater inflater;

    public ChapterAdapter(List<ChapterModel> chapterModelList, Context mContext) {
        this.chapterModelList = chapterModelList;
        this.mContext = mContext;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        if(chapterModelList != null) {
            return chapterModelList.size();
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
        tv.setText(chapterModelList.get(position).chapterName);
        return convertView;
    }
}