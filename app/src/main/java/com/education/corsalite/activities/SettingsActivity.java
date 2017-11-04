package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.corsalite.BuildConfig;
import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.ClientEntityAppConfig;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.LoginResponse;
import com.education.corsalite.utils.L;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.client.Response;

public class SettingsActivity extends AbstractBaseActivity {

    @Bind(R.id.app_up_to_date_txt)
    TextView upToDateTxt;
    @Bind(R.id.app_current_version_txt)
    TextView currentVersionTxt;
    @Bind(R.id.app_latest_version_txt)
    TextView latestVersionTxt;
    @Bind(R.id.app_upgrade_btn)
    Button appUpgradeButton;
    @Bind(R.id.app_current_version_layout)
    LinearLayout currentVersionLayout;
    @Bind(R.id.app_latest_version_layout)
    LinearLayout latestVersionLayout;

    private ClientEntityAppConfig cliententityconfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_settings, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this, myView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestClientEntityConfig();
    }

    @Override
    protected void requestClientEntityConfig() {
        LoginResponse loginResponse = LoginUserCache.getInstance().getLoginResponse();
        if (loginResponse != null && !TextUtils.isEmpty(loginResponse.idUser) && !TextUtils.isEmpty(loginResponse.entitiyId)) {
            showProgress();
            ApiManager.getInstance(this).getClientEntityAppConfig(loginResponse.idUser, loginResponse.entitiyId,
                    new ApiCallback<ClientEntityAppConfig>(this) {
                        @Override
                        public void success(ClientEntityAppConfig clientEntityAppConfig, Response response) {
                            super.success(clientEntityAppConfig, response);
                            closeProgress();
                            showData(clientEntityAppConfig);
                            SettingsActivity.this.cliententityconfig = clientEntityAppConfig;
                        }

                        @Override
                        public void failure(CorsaliteError error) {
                            super.failure(error);
                            closeProgress();
                            showToast("something went wrong");
                            finish();
                        }
                    });
        }
    }

    private void showData(ClientEntityAppConfig clientEntityAppConfig) {
        try {
            if (clientEntityAppConfig != null) {
                currentVersionTxt.setText(BuildConfig.VERSION_NAME);
                int latestAppVersion;
                if (TextUtils.isEmpty(clientEntityAppConfig.getAppVersionNumber())) {
                    latestAppVersion = Integer.parseInt(clientEntityAppConfig.getAppVersionNumber());
                } else {
                    latestAppVersion = AbstractBaseActivity.appConfig.getLatestVersionCode();
                }
                if (latestAppVersion > BuildConfig.VERSION_CODE) {
                    latestVersionLayout.setVisibility(View.VISIBLE);
                    latestVersionTxt.setText(clientEntityAppConfig.getAppVersionName());
                    upToDateTxt.setVisibility(View.GONE);
                    appUpgradeButton.setEnabled(true);
                } else {
                    latestVersionLayout.setVisibility(View.GONE);
                    upToDateTxt.setVisibility(View.VISIBLE);
                    appUpgradeButton.setEnabled(false);
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    @OnClick(R.id.app_upgrade_btn)
    public void onUpgradeClicked() {
        if (cliententityconfig != null) {
            if (cliententityconfig.isAppFromUnknownSources()) {
                updateAppFromThirdParty(cliententityconfig.getUpdateUrl());
            } else {
                updateAppFromPlayStore();
            }
        }
    }
}

