package com.education.corsalite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.LoginResponse;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.Encryption;

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

                login();
            }
        });
        forgotPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, WebviewActivity.class);
                intent.putExtra(URL, Constants.FORGOT_PASSWORD_URL);
                intent.putExtra(TITLE, getString(R.string.forgot_password));
                intent.putExtra("clear_cookies", true);
                startActivity(intent);
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

    private void login(String username, String password) {
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
                    onLoginsuccess(loginResponse);
                } else {
                    showToast(getResources().getString(R.string.login_failed));
                }
            }
        });
    }

    private void onLoginsuccess(LoginResponse response) {
        if(response != null) {
            LoginUserCache.getInstance().setLoginResponse(response);
            showToast(getResources().getString(R.string.login_successful));
            startActivity(new Intent(LoginActivity.this, StudyCentreActivity.class));
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
                login(usernameTxt.getText().toString(), Encryption.md5(passwordTxt.getText().toString()));
            }else {
                showToast("Please enter password");
            }
        }else {
            showToast("Please enter a valid email id");
        }
    }
}
