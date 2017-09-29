package kuchingitsolution.betterpepperboard.admin;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.action.ActionActivity;
import kuchingitsolution.betterpepperboard.complaint.DetailsComplaintActivity;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.Session;

import static kuchingitsolution.betterpepperboard.R.id.desc;

public class AssignActivity extends AppCompatActivity implements AssignAdapter.AssignOption {

    private AssignAdapter assignAdapter;
    private RecyclerView recyclerView;
    private ArrayList<AssignModel> officerModels = new ArrayList<>();
    private String title, desc, imgLink, report_id, officer_id, officer_name;
    private TextView tvTitle, tvDesc, tvOfficername;
    private ImageView profileImg;
    private LinearLayout linearLayout;
    private MenuItem item;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent() != null)
            report_id = getIntent().getStringExtra("report_id");

        recyclerView = (RecyclerView) findViewById(R.id.officer);
        assignAdapter = new AssignAdapter(AssignActivity.this, officerModels);
        recyclerView.setAdapter(assignAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(AssignActivity.this));
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvDesc = (TextView) findViewById(R.id.tvDesc);
        tvOfficername = (TextView) findViewById(R.id.tvOfficerName);
        profileImg = (ImageView) findViewById(R.id.reportImg);
        linearLayout = (LinearLayout) findViewById(R.id.officer_details);
        session = new Session(this);

        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");
        imgLink = getIntent().getStringExtra("img");
        report_id = getIntent().getStringExtra("report_id");

        tvTitle.setText(title);
        tvDesc.setText(desc);
        Picasso.with(this).load(imgLink).into(profileImg);

        get_free_officer();
    }

    private void assign_officer(final String officer_id){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ASSIGN_OFFICER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("status: ", response);
                        if(response.equals("success")){
                            Intent intent = new Intent(AssignActivity.this, DetailsComplaintActivity.class);
                            intent.putExtra("report_id", report_id);
                            intent.putExtra("officer_name", officer_name);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(AssignActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("error", error.toString());
                        Toast.makeText(AssignActivity.this, "An Error occur, Please try again later " + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("admin_id", session.getUserID());
                map.put("officer_id",officer_id);
                map.put("report_id", report_id);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void get_free_officer(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_OFFICER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("officer: ", response);
                        process_officer(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("error", error.toString());
                        Toast.makeText(AssignActivity.this, "An Error occur, Please try again later " + error.toString(), Toast.LENGTH_SHORT).show();

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

    private void process_officer(String result){
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("free_officer");
            JSONObject assigned = jsonObject.optJSONObject("officer_assigned");

            if(assigned != null){
                tvOfficername.setText(assigned.getString("name"));
                linearLayout.setVisibility(View.VISIBLE);
            }

            int length = jsonArray.length();

            for(int i = 0; i < length ; i++){
                JSONObject officer = jsonArray.getJSONObject(i);
                AssignModel assignModel = new AssignModel(officer.getString("id"), officer.getString("name"), officer.getString("avatar_link"));
                officerModels.add(assignModel);
            }

            assignAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        switch (itemId){
            case android.R.id.home:
                onBackPressed();
                Log.d("report_id", "pressed");
                break;

            case R.id.submit:
                Toast.makeText(this, "Submit selected", Toast.LENGTH_SHORT)
                        .show();
                assign_officer(officer_id);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.submit, menu);
        item = menu.findItem(R.id.submit);
        item.setVisible(false);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onOfficerClick(String officer_id, String name, Boolean checked) {
        item.setVisible(checked);
        Toast.makeText(AssignActivity.this, "Status: " + officer_id, Toast.LENGTH_SHORT).show();
        this.officer_id = officer_id;
        this.officer_name = name;
    }
}
