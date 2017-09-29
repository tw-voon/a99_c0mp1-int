package kuchingitsolution.betterpepperboard.hotline;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Network;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HotlineActivity extends AppCompatActivity {

    private RecyclerView hotlinelist;
    private HotlineAdapter hotlineAdapter;
    ArrayList<HotlineModel> data = new ArrayList<>();
    Network network;
    Config config;
    ProgressBar loading;
    DB_Offline db_offline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotline);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loading = (ProgressBar) findViewById(R.id.loading);
        db_offline = new DB_Offline(this);
        hotlinelist = (RecyclerView) findViewById(R.id.contact_recycler_view);
        hotlineAdapter = new HotlineAdapter(HotlineActivity.this, data);
        hotlinelist.setAdapter(hotlineAdapter);
        hotlinelist.setLayoutManager(new LinearLayoutManager(HotlineActivity.this));
        get_offline_data();

        if(Network.isNetworkStatusAvailable(this))
            get_Hot_line();
        else
            get_offline_data();
    }

    private void get_offline_data(){
        String result = db_offline.getHotline();
        Log.d("length", "result " + result.equals("[]"));
        if(!result.equals("[]"))
            process_result(result, Config.OFFLINE);
        else {
//            Toast.makeText(HotlineActivity.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.GONE);
        }
    }

    private void get_Hot_line(){

        loading.setVisibility(View.VISIBLE);

        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... url) {
                Response response = null;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url[0])
                        .build();
                try {
                    response = client.newCall(request).execute();
                    return response.body().string();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.e("ANSWER", "" + result);
                if(result == null || result.equals("null"))
                    get_offline_data();
                else
                    process_result(result, Config.ONLINE);
            }
        }.execute(Config.HOTLINE_URL);
    }

    private void process_result(String result, int status){

        if(data.size() > 0)
            data.clear();

        try {
            JSONArray jsonArray = new JSONArray(result);
            int length = jsonArray.length();
            for(int i = 0; i < length; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HotlineModel hotlineModel = new HotlineModel(
                        jsonObject.getInt("id"),
                        jsonObject.getString("name"),
                        jsonObject.getString("number"),
                        jsonObject.getString("description")
                );
                data.add(hotlineModel);
            }

            if(status == Config.ONLINE)
                db_offline.insertHotline(result);

            hotlineAdapter.notifyDataSetChanged();
            hotlinelist.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
