package com.education.corsalite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.responsemodels.CorsaliteError;
import com.education.corsalite.responsemodels.LoginResponse;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.Encryption;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

public class LoginActivity extends AbstractBaseActivity {


    private final String URL = "URL";
    @Bind(R.id.login_btn) Button loginBtn;
    @Bind(R.id.tv_forgot_password) TextView forgotPasswordTxt;
    @Bind(R.id.username_txt) EditText usernameTxt;
    @Bind(R.id.password_txt) EditText passwordTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setListeners();
    }

    private void setListeners() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(usernameTxt.getText().toString(), Encryption.md5(passwordTxt.getText().toString()));
            }
        });
        forgotPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, WebviewActivity.class);
                intent.putExtra(URL, Constants.FORGOT_PASSWORD_URL);
                startActivity(intent);
            }
        });
    }

    private void login(String username, String password) {
        ApiManager.getInstance(this).login(username, password, new ApiCallback<LoginResponse>() {
            @Override
            public void failure(CorsaliteError error) {
                showToast(getResources().getString(R.string.login_failed));
            }

            @Override
            public void success(LoginResponse loginResponse, Response response) {
                if(loginResponse.isSuccessful()) {
                    saveSessionCookie(response);
                    showToast(getResources().getString(R.string.login_successful));
                    storeUserCredentials(loginResponse);
                    startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
                    finish();
                } else {
                    showToast(getResources().getString(R.string.login_failed));
                }
            }
        });
    }

    // cache the response
    private void storeUserCredentials(LoginResponse response) {
        LoginUserCache.getInstance().setLoginResponse(response);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
