package kuchingitsolution.betterpepperboard.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kuchingitsolution.betterpepperboard.message.UserListModel;
import kuchingitsolution.betterpepperboard.notification.NotificationModel;

public class DB_Offline extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME ="pepper_board";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String USERNAME = "username";
    private static final String TYPE_NAME = "type_name";
    private static final String LOCATION_NAME = "location_name";
    private static final String STATUS_NAME = "status_name";
    private static final String OFFICER_NAME = "officer_name";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";
    private static final String OFFICER_ID = "officer_id";
    private static final String ACTION_TAKEN = "action_taken";
    private static final String LAST_ACTION = "last_action";
    private static final String CURRENT_STATUS_ID = "current_status_id";

    // User Table & Column Names
    private static final String TABLE_USER = "user";
    private static final String ROLE_ID = "role_id";

    // Complaint type
    private static final String TABLE_REPORT_TYPE = "report_types";

    // Location
    private static final String TABLE_LOCATION = "location";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";

    // Complaint table
    private static final String TABLE_REPORT = "reports";
    private static final String USER_ID = "user_id";
    private static final String TYPE_ID = "type_id";
    private static final String LOCATION_ID = "location_id";
    private static final String STATUS_ID = "status_id";
    private static final String TITLE = "title";
    private static final String DESC = "description";
    private static final String SUPPORTED = "support";
    private static final String AFFECTED = "affected";

    // Status table
    private static final String TABLE_STATUS = "status_table";

    // Report handler
    private static final String TABLE_REPORT_STATUS = "report_status";
    private static final String REPORT_ID = "report_id";
    private static final String MEDIA_ID = "media_id";
    private static final String ACTION_ID = "action_id";
    private static final String IMGLINK = "image_link";

    // Comment table
    private static final String TABLE_COMMENT = "comment";
    private static final String COMMENT_MSG = "msg";

    // Response table
    private static final String TABLE_RESPONSE = "response_table";

    // Hotline table
    private static final String TABLE_HOTLINE = "hotline";
    private static final String HOTLINE_NUMBER = "number";
    private static final String HOTLINE_DESC = "description";

    // Info Table
    private static final String TABLE_INFO = "info";

    // Details info table
    private static final String TABLE_DETAILS = "info_details";
    private static final String CATEGORY_ID = "category_id";
    private static final String MESSAGE = "message";

    // Media table
    private static final String TABLE_REPORT_MEDIA = "report_media";
    private static final String MEDIA_TYPE = "media_type";
    private static final String LINK = "link";

    //Action Table
    private static final String TABLE_ACTION = "action_table";
    private static final String REPORT_STATUS_ID = "report_status_id";

    //Chat room table
    private static final String CHAT_ROOM_TABLE = "chat_room_table";
    private static final String TIMESTAMP = "timestamp";
    private static final String LAST_MSG = "last_message";
    private static final String LAST_SEEN = "last_seen";
    private static final String ROOM_ID = "room_id";
    private static final String AVATAR = "avatar";

    private static final String NOTIFICATION_TABLE = "notification_table";

    private Context context;
    SharedPreferences sharedPreferences;

    public DB_Offline(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase pepperboard) {

        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + ID + " INTEGER PRIMARY KEY,"
                + NAME + " VARCHAR,"
                + ROLE_ID + " INTEGER,"
                + CREATED_AT + " DATETIME,"
                + UPDATED_AT + " DATETIME" + ")";

        String CREATE_REPORT_TYPE_TABLE = "CREATE TABLE " + TABLE_REPORT_TYPE + "("
                + ID + " INTEGER PRIMARY KEY,"
                + NAME + " VARCHAR,"
                + CREATED_AT + " DATETIME,"
                + UPDATED_AT + " DATETIME" + ")";

        String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATION + "("
                + ID + " INTEGER PRIMARY KEY,"
                + NAME + " VARCHAR,"
                + LATITUDE + " REAL,"
                + LONGITUDE + " REAL,"
                + CREATED_AT + " DATETIME,"
                + UPDATED_AT + " DATETIME" + ")";

        /*  offline data to store
        *   id, user_id, username, type_id, type_name, location_id, location_name, lat, lon, status_id, status_name
        *   officer_id, officer_name, media_type, link, last_action, title, description, affected, support, link
        * */

        String CREATE_COMPLAINT_TABLE = "CREATE TABLE " + TABLE_REPORT + "("
                + ID + " VARCHAR PRIMARY KEY,"
                + USER_ID + " INTEGER,"
                + USERNAME + " VARCHAR,"
                + TYPE_ID + " INTEGER,"
                + TYPE_NAME + " VARCHAR,"
                + LOCATION_ID + " INTEGER,"
                + LOCATION_NAME + " VARCHAR,"
                + LONGITUDE + " REAL,"
                + LATITUDE + " REAL,"
                + STATUS_ID + " INTEGER,"
                + STATUS_NAME + " VARCHAR,"
                + OFFICER_ID + " INTEGER,"
                + OFFICER_NAME + " VARCHAR,"
                + TITLE + " VARCHAR,"
                + DESC + " VARCHAR,"
                + MEDIA_TYPE + " INTEGER,"
                + LINK + " VARCHAR,"
                + LAST_ACTION + " VARCHAR,"
                + AFFECTED + " INTEGER,"
                + SUPPORTED + " INTEGER,"
                + CREATED_AT + " VARCHAR,"
                + UPDATED_AT + " VARCHAR" + ")";

        String CREATE_STATUS_TABLE = "CREATE TABLE " + TABLE_STATUS + "("
                + ID + " INTEGER PRIMARY KEY,"
                + NAME + " VARCHAR,"
                + CREATED_AT + " DATETIME,"
                + UPDATED_AT + " DATETIME" + ")";

        String CREATE_COMMENT_TABLE = "CREATE TABLE " + TABLE_COMMENT + "("
                + ID + " INTEGER PRIMARY KEY,"
                + REPORT_ID + " VARCHAR,"
                + USER_ID + " INTEGER,"
                + COMMENT_MSG + " VARCHAR,"
                + CREATED_AT + " DATETIME,"
                + UPDATED_AT + " DATETIME" + ")";

        String CREATE_RESPONSE_TABLE = "CREATE TABLE " + TABLE_RESPONSE + "("
                + ID + " INTEGER PRIMARY KEY,"
                + USER_ID + " INTEGER,"
                + REPORT_ID + " VARCHAR,"
                + SUPPORTED + " INTEGER,"
                + AFFECTED + " INTEGER,"
                + CREATED_AT + " DATETIME,"
                + UPDATED_AT + " DATETIME" + ")";

        String CREATE_HOTLINE_TABLE = "CREATE TABLE " + TABLE_HOTLINE + "("
                + ID + " INTEGER PRIMARY KEY,"
                + NAME + " VARCHAR,"
                + HOTLINE_NUMBER + " VARCHAR,"
                + HOTLINE_DESC + " VARCHAR,"
                + CREATED_AT + " DATETIME,"
                + UPDATED_AT + " DATETIME" + ")";

        String CREATE_INFO_TABLE = "CREATE TABLE " + TABLE_INFO + "("
                + ID + " INTEGER PRIMARY KEY,"
                + NAME + " VARCHAR,"
                + CREATED_AT + " DATETIME,"
                + UPDATED_AT + " DATETIME" + ")";

        String CREATE_DETAIL_TABLE = "CREATE TABLE " + TABLE_DETAILS + "("
                + ID + " INTEGER PRIMARY KEY,"
                + CATEGORY_ID + " INTEGER,"
                + TITLE + " VARCHAR,"
                + MESSAGE + " VARCHAR,"
                + CREATED_AT + " DATETIME,"
                + UPDATED_AT + " DATETIME" + ")";

        String CREATE_MEDIA_TABLE = "CREATE TABLE " + TABLE_REPORT_MEDIA + "("
                + ID + " INTEGER PRIMARY KEY,"
                + REPORT_ID + " VARCHAR,"
                + MEDIA_TYPE + " INTEGER,"
                + LINK + " VARCHAR,"
                + CREATED_AT + " DATETIME,"
                + UPDATED_AT + " DATETIME" + ")";

        String CREATE_ACTION_TABLE = "CREATE TABLE " + TABLE_ACTION + "("
                + ID + " INTEGER PRIMARY KEY,"
                + REPORT_ID + " VARCHAR,"
                + ACTION_TAKEN + " VARCHAR,"
                + MEDIA_TYPE + " INTEGER,"
                + LINK + " VARCHAR,"
                + CREATED_AT + " DATETIME,"
                + UPDATED_AT + " DATETIME" + ")";

        String CREATE_ROOM_LIST_TABLE = "CREATE TABLE " + CHAT_ROOM_TABLE + "("
                + ID + " INTEGER PRIMARY KEY,"
                + NAME + " VARCHAR,"
                + AVATAR + " VARCHAR,"
                + TIMESTAMP + " VARCHAR,"
                + LAST_MSG + " VARCHAR,"
                + LAST_SEEN + " VARCHAR" + ")";

        String CREATE_NOTIFICATION_TABLE = "CREATE TABLE " + NOTIFICATION_TABLE + "("
                + ID + " INTEGER PRIMARY KEY,"
                + REPORT_ID + " VARCHAR,"
                + MESSAGE + " VARCHAR,"
                + AVATAR + " VARCHAR,"
                + USER_ID + " VARCHAR,"
                + CREATED_AT + " DATETIME" + ")";

        pepperboard.execSQL(CREATE_COMMENT_TABLE);
        pepperboard.execSQL(CREATE_COMPLAINT_TABLE);
        pepperboard.execSQL(CREATE_LOCATION_TABLE);
        pepperboard.execSQL(CREATE_REPORT_TYPE_TABLE);
        pepperboard.execSQL(CREATE_STATUS_TABLE);
        pepperboard.execSQL(CREATE_USER_TABLE);
        pepperboard.execSQL(CREATE_RESPONSE_TABLE);
        pepperboard.execSQL(CREATE_HOTLINE_TABLE);
        pepperboard.execSQL(CREATE_INFO_TABLE);
        pepperboard.execSQL(CREATE_DETAIL_TABLE);
        pepperboard.execSQL(CREATE_MEDIA_TABLE);
        pepperboard.execSQL(CREATE_ACTION_TABLE);
        pepperboard.execSQL(CREATE_ROOM_LIST_TABLE);
        pepperboard.execSQL(CREATE_NOTIFICATION_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase pepperboard, int i, int i1) {

        pepperboard.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENT);
        pepperboard.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        pepperboard.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORT);
        pepperboard.execSQL("DROP TABLE IF EXISTS " + TABLE_STATUS);
        pepperboard.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        pepperboard.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORT_TYPE);
        pepperboard.execSQL("DROP TABLE IF EXISTS " + TABLE_HOTLINE);
        pepperboard.execSQL("DROP TABLE IF EXISTS " + TABLE_INFO);
        pepperboard.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAILS);
        pepperboard.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORT_MEDIA);
        pepperboard.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTION);
        pepperboard.execSQL("DROP TABLE IF EXISTS " + CHAT_ROOM_TABLE);
        pepperboard.execSQL("DROP TABLE IF EXISTS " + NOTIFICATION_TABLE);
        onCreate(pepperboard);

    }

    public void clearTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COMMENT, null, null);
        db.delete(TABLE_USER, null, null);
        db.delete(TABLE_REPORT, null, null);
        db.delete(TABLE_STATUS, null, null);
        db.delete(TABLE_LOCATION, null, null);
        db.delete(TABLE_REPORT_TYPE, null, null);
        db.delete(TABLE_HOTLINE, null, null);
        db.delete(TABLE_INFO, null, null);
        db.delete(TABLE_DETAILS, null, null);
        db.delete(TABLE_REPORT_MEDIA, null, null);
        db.delete(TABLE_ACTION, null, null);
        db.delete(CHAT_ROOM_TABLE, null, null);
        db.delete(NOTIFICATION_TABLE, null, null);
        db.close();
    }

    public void insertNotification(NotificationModel notificationModel){

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        ContentValues item = new ContentValues();
        item.put(REPORT_ID, notificationModel.getReport_id());
        item.put(MESSAGE, notificationModel.getAction());
        item.put(USER_ID, notificationModel.getUser_id());
        item.put(AVATAR, notificationModel.getAvatar_link());
        item.put(CREATED_AT, notificationModel.getTimestamp());

        db.insert(NOTIFICATION_TABLE, null, item);

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

    }

    public void deleteNotification(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NOTIFICATION_TABLE, null, null);
        db.close();
    }

    public void deleteRoom(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CHAT_ROOM_TABLE, null, null);
        db.close();
    }

    public void insertRoom(UserListModel userListModel){

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        ContentValues item = new ContentValues();
        item.put(ID, userListModel.getRoom_id());
        item.put(NAME, userListModel.getUsername());
        item.put(TIMESTAMP, userListModel.getTimestamp());
        item.put(AVATAR, userListModel.getAvatar());
        item.put(LAST_SEEN, userListModel.getLast_seen());
        item.put(LAST_MSG, userListModel.getLast_msg());

        if(ifRoomExists(userListModel.getRoom_id()))
            db.update(CHAT_ROOM_TABLE, item, " id = '" + userListModel.getRoom_id() + "'", null);
        else
            db.insert(CHAT_ROOM_TABLE, null, item);

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void insertResponse(JSONArray data){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RESPONSE, null, null);
        db.beginTransaction();
        int length = data.length();
        try {

            for (int i = 0; i < length; i++) {
                JSONObject action = data.getJSONObject(i);
                ContentValues item = new ContentValues();
                item.put(ID, action.getString(ID));
                item.put(USER_ID, action.getString(USER_ID));
                item.put(REPORT_ID, action.getString(REPORT_ID));
                item.put(AFFECTED, action.getString(AFFECTED));
                item.put(SUPPORTED, action.getString(SUPPORTED));
                item.put(CREATED_AT, action.getString(CREATED_AT));
                item.put(UPDATED_AT, action.getString(UPDATED_AT));
                long tag_id = db.insert(TABLE_RESPONSE, null, item);
                Log.d("response id = ", tag_id + " ");
            }
            db.setTransactionSuccessful();

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void insertAction(JSONArray data){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTION, null, null);
        db.beginTransaction();
        int length = data.length();
        try {

            for (int i = 0; i < length; i++) {
                JSONObject action = data.getJSONObject(i);
                ContentValues item = new ContentValues();
                item.put(ID, action.getString(ID));
                item.put(REPORT_ID, action.getString(REPORT_ID));
                item.put(ACTION_TAKEN, action.getString(ACTION_TAKEN));
                item.put(MEDIA_TYPE, action.getString(MEDIA_TYPE));
                item.put(LINK, action.getString(LINK));
                item.put(CREATED_AT, action.getString(CREATED_AT));
                item.put(UPDATED_AT, action.getString(UPDATED_AT));
                long tag_id = db.insert(TABLE_ACTION, null, item);
                Log.d("type id = ", tag_id + " ");
            }
            db.setTransactionSuccessful();

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void insertReportMedia(JSONObject data){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REPORT_MEDIA, null, null);
        db.beginTransaction();
        int length = data.length();

        ContentValues item = new ContentValues();
        try {
            item.put(ID, data.getString(ID));
            item.put(REPORT_ID, data.getString(REPORT_ID));
            item.put(MEDIA_TYPE, data.getString(MEDIA_TYPE));
            item.put(LINK, data.getString(LINK));
            long tag_id = db.insert(TABLE_REPORT_MEDIA, null, item);
            Log.d("type id = ", tag_id + " ");
            db.setTransactionSuccessful();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void insertReportType(String result){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REPORT_TYPE, null, null);
        db.beginTransaction();

        try {
            JSONArray data = new JSONArray(result);
            int length = data.length();
            for (int i = 0; i<length; i++){
                JSONObject jsonObject = data.getJSONObject(i);
                ContentValues item = new ContentValues();
                item.put(ID, jsonObject.getString("id"));
                item.put(NAME, jsonObject.getString("name"));
                item.put(CREATED_AT, jsonObject.getString("created_at"));
                item.put(UPDATED_AT, jsonObject.getString("updated_at"));

                long tag_id = db.insert(TABLE_REPORT_TYPE, null, item);
                Log.d("type id = ", tag_id + " ");
            }
            db.setTransactionSuccessful();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void insertInfo(String result){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INFO, null, null);
        db.beginTransaction();

        try {
            JSONArray data = new JSONArray(result);
            int length = data.length();
            for (int i = 0; i<length; i++){
                JSONObject jsonObject = data.getJSONObject(i);
                ContentValues item = new ContentValues();
                item.put(ID, jsonObject.getString("id"));
                item.put(NAME, jsonObject.getString("name"));
                item.put(CREATED_AT, jsonObject.getString("created_at"));
                item.put(UPDATED_AT, jsonObject.getString("updated_at"));

                long tag_id = db.insert(TABLE_INFO, null, item);
                Log.d("info id = ", tag_id + " ");
            }
            db.setTransactionSuccessful();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void insertDetails(String result){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DETAILS, null, null);
        db.beginTransaction();

        try {
            JSONArray data = new JSONArray(result);
            int length = data.length();
            for (int i = 0; i<length; i++){
                JSONObject jsonObject = data.getJSONObject(i);
                ContentValues item = new ContentValues();
                item.put(ID, jsonObject.getString("id"));
                item.put(CATEGORY_ID, jsonObject.getString("category_id"));
                item.put(TITLE, jsonObject.getString("title"));
                item.put(MESSAGE, jsonObject.getString("message"));
                item.put(CREATED_AT, jsonObject.getString("created_at"));
                item.put(UPDATED_AT, jsonObject.getString("updated_at"));

                long tag_id = db.insert(TABLE_DETAILS, null, item);
                Log.d("info id = ", tag_id + " ");
            }
            db.setTransactionSuccessful();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();

    }

    public void insertHotline(String result){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HOTLINE, null, null);
        db.beginTransaction();

        try {
            JSONArray data = new JSONArray(result);
            int length = data.length();
            for (int i = 0; i<length; i++){
                JSONObject jsonObject = data.getJSONObject(i);
                ContentValues item = new ContentValues();
                item.put(ID, jsonObject.getString("id"));
                item.put(NAME, jsonObject.getString("name"));
                item.put(HOTLINE_NUMBER, jsonObject.getString("number"));
                item.put(HOTLINE_DESC, jsonObject.getString("description"));
                item.put(CREATED_AT, jsonObject.getString("created_at"));
                item.put(UPDATED_AT, jsonObject.getString("updated_at"));

                long tag_id = db.insert(TABLE_HOTLINE, null, item);
                Log.d("hotline id = ", tag_id + " ");
            }
            db.setTransactionSuccessful();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void insertReport(String result){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REPORT, null, null);
        db.beginTransaction();

        try {
            JSONObject data = new JSONObject(result);
            JSONArray jsonArray;
            jsonArray = data.getJSONArray("data");
            int length = jsonArray.length();
            for (int i = 0; i<length; i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ContentValues item = new ContentValues();
                item.put(ID, jsonObject.getString("id"));
                item.put(USER_ID, jsonObject.getString("user_id"));
                item.put(TYPE_ID, jsonObject.getString("type_id"));
                item.put(LOCATION_ID, jsonObject.getString("location_id"));
                item.put(STATUS_ID, jsonObject.getString("status_id"));
                item.put(OFFICER_ID, jsonObject.getString(OFFICER_ID));
                item.put(TITLE, jsonObject.getString("title"));
                item.put(DESC, jsonObject.getString("description"));
                item.put(AFFECTED, jsonObject.getInt(AFFECTED));
                item.put(SUPPORTED, jsonObject.getInt(SUPPORTED));
                item.put(CREATED_AT, jsonObject.getString("created_at"));
                item.put(UPDATED_AT, jsonObject.getString("updated_at"));

                long tag_id = db.insert(TABLE_REPORT, null, item);
                Log.d("report id = ", tag_id + " ");

            }
            db.setTransactionSuccessful();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    /*  offline data to store
     *   id, user_id, username, type_id, type_name, location_id, location_name, lat, lon, status_id, status_name
     *   officer_id, officer_name, media_type, link, last_action, title, description, affected, support, link
     * */

    /*
    * data.getString("id"),
                        data.getInt("user_id"),
                        data.getJSONObject("user").getString("name"),
                        data.getInt("type_id"),
                        data.getJSONObject("category").getString("name"),
                        data.getInt("location_id"),
                        data.getJSONObject("location").getString("name"),
                        data.getJSONObject("location").getDouble("lat"),
                        data.getJSONObject("location").getDouble("lon"),
                        data.getInt("status_id"),
                        data.getJSONObject("status").getString("name"),
                        data.optInt("officer_id"),
                        data.getJSONObject("officer").optString("name"),
                        data.getJSONObject("media").getInt("media_type"),
                        data.getJSONObject("media").getString("link"),
                        data.getJSONArray("action").getJSONObject(0).getString("action_taken"),
                        data.getString("title"), data.getString("description"),
                        data.getInt("affected"), data.getInt("support"),
                        data.getString("created_at"), data.getString("updated_at"));

                        String id, int user_id, String username, int type_id, String type_name, int location_id,
            String location_name, double lat, double lon, int status_id, String status_name,
            @Nullable int officer_id, @Nullable String officer_name, int media_type, String link, String last_action,
            String title, String description, int affected, int support, String created, String updated
                        */

    public void clearComplaint(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REPORT, null, null);
        db.close();
    }

    public void insertComplaint(JSONObject data){

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        String id = "0";

        ContentValues item = new ContentValues();
        try {

            id = data.getString("id");
            item.put(ID, data.getString("id"));
            item.put(USER_ID, data.getInt("user_id"));
            item.put(USERNAME, data.getJSONObject("user").getString("name"));
            item.put(TYPE_ID, data.getInt("type_id"));
            item.put(TYPE_NAME, data.getJSONObject("category").getString("name"));
            item.put(LOCATION_ID, data.getInt("location_id"));
            item.put(LOCATION_NAME, data.getJSONObject("location").getString("name"));
            item.put(LATITUDE, data.getJSONObject("location").getDouble("lat"));
            item.put(LONGITUDE, data.getJSONObject("location").getDouble("lon"));
            item.put(STATUS_ID, data.getInt("status_id"));
            item.put(STATUS_NAME, data.getJSONObject("status").getString("name"));
            item.put(OFFICER_ID, data.optInt("officer_id"));
            if(data.isNull("officer"))
                item.put(OFFICER_NAME, "NULL");
            else
                item.put(OFFICER_NAME, data.getJSONObject("officer").optString("name"));
            item.put(MEDIA_TYPE, data.getJSONObject("media").getInt("media_type"));
            item.put(LINK, data.getJSONObject("media").getString("link"));
            item.put(LAST_ACTION, data.getJSONArray("action").getJSONObject(0).getString("action_taken"));
            item.put(TITLE, data.getString("title"));
            item.put(DESC, data.getString("description"));
            item.put(AFFECTED, data.getInt("affected"));
            item.put(SUPPORTED, data.getInt("support"));
            item.put(CREATED_AT, data.getString("created_at"));
            item.put(UPDATED_AT, data.getString("updated_at"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(ifExists(id))
            db.update(TABLE_REPORT, item, " id = '" + id + "'", null);
        else
            db.insert(TABLE_REPORT, null, item);

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

    }

    public int get_total(int status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String checkQuery = "SELECT " + STATUS_ID + " FROM " + TABLE_REPORT + " WHERE " + STATUS_ID + "= '"+ status + "'";
        cursor= db.rawQuery(checkQuery,null);
        int exists = cursor.getCount();
        cursor.close();
        return exists;
    }

    public int get_support(String report_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String checkQuery = "SELECT support FROM " + TABLE_REPORT + " WHERE " + ID + "= '"+ report_id + "'";
        cursor = db.rawQuery(checkQuery, null);
        int support = 0;
        if(cursor.moveToFirst())
            support = Integer.valueOf(cursor.getString(0));
        else Log.d("Result", "support : 0");
        Log.d("support", support + " ");
        cursor.close();
        return support;
    }

    public int get_affect(String report_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String checkQuery = "SELECT "+ AFFECTED +" FROM " + TABLE_REPORT + " WHERE " + ID + "= '"+ report_id + "'";
        cursor = db.rawQuery(checkQuery, null);
        int affect = 0;
        if(cursor.moveToFirst())
            affect = Integer.valueOf(cursor.getString(0));
        else Log.d("Result", "support : 0");
        Log.d("support", affect + " ");
        cursor.close();
        return affect;
    }

    private boolean ifRoomExists(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String checkQuery = "SELECT " + ID + " FROM " + CHAT_ROOM_TABLE + " WHERE " + ID + "= '"+ id + "'";
        cursor= db.rawQuery(checkQuery,null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    private boolean ifExists(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String checkQuery = "SELECT " + ID + " FROM " + TABLE_REPORT + " WHERE " + ID + "= '"+ id + "'";
        cursor= db.rawQuery(checkQuery,null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    private boolean ifResponseExists(String report_id, String user_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String checkQuery = "SELECT * FROM " + TABLE_RESPONSE + " WHERE " + REPORT_ID + "= '"+ report_id + "'" + " AND " + USER_ID + "= " + user_id;
        cursor= db.rawQuery(checkQuery,null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public void insertReportStatus(JSONArray status){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTION, null, null);
        db.beginTransaction();

        try {
            int length = status.length();
            for(int i = 0; i < length; i++){
                JSONObject action = status.getJSONObject(i);
                ContentValues item = new ContentValues();
                item.put(ID, action.getString(ID));
                item.put(REPORT_ID, action.getString(REPORT_ID));
                item.put(ACTION_TAKEN, action.getString(ACTION_TAKEN));
                item.put(MEDIA_TYPE, action.getString(MEDIA_TYPE));
                item.put(LINK, action.getString(LINK));
                item.put(CURRENT_STATUS_ID, action.getString(CURRENT_STATUS_ID));
                item.put(CREATED_AT, action.getString(CREATED_AT));
                item.put(UPDATED_AT, action.getString(UPDATED_AT));
                long tag_id = db.insert(TABLE_REPORT_STATUS, null, item);
                Log.d("report id = ", tag_id + " ");
            }
            db.setTransactionSuccessful();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void insertUserData(JSONObject user) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, null, null);
        db.beginTransaction();

        try {
                ContentValues item = new ContentValues();
                item.put(ID, user.getString(ID));
                item.put(NAME, user.getString(NAME));
                item.put(ROLE_ID, user.getString(ROLE_ID));
                item.put(CREATED_AT, user.getString(CREATED_AT));
                item.put(UPDATED_AT, user.getString(UPDATED_AT));

                long tag_id = db.insert(TABLE_USER, null, item);
                Log.d("user id = ", tag_id + " ");

            db.setTransactionSuccessful();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void insert_report_media(JSONObject media){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REPORT_MEDIA, null, null);
        db.beginTransaction();

        ContentValues item = new ContentValues();
        try {
            item.put(ID, media.getString(ID));
            item.put(REPORT_ID, media.getString(REPORT_ID));
            item.put(MEDIA_TYPE, media.getString(MEDIA_TYPE));
            item.put(LINK, media.getString(LINK));
            item.put(CREATED_AT, media.getString(CREATED_AT));
            item.put(UPDATED_AT, media.getString(UPDATED_AT));
            long tag_id = db.insert(TABLE_REPORT_MEDIA, null, item);
            Log.d("user id = ", tag_id + " ");
            db.setTransactionSuccessful();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void insert_location(JSONObject location){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATION, null, null);
        db.beginTransaction();
        ContentValues item = new ContentValues();

        try {

            item.put(ID, location.getString(ID));
            item.put(NAME, location.getString(NAME));
            item.put(LATITUDE, location.getString(LATITUDE));
            item.put(LONGITUDE, location.getString(LONGITUDE));
            item.put(CREATED_AT, location.getString(CREATED_AT));
            item.put(UPDATED_AT, location.getString(UPDATED_AT));
            long tag_id = db.insert(TABLE_LOCATION, null, item);
            Log.d("user id = ", tag_id + " ");
            db.setTransactionSuccessful();

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public Cursor get_notification(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> notification = new ArrayList<>();

        String SELECT_NOTIFICATION = String.format("SELECT * FROM %S ORDER BY %S DESC",
                NOTIFICATION_TABLE, CREATED_AT);

        Log.d("query", SELECT_NOTIFICATION);

//        Cursor cursor = db.rawQuery(SELECT_NOTIFICATION, null);
//        if(cursor.moveToFirst()){
//            Log.d("Result complaint", DatabaseUtils.dumpCursorToString(cursor));
//
//            do {
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put(ID, cursor.getString(0));
//                    jsonObject.put(REPORT_ID, cursor.getString(1));
//                    jsonObject.put(MESSAGE, cursor.getString(2));
//                    jsonObject.put(AVATAR, cursor.getString(3));
//                    jsonObject.put(USER_ID, cursor.getString(4));
//                    jsonObject.put(CREATED_AT, cursor.getString(5));
//                    notification.add(cursor.getPosition(), jsonObject);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } while (cursor.moveToNext());
//        } else Log.d("Result == ", "NO");
//
//        Log.d("Overall result", String.valueOf(notification));
//
//        cursor.close();
//        db.close();
//        return String.valueOf(notification);
        return db.rawQuery(SELECT_NOTIFICATION, null);
    }

    public String getComplaint(int status, int next){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> complaintData = new ArrayList<>();

        String SELECT_COMPLAINTS = String.format("SELECT * FROM %S WHERE %S = %S ORDER BY %S DESC LIMIT 5 OFFSET %S",
                TABLE_REPORT, STATUS_ID, status, CREATED_AT, next);

        Log.d("query", SELECT_COMPLAINTS);

        Cursor cursor = db.rawQuery(SELECT_COMPLAINTS, null);

        if(cursor.moveToFirst()){
            Log.d("Result complaint", DatabaseUtils.dumpCursorToString(cursor));

            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(ID, cursor.getString(0));
                    jsonObject.put(USER_ID, cursor.getString(1));
                    jsonObject.put(USERNAME, cursor.getString(2));
                    jsonObject.put(TYPE_ID, cursor.getString(3));
                    jsonObject.put(TYPE_NAME, cursor.getString(4));
                    jsonObject.put(LOCATION_ID, cursor.getString(5));
                    jsonObject.put(LOCATION_NAME, cursor.getString(6));
                    jsonObject.put(LONGITUDE, cursor.getString(7));
                    jsonObject.put(LATITUDE, cursor.getString(8));
                    jsonObject.put(STATUS_ID, cursor.getString(9));
                    jsonObject.put(STATUS_NAME, cursor.getString(10));
                    jsonObject.put(OFFICER_ID, cursor.getString(11));
                    jsonObject.put(OFFICER_NAME, cursor.getString(12));
                    jsonObject.put(TITLE, cursor.getString(13));
                    jsonObject.put(DESC, cursor.getString(14));
                    jsonObject.put(MEDIA_TYPE, cursor.getString(15));
                    jsonObject.put(LINK, cursor.getString(16));
                    jsonObject.put(LAST_ACTION, cursor.getString(17));
                    jsonObject.put(AFFECTED, cursor.getString(18));
                    jsonObject.put(SUPPORTED, cursor.getString(19));
                    jsonObject.put(CREATED_AT, cursor.getString(20));
                    jsonObject.put(UPDATED_AT, cursor.getString(21));
                    complaintData.add(cursor.getPosition(), jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        } else Log.d("Result == ", "NO");

        Log.d("Overall result", String.valueOf(complaintData));

        cursor.close();
        db.close();
        return String.valueOf(complaintData);
    }

    public Cursor get_room(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<UserListModel> complaintData = new ArrayList<>();

        String SELECT_ROOM = String.format("SELECT * FROM %S ORDER BY %S DESC",
                CHAT_ROOM_TABLE, TIMESTAMP);

        Log.d("query", SELECT_ROOM);

        //        if(cursor.moveToFirst()){
//            Log.d("Result complaint", DatabaseUtils.dumpCursorToString(cursor));
//
//            do {
//                UserListModel jsonObject = new UserListModel(
//                        cursor.getString(1), cursor.getString(3), cursor.getString(2),
//                        cursor.getString(4), cursor.getString(5), cursor.getString(0)
//                );
//                complaintData.add(jsonObject);
//
//            } while (cursor.moveToNext());
//        } else Log.d("Result == ", "NO");

//        Log.d("Overall result", String.valueOf(complaintData));

//        cursor.close();
//        db.close();
        return db.rawQuery(SELECT_ROOM, null);
    }

    public JSONObject ifResponse(String report_id){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> responseData = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();

        String CHECK_RESPONSE = String.format("SELECT * FROM %S WHERE %S = '%S'",
                TABLE_RESPONSE, REPORT_ID, report_id);
//        String CHECK_RESPONSE = String.format("SELECT * FROM %S",
//                TABLE_RESPONSE);

        Log.d("query", CHECK_RESPONSE);

        Cursor cursor = db.rawQuery(CHECK_RESPONSE, null);

        if(cursor.moveToFirst()){
            Log.d("Result response", DatabaseUtils.dumpCursorToString(cursor));

            try {
                jsonObject.put(SUPPORTED, cursor.getString(3));
                jsonObject.put(AFFECTED, cursor.getString(4));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else Log.d("Result == ", "NO RESPONSE");

        Log.d("Overall response", String.valueOf(jsonObject));

        cursor.close();
        db.close();

        return jsonObject;
    }

    public String getSingleReport(String id){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> complaintData = new ArrayList<>();

        String SELECT_COMPLAINTS = String.format("SELECT * FROM %S WHERE %S = '%S'",
                TABLE_REPORT, ID, id);

        Log.d("query", SELECT_COMPLAINTS);

        Cursor cursor = db.rawQuery(SELECT_COMPLAINTS, null);

        if(cursor.moveToFirst()){
            Log.d("Result complaint", DatabaseUtils.dumpCursorToString(cursor));

            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(ID, cursor.getString(0));
                    jsonObject.put(USER_ID, cursor.getString(1));
                    jsonObject.put(USERNAME, cursor.getString(2));
                    jsonObject.put(TYPE_ID, cursor.getString(3));
                    jsonObject.put(TYPE_NAME, cursor.getString(4));
                    jsonObject.put(LOCATION_ID, cursor.getString(5));
                    jsonObject.put(LOCATION_NAME, cursor.getString(6));
                    jsonObject.put(LONGITUDE, cursor.getString(7));
                    jsonObject.put(LATITUDE, cursor.getString(8));
                    jsonObject.put(STATUS_ID, cursor.getString(9));
                    jsonObject.put(STATUS_NAME, cursor.getString(10));
                    jsonObject.put(OFFICER_ID, cursor.getString(11));
                    jsonObject.put(OFFICER_NAME, cursor.getString(12));
                    jsonObject.put(TITLE, cursor.getString(13));
                    jsonObject.put(DESC, cursor.getString(14));
                    jsonObject.put(MEDIA_TYPE, cursor.getString(15));
                    jsonObject.put(LINK, cursor.getString(16));
                    jsonObject.put(LAST_ACTION, cursor.getString(17));
                    jsonObject.put(AFFECTED, cursor.getString(18));
                    jsonObject.put(SUPPORTED, cursor.getString(19));
                    jsonObject.put(CREATED_AT, cursor.getString(20));
                    jsonObject.put(UPDATED_AT, cursor.getString(21));
                    complaintData.add(cursor.getPosition(), jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        } else Log.d("Result == ", "NO");

        Log.d("Overall result", String.valueOf(complaintData));

        cursor.close();
        db.close();
        return String.valueOf(complaintData);

    }

    public String getHotline(){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> hotlineData = new ArrayList<>();
        String SELECT_COMPLAINTS = String.format("SELECT * FROM %S",
                TABLE_HOTLINE);

        Log.d("query", SELECT_COMPLAINTS);
        Cursor cursor = db.rawQuery(SELECT_COMPLAINTS, null);
        if(cursor.moveToFirst()){
            Log.d("Result complaint", DatabaseUtils.dumpCursorToString(cursor));
            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(ID, cursor.getString(0));
                    jsonObject.put(NAME, cursor.getString(1));
                    jsonObject.put(HOTLINE_NUMBER, cursor.getString(2));
                    jsonObject.put(HOTLINE_DESC, cursor.getString(3));
                    hotlineData.add(cursor.getPosition(), jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        } else {
            Log.d("Result == ", "NO");
        }
        Log.d("Overall result", String.valueOf(hotlineData));
        cursor.close();
        db.close();
        return String.valueOf(hotlineData);
    }

    public String getInfo(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> infoData = new ArrayList<>();
        String SELECT_INFO = String.format("SELECT * FROM %S",
                TABLE_INFO);

        Log.d("query", SELECT_INFO);
        Cursor cursor = db.rawQuery(SELECT_INFO, null);
        if(cursor.moveToFirst()){
            Log.d("Result complaint", DatabaseUtils.dumpCursorToString(cursor));
            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(ID, cursor.getString(0));
                    jsonObject.put(NAME, cursor.getString(1));
                    infoData.add(cursor.getPosition(), jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        } else {
            Log.d("Result == ", "NO");
        }
        Log.d("Overall result", String.valueOf(infoData));
        cursor.close();
        db.close();
        return String.valueOf(infoData);
    }

    public String getReportType(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> reportData = new ArrayList<>();
        String SELECT_INFO = String.format("SELECT * FROM %S",
                TABLE_REPORT_TYPE);

        Log.d("query", SELECT_INFO);
        Cursor cursor = db.rawQuery(SELECT_INFO, null);
        if(cursor.moveToFirst()){
            Log.d("Result complaint", DatabaseUtils.dumpCursorToString(cursor));
            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(ID, cursor.getString(0));
                    jsonObject.put(NAME, cursor.getString(1));
                    reportData.add(cursor.getPosition(), jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        } else {
            Log.d("Result == ", "NO");
        }
        Log.d("Overall result", String.valueOf(reportData));
        cursor.close();
        db.close();
        return String.valueOf(reportData);
    }

    public String getDetailsInfo(int id){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> detailData = new ArrayList<>();
        String SELECT_DETAIL = String.format("SELECT * FROM %S WHERE %S = %S",
                TABLE_DETAILS, CATEGORY_ID, id);

        Log.d("query", SELECT_DETAIL);
        Cursor cursor = db.rawQuery(SELECT_DETAIL, null);
        if(cursor.moveToFirst()){
            Log.d("Result complaint", DatabaseUtils.dumpCursorToString(cursor));
            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(ID, cursor.getString(0));
                    jsonObject.put(CATEGORY_ID, cursor.getString(1));
                    jsonObject.put(TITLE, cursor.getString(2));
                    jsonObject.put(MESSAGE, cursor.getString(3));
                    detailData.add(cursor.getPosition(), jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        } else {
            Log.d("Result == ", "NO");
        }
        Log.d("Overall result", String.valueOf(detailData));
        cursor.close();
        db.close();
        return String.valueOf(detailData);

    }

    public boolean updateResponse(String type, int value, String user_id, String report_id, int initial){
        SQLiteDatabase db = this.getReadableDatabase();
        long id = 0;

        switch (type){
            case "support":
                if(ifResponseExists(report_id, user_id)){
                    ContentValues item = new ContentValues();
                    item.put("support", value);
                    id = db.update(TABLE_RESPONSE, item, " report_id ='" + report_id + "' AND user_id =" + user_id, null);
                    Log.d("type", " support update" + id);
                } else {
                    ContentValues item = new ContentValues();
                    item.put("user_id", user_id);
                    item.put("report_id", report_id);
                    item.put("support", value);
                    item.put("affected", 0);
                    id = db.insert(TABLE_RESPONSE, null, item);
                    Log.d("type", " support insert" + id);
                }

                ContentValues report = new ContentValues();
                report.put(SUPPORTED, initial);
                db.update(TABLE_REPORT, report, " id = '" + report_id + "'", null);

                break;
            case "affected":
                if(ifResponseExists(report_id, user_id)){
                    ContentValues item = new ContentValues();
                    item.put("affected", value);
                    id = db.update(TABLE_RESPONSE, item, " report_id ='" + report_id + "' AND user_id =" + user_id, null);
                    Log.d("type", " affected update" + id);
                } else {
                    ContentValues item = new ContentValues();
                    item.put("user_id", user_id);
                    item.put("report_id", report_id);
                    item.put("affected", value);
                    item.put("support", 0);
                    id = db.insert(TABLE_RESPONSE, null, item);
                    Log.d("type", " affected insert" + id);
                }

                ContentValues reports = new ContentValues();
                reports.put(AFFECTED, initial);
                db.update(TABLE_REPORT, reports, " id = '" + report_id + "'", null);

                break;
            default:
                break;
        }
        Log.d("tag", " " + id);
        db.close();

        return id > 0;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
