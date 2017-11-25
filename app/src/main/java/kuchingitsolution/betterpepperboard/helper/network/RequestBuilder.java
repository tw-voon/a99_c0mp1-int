package kuchingitsolution.betterpepperboard.helper.network;

import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class RequestBuilder {

    /*create post api
    * aync call for request */

    //Upload request body
    public static MultipartBody uploadRequestBody(String title, String imageFormat, String token, File file) {

        MediaType MEDIA_TYPE = MediaType.parse("image/" + imageFormat); // e.g. "image/png"
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("action", "upload")
                .addFormDataPart("format", "json")
                .addFormDataPart("filename", title + "." + imageFormat) //e.g. title.png --> imageFormat = png
                .addFormDataPart("file", "...", RequestBody.create(MEDIA_TYPE, file))
                .addFormDataPart("token", token)
                .build();
    }

    public static MultipartBody uploadNewComplaint(String title, String category_id, String description, String suggestion, String lat, String lon, String location_name, File image, String user_id){
//        Log.d("result2", image);
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", title)
                .addFormDataPart("desc", description)
                .addFormDataPart("suggestion", suggestion)
                .addFormDataPart("category_id", category_id)
                .addFormDataPart("latitude", lat)
                .addFormDataPart("longitude", lon)
                .addFormDataPart("location_name", location_name)
                .addFormDataPart("image","...", RequestBody.create(MediaType.parse("image/jpg"), image))
                .addFormDataPart("user_id", user_id)
                .build();
    }

    public static MultipartBody uploadImageComment(String report_id, String user_id, String message, File image){
//        Log.d("result2", image);
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("report_id", report_id)
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("comment", message)
                .addFormDataPart("image","...", RequestBody.create(MediaType.parse("image/jpg"), image))
                .build();
    }

    public static MultipartBody addCommentNoImage(String report_id, String user_id, String message){
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("report_id", report_id)
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("comment", message)
                .build();
    }

    public static MultipartBody getComplaint(String user_id){
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", user_id)
                .build();
    }

    public static MultipartBody get_Complaint(String user_id, int page){
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("page", String.valueOf(page))
                .build();
    }

    public static MultipartBody updateAction(String report_id, File image, String action_taken, String status_id){
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("report_id", report_id)
                .addFormDataPart("image","...", RequestBody.create(MediaType.parse("image/jpg"), image))
                .addFormDataPart("action", action_taken)
                .addFormDataPart("status_id", status_id)
                .build();
    }

    public static MultipartBody update_action_no_image(String report_id, String action_taken, String status_id){
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("report_id", report_id)
                .addFormDataPart("action", action_taken)
                .addFormDataPart("status_id", status_id)
                .build();
    }
}
