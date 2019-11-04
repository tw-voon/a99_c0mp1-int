package kuchingitsolution.betterpepperboard.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
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

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.Session;
import kuchingitsolution.betterpepperboard.message.ChatActivity;

public class SearchUserActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener, SearchUserAdapter.AdapterCallback, View.OnClickListener{

    ArrayList<SearchUserModel> data = new ArrayList<SearchUserModel>();
    private RecyclerView userlist;
    private SearchUserAdapter searchAddAdapter;
    private GridView selectedUser;
    final ArrayList<String> list = new ArrayList<String>();
    final ArrayList<String> track_id = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    Session session;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add user");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        session = new Session(this);
        selectedUser = (GridView) findViewById(R.id.selectedUser);
        selectedUser.setVisibility(View.GONE);
        submit = (Button) findViewById(R.id.add);
        submit.setOnClickListener(this);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        selectedUser.setAdapter(adapter);
        selectedUser.setMinimumHeight(180);
        selectedUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SearchUserActivity.this, view.getId()+" " + i, Toast.LENGTH_SHORT).show();
            }
        });
        userlist = (RecyclerView) findViewById(R.id.newuser_recycler_view);
        searchAddAdapter = new SearchUserAdapter(SearchUserActivity.this, data);
        userlist.setAdapter(searchAddAdapter);
        userlist.setLayoutManager(new LinearLayoutManager(SearchUserActivity.this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search user");
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
//        getUser(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if(!query.equals(""))
            getUser(query);
        else {
            if(data.size() > 0) {
                data.clear();
                searchAddAdapter.notifyDataSetChanged();
            }
        }
        return true;
    }

    private void getUser(final String query){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_SEARCH_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(getLocalClassName(), response);
                        processResponse(response);

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(SearchUserActivity.this, "An Error occur, Please try again later", Toast.LENGTH_SHORT).show();

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("name",query);
                map.put("user_id", session.getUserID());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void processResponse(String result){

        if(data.size() > 0)
            data.clear();

        Log.d("search", result);

        if(result.equals("empty")){
            data.clear();
            searchAddAdapter.notifyDataSetChanged();
        } else {
            try {
                JSONArray response = new JSONArray(result);
                int length = response.length();
                for(int i =0; i < length; i++){
                    JSONObject user = response.getJSONObject(i);
                    SearchUserModel newuser = new SearchUserModel(
                            user.getString("id"),
                            user.getString("name"),
                            user.optString("avatar_link", null));
                    data.add(newuser);
                }
                searchAddAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addUser() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADD_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("status", response);
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject chat_room = jsonObject.optJSONObject("chat_room");
                            Intent intent = new Intent(SearchUserActivity.this, ChatActivity.class);
                            intent.putExtra("chat_room_id", chat_room.getString("id"));
                            intent.putExtra("name", chat_room.getString("name"));
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("status", error.toString());
                        Toast.makeText(SearchUserActivity.this, "An Error occur, Please try again later", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("target_user_id",String.valueOf(track_id));
                map.put("user_id", session.getUserID());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SearchUserActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onUserClick(String target_user_id, String name, int position, Boolean is_checked) {
        if(is_checked && !list.contains(name)){
            list.add(name);
            track_id.add(target_user_id);
            adapter.notifyDataSetChanged();
            submit.setVisibility(list.size() > 0 ? View.VISIBLE : View.GONE);
        } else if(!is_checked && list.contains(name)) {
            Toast.makeText(SearchUserActivity.this, name + " already in list", Toast.LENGTH_SHORT).show();
            list.remove(name);
            track_id.remove(target_user_id);
            adapter.notifyDataSetChanged();
            submit.setVisibility(list.size() > 0 ? View.VISIBLE : View.GONE);
        }
        selectedUser.setVisibility(list.size() > 0 ? View.VISIBLE : View.GONE);
        selectedUser.smoothScrollToPosition(list.size() - 1);
    }

    private void showMessage(String title, String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        addUser();
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
