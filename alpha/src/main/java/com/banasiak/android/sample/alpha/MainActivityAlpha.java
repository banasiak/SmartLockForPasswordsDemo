package com.banasiak.android.sample.alpha;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.banasiak.android.sample.authenticator.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivityAlpha extends AppCompatActivity {

    @BindView(R.id.username)
    EditText username;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.output)
    TextView output;

    AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alpha);

        ButterKnife.bind(this);

        accountManager = AccountManager.get(this);

    }

    @OnClick(R.id.add_account)
    void onAddButtonClick() {
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();
        Account account = new Account(username, Constants.ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, password, null);
    }

    @OnClick(R.id.get_account)
    @AfterPermissionGranted(Constants.RC_ACCOUNTS_PERM)
    void onGetButtonClick() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            @SuppressWarnings("MissingPermission")
            Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
            if (accounts.length > 0) {
                getAccountInfo(accounts);
            } else {
                output.setText("No accounts found.");
            }
        } else {
            EasyPermissions.requestPermissions(this, "Please grant access to Account Manager", Constants.RC_ACCOUNTS_PERM, Manifest.permission.GET_ACCOUNTS);
        }

    }

    private void getAccountInfo(Account[] accounts) {
        String string = "";
        for (Account account : accounts) {
            String token = accountManager.getPassword(account);
            string = string + account.name + " / " + token + "\n";
        }
        output.setText(string);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
