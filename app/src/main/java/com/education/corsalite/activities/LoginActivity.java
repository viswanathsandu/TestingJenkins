package com.education.corsalite.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
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
import com.education.corsalite.models.responsemodels.ClientEntityAppConfig;
import com.education.corsalite.models.responsemodels.CommonResponseModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.LoginResponse;
import com.education.corsalite.security.Encrypter;
import com.education.corsalite.utils.SystemUtils;
import com.education.corsalite.utils.WebUrls;

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
                if (SystemUtils.isNetworkConnected(LoginActivity.this)) {
                    Intent intent = new Intent(LoginActivity.this, WebviewActivity.class);
                    intent.putExtra(URL, WebUrls.getForgotPasswordUrl());
                    intent.putExtra(TITLE, getString(R.string.forgot_password));
                    intent.putExtra("clear_cookies", true);
                    startActivity(intent);
                } else {
                    showToast("Please check your network connection");
                }
            }
        });

        passwordTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                }
                return false;
            }
        });

        passwordTxt.setTag("N");
        passwordTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() > (passwordTxt.getRight() - passwordTxt
                            .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds()
                            .width())) {

                        String showing = passwordTxt.getTag().toString();

                        if(showing.equalsIgnoreCase("Y")){
                            passwordTxt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordTxt.setTag("N");
                        }else{
                            passwordTxt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordTxt.setTag("Y");
                        }
                        return false;
                    }
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
                    setCrashlyticsUserData();
                    ApiCacheHolder.getInstance().setLoginResponse(loginResponse);
                    dbManager.saveReqRes(ApiCacheHolder.getInstance().login);
                    appPref.save("loginId", username);
                    appPref.save("passwordHash", password);
                    appPref.setUserId(loginResponse.idUser);
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
            startWebSocket();
            loadAppConfig();
            if(!fetchLocal) {
                showToast(getResources().getString(R.string.login_successful));
            }
            requestClientEntityConfig(response);
        } else {
            showToast(getResources().getString(R.string.login_failed));
        }
    }

    private void requestClientEntityConfig(LoginResponse loginResponse) {
        if(loginResponse != null && !TextUtils.isEmpty(loginResponse.idUser) && !TextUtils.isEmpty(loginResponse.entitiyId)) {
            ApiManager.getInstance(this).getClientEntityAppConfig(loginResponse.idUser, loginResponse.entitiyId,
                    new ApiCallback<ClientEntityAppConfig>(this) {
                        @Override
                        public void success(ClientEntityAppConfig clientEntityAppConfig, Response response) {
                            super.success(clientEntityAppConfig, response);
                            checkUserDeviceValidity(clientEntityAppConfig);
                        }

                        @Override
                        public void failure(CorsaliteError error) {
                            super.failure(error);
                            showToast(getResources().getString(R.string.login_failed));
                        }
                    });
        }
    }

    private void checkUserDeviceValidity(ClientEntityAppConfig config) {
        if(config != null) {
            if (TextUtils.isEmpty(config.deviceId)) {
                postClientEntityConfig(config.idUser);
            } else if(config.deviceId.equalsIgnoreCase(SystemUtils.getImeiNumber(this))) {
                navigateToWelcomeScreen();
            } else {
                showDeviceAffinityAlert();
            }
        } else {
            navigateToWelcomeScreen();
        }
    }

    private void showDeviceAffinityAlert() {
        new AlertDialog.Builder(this)
            .setTitle("Login Failure")
            .setMessage("Please Login from your assigned device”.")
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    logout();
                }
            }).show();
    }

    private void postClientEntityConfig(String userId) {
        ApiManager.getInstance(this).postClientEntityAppConfig(userId, SystemUtils.getImeiNumber(this),
                new ApiCallback<CommonResponseModel>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        showToast(getResources().getString(R.string.login_failed));
                    }

                    @Override
                    public void success(CommonResponseModel commonResponseModel, Response response) {
                        super.success(commonResponseModel, response);
                        navigateToWelcomeScreen();
                    }
                });
    }

    private void navigateToWelcomeScreen() {
        startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
        finish();
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
                login(usernameTxt.getText().toString(), Encrypter.md5(passwordTxt.getText().toString()), false);
            }else {
                showToast("Please enter password");
            }
        }else {
            showToast("Please enter a valid email id");
        }
    }
}
