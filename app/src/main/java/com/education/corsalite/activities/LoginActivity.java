package com.education.corsalite.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.requestmodels.LoginUser;
import com.education.corsalite.responsemodels.CorsaliteError;
import com.education.corsalite.responsemodels.LoginResponse;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.Encryption;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.security.MessageDigest;

import retrofit.client.Response;

public class LoginActivity extends AbstractBaseActivity {

    Button loginBtn;
    EditText usernameTxt;
    EditText passwordTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUi();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(usernameTxt.getText().toString(), Encryption.md5(passwordTxt.getText().toString()));
            }
        });
    }

    private void initUi() {
        loginBtn = (Button)findViewById(R.id.login_btn);
        usernameTxt = (EditText) findViewById(R.id.username_txt);
        passwordTxt = (EditText) findViewById(R.id.password_txt);
    }

    private void login(String username, String password) {
        ApiClientService.get() .login(username, password, new ApiCallback<LoginResponse>() {
            @Override
            public void failure(CorsaliteError error) {
                showToast("Login failed");
            }

            @Override
            public void success(LoginResponse loginResponse, Response response) {
                showToast("Logged in successfully");
                startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
                finish();
            }
        });
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
