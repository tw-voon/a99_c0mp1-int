package kuchingitsolution.betterpepperboard.complaint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Session;

public class UnresolveFragment extends Fragment {

    private RecyclerView complaintlist;
    private ComplaintAdapter complaintAdapter;
    ProgressBar loading;
    ArrayList<ComplaintModel> newsData = new ArrayList<>();
    TextView noContent;
    DB_Offline db_offline;
    Session session;
    int item = 0;
    boolean loadmore = false, temp_finish = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_unresolve, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getView() != null){

            complaintlist = (RecyclerView) getView().findViewById(R.id.unresolve_report);
            loading = (ProgressBar) getView().findViewById(R.id.loading);
            loading.setVisibility(View.VISIBLE);
            noContent = (TextView) getView().findViewById(R.id.no_content);
            noContent.setVisibility(View.GONE);
            db_offline = new DB_Offline(getContext());
            session = new Session(getContext());

            complaintAdapter = new ComplaintAdapter(getContext(), newsData);
            complaintlist.setAdapter(complaintAdapter);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            complaintlist.setLayoutManager(linearLayoutManager);
            complaintlist.setNestedScrollingEnabled(false);
            complaintlist.setItemAnimator(new SlideUpAnimator());
            complaintlist.setItemViewCacheSize(0);

            complaintlist.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int visibleItemCount = linearLayoutManager.getChildCount();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                    if (pastVisibleItems + visibleItemCount == totalItemCount && loadmore) {
                        Log.d("OnScroll", "Past Visible : " + pastVisibleItems + " visibleItem : " + visibleItemCount + " total : " + totalItemCount);
                        Log.d("OnScroll", "Current page : " + complaintAdapter.getItemCount());
                        if(complaintAdapter.getItemCount() < db_offline.get_total(2)) {
                            Log.d("Called", "Called --- " + totalItemCount);
                            loadmore = false;
                            initiatedata(totalItemCount);
                        }
                    }
                }
            });
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            initiatedata(complaintAdapter.getItemCount());
        }
    };

    private void initiatedata(int page){

        final String result = db_offline.getComplaint(2, page);
        Log.d("result2", result);

        if(result.equals("[]")) {
            if(complaintAdapter.getItemCount() > 0)
                complaintAdapter.notifyDataSetChanged();
            loading.setVisibility(View.GONE);
            return;
        }

        if(complaintAdapter.getItemCount() == db_offline.get_total(2))
            return;

        final int cursize = complaintAdapter.getItemCount();

        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                try {

                    JSONArray jsonArray = new JSONArray(result);
                    int length = jsonArray.length();
                    for (int i = 0 ; i < length ; i ++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if(jsonObject.getString("status_id").equals("1"))
                            continue;
                        ComplaintModel news = new ComplaintModel(jsonObject);
                        newsData.add(news);
                    }

                    if(length == 0)
                        noContent.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                complaintAdapter.notifyItemRangeInserted(cursize, newsData.size());
                Log.d("compare", "db : " + db_offline.get_total(2) + "  adapter :" + complaintAdapter.getItemCount());
                loadmore = db_offline.get_total(2) >= complaintAdapter.getItemCount();
            }
        };

        handler.post(r);
        complaintlist.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter("custom-event-name"));
        loading.setVisibility(View.VISIBLE);
        complaintAdapter.clear();
//        if(complaintAdapter.getItemCount() > 0)
//            complaintAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }
}
