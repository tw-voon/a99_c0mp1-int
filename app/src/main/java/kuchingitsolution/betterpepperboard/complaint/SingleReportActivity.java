package kuchingitsolution.betterpepperboard.complaint;

import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.action.ActionActivity;
import kuchingitsolution.betterpepperboard.admin.AssignActivity;
import kuchingitsolution.betterpepperboard.helper.BottomSheetDialogFragmentOption;
import kuchingitsolution.betterpepperboard.helper.BottomSheetDialogFragmentStatus;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.ImageCompressionUtils;
import kuchingitsolution.betterpepperboard.helper.Session;
import kuchingitsolution.betterpepperboard.helper.Utility;
import kuchingitsolution.betterpepperboard.helper.network.ApiCall;
import kuchingitsolution.betterpepperboard.helper.network.CountingRequestBody;
import kuchingitsolution.betterpepperboard.helper.network.RequestBuilder;
import kuchingitsolution.betterpepperboard.map.ActivityViewOnMap;
import kuchingitsolution.betterpepperboard.officer.OfficerActivity;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

import static android.R.attr.bitmap;

public class SingleReportActivity extends AppCompatActivity implements BottomSheetDialogFragmentOption.OptionBottomSheetCallback {

    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;
    MenuInflater menuInflater;
    MenuItem menuItem;

    private List<CommentModel> comments;
    private RecyclerView commentlist;
    private CommentAdapter commentAdapter;
    private String action, reportLink, report_id, status_id, officer_id, officer_name, lat, lon, report_title, url, status, imagePath, message;
    private TextView category, desc, location, title, username, no_comment, timestamp, officer_incharge, like, like_no, follow, follow_no, last_status, suggestion;
    final static int PLACE_PICKER_CODE = 1000, REQUEST_CAMERA = 1888, PICK_IMAGE_REQUEST = 2, ENABLE = 1, DISABLE = 2;
    private Button btnsendComment;
    private EditText edtcomment;
    private ImageView pic, like_logo, follow_logo, comment_img, attached_image;
    private Session session;
    private RelativeLayout comment_section;
    LinearLayout sendComment, last_action, suggestion_layer;
    LinearLayout like_region, follow_region;
    ProgressBar loading_img;
    DB_Offline db_offline;
    BottomSheetDialogFragmentStatus statusBottomSheetDialogFragment = null;
    BottomSheetDialogFragmentOption optionBottomSheetDialogFragment = null;
    int affected_no, support_no;

    private File imgFile;
    private Uri tempImgFile;
    private Bitmap selectedImage;
    private ImageCompressionUtils imageCompressionUtils;
    private Boolean can_back = true;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_report);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setting_up();

        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        appBarLayout = findViewById(R.id.app_bar);
        ViewCompat.setElevation(appBarLayout, 10f);
//        appBarLayout.setStateListAnimator(AnimatorInflater.loadStateListAnimator(this, R.animator.appbar_elevation));
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.trans));

        btnsendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = edtcomment.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
                } else
                    uploadImage();
                //uploadImage();
            }
        });

        commentAdapter = new CommentAdapter(SingleReportActivity.this, comments);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        commentlist.setLayoutManager(layoutManager);
        commentlist.setItemAnimator(new DefaultItemAnimator());
        commentlist.setAdapter(commentAdapter);
        commentlist.setFocusable(false);

        fetch_single_date(report_id);
    }

    private void fetch_single_date(final String report_id){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.SINGLE_REPORT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("result", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            db_offline.insertComplaint(jsonObject.getJSONArray("report").getJSONObject(0));
                            getComment(report_id);
                            initiate_data(report_id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SingleReportActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error1", error.toString());
                        Toast.makeText(SingleReportActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("report_id", report_id);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void addComment(final String report_id, final String message){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_AddComment,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Status: ", response);
                        append_new_comment(response);

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(SingleReportActivity.this, "An Error occur, Please try again later " + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("report_id",report_id);
                map.put("user_id", session.getUserID());
                map.put("comment", message);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void append_new_comment(String response){
        Log.d("comment_result", response + " -- ");
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);

            CommentModel comment = new CommentModel(
                    session.getUsername(),
                    jsonObject.getString("message"),
                    jsonObject.getString("created_at"),
                    jsonObject.getInt("user_id"),
                    jsonObject.getInt("id"),
                    jsonObject.optString("image_link")
            );
            comments.add(comment);

//            commentAdapter.notifyDataSetChanged();
            commentAdapter.notifyItemInserted(commentAdapter.getItemCount());
            no_comment.setVisibility(View.GONE);
            edtcomment.setText("");
            attached_image.setVisibility(View.GONE);
            attached_image.setImageResource(0);
            imgFile = null;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    commentlist.getLayoutManager().smoothScrollToPosition(commentlist, null,commentAdapter.getItemCount()-1);
                }
            }, 1000);

            Log.d("comment_result", commentAdapter.getItemCount() - 1 + " Total Item");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getComment(final String report_ID){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GetComment,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Status: ", response);
                        processComment(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("error", error.toString());
                        Toast.makeText(SingleReportActivity.this, "An Error occur, Please try again later " + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("report_id",report_ID);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void processComment(String response){

        try {
            JSONArray jsonArray = new JSONArray(response);

            if(jsonArray.length() != 0){
                int length = jsonArray.length();
                for(int i = 0; i < length; i++){

                    JSONObject result = jsonArray.getJSONObject(i);
                    JSONObject user = result.getJSONObject("user");
                    CommentModel comment = new CommentModel(
                            user.getString("name"),
                            result.getString("message"),
                            result.getString("created_at"),
                            result.getInt("user_id"),
                            result.getInt("id"),
                            result.optString("image_link")
                    );
                    comments.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
                no_comment.setVisibility(View.GONE);

            } else {
//                Toast.makeText(DetailsComplaintActivity.this, "No Comment in this post", Toast.LENGTH_SHORT).show();
                no_comment.setVisibility(View.VISIBLE);
            }

            comment_section.setVisibility(View.VISIBLE);
            sendComment.setVisibility(View.VISIBLE);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initiate_data(final String report_id){

        String result = db_offline.getSingleReport(report_id);
        JSONArray jsonArray;
        final JSONObject jsonObject;
        try {
            jsonArray = new JSONArray(result);
            jsonObject = jsonArray.getJSONObject(0);

            report_title = jsonObject.getString("title");
            reportLink = jsonObject.getString("link");
            url = jsonObject.getString("link");
            status_id = jsonObject.getString("status_id");
            officer_id = jsonObject.getString("officer_id");
            lat = jsonObject.getString("lat");
            lon = jsonObject.getString("lon");
            officer_name = jsonObject.getString("officer_name");
            action = jsonObject.getString("last_action");

            if(status_id.equals("1"))
                status = "Solved";
            else
                status = "Unsolved";

            if(menuItem!=null){

                if(session.getPosition().equals("3")){
                    menuItem.setVisible(false);
                } else if (session.getPosition().equals("2")) {

                    if(!session.getUserID().equals(jsonObject.optString("officer_id", "NULL"))){
                        menuItem.setVisible(false);
                    } else if(jsonObject.getString("status_id").equals("1")){
                        menuItem.setVisible(false);
                    } else
                        menuItem.setVisible(true);

                } else if(session.getPosition().equals("1")){

                    if(jsonObject.getString("status_id").equals("1")){
                        menuItem.setVisible(false);
                    } else
                        menuItem.setVisible(true);

                }
            }

            if(getSupportActionBar()!= null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(report_title);
            }

            collapsingToolbarLayout.setTitle(report_title);

            title.setText(report_title);
            username.setText(jsonObject.getString("username"));
            timestamp.setText(get_time(jsonObject.getString("created_at")));
            category.setText(jsonObject.getString("type_name"));

            if(jsonObject.optString("suggestion", "null").equals("null")){
                suggestion_layer.setVisibility(View.GONE);
            } else {
                suggestion_layer.setVisibility(View.VISIBLE);
                suggestion.setText(jsonObject.getString("suggestion"));
            }


            if(jsonObject.getString("officer_name").equals("NULL")){
                officer_incharge.setText("Waiting admin assign officer");
            } else
                officer_incharge.setText("Assigned to : " + jsonObject.getString("officer_name"));

            desc.setText(jsonObject.getString("description"));
            location.setText(jsonObject.getString("location_name"));

            Log.d("linkss", url);

            Glide.with(this)
                    .load(jsonObject.getString("link"))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Log.d("linkss", "fails");
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Log.d("linkss", "success");
                            return false;
                        }
                    })
                    .crossFade()
                    .into(pic);
            pic.setVisibility(View.VISIBLE);
            JSONObject response = db_offline.ifResponse(report_id);

            if(response.length() != 0){

                if(response.getString("support").equals("1")) {
                    like.setTextColor(Color.BLUE);
                    like_no.setTextColor(Color.BLUE);
                    like_logo.setImageResource(R.drawable.ic_favorite_black_24dp);
                } else {
                    like.setTextColor(Color.BLACK);
                    like_no.setTextColor(Color.BLACK);
                    like_logo.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }

                if(response.getString("affected").equals("1")) {
                    follow.setTextColor(Color.BLUE);
                    follow_no.setTextColor(Color.BLUE);
                    follow_logo.setImageResource(R.drawable.ic_star_black_24dp);
                } else {
                    follow.setTextColor(Color.BLACK);
                    follow_no.setTextColor(Color.BLACK);
                    follow_logo.setImageResource(R.drawable.ic_star_border_black_24dp);
                }

            } else {

                follow.setTextColor(Color.BLACK);
                follow_no.setTextColor(Color.BLACK);
                follow_logo.setImageResource(R.drawable.ic_star_border_black_24dp);

                like.setTextColor(Color.BLACK);
                like_no.setTextColor(Color.BLACK);
                like_logo.setImageResource(R.drawable.ic_favorite_border_black_24dp);

            }
            setResponse(jsonObject.getInt("affected"), jsonObject.getInt("support"));
            like_no.setText(String.format("(%S)",support_no));
            follow_no.setText(String.format("(%S)",affected_no));
            last_status.setText(jsonObject.getString("last_action"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        like_region.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(like.getCurrentTextColor() == Color.BLUE) {

                    like.setTextColor(Color.BLACK);
                    like_no.setTextColor(Color.BLACK);
                    like_logo.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    updateResponse("support", "minus");
                    String support = String.format("(%S)", getSupport_no());
                    like_no.setText(support);

                }
                else {

                    like.setTextColor(Color.BLUE);
                    like_no.setTextColor(Color.BLUE);
                    like_logo.setImageResource(R.drawable.ic_favorite_black_24dp);
                    updateResponse("support", "add");
                    String support = String.format("(%S)", getSupport_no());
                    like_no.setText(support);

                }
            }
        });

        follow_region.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(follow.getCurrentTextColor() == Color.BLUE) {

                    follow.setTextColor(Color.BLACK);
                    follow_no.setTextColor(Color.BLACK);
                    follow_logo.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    updateResponse("follow", "minus");
                    String support = String.format("(%S)", getAffected_no());
                    follow_no.setText(support);

                }
                else {

                    follow.setTextColor(Color.BLUE);
                    follow_no.setTextColor(Color.BLUE);
                    follow_logo.setImageResource(R.drawable.ic_favorite_black_24dp);
                    updateResponse("follow", "add");
                    String support = String.format("(%S)", getAffected_no());
                    follow_no.setText(support);

                }
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleReportActivity.this, ActivityViewOnMap.class);
                intent.putExtra("lat", Double.valueOf(lat));
                intent.putExtra("lon", Double.valueOf(lon));
                intent.putExtra("title", report_title);
                startActivity(intent);
            }
        });

        last_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!report_id.isEmpty()){
                    Intent intent = new Intent(SingleReportActivity.this, ActionActivity.class);
                    intent.putExtra("report_id", report_id);
                    intent.putExtra("officer_name", officer_name);
                    intent.putExtra("status_id", status_id);
                    startActivity(intent);
                }
            }
        });

        final String finalUrl = url;
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleReportActivity.this, ImageFullscreenActivity.class);
                intent.putExtra("image", finalUrl);
                startActivity(intent);
            }
        });
    }

    private String get_time(String time){

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        SimpleDateFormat dateFormat2 = new SimpleDateFormat(
                "EEE, d MMM yyyy HH:mm", Locale.getDefault());

        try {
            Date date = dateFormat.parse(time);
            return String.valueOf(dateFormat2.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "time parse error";

    }

    private void setResponse(int affect, int support){
        this.affected_no = affect;
        this.support_no = support;
    }

    private int getAffected_no(){
        return affected_no;
    }

    private int getSupport_no(){
        return support_no;
    }

    private void updateResponse(String type, String action){

        switch (type){
            case "support":
                if(action.equals("add")){
                    int temp_support = getSupport_no();
                    setResponse(getAffected_no(), temp_support + 1);
                    Log.d("currentsupportclick", getSupport_no() + " ");
                    updateResponse("support", 1, report_id, getSupport_no());
                }

                if(action.equals("minus")){
                    int temp_support = getSupport_no();
                    setResponse(getAffected_no(), temp_support - 1);
                    Log.d("currentsupportclick", getSupport_no() + " ");
                    updateResponse("support", 0, report_id, getSupport_no());
                }
                break;

            case "follow":
                if(action.equals("add")){
                    setResponse(getAffected_no() + 1, getSupport_no());
                    updateResponse("affected", 1, report_id, getAffected_no());
                }

                if(action.equals("minus")){
                    setResponse(getAffected_no() - 1, getSupport_no());
                    updateResponse("affected", 0, report_id, getAffected_no());
                }
                break;
        }
    }

    private void updateResponse(final String type,final int value, final String report_id, final int initial){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.UPDATE_RESPONSE,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("result", response);
                        if(response.equals("success")){
                            db_offline.updateResponse(type, value, session.getUserID(), report_id, initial);
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SingleReportActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("user_id",session.getUserID());
                map.put("report_id", report_id);
                map.put("type", type);
                map.put("value", String.valueOf(value));
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SingleReportActivity.this);
        requestQueue.add(stringRequest);
    }

    private void setting_up(){

        comments = new ArrayList<>();
        commentlist = findViewById(R.id.comment_list_view);
        session = new Session(this);
        db_offline = new DB_Offline(this);
        imageCompressionUtils = new ImageCompressionUtils(this);

        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.readTimeout(300, TimeUnit.SECONDS);
        b.writeTimeout(400, TimeUnit.SECONDS);
        b.retryOnConnectionFailure(false); // Don't retry the connection (prevent twice entry)
        client = b.build();

        if (getIntent() != null) {
            report_id = getIntent().getStringExtra("report_id");
            if(report_id != null)
                session.setCurrentId(report_id);
            else
                report_id = session.getCurrentId();
        } else
            report_id = session.getCurrentId();

        desc = findViewById(R.id.desc);
        location = findViewById(R.id.location_name);
        title = findViewById(R.id.title);
        username = findViewById(R.id.submit_by);
        pic = findViewById(R.id.complaint_image);
        timestamp =  findViewById(R.id.submit_at);
        officer_incharge =  findViewById(R.id.officer_incharge);
        category = findViewById(R.id.category);
        suggestion = findViewById(R.id.tvtSuggestion);
        suggestion_layer = findViewById(R.id.suggestion_layout);

        no_comment = findViewById(R.id.no_comment);
        btnsendComment = findViewById(R.id.btn_send);
        edtcomment =  findViewById(R.id.message);
        comment_section =  findViewById(R.id.commentsection);
        comment_img = findViewById(R.id.comment_img);
        attached_image = findViewById(R.id.attached_image);
        sendComment =  findViewById(R.id.sendComment);

        follow = findViewById(R.id.follow);
        follow_no = findViewById(R.id.follow_no);
        follow_logo = findViewById(R.id.follow_logo);
        like =  findViewById(R.id.like);
        like_no = findViewById(R.id.like_no);
        like_logo = findViewById(R.id.like_logo);
        like_region = findViewById(R.id.like_region);
        follow_region = findViewById(R.id.follow_region);

        loading_img = findViewById(R.id.loading_img);
        last_status =  findViewById(R.id.last_status);
        last_action =  findViewById(R.id.last_action);

        comment_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_single_report, menu);
        menuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == android.R.id.home) {
            if(can_back)
                onBackPressed();
            return true;
        }

        if(itemId == R.id.action_settings){
//            Toast.makeText(SingleReportActivity.this, "More click", Toast.LENGTH_SHORT).show();
            if(session.getPosition().equals("3")) {
                statusBottomSheetDialogFragment = new BottomSheetDialogFragmentStatus();
                statusBottomSheetDialogFragment.setData(status, action, url);
                statusBottomSheetDialogFragment.show(getSupportFragmentManager(), "Dialog");
            } else {

                if(session.getPosition().equals("2") && !officer_id.equals(session.getUserID())){
                    statusBottomSheetDialogFragment = new BottomSheetDialogFragmentStatus();
                    statusBottomSheetDialogFragment.setData(status, action, url);
                    statusBottomSheetDialogFragment.show(getSupportFragmentManager(), "Dialog");
                } else {
                    optionBottomSheetDialogFragment = new BottomSheetDialogFragmentOption();
                    optionBottomSheetDialogFragment.setData(status, action);
                    optionBottomSheetDialogFragment.show(getSupportFragmentManager(), "Dialog");
                }
            }

            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onUserClick(String status, String action_taken) {

        BottomSheetDialogFragmentStatus bottomSheetDialogFragment = new BottomSheetDialogFragmentStatus();
        bottomSheetDialogFragment.setData(status, action, url);
        if(status_id.equals("1"))
            showMessage("This complaint already mark as Solved. If you wish to change please contact system admin");
        else
            bottomSheetDialogFragment.show(getSupportFragmentManager(), "Dialog");

    }

    private void showMessage(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SingleReportActivity.this);
        alertDialogBuilder.setTitle("Restriction");
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    @Override
    public void assignOfficer() {

        if(session.getPosition().equals("1")){
            Intent intent = new Intent(SingleReportActivity.this, AssignActivity.class);
            intent.putExtra("title", title.getText());
            intent.putExtra("desc", desc.getText());
            intent.putExtra("img", reportLink);
            intent.putExtra("report_id", report_id);
            startActivityForResult(intent, 1001);
        } else if (session.getPosition().equals("2")){
            Intent intent = new Intent (SingleReportActivity.this, OfficerActivity.class);
            intent.putExtra("report_id", report_id);
            startActivityForResult(intent, 1002);
        }
        optionBottomSheetDialogFragment.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1001 && resultCode == RESULT_OK){
            officer_name = data.getStringExtra("officer_name");
            String officer = String.format("Assigned to - %S", officer_name);
            officer_incharge.setText(officer);
            if(officer_incharge.getVisibility() != View.VISIBLE)
                officer_incharge.setVisibility(View.VISIBLE);
        }

        Log.d("report id", resultCode +" code");

        if(requestCode == 1002 && resultCode == RESULT_OK){
            last_status.setText(data.getStringExtra("action_taken"));
            if(data.getStringExtra("status_id").equals("1")){
                menuItem.setVisible(false);
                status_id = "1";
            }
        }

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            imagePath = tempImgFile.getPath();
            Log.d("TAG", "File Saved::--->" + tempImgFile.getPath());
            if(Build.VERSION.SDK_INT> 23) {
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tempImgFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
                selectedImage = BitmapFactory.decodeFile(imagePath);
            imagePath = imageCompressionUtils.saveImage(selectedImage, this); /* change the image path to modified image */
            Log.d("TAG", "File Saved::--->" + imagePath);
            imgFile = new File(imagePath);
            Bitmap preview = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            attached_image.setImageBitmap(Bitmap.createScaledBitmap(preview, preview.getWidth() / 2 , preview.getHeight() / 2, false));
            attached_image.setVisibility(View.VISIBLE);
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.d("result:","result code: " + uri.getLastPathSegment());
            String path = imageCompressionUtils.compressImage(uri);
            try {
                Uri imageUri = data.getData();
                imgFile = new File(path);
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = getResizedBitmap(selectedImage, selectedImage.getWidth() / 2, selectedImage.getHeight() / 2);
                attached_image.setImageBitmap(selectedImage);
                attached_image.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                Log.d("bitmapE", String.valueOf(bitmap));
                e.printStackTrace();
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight){

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    /* Image selection */
     /* Select camera or image from gallery */
    public void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(SingleReportActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(SingleReportActivity.this);
                boolean write_external = Utility.check_write_external_permission(SingleReportActivity.this);
                if (items[item].equals("Take Photo")) {
                    if(result && write_external)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent(){

        Intent intent;
        if(Build.VERSION.SDK_INT  < 24) {
            intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            tempImgFile = Uri.fromFile(createImageFile());
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            tempImgFile = FileProvider.getUriForFile(SingleReportActivity.this, "kuchingitsolution.betterpepperboard.provider", getOutputMediaFile());
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgFile);
        startActivityForResult(intent, REQUEST_CAMERA);

    }

    private void galleryIntent(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(Intent.createChooser(intent, "Select File"),PICK_IMAGE_REQUEST);
    }

    private File createImageFile() {

        long timeStamp = System.currentTimeMillis();
        String imageFileName = "NAME_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/BetteCity");

        if(!storageDir.exists())
            storageDir.mkdirs();

        File images = null;
        try {
            images = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return images;
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Better City");

        if (!mediaStorageDir.exists()){
            boolean is_dir_make = mediaStorageDir.mkdirs();
            if (!is_dir_make){
                Log.d("error", "null thing");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    @SuppressLint("StaticFieldLeak")
    private void uploadImage(){
        new AsyncTask<String, Integer, String>(){

            private AlertDialog.Builder alert;
            private AlertDialog ad;

            private ProgressBar dialog_upload;
            private TextView dialog_upload_status;
            private ImageView status_done;
            private AlertDialog upload_dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                can_back = false;
                alert = new AlertDialog.Builder(SingleReportActivity.this);
                LayoutInflater inflater = SingleReportActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.bottom_sheet_upload, null);

                dialog_upload = dialogView.findViewById(R.id.uploading);
                dialog_upload_status = dialogView.findViewById(R.id.uploading_progress);
                status_done = dialogView.findViewById(R.id.status_done);

                alert.setView(dialogView);
                alert.setCancelable(false);
                alert.create();
                ad = alert.show();
            }

            @Override
            protected String doInBackground(String... strings) {

                /*map.put("report_id",report_id);
                map.put("user_id", session.getUserID());
                map.put("comment", message);*/
                MultipartBody body = null;
                if(imgFile != null) {
                    body = RequestBuilder.uploadImageComment(
                            report_id,
                            session.getUserID(),
                            message, imgFile);
                } else {
                    body = RequestBuilder.addCommentNoImage(
                            report_id,
                            session.getUserID(),
                            message
                    );
                }

                CountingRequestBody monitoring = new CountingRequestBody(body, new CountingRequestBody.Listener() {
                    @Override
                    public void onRequestProgress(long bytesWritten, long contentLength) {

                        float percentage = 100f * bytesWritten / contentLength;
                        if (percentage >= 0) {
                            publishProgress((int)percentage);
                            Log.d("upload", String.valueOf(percentage));
                        }
                    }
                });

                try {
                    return ApiCall.POST(client, Config.URL_AddComment, monitoring);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                dialog_upload.setProgress(values[0]);
                dialog_upload_status.setText(String.valueOf(values[0]));
                if(values[0] == 100) {
                    status_done.setVisibility(View.VISIBLE);
                    dialog_upload.setIndeterminate(true);
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s != null) {
                    append_new_comment(s);
                } else showMessage("Error while uploading complaint, please try again");
                disable_interaction(ENABLE);
                ad.dismiss();
                can_back = true;
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        if(can_back)
            super.onBackPressed();
    }

    private void disable_interaction(int status){

        switch (status){
            case 1:
                btnsendComment.setEnabled(true);
                break;
            case 2:
                btnsendComment.setEnabled(false);
                break;
        }

    }
}
