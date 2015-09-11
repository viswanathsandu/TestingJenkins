package com.education.corsalite.activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.fragments.UserProfileDetailsFragment;
import com.education.corsalite.responsemodels.CorsaliteError;
import com.education.corsalite.responsemodels.UserProfileResponse;
import com.education.corsalite.responsemodels.VirtualCurrencyBalanceResponse;
import com.education.corsalite.services.ApiClientService;

import retrofit.client.Response;

public class UserProfileActivity extends AbstractBaseActivity implements UserProfileDetailsFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        fetchUserProfileData();
        fetchVirtualCurrencyBalance();
    }

    private void fetchUserProfileData() {
        ApiClientService.get().getUserProfile(LoginUserCache.getInstance().loginResponse.studentId,
                new ApiCallback<UserProfileResponse>() {
                    @Override
                    public void failure(CorsaliteError error) {

                    }

                    @Override
                    public void success(UserProfileResponse userProfileResponse, Response response) {
                        if (userProfileResponse.isSuccessful()) {
                            showToast("User Profile fetched successfully..." + userProfileResponse.basicProfile.displayName);
                            // TODO : show data on UI

                        } else {
                            showToast("Failed to fetch user profile information");
                        }
                    }
                });
    }

    private void fetchVirtualCurrencyBalance() {
        ApiClientService.get().getVirtualCurrencyBalance(LoginUserCache.getInstance().loginResponse.studentId,
            new ApiCallback<VirtualCurrencyBalanceResponse>() {
                @Override
                public void failure(CorsaliteError error) {

                }

                @Override
                public void success(VirtualCurrencyBalanceResponse virtualCurrencyBalanceResponse, Response response) {
                    if (virtualCurrencyBalanceResponse.isSuccessful()) {
                        showToast("virtual currency fetched successfully..." + virtualCurrencyBalanceResponse.balance);
                        // TODO : show data on UI

                    } else {
                        showToast("Failed to fetch virtual currency information");
                    }
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
