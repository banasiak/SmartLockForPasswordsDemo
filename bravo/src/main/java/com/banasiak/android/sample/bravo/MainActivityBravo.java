package com.banasiak.android.sample.bravo;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivityBravo extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivityBravo.class.getSimpleName();

    private static final String IDENTITY_PROVIDER = "https://api.banasiak.com";

    private static final int RC_SAVE_CREDENTIAL = 123;

    @BindView(R.id.username)
    EditText username;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.output)
    TextView output;

    private AccountManager accountManager;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bravo);

        ButterKnife.bind(this);

        accountManager = AccountManager.get(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @OnClick(R.id.add_account)
    void onAddButtonClick() {
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();

        Credential credential = new Credential.Builder(username)
                .setName(username)
                .setPassword(password)
                .build();

        Auth.CredentialsApi.save(googleApiClient, credential).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    Toast.makeText(MainActivityBravo.this, "credentials saved", Toast.LENGTH_SHORT).show();
                } else {
                    if (status.hasResolution()) {
                        try {
                            // try to resolve the save request and prompt the user if the credential is new
                            status.startResolutionForResult(MainActivityBravo.this, RC_SAVE_CREDENTIAL);
                        } catch (IntentSender.SendIntentException e) {
                            Toast.makeText(MainActivityBravo.this, "save failed with resolution exception :(", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivityBravo.this, "saved failed with no resolution :(", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @OnClick(R.id.get_account)
    void onGetButtonClick() {
        CredentialRequest credentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .setAccountTypes(IDENTITY_PROVIDER)
                .build();

        Auth.CredentialsApi.request(googleApiClient, credentialRequest).setResultCallback(
                new ResultCallback<CredentialRequestResult>() {
                    @Override
                    public void onResult(@NonNull CredentialRequestResult credentialRequestResult) {
                        if (credentialRequestResult.getStatus().isSuccess()) {
                            Credential credential = credentialRequestResult.getCredential();
                            String username = credential.getName();
                            String password = credential.getPassword();
                            output.setText(username + " / " + password);
                        } else {
                            output.setText("no credentials found...");
                        }
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SAVE_CREDENTIAL) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "credentials saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "save canceled by user :(", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: Google API Client connected");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: Google API Client suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: Google API Client connection failed: " + connectionResult.getErrorMessage());
    }
}
