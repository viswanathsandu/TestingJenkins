package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.education.corsalite.R;

/**
 * Created by GIRISH on 13/09/15.
 */
public class VirtualCurrencyActivity extends AbstractBaseActivity {

    private Button redeemBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_currency, null);
        frameLayout.addView(myView);
        setToolbarForVirtualCurrency();
        initToolbar();
    }

    protected void initToolbar() {
        redeemBtn = (Button) toolbar.findViewById(R.id.redeem_btn);
        redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeem();
            }
        });
    }
}
