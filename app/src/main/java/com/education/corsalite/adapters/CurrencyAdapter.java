package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.UserProfileActivity;
import com.education.corsalite.models.responsemodels.VirtualCurrencyTransaction;

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
        return new CurrencyDataHolder(inflater.inflate(R.layout.row_virtual_currency_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((CurrencyDataHolder) holder).bindData(position, (VirtualCurrencyTransaction)getItem(position));
    }

    public class CurrencyDataHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_date) TextView dateTxt;
        @Bind(R.id.tv_time) TextView timeTxt;
        @Bind(R.id.tv_event) TextView eventTxt;
        @Bind(R.id.tv_earned) TextView earnedTxt;
        @Bind(R.id.tv_balance) TextView balanceTxt;

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
            if(virtualCurrencyTransaction.eventDate != null && virtualCurrencyTransaction.eventDate.contains(" ")) {
                dateTxt.setText(virtualCurrencyTransaction.eventDate.split(" ")[0]);
                timeTxt.setText(virtualCurrencyTransaction.eventDate.split(" ")[1]);
            } else {
                dateTxt.setText(virtualCurrencyTransaction.eventDate);
                timeTxt.setText("");
            }
            eventTxt.setText(virtualCurrencyTransaction.eventName);
            earnedTxt.setText(virtualCurrencyTransaction.earnedVirtualCurrency);
            balanceTxt.setText(UserProfileActivity.BALANCE_CURRENCY);
        }
    }
}