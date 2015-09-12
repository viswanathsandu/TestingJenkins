package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.responsemodels.ExamDetail;
import com.education.corsalite.responsemodels.VirtualCurrencyTransaction;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mt0060 on 12/09/15.
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
    public ExamDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExamDataHolder(inflater.inflate(R.layout.row_virtual_currency_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ExamDataHolder) holder).bindData(position, (VirtualCurrencyTransaction)getItem(position));
    }

    public class ExamDataHolder extends RecyclerView.ViewHolder {

        public ExamDataHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindData(final int position, final VirtualCurrencyTransaction virtualCurrencyTransaction) {

        }
    }
}