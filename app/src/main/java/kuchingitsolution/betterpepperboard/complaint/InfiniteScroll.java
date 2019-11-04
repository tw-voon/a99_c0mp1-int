package kuchingitsolution.betterpepperboard.complaint;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.srx.widget.PullCallback;
import com.srx.widget.PullToLoadView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Session;
import kuchingitsolution.betterpepperboard.helper.network.RequestBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfiniteScroll {

    Context c;
    RecyclerView rv;
    private PullToLoadView pullToLoadView;
    private ComplaintAdapter adapter;
    private boolean isLoading = false;
    private boolean hasLoadedAll = false;
    private Session session;
    private DB_Offline db_offline;
    private int nextPage;

    public InfiniteScroll(Context c, PullToLoadView pullToLoadView) {
        this.c = c;
        this.pullToLoadView = pullToLoadView;
        //RECYCLERVIEW
        RecyclerView rv = pullToLoadView.getRecyclerView();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(c.getApplicationContext());
        rv.setLayoutManager(linearLayoutManager);
        ArrayList<ComplaintModel> newslist = new ArrayList<>();
        adapter=new ComplaintAdapter(c,newslist);
        rv.setAdapter(adapter);
        session = new Session(c.getApplicationContext());
        db_offline = new DB_Offline(c.getApplicationContext());
        initializePaginator();
    }

    public void initializePaginator()
    {
        pullToLoadView.isLoadMoreEnabled(true);
        pullToLoadView.setPullCallback(new PullCallback() {
            //LOAD MORE DATA
            @Override
            public void onLoadMore() {
                loadData(nextPage);
            }
            //REFRESH AND TAKE US TO FIRST PAGE
            @Override
            public void onRefresh() {
                adapter.clear();
                hasLoadedAll=false;
                loadData(1);
            }
            //IS LOADING
            @Override
            public boolean isLoading() {
                return isLoading;
            }
            //CURRENT PAGE LOADED
            @Override
            public boolean hasLoadedAllItems() {
                return hasLoadedAll;
            }
        });
        pullToLoadView.initLoad();
    }

    /*
 LOAD MORE DATA
 SIMULATE USING HANDLERS
 */
    private void loadData(final int page)
    {
        isLoading=true;
        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... url) {
                Response response = null;
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBuilder.get_Complaint(session.getUserID(), page);
                Request request = new Request.Builder()
                        .url(url[0])
                        .post(body)
                        .build();
                try {
                    response = client.newCall(request).execute();
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.e("ANSWER INFINITE SCROLL", "" + result);
                process_response(result);
//                    db_offline.insertReport(result);
            }
        }.execute(Config.URL_GET_UNSOLVE);
    }

    private void process_response(String result){

        try {

            JSONObject jsonObject = new JSONObject(result);
            JSONObject jsonObject1 = jsonObject.getJSONObject("report");
            JSONArray jsonArray = jsonObject1.getJSONArray("data");
            JSONArray response = jsonObject.getJSONArray("response");
            int length = jsonArray.length();

            for (int i = 0; i < length; i++){
                JSONObject data = jsonArray.getJSONObject(i);
                db_offline.insertComplaint(data);
            }
            db_offline.insertResponse(response);
            initiatedata();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initiatedata(){

        String result = db_offline.getComplaint(1, 0);
        Log.d("result2", result);
//        if(adapter.getItemCount() > 0)
//            adapter.clear();

        try {

            JSONArray jsonArray = new JSONArray(result);
            int length = jsonArray.length();
            for (int i = 0 ; i < length ; i ++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.getString("status_id").equals("1"))
                    continue;
                ComplaintModel news = new ComplaintModel(jsonObject);
                adapter.add(news);
            }

            pullToLoadView.setComplete();
            isLoading=false;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
