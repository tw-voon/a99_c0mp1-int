package kuchingitsolution.betterpepperboard.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import kuchingitsolution.betterpepperboard.PepperApps;

public class Broadcast_Internet extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            boolean isVisible = PepperApps.isActivityVisible();
            // Check if
            // activity
            // is
            // visible
            // or not
            Log.i("Activity is Visible ", "Is activity visible : " + isVisible);

            // If it is visible then trigger the task else do nothing
            if (isVisible) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager
                        .getActiveNetworkInfo();

                // Check internet connection and accrding to state change the
                // text of activity by calling method
                if (networkInfo != null && networkInfo.isConnected()) {
                    PepperApps.ActiveConnection();
                } else {
                    PepperApps.notActiveConnection();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
