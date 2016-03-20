package com.education.corsalite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.LoginResponse;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.Encryption;
import com.education.corsalite.utils.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

public class LoginActivity extends AbstractBaseActivity {


    public static final String URL = "URL";
    public static final String TITLE = "title";
    @Bind(R.id.login_btn) Button loginBtn;
    @Bind(R.id.tv_forgot_password) TextView forgotPasswordTxt;
    @Bind(R.id.username_txt) EditText usernameTxt;
    @Bind(R.id.password_txt) EditText passwordTxt;
    @Bind(R.id.build_type_toggle) ToggleButton buildTypeToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setListeners();
        sendAnalytics(getString(R.string.screen_login));
    }

    private void setListeners() {
        buildTypeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    ApiClientService.enableProduction();
                } else {
                    ApiClientService.enableStaging();
                }
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        forgotPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemUtils.isNetworkConnected(LoginActivity.this)) {
                    Intent intent = new Intent(LoginActivity.this, WebviewActivity.class);
                    intent.putExtra(URL, Constants.FORGOT_PASSWORD_URL);
                    intent.putExtra(TITLE, getString(R.string.forgot_password));
                    intent.putExtra("clear_cookies", true);
                    startActivity(intent);
                }else {
                    showToast("Please check your network connection");
                }
            }
        });

        passwordTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                   login();
                }
                return false;
            }
        });
    }

    private void login(final String username, final String password, final boolean fetchLocal) {
        showProgress();
        ApiManager.getInstance(this).login(username, password, new ApiCallback<LoginResponse>(this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                closeProgress();
                if (error != null && !TextUtils.isEmpty(error.message)) {
                    showToast(error.message);
                }
            }

            @Override
            public void success(LoginResponse loginResponse, Response response) {
                super.success(loginResponse, response);
                closeProgress();
                if (loginResponse.isSuccessful()) {
                    ApiCacheHolder.getInstance().setLoginResponse(loginResponse);
                    dbManager.saveReqRes(ApiCacheHolder.getInstance().login);
                    appPref.save("loginId", username);
                    appPref.save("passwordHash", password);
                    onLoginsuccess(loginResponse, fetchLocal);
                } else {
                    showToast(getResources().getString(R.string.login_failed));
                }
            }
        }, fetchLocal);
    }

    private void onLoginsuccess(LoginResponse response, boolean fetchLocal) {
        if(response != null) {
            LoginUserCache.getInstance().setLoginResponse(response);
            if(!fetchLocal) {
                showToast(getResources().getString(R.string.login_successful));
            }
            startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
            finish();
        } else {
            showToast(getResources().getString(R.string.login_failed));
        }
    }

    private boolean checkForValidEmail(){
        if(usernameTxt.getText() != null && !usernameTxt.getText().toString().isEmpty()){

            return android.util.Patterns.EMAIL_ADDRESS.matcher(usernameTxt.getText().toString()).matches();
        }
        return false;
    }

    private boolean checkPasswordField(){
        if(passwordTxt.getText() != null && !passwordTxt.getText().toString().isEmpty()){
            return true;
        }
        return false;
    }

    private void login(){
        if(checkForValidEmail()) {
            if(checkPasswordField()) {
                login(usernameTxt.getText().toString(), Encryption.md5(passwordTxt.getText().toString()), false);
            }else {
                showToast("Please enter password");
            }
        }else {
            showToast("Please enter a valid email id");
        }
    }
}
