package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.responsemodels.ExamDetail;
import com.education.corsalite.responsemodels.Message;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mt0060 on 12/09/15.
 */
public class MessageAdapter extends AbstractRecycleViewAdapter {

    LayoutInflater inflater;

    public MessageAdapter(List<Message> messageList, LayoutInflater inflater) {
        this(messageList);
        this.inflater = inflater;
    }

    public MessageAdapter(List<Message> messageList) {
        addAll(messageList);
    }

    @Override
    public MessageDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageDataHolder(inflater.inflate(R.layout.row_messages_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MessageDataHolder) holder).bindData(position, (Message)getItem(position));
    }

    public class MessageDataHolder extends RecyclerView.ViewHolder {

        public MessageDataHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindData(final int position, final Message message) {
        }
    }
}