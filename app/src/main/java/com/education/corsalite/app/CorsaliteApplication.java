package com.education.corsalite.app;

import android.app.Application;
import android.content.Context;

import com.education.corsalite.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by vissu on 9/18/15.
 */
public class CorsaliteApplication extends Application{

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        loadFonts();
    }

    private void loadFonts() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.roboto_regular))
                .setFontAttrId(R.attr.fontPath)
                .build());
    }
}
