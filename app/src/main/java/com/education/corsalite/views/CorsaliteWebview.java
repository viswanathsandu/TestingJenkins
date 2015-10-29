package com.education.corsalite.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.education.corsalite.utils.L;

import java.lang.reflect.Method;

/**
 * Created by vissu on 10/27/15.
 */
public class CorsaliteWebview extends WebView {

    public CorsaliteWebview(Context context) {
        super(context, null);
    }

    public CorsaliteWebview(Context context, AttributeSet attrs) {
        this(context, attrs,
                Resources.getSystem().getIdentifier("webViewStyle", "attr", "android"));
//                com.android.internal.R.attr.webViewStyle);
    }

    public CorsaliteWebview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * It is a relection method which helps in getting the selected text from the webview
     * @return selectedText
     */
    public String getSelectedText() {
        try{
            for(Method m : WebView.class.getDeclaredMethods()){
                if(m.getName().equals("getSelection")){
                    m.setAccessible(true);
                    String text = (String) m.invoke(this, null);
                    L.info("selected text : " + text);
                    return text;
                }
            }
        }
        catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return "";
    }
}
