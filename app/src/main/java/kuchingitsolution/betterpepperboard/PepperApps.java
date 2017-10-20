package kuchingitsolution.betterpepperboard;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import kuchingitsolution.betterpepperboard.complaint.DetailsComplaintActivity;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.message.ChatActivity;

public class PepperApps extends Application{

    private static PepperApps pepperApps;
    private RequestQueue mRequestQueue;
    private Context mContext;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pepperApps = this;
        mContext = getApplicationContext();
        OneSignal.startInit(this)
                .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                .setNotificationOpenedHandler(new OpenNotification())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
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

    private class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            JSONObject data = notification.payload.additionalData;
            String customKey;
            Log.v("OneSignalExample", "customkey set with value: " + data.toString());

            customKey = data.optString("state", null);
            if (customKey != null && customKey.equals("chat")) {
                Log.i("OneSignalExample", "customkey set with value: " + customKey);
                broadcast(data.toString());
            }
        }
    }

    /*{"state":"chat", "message":{"updated_at":"2017-09-09 22:32:52","user_id":6,"chat_room_id":1,"created_at":"2017-09-09 22:32:52",
      "id":11,"message":"hahahahahahahahhahahahahahahahahahahahhahahahahahahahahahahahhahahaha",
      "user":{"updated_at":"2017-09-05 23:29:45","role_id":3,"name":"user_demo_2","created_at":"2017-09-05 23:29:45",
      "avatar_link":null,"id":6,"email":"demo@demo.com"}},"chat_room":[{"room_id":1,"updated_at":"2017-09-09 00:00:38",
      "user_id":6,"chatroom":{"updated_at":null,"name":"Test Room","created_at":"2017-09-09 00:00:59",
      "last_message":{"updated_at":"2017-09-09 22:32:52","user_id":6,"chat_room_id":1,"created_at":"2017-09-09 22:32:52",
      "id":11,"message":"hahahahahahahahhahahahahahahahahahahahhahahahahahahahahahahahhahahaha"},"id":1,"member_count":2},
      "created_at":"2017-09-09 00:00:38","id":2}]}*/

    private void broadcast(String data){
        Intent intent = new Intent(Config.CHAT_NOTIFICATION);
        // You can also include some extra data.
        intent.putExtra("data", data);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        Log.d("sender", "Broadcasting message");
    }

    private class OpenNotification implements OneSignal.NotificationOpenedHandler{

        @Override
        public void notificationOpened(OSNotificationOpenResult result) {

            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String report_id = null;
            String customKey;
            String group_key = result.notification.payload.groupKey;
            Intent intent = null;
            customKey = data.optString("state", null);

            if (customKey != null && customKey.equals("chat")){
                JSONObject room_name = data.optJSONObject("message").optJSONObject("room");
                String name = room_name.optString("name");
                intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("chat_room_id", group_key);
                intent.putExtra("name", name);
            } else if(customKey != null){
                intent = new Intent(mContext, DetailsComplaintActivity.class);
                report_id = data.optString("report_id", "null");
                Log.d("OneSignalData", "report id : " + report_id);
                intent.putExtra("report_id", report_id);
            }

            Log.d("OneSignalData", data.toString() + " ");

            if(intent != null) { /* only run this if intent not = to null */
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }
}
