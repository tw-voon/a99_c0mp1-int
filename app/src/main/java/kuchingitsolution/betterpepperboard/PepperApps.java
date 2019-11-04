package kuchingitsolution.betterpepperboard;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import kuchingitsolution.betterpepperboard.helper.Session;

public class PepperApps extends Application{

    private static PepperApps pepperApps;
    private RequestQueue mRequestQueue;
    private Context mContext;
    private Session session;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pepperApps = this;
        mContext = getApplicationContext();
        session = new Session(this);
    }

    public PepperApps(Context context){
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public PepperApps(){}

    public static synchronized PepperApps getInstance(Context context) {
        if (pepperApps == null) {
            pepperApps = new PepperApps(context);
        }
        return pepperApps;
    }

    public RequestQueue getRequestQueue(){
        // If RequestQueue is null the initialize new RequestQueue
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        // Return RequestQueue
        return mRequestQueue;
    }

    public<T> void addToRequestQueue(Request<T> request, String tag){
        // Add the specified request to the request queue
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    // Gloabl declaration of variable to use in whole app

    public static boolean activityVisible; // Variable that will check the
    public static boolean activeConnection;
    // current activity state

    public static boolean isActivityVisible() {
        return activityVisible; // return true or false
    }

    public static void activityResumed() {
        activityVisible = true;// this will set true when activity resumed

    }

    public static void activityPaused() {
        activityVisible = false;// this will set false when activity paused
    }

    public static void ActiveConnection(){
        activeConnection = true;
    }

    public static void notActiveConnection(){
        activeConnection = false;
    }

    public static boolean isActiveConnection(){
        return activeConnection;
    }

//    private void broadcast(String data){
//        Intent intent = new Intent(Config.CHAT_NOTIFICATION);
//        // You can also include some extra data.
//        intent.putExtra("data", data);
//        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//        Log.d("sender", "Broadcasting message");
//    }
//
//    private void badge_broadcast(String data){
//        Intent intent = new Intent("notification_received");
//        // You can also include some extra data.
//        intent.putExtra("data", data);
//        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//        Log.d("sender", "Broadcasting message");
//    }

}
