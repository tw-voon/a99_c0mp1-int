package kuchingitsolution.betterpepperboard.complaint;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import de.hdodenhof.circleimageview.CircleImageView;
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

public class DetailsComplaintActivity extends AppCompatActivity implements BottomSheetDialogFragmentOption.OptionBottomSheetCallback{

    private List<CommentModel> comments;
    private RecyclerView commentlist;
    private CommentAdapter commentAdapter;
    private String status, action, imgLink, reportLink, report_id, status_id, officer_id, officer_name, lat, lon, report_title;
    TextView desc, location, title, username, no_comment, timestamp, officer_incharge, affected, supported, last_status;
    Button btnsendComment, affect, support;
    EditText edtcomment;
    ImageView pic, extraoption;
    CircleImageView user_profile;
    Session session;
    RelativeLayout inforpart, commentsection;
    LinearLayout sendComment, last_action;
    ProgressBar loading, loading_img;
    DB_Offline db_offline;
    BottomSheetDialogFragmentStatus statusBottomSheetDialogFragment = null;
    BottomSheetDialogFragmentOption optionBottomSheetDialogFragment = null;
    int affected_no, support_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_complaint);

        Log.d("view_order", "oncreate");

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

        Log.d("report_id", report_id + " ;" + session.getCurrentId());

        desc = findViewById(R.id.post_description);
        location = findViewById(R.id.locationName);
        title = findViewById(R.id.stickyView);
        username = findViewById(R.id.username);
        user_profile = findViewById(R.id.profile_image);
        pic = findViewById(R.id.heroImageView);
        extraoption = findViewById(R.id.extraoption);
        extraoption.setVisibility(View.VISIBLE);
        no_comment = findViewById(R.id.no_comment);
        btnsendComment = findViewById(R.id.btn_send);
        affected = findViewById(R.id.affected);
        supported =  findViewById(R.id.supported);
        affect = findViewById(R.id.affect);
        support =  findViewById(R.id.support);
        edtcomment =  findViewById(R.id.message);
        timestamp =  findViewById(R.id.timestamp);
        commentsection =  findViewById(R.id.commentsection);
        inforpart =  findViewById(R.id.info_part);
        sendComment =  findViewById(R.id.sendComment);
        loading =  findViewById(R.id.loading);
        loading_img = findViewById(R.id.loading_img);
        officer_incharge =  findViewById(R.id.officer_incharge);
        last_status =  findViewById(R.id.last_status);
        last_action =  findViewById(R.id.last_action);

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

        if(session.getPosition().equals("3")) {
            extraoption.setVisibility(View.GONE);
        }

        extraoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(session.getPosition().equals("3")) {
                    statusBottomSheetDialogFragment = new BottomSheetDialogFragmentStatus();
                    statusBottomSheetDialogFragment.setData(status, action, imgLink);
                    statusBottomSheetDialogFragment.show(getSupportFragmentManager(), "Dialog");
                } else {

                    if(session.getPosition().equals("2") && !officer_id.equals(session.getUserID())){
                        statusBottomSheetDialogFragment = new BottomSheetDialogFragmentStatus();
                        statusBottomSheetDialogFragment.setData(status, action, imgLink);
                        statusBottomSheetDialogFragment.show(getSupportFragmentManager(), "Dialog");
                    } else {
                        optionBottomSheetDialogFragment = new BottomSheetDialogFragmentOption();
                        optionBottomSheetDialogFragment.setData(status, action);
                        optionBottomSheetDialogFragment.show(getSupportFragmentManager(), "Dialog");
                    }
                }
            }
        });

        commentAdapter = new CommentAdapter(DetailsComplaintActivity.this, comments);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        commentlist.setLayoutManager(layoutManager);
        commentlist.setItemAnimator(new DefaultItemAnimator());
        commentlist.setAdapter(commentAdapter);
        commentlist.setFocusable(false);
        commentlist.setNestedScrollingEnabled(false);

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
                            initiatedata(report_id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DetailsComplaintActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error1", error.toString());
                        Toast.makeText(DetailsComplaintActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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

    private void initiatedata(final String report_id){

        String result = db_offline.getSingleReport(report_id);
        JSONArray jsonArray;
        final JSONObject jsonObject;
        String url = null;
        try {
            jsonArray = new JSONArray(result);
            jsonObject = jsonArray.getJSONObject(0);
            username.setText(jsonObject.getString("username"));
            user_profile.setImageResource(R.drawable.ic_person_outline_black_24dp);
            title.setText(jsonObject.getString("title"));
            report_title = jsonObject.getString("title");
            desc.setText(jsonObject.getString("description"));
            url = jsonObject.getString("link");
            Glide.with(DetailsComplaintActivity.this)
                    .load(jsonObject.getString("link"))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            loading_img.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .skipMemoryCache(false)
                    .into(pic);
            reportLink = jsonObject.getString("link");
            timestamp.setText(jsonObject.getString("created_at"));
            String locate = String.format("at - %s", jsonObject.getString("location_name"));
            location.setText(locate);
            status_id = jsonObject.getString("status_id");
            officer_id = jsonObject.getString("officer_id");
            lat = jsonObject.getString("lat");
            lon = jsonObject.getString("lon");

            if(session.getPosition().equals("1"))
                extraoption.setVisibility(View.VISIBLE);

            if(jsonObject.getString("status_id").equals("1")) {
                status = "In progress";
                imgLink = "null";
                extraoption.setVisibility(View.GONE);
            }
            else if (jsonObject.getString("status_id").equals("2")) {
                status = "Solved";
                imgLink = jsonObject.getString("link");
            }
//            reason = jsonObject.getString("reason");
            action = jsonObject.getString("last_action");
            String tvOfficer = String.format("Assigned to - %S", jsonObject.getString("officer_name"));
            String tvSupport = String.format("%s Supported", jsonObject.getString("support"));
            String tvAffected = String.format("%s Affected", jsonObject.getString("affected"));
            setResponse(jsonObject.getInt("affected"), jsonObject.getInt("support"));
            supported.setText(tvSupport);
            affected.setText(tvAffected);

            if(jsonObject.getString("officer_name").equals("NULL")){
                officer_incharge.setVisibility(View.GONE);
            } else
                officer_incharge.setText(tvOfficer);

            if(!session.getUserID().equals(jsonObject.optString("officer_id", "NULL")) && session.getPosition().equals("2")){
                extraoption.setVisibility(View.GONE);
            }

            officer_name = jsonObject.getString("officer_name");
            last_status.setText(jsonObject.getString("last_action"));

            JSONObject response = db_offline.ifResponse(report_id);

            if(response.length() != 0){

                if(response.getString("support").equals("1")) {
                    support.setTextColor(Color.BLUE);
                }

                if(response.getString("affected").equals("1"))
                    affect.setTextColor(Color.BLUE);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        inforpart.setVisibility(View.VISIBLE);

        affect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(affect.getCurrentTextColor() == Color.BLUE){
                    /* deduct affected quantity */
                    updateResponse("affected", "minus");
//                    complaintModel.noAffected();
                    String tvAffected = String.format("%s Affected", getAffected_no());
                    affected.setText(tvAffected);
                    affect.setTextColor(Color.BLACK);
                } else {
                    updateResponse("affected", "add");
//                    complaintModel.affected();
                    String tvAffected = String.format("%s Affected", getAffected_no());
                    affected.setText(tvAffected);
                    affect.setTextColor(Color.BLUE);
                }
            }
        });

        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(support.getCurrentTextColor() == Color.BLUE){
                    updateResponse("support", "minus");
//                    complaintModel.noSupport();
                    String tvSupported = String.format("%s Supported", getSupport_no());
                    supported.setText(tvSupported);
                    support.setTextColor(Color.BLACK);
                } else {
                    updateResponse("support", "add");
//                    complaintModel.support();
                    String tvSupported = String.format("%s Supported", getSupport_no());
                    supported.setText(tvSupported);
                    support.setTextColor(Color.BLUE);
                }
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsComplaintActivity.this, ActivityViewOnMap.class);
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
                    Intent intent = new Intent(DetailsComplaintActivity.this, ActionActivity.class);
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
                Intent intent = new Intent(DetailsComplaintActivity.this, ImageFullscreenActivity.class);
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

            case "affected":
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
                        Toast.makeText(DetailsComplaintActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(DetailsComplaintActivity.this);
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

                        Toast.makeText(DetailsComplaintActivity.this, "An Error occur, Please try again later " + error.toString(), Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(DetailsComplaintActivity.this, "An Error occur, Please try again later " + error.toString(), Toast.LENGTH_SHORT).show();

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

            commentsection.setVisibility(View.VISIBLE);
            sendComment.setVisibility(View.VISIBLE);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        loading.setVisibility(View.GONE);
    }

    @Override
    public void onUserClick(String status, String action_taken) {

        BottomSheetDialogFragmentStatus bottomSheetDialogFragment = new BottomSheetDialogFragmentStatus();
        bottomSheetDialogFragment.setData(status, action, imgLink);
        if(status_id.equals("1"))
            showMessage("This complaint already mark as Solved. If you wish to change please contact system admin");
        else
            bottomSheetDialogFragment.show(getSupportFragmentManager(), "Dialog");

    }

    private void showMessage(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetailsComplaintActivity.this);
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
            Intent intent = new Intent(DetailsComplaintActivity.this, AssignActivity.class);
            intent.putExtra("title", title.getText());
            intent.putExtra("desc", desc.getText());
            intent.putExtra("img", reportLink);
            intent.putExtra("report_id", report_id);
            startActivityForResult(intent, 1001);
        } else if (session.getPosition().equals("2")){
            Intent intent = new Intent (DetailsComplaintActivity.this, OfficerActivity.class);
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
                 extraoption.setVisibility(View.GONE);
                 status_id = "1";
             }
         }

//         if(requestCode == 1003 && resultCode == RESULT_OK){
//             report_id = data.getStringExtra("report_id");
//             Log.d("report_id", "  id : " + report_id);
//         }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent (SinglePost.this, NewsActivity.class);
//        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("view_order", "onresume");
//        initiatedata(report_id);
//        Log.d("report_id", " report: " + report_id);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
