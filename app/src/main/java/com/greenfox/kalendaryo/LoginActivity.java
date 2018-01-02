package com.greenfox.kalendaryo;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.calendar.model.AclRule;
import com.greenfox.kalendaryo.httpconnection.ApiService;
import com.greenfox.kalendaryo.httpconnection.RetrofitClient;
import com.greenfox.kalendaryo.models.KalAuth;
import com.greenfox.kalendaryo.models.KalUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.accounts.AccountManager.newChooseAccountIntent;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private SignInButton signIn;
    private Button choose;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 900;
    private static final int REQUEST_ACCOUNT_PICKER = 500;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private GoogleSignInAccount account;
    private String googleAccountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signIn = findViewById(R.id.bn_login);
        choose = findViewById(R.id.choose_account);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfLoggedInAndSignIn();
            }
        });
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseAccount();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void checkIfLoggedInAndSignIn() {
        if(googleApiClient == null) {
            GoogleSignInOptions signInOptions = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(new Scope("https://www.googleapis.com/auth/calendar"))
                    .setAccountName(googleAccountName)
                    .requestEmail()
                    .requestIdToken("141350348735-p37itsqvg8599ebc3j9cr1eur0n0d1iv.apps.googleusercontent.com")
                    .requestServerAuthCode("141350348735-p37itsqvg8599ebc3j9cr1eur0n0d1iv.apps.googleusercontent.com")
                    .build();
            googleApiClient = new GoogleApiClient
                    .Builder(this)
                    .enableAutoManage(this,this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                    .build();
        }
        signIn();
    }

    private void chooseAccount() {
        startActivityForResult(newChooseAccountIntent(null, null, new String[]{"com.google"},
                false, null, null, null, null), REQUEST_ACCOUNT_PICKER);
    }

    public void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleResult(result);
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
                    googleAccountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    if (googleAccountName != null) {
                        TextView newaccountName = findViewById(R.id.new_accountname);
                        newaccountName.setText(googleAccountName);
                    }
                }
                break;
        }
    }

    public void handleResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            account = result.getSignInAccount();
            final String userName = account.getDisplayName();
            final String userEmail = account.getEmail();
            ApiService apiService = RetrofitClient.getApi();
            apiService.getAccessToken(new KalAuth(account.getServerAuthCode(), userEmail, userName)).enqueue(new Callback<KalUser>() {
                @Override
                public void onResponse(Call<KalUser> call, Response<KalUser> response) {
                    String accessToken = response.body().getAccessToken();
                    editSharedPref(userEmail, userName, accessToken);
                    Log.d("shared", sharedPref.getString("email", ""));
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }

                @Override
                public void onFailure(Call<KalUser> call, Throwable t) {
                    t.printStackTrace();
                }
            });
            Toast.makeText(this, "Saved!", Toast.LENGTH_LONG).show();
        }
    }

    private void editSharedPref(String email, String userName, String token) {
        sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("email", email);
        editor.putString("username", userName);
        editor.putString("token", token);
        editor.apply();
    }
}
