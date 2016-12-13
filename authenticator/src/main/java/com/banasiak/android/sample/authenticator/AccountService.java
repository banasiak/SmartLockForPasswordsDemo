package com.banasiak.android.sample.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Service to handle Account authentication. It instantiates the authenticator
 * and returns its IBinder.
 */
public class AccountService extends Service {

    private static final String TAG = "AuthenticationService";
    private AccountAuthenticator authenticator;

    @Override
    public void onCreate() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "SampleSyncAdapter Authentication Service started.");
        }
        authenticator = new AccountAuthenticator(this);
    }

    @Override
    public void onDestroy() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "SampleSyncAdapter Authentication Service stopped.");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "getBinder()...  returning the AccountAuthenticator binder for intent "
                    + intent);
        }
        return authenticator.getIBinder();
    }
}
