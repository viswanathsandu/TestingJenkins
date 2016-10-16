package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.Message;
import com.education.corsalite.utils.L;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Girish on 12/09/15.
 */
public class MessageAdapter extends AbstractRecycleViewAdapter {

    LayoutInflater inflater;

    public MessageAdapter(List<Message> messages, LayoutInflater inflater) {
        this(messages);
        this.inflater = inflater;
    }

    public MessageAdapter(List<Message> messages) {
        addAll(messages);
    }

    public void loadData(List<Message> messages) {
        setData(messages);
        notifyDataSetChanged();
    }

    @Override
    public MessageDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  =inflater.inflate(R.layout.row_messages_list, parent, false) ;
        if((viewType+1)% 2 == 0) {
            view.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.tab_recycler_alternate_row));
        }else {
            view.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.white));
        }
        return new MessageDataHolder(view);
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            ((MessageDataHolder) holder).bindData(position, (Message)getItem(position));
        } catch (Exception e) {
            L.error("Handle it carefully", e);
        }
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
            messageTxt.setText(Html.fromHtml(message.message));
        }
    }
}