package kuchingitsolution.betterpepperboard.action;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.complaint.DetailsComplaintActivity;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.Session;

public class ActionActivity extends AppCompatActivity{

    private RecyclerView action_recycler_view;
    private ActionAdapter actionAdapter;
    private List<ActionModel> actionModels;
    private TextView tvStatus, tvOfficerName;
    String report_id, officer_name, status_id;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        if(getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(false);

        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvOfficerName = (TextView) findViewById(R.id.tvOfficerName);
        session = new Session(this);

        if(getIntent() != null){
            report_id = getIntent().getStringExtra("report_id");
            officer_name = getIntent().getStringExtra("officer_name");
            status_id = getIntent().getStringExtra("status_id");

            if(status_id.equals("1"))
                tvStatus.setText("Solved");
            else tvStatus.setText("Unsolved");

            if(officer_name.equals("NULL"))
                tvOfficerName.setText("No Officer Assigned");
            else
                tvOfficerName.setText(officer_name);
        }

        Log.d("report_id", " report id : " + report_id);

        actionModels = new ArrayList<>();
        action_recycler_view = (RecyclerView) findViewById(R.id.action_list);
        actionAdapter = new ActionAdapter(this, actionModels);
        action_recycler_view.setAdapter(actionAdapter);
        action_recycler_view.setLayoutManager(new LinearLayoutManager(ActionActivity.this));
        action_recycler_view.setNestedScrollingEnabled(false);

        action_recycler_view.setItemViewCacheSize(20);
//        action_recycler_view.setDrawingCacheEnabled(true);
//        action_recycler_view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        get_action();
    }

    private void get_action(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_ACTION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Status: ", response);
                        process_action(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("Error", error.toString());

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("report_id",report_id);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void process_action(String result){

        try {
            JSONArray jsonArray = new JSONArray(result);
            int length = jsonArray.length();

            for (int i = 0; i < length; i++)
            {
                JSONObject action = jsonArray.getJSONObject(i);

                ActionModel actionModel = new ActionModel(
                        action.getString("action_taken"),
                        action.getString("created_at"),
                        action.optString("link"), action.getString("report_id"),
                        action.getInt("current_status_id"), action.optInt("media_type", 0));

                actionModels.add(actionModel);
            }

            actionAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        switch (itemId){
            case android.R.id.home:
                super.onBackPressed();
                Log.d("report_id", "pressed");
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onPause() {
        super.onPause();
        session.setCurrentId(report_id);
        Log.d("onpause", "pause " + report_id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.setCurrentId(report_id);
        Log.d("onpause", "pause " + report_id);
    }
}
