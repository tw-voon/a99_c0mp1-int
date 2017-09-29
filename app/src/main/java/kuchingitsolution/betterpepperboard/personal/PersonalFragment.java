package kuchingitsolution.betterpepperboard.personal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.complaint.GetComplaint;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.Session;

public class PersonalFragment extends Fragment {

    FrameLayout report_frame;
    TextView showReport, showFollowed, status_1, status_2, status_3;
    TextView username, total_complaint, solved_complaint, followed_complaint;
    Session session;
    GetComplaint getComplaint;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getView() != null){
            report_frame = (FrameLayout) getView().findViewById(R.id.content_personal);
            session = new Session(getContext());
            getComplaint = new GetComplaint(getContext());
            showReport = getView().findViewById(R.id.showReport);
            showFollowed = getView().findViewById(R.id.showFollowed);
            total_complaint = (TextView) getView().findViewById(R.id.total_complaint);
            solved_complaint = (TextView) getView().findViewById(R.id.solved_complaint);
            followed_complaint = (TextView) getView().findViewById(R.id.followed_report);
            status_1 = getView().findViewById(R.id.status_1);
            status_2 = getView().findViewById(R.id.status_2);
            status_3 = getView().findViewById(R.id.status_3);

            if(session.getPosition().equals("1")){
                setTextView("Total Complaint", "Solved", "Unsolved", "Unassigned", "Assigned");
            } else if(session.getPosition().equals("2")){
                setTextView("Assigned Complaint", "Solved", "Unsolved", "Unsolved", "Solved");
            } else {
                setTextView("Submitted Complaint", "Solved", "Followed", "My Report", "Followed Report");
            }

            showReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setFragment(new ReportFragment());
                }
            });

            showFollowed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setFragment(new FollowedFragment());
                }
            });

            session = new Session(getActivity());
            username = (TextView) getView().findViewById(R.id.username);
            username.setText(session.getUsername());
            //default fragment
            setFragment(new ReportFragment());
        }
    }

    private void setTextView(String label_1, String label_2, String label_3, String tab_1, String tab_2){

        showReport.setText(tab_1);
        showFollowed.setText(tab_2);
        status_1.setText(label_1);
        status_2.setText(label_2);
        status_3.setText(label_3);

    }

    private void get_details(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("result", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            total_complaint.setText(jsonObject.getString("total_report"));
                            solved_complaint.setText(jsonObject.getString("solved_report"));
                            followed_complaint.setText(jsonObject.getString("followed_report"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error1", error.toString());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("user_id", session.getUserID());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        getComplaint.pull_complaint();
        get_details();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(true);
    }

    protected void setFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.content_personal, fragment);
                ft.commit();
            }
        }
    }
}
