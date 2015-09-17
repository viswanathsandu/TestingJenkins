package com.education.corsalite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.requestmodels.LogoutModel;
import com.education.corsalite.responsemodels.CorsaliteError;
import com.education.corsalite.responsemodels.LogoutResponse;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.CookieUtils;
import com.google.gson.Gson;

import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by vissu on 9/11/15.
 */
public abstract class AbstractBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        LogoutModel logout = new LogoutModel();
        logout.AuthToken = LoginUserCache.getInstance().getLongResponse().authtoken;
        ApiManager.getInstance(this).logout(new Gson().toJson(logout), new ApiCallback<LogoutResponse>() {
            @Override
            public void failure(CorsaliteError error) {
                showToast(getResources().getString(R.string.logout_failed));
            }

            @Override
            public void success(LogoutResponse logoutResponse, Response response) {
                if (logoutResponse.isSuccessful()) {
                    showToast(getResources().getString(R.string.logout_successful));
                    startActivity(new Intent(AbstractBaseActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
    }

    public void saveSessionCookie(Response response) {
        String cookie = CookieUtils.getCookieString(response);
        if (cookie != null) {
            ApiClientService.setSetCookie(cookie);
        }
    }
}
