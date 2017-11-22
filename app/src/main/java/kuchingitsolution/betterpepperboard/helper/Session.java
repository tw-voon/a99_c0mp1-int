package kuchingitsolution.betterpepperboard.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context ctx;
    private Config config;

    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_GUEST_MODE = "guest_mode";
    private static final String KEY_IS_LOGGED = "logged_In_mode";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AVATAR = "avatar";
    private static final String KEY_SHOW_PASSWORD = "show_password";

    public Session(Context ctx){
        this.ctx = ctx;
        config = new Config();
        prefs = ctx.getSharedPreferences(Config.PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedin(boolean logggedin){
        editor.putBoolean(KEY_IS_LOGGED,logggedin);
        editor.commit();
    }

    public void setCurrentId(String id){
        editor.putString("report_id", id);
        editor.commit();
    }

    public void setCurrentPosition(String position){
        editor.putString("report_position", position);
        editor.commit();
    }

    public String getCurrentId(){ return prefs.getString("report_id", null);}

    public String getCurrentPosition(){return prefs.getString("report_position", null);}

    public void setKeyShowPassword(){
        editor.putBoolean(KEY_SHOW_PASSWORD, true);
        editor.commit();
    }

    public boolean getStatusPswd(){
        return prefs.getBoolean(KEY_SHOW_PASSWORD, false);
    }

    public void setGuess(boolean guestmode){
        editor.putBoolean(KEY_GUEST_MODE,guestmode);
        editor.commit();
    }

    public boolean isGuest(){
        return prefs.getBoolean(KEY_GUEST_MODE, false);
    }

    public void setPermission(String key, boolean permission){
        editor.putBoolean(key,permission);
        editor.commit();
    }

    public boolean getPermission(String key){return prefs.getBoolean(key, false);}

    public void putData(String userID, String username, String avatar, String position){

        editor.putString(KEY_USER_ID, userID);
        editor.putString(KEY_USER_NAME, username);
        editor.putString(KEY_AVATAR, avatar);
        editor.putString(KEY_POSITION, position);
        editor.commit();

    }

    public void putFirebaseID(String firebaseID){
        editor.putString("firebaseID", firebaseID);
        editor.commit();
    }

    public void putUserAvatar(String encodeImage){
        editor.putString(KEY_AVATAR, encodeImage);
        editor.commit();
    }

    public String getUserAvatar() { return prefs.getString("AVATAR", null); }

    public boolean loggedin(){
        return prefs.getBoolean(KEY_IS_LOGGED, false);
    }

    public String getUsername(){
        return prefs.getString(KEY_USER_NAME, null);
    }

    public String getUserID(){
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getPosition(){ return prefs.getString(KEY_POSITION, null); }

    public void addNotification(String notification) {

        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return prefs.getString(KEY_NOTIFICATIONS, null);
    }

    public void clearPreference()
    {
        prefs.edit().clear().commit();
    }

    public void setup_tab_badge(int number){

        editor.putInt(Config.COMPLAINT, number);
        editor.putInt(Config.MESSAGE, number);
        editor.putInt(Config.NOTI, number);

        editor.commit();

    }

    public void putComplaintBadge(int number){
        editor.putInt(Config.COMPLAINT, getComplaintBadge() + number);
        editor.commit();
    }

    public int getComplaintBadge(){
        return prefs.getInt(Config.COMPLAINT, 0);
    }

    public void putMessageBadge(int number){
        editor.putInt(Config.MESSAGE, getMessageBadge() + number);
        editor.commit();
    }

    public int getMessageBadge(){
        return prefs.getInt(Config.MESSAGE, 0);
    }

    public void putNotiBadge(int number){
        editor.putInt(Config.NOTI, getNotiBadge() + number);
        editor.commit();
    }

    public int getNotiBadge(){
        return prefs.getInt(Config.NOTI, 0);
    }

    public void resetBadge(int position){
        switch (position){
            case 1:
                editor.putInt(Config.COMPLAINT, 0);
                editor.commit();
                break;
            case 2:
                editor.putInt(Config.MESSAGE, 0);
                editor.commit();
                break;
            case 3:
                editor.putInt(Config.NOTI, 0);
                editor.commit();
                break;
        }
    }

}
