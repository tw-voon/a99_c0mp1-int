package kuchingitsolution.betterpepperboard.new_complaint;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CategoryActivity extends AppCompatActivity {

    ListView category_list;
    ArrayList<ReportTypeModel> reportTypeModels = new ArrayList<>();
    List<String> list_view;
    ArrayAdapter<String> arrayAdapter;
    public static final String CATEGORY = "category";
    DB_Offline db_offline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        category_list = (ListView) findViewById(R.id.cateogrylist);
        db_offline = new DB_Offline(this);
        list_view = new ArrayList<String>();
//        your_array_list.add("Flood");
//        your_array_list.add("Staff attitude");

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                list_view );

        category_list.setAdapter(arrayAdapter);
        category_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(CategoryActivity.this,adapterView.getAdapter().getItem(i).toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CategoryActivity.this, NewComplaintActivity.class);
                intent.putExtra(CATEGORY, adapterView.getAdapter().getItem(i).toString());
                intent.putExtra("category_id", reportTypeModels.get(i).getCategoryID());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        get_offline_data();

        if(Network.isNetworkStatusAvailable(this))
            get_type();
        else
            get_offline_data();

    }

    private void get_offline_data(){
        String result = db_offline.getReportType();
        Log.d("length", "result " + result.equals("[]"));
        if(!result.equals("[]"))
            process_result(result, Config.OFFLINE);
    }

    private void get_type(){
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
        }.execute(Config.URL_ReportType);
    }

    private void process_result(String result, int status){

        if(reportTypeModels.size() > 0)
            reportTypeModels.clear(); list_view.clear();
        Log.e("Process result", "" + result);
        try {
            JSONArray jsonArray = new JSONArray(result);
            int length = jsonArray.length();

            for(int i = 0; i < length; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                /*Populate list view*/
                list_view.add(jsonObject.getString("name"));
                ReportTypeModel reportTypeModel = new ReportTypeModel(jsonObject.getString("name"), jsonObject.getString("id"));
                reportTypeModels.add(reportTypeModel);
            }

            if(status == Config.ONLINE)
                db_offline.insertReportType(result);

            arrayAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
