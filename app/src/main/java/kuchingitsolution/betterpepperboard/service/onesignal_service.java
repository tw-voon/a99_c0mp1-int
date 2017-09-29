package kuchingitsolution.betterpepperboard.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.onesignal.OneSignal;

import kuchingitsolution.betterpepperboard.helper.Session;

public class onesignal_service extends Service {
    Session session;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        session = new Session(getApplicationContext());
        OneSignal.sendTag("user_id", session.getUserID());
        Log.d("one", session.getUserID());
        return super.onStartCommand(intent, flags, startId);
    }
}
