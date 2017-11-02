package kuchingitsolution.betterpepperboard.info;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Network;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.attr.data;

public class DetailActivity extends AppCompatActivity {

    private List<DetailsModel> detailModelList = new ArrayList<>();
    private RecyclerView detailsView;
    private DetailsAdapter detailsAdapter;
    ProgressBar loading;
    DB_Offline db_offline;
    TextView status;
    int category_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        status = (TextView) findViewById(R.id.status);
        db_offline = new DB_Offline(this);
        category_id = getIntent().getIntExtra("category_id", 0);
        loading = (ProgressBar) findViewById(R.id.loading);

        detailsView = (RecyclerView) findViewById(R.id.detailList);
        detailsAdapter = new DetailsAdapter(DetailActivity.this, detailModelList);
        detailsView.setAdapter(detailsAdapter);
        detailsView.setLayoutManager(new LinearLayoutManager(DetailActivity.this));

        get_offline_data();

        if(Network.isNetworkStatusAvailable(this))
            get_details();
        else
            get_offline_data();
    }

    private void get_offline_data(){
        String result = db_offline.getDetailsInfo(category_id);
        Log.d("length", "result " + result.equals("[]"));
        if(!result.equals("[]"))
            process_result(result, Config.OFFLINE);
        else {
            loading.setVisibility(View.GONE);
        }
    }

    private void get_details(){
        loading.setVisibility(View.VISIBLE);

        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... url) {
                Response response = null;
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("category_id", String.valueOf(category_id))
                        .build();

                Request request = new Request.Builder()
                        .method("POST", requestBody)
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
                status.setText(result);
            }
        }.execute(Config.DETAIL_URL);
    }

    private void process_result(String result, int status){
        if(detailModelList.size() > 0)
            detailModelList.clear();

        try {
            JSONArray jsonArray = new JSONArray(result);
            int length = jsonArray.length();
            for(int i = 0; i < length; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                DetailsModel detailsModel = new DetailsModel(
                        jsonObject.getString("id"),
                        jsonObject.getString("category_id"),
                        jsonObject.getString("title"),
                        jsonObject.getString("message")
                );
                detailModelList.add(detailsModel);
            }

            if(status == Config.ONLINE)
                db_offline.insertDetails(result);

            detailsAdapter.notifyDataSetChanged();
            detailsView.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loading.setVisibility(View.GONE);
    }
}
