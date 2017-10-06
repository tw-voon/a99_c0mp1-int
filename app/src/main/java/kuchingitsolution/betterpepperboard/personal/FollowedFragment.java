package kuchingitsolution.betterpepperboard.personal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Map;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.Session;

public class FollowedFragment extends Fragment{

    RecyclerView my_followed_list;
    ReportAdapter followedAdapter;
    TextView no_content;
    ArrayList<ReportModel> followedModelArrayList = new ArrayList<>();
    Session session;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_followed, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getView() != null){
            no_content = getView().findViewById(R.id.no_content);
            my_followed_list = (RecyclerView) getView().findViewById(R.id.my_followed_list);
            followedAdapter = new ReportAdapter(getActivity(), followedModelArrayList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            my_followed_list.setLayoutManager(new LinearLayoutManager(getActivity()));
            my_followed_list.setAdapter(followedAdapter);
            my_followed_list.setNestedScrollingEnabled(false);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(my_followed_list.getContext(),
                    linearLayoutManager.getOrientation());
            my_followed_list.addItemDecoration(dividerItemDecoration);
        }
        session = new Session(getActivity());
        get_own_report();
    }

    private void get_own_report(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_FOLLOWED_REPORT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("result", response);
                        process_report(response);
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

    private void process_report(String result){
        if(result.equals("empty")){
            no_content.setVisibility(View.VISIBLE);
        } else {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray report = jsonObject.getJSONArray("affected_report");
                int length = report.length();

                for(int i = 0; i < length ; i++){
                    JSONObject data = report.getJSONObject(i);
                    JSONObject media = data.getJSONObject("media");
                    JSONObject user = data.getJSONObject("user");
                    JSONObject category = data.getJSONObject("category");
                    JSONObject location = data.getJSONObject("location");
                    ReportModel followedModel = new ReportModel(
                            user.getString("name"), category.getString("name"),
                            data.getString("created_at"), data.getString("title"),
                            media.getString("link"), data.getString("id"),
                            data.getString("status_id"), location.getString("name")
                    );
                    followedModelArrayList.add(followedModel);
                }

                followedAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
