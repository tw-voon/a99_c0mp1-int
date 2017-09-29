package kuchingitsolution.betterpepperboard.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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

import kuchingitsolution.betterpepperboard.MainActivity;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView = null;
    RecyclerView search_list;
    SearchAdapter searchAdapter;
    ArrayList<SearchModel> searchModels;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if(getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loading = findViewById(R.id.loading);
        search_list = findViewById(R.id.search_item);
        searchModels = new ArrayList<>();

        searchAdapter = new SearchAdapter(SearchActivity.this, searchModels);
        search_list.setAdapter(searchAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        search_list.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(search_list.getContext(),
                linearLayoutManager.getOrientation());
        search_list.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // adds item to action bar
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Get Search item from action bar and Get Search service
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) SearchActivity.this.getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(SearchActivity.this.getComponentName()));
            searchView.setIconified(false);
        }

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Get search query and create object of class AsyncFetch
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (searchView != null) {
                searchView.clearFocus();
            }
            loading.setVisibility(View.VISIBLE);
            Log.d("query", "Query : " + query);
            query_data(query);
        }
    }

    private void query_data(final String query){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_QUERY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("result", response);
                        process_result(response);
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
                map.put("query", query);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void process_result(String result){

        if(result.equals("empty"))
            Toast.makeText(this, "Empty result", Toast.LENGTH_SHORT).show();
        else {

            if(searchModels.size() > 0)
                searchModels.clear();

            try {
                JSONArray jsonArray = new JSONArray(result);
                int length = jsonArray.length();
                Log.d("length", " " + length);
                for (int i = 0; i < length ; i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SearchModel searchModel = new SearchModel(jsonObject.getString("id"), jsonObject.getString("title"), jsonObject.getString("description"));
                    searchModels.add(searchModel);
                }

                searchAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        loading.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        switch (itemId){
            case android.R.id.home:
                super.onBackPressed();
                Log.d("report_id", "pressed");
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
