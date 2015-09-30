package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.Message;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Girish on 12/09/15.
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
        ((MessageDataHolder) holder).bindData(position, (Message) getItem(position));
    }

    public class MessageDataHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_message) TextView messageTxt;

        View parent;

        public MessageDataHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void bindData(final int position, final Message message) {
            if((position+1)% 2 == 0) {
                parent.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.tab_recycler_alternate_row));
            }
            messageTxt.setText(message.message);
        }
    }
}