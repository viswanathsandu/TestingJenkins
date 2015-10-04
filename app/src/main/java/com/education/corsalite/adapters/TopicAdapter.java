package com.education.corsalite.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.TopicModel;

import java.util.List;

/**
 * Created by Girish on 03/10/15.
 */
public class TopicAdapter extends BaseAdapter {

    List<TopicModel> topicModelList;
    Context mContext;
    LayoutInflater inflater;

    public TopicAdapter(List<TopicModel> topicModelList, Context mContext) {
        this.topicModelList = topicModelList;
        this.mContext = mContext;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(topicModelList != null) {
            return topicModelList.size();
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
        tv.setText(topicModelList.get(position).topicName);
        return convertView;
    }
}
