package com.education.corsalite.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.requestmodels.LogoutModel;
import com.education.corsalite.responsemodels.CorsaliteError;
import com.education.corsalite.responsemodels.LogoutResponse;
import com.education.corsalite.services.ApiClientService;
import com.google.gson.Gson;

import retrofit.client.Response;

/**
 * Created by vissu on 9/11/15.
 */
public abstract class AbstractBaseActivity extends AppCompatActivity {

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        LogoutModel logout = new LogoutModel();
        logout.AuthToken = LoginUserCache.getInstance().getLongResponse().authtoken;
        ApiClientService.get().logout(new Gson().toJson(logout), new ApiCallback<LogoutResponse>() {
            @Override
            public void failure(CorsaliteError error) {
                showToast("Logout failed");
            }

            @Override
            public void success(LogoutResponse logoutResponse, Response response) {
                if(logoutResponse.isSuccessful()) {
                    showToast("Logged out Succesfully");
                    startActivity(new Intent(AbstractBaseActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
    }
}
