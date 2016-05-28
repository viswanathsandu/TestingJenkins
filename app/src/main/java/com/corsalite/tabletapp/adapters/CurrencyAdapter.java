package com.corsalite.tabletapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.corsalite.tabletapp.R;
import com.corsalite.tabletapp.models.responsemodels.VirtualCurrencyTransaction;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Girish on 12/09/15.
 */
public class CurrencyAdapter extends AbstractRecycleViewAdapter {

    LayoutInflater inflater;

    public CurrencyAdapter(List<VirtualCurrencyTransaction> virtualCurrencyTransactionList, LayoutInflater inflater) {
        this(virtualCurrencyTransactionList);
        this.inflater = inflater;
    }

    public CurrencyAdapter(List<VirtualCurrencyTransaction> virtualCurrencyTransactionList) {
        addAll(virtualCurrencyTransactionList);
    }

    @Override
    public CurrencyDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  =inflater.inflate(R.layout.row_virtual_currency_list, parent, false) ;
        if((viewType+1)% 2 == 0)
        {
            view.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.tab_recycler_alternate_row));

        }else {
            view.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.white));
        }
        return new CurrencyDataHolder(view);
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((CurrencyDataHolder) holder).bindData(position, (VirtualCurrencyTransaction)getItem(position));
    }

    public class CurrencyDataHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_date) TextView dateTxt;
        @Bind(R.id.tv_event) TextView eventTxt;
        @Bind(R.id.tv_earned) TextView earnedTxt;
        @Bind(R.id.tv_balance) TextView balanceTxt;
        @Bind(R.id.tv_time) TextView timeText;

        View parent;

        public CurrencyDataHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void bindData(final int position, final VirtualCurrencyTransaction virtualCurrencyTransaction) {
            // different color for alternate rows
            if((position+1)% 2 == 0) {
                parent.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.tab_recycler_alternate_row));
            }
            if(virtualCurrencyTransaction.eventDate != null ) {
                String[] data = virtualCurrencyTransaction.eventDate.split(" ");
                if(data != null){
                    if(data[0] != null && data.length > 1)
                        dateTxt.setText(data[0]);
                    if(data[1] != null)
                        timeText.setText(data[1]);
                }else
                    dateTxt.setText(virtualCurrencyTransaction.eventDate);
            }
            eventTxt.setText(virtualCurrencyTransaction.eventName);
            earnedTxt.setText(virtualCurrencyTransaction.earnedVirtualCurrency);
            balanceTxt.setText(virtualCurrencyTransaction.balance);
        }
    }
}