package kuchingitsolution.betterpepperboard.info;

import android.content.Intent;
import android.icu.text.IDNA;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import kuchingitsolution.betterpepperboard.hotline.HotlineModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoActivity extends AppCompatActivity {

    ListView info_category;
    ProgressBar loading;
    DB_Offline db_offline;
    List<String> info_array_list = new ArrayList<String>();
    ArrayList<CategoryModel> categoryModels = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        info_category = (ListView) findViewById(R.id.info_cateogry);
        loading = (ProgressBar) findViewById(R.id.loading);
        db_offline = new DB_Offline(this);
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                info_array_list );

        info_category.setAdapter(arrayAdapter);

        info_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(InfoActivity.this,adapterView.getAdapter().getItem(i).toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(InfoActivity.this," " + categoryModels.get(i).getCategory_id(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InfoActivity.this, DetailActivity.class);
                intent.putExtra("category_id", categoryModels.get(i).getCategory_id());
                startActivity(intent);
            }
        });

        get_offline_data();

        if(Network.isNetworkStatusAvailable(this))
            get_info();
        else
            get_offline_data();
    }

    private void get_offline_data(){

        String result = db_offline.getInfo();
        Log.d("length", "result " + result.equals("[]"));
        if(!result.equals("[]"))
            process_result(result, Config.OFFLINE);
        else {
//            Toast.makeText(HotlineActivity.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.GONE);
        }

    }

    private void get_info(){

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
        }.execute(Config.INFO_URL);
    }

    private void process_result(String result, int status){

        if(info_array_list.size() > 0)
            info_array_list.clear();

        try {
            JSONArray jsonArray = new JSONArray(result);
            int length = jsonArray.length();

            for(int i = 0; i < length; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                /*Populate list view*/
                info_array_list.add(jsonObject.getString("name"));
                CategoryModel categoryModel = new CategoryModel(jsonObject.getInt("id"), jsonObject.getString("name"));
                categoryModels.add(categoryModel);
            }

            if(status == Config.ONLINE)
                db_offline.insertInfo(result);

            arrayAdapter.notifyDataSetChanged();
            loading.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
