package com.corsalite.tabletapp.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * Created by vissu on 5/1/16.
 */
public class CustomButton extends Button implements View.OnClickListener {

    private OnClickListener onClickListener;
    private boolean isClicked = false;

    public CustomButton(Context context) {
        super(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(this);
        onClickListener = l;
    }

    @Override
    public void onClick(View v) {
        if(!isClicked) {
            if (onClickListener != null) {
                onClickListener.onClick(v);
                isClicked = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isClicked = false;
                    }
                }, 2000);
            }
        }
    }

    @Override
    public boolean callOnClick() {
        if(onClickListener != null) {
            onClick(this);
            return true;
        }
        return false;
    }
}
