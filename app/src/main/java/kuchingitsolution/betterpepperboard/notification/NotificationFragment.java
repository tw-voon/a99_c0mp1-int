package kuchingitsolution.betterpepperboard.notification;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import kuchingitsolution.betterpepperboard.MainActivity2;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Session;

public class NotificationFragment extends Fragment {

    RecyclerView list_notification;
    NotificationAdapter notificationAdapter;
    ArrayList<NotificationModel> notificationModels = new ArrayList<>();
    ProgressBar loading;
    TextView no_content;
    Session session;
    DB_Offline db_offline;

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
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getView() != null){
            list_notification = getView().findViewById(R.id.list_notification);
            notificationAdapter = new NotificationAdapter(getActivity(), notificationModels);
            list_notification.setAdapter(notificationAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            list_notification.setLayoutManager(linearLayoutManager);
            list_notification.setNestedScrollingEnabled(false);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list_notification.getContext(),
                    linearLayoutManager.getOrientation());
            list_notification.addItemDecoration(dividerItemDecoration);
            db_offline = new DB_Offline(getActivity());
            no_content = (TextView) getView().findViewById(R.id.no_notification);
            no_content.setVisibility(View.GONE);
            loading = getView().findViewById(R.id.loading);
            load_view(db_offline.get_notification());
        }
        session = new Session(getActivity());
        getOwnActivity();
    }

    private void getOwnActivity(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_NOTI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Status75: ", response);
                        if(response.equals("empty")) {
                            no_content.setVisibility(View.VISIBLE);
                            db_offline.deleteNotification();
                            if(notificationModels.size() > 0)
                                notificationModels.clear();
//                            Toast.makeText(getActivity(), "empty", Toast.LENGTH_SHORT).show();
                        }
                        else
                            processActivity(response);
                        loading.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "An Error occur, Please try again later " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("user_id",session.getUserID());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
//
    private void processActivity(String response){
        no_content.setVisibility(View.GONE);

        if(response.equals("empty")) {
            no_content.setVisibility(View.VISIBLE);
        }

        db_offline.deleteNotification();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("noti");
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject notification = jsonArray.getJSONObject(i);
                NotificationModel myActivityModel = new NotificationModel(
                        notification.getString("report_id"),
                        notification.getString("content"),
                        notification.getString("created_at"),
                        " ",
                        notification.getString("id")
                );
//                notificationModels.add(myActivityModel);
                db_offline.insertNotification(myActivityModel);
            }
            load_view(db_offline.get_notification());
//            notificationAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void load_view(Cursor cursor){

        if(notificationModels.size() > 0)
            notificationModels.clear();

        if(cursor.moveToFirst()){
            Log.d("Result complaint", DatabaseUtils.dumpCursorToString(cursor));

            do {
                NotificationModel myActivityModel = new NotificationModel(
                        cursor.getString(1), cursor.getString(2), cursor.getString(5),
                        cursor.getString(3), cursor.getString(4)
                );
                notificationModels.add(myActivityModel);

            } while (cursor.moveToNext());
            no_content.setVisibility(View.GONE);
        } else { Log.d("Result == ", "NO"); no_content.setVisibility(View.VISIBLE); }

        notificationAdapter.notifyDataSetChanged();
        cursor.close();
        db_offline.close();
    }

    @Override
    public void onResume() {
        ((MainActivity2)getActivity()).removeBadgeAt(3);
        super.onResume();
    }
}
