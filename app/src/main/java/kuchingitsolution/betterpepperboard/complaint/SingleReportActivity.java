package kuchingitsolution.betterpepperboard.complaint;

import android.animation.AnimatorInflater;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.action.ActionActivity;
import kuchingitsolution.betterpepperboard.admin.AssignActivity;
import kuchingitsolution.betterpepperboard.helper.BottomSheetDialogFragmentOption;
import kuchingitsolution.betterpepperboard.helper.BottomSheetDialogFragmentStatus;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Session;
import kuchingitsolution.betterpepperboard.map.ActivityViewOnMap;
import kuchingitsolution.betterpepperboard.officer.OfficerActivity;

public class SingleReportActivity extends AppCompatActivity implements BottomSheetDialogFragmentOption.OptionBottomSheetCallback {

    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;
    MenuInflater menuInflater;
    MenuItem menuItem;

    private List<CommentModel> comments;
    private RecyclerView commentlist;
    private CommentAdapter commentAdapter;
    private String action, reportLink, report_id, status_id, officer_id, officer_name, lat, lon, report_title, url, status;
    TextView category, desc, location, title, username, no_comment, timestamp, officer_incharge, like, like_no, follow, follow_no, last_status, suggestion;
    Button btnsendComment;
    EditText edtcomment;
    ImageView pic, like_logo, follow_logo;
    Session session;
    RelativeLayout comment_section;
    LinearLayout sendComment, last_action, suggestion_layer;
    LinearLayout like_region, follow_region;
    ProgressBar loading_img;
    DB_Offline db_offline;
    BottomSheetDialogFragmentStatus statusBottomSheetDialogFragment = null;
    BottomSheetDialogFragmentOption optionBottomSheetDialogFragment = null;
    int affected_no, support_no;

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
                String msg = edtcomment.getText().toString().trim();
                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
                } else
                    addComment(report_id, msg);
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
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);

                            CommentModel comment = new CommentModel(
                                    session.getUsername(),
                                    jsonObject.getString("message"),
                                    jsonObject.getString("created_at"),
                                    jsonObject.getInt("user_id"),
                                    jsonObject.getInt("id"),
                                    session.getUserAvatar()
                            );
                            comments.add(comment);

                            commentAdapter.notifyDataSetChanged();
                            no_comment.setVisibility(View.GONE);
                            edtcomment.setText("");
                            commentlist.getLayoutManager().smoothScrollToPosition(commentlist, null, commentAdapter.getItemCount() - 1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                            " "
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
            timestamp.setText(jsonObject.getString("created_at"));
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
                            loading_img.setVisibility(View.GONE);
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

//         if(requestCode == 1003 && resultCode == RESULT_OK){
//             report_id = data.getStringExtra("report_id");
//             Log.d("report_id", "  id : " + report_id);
//         }
    }
}
