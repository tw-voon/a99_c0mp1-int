package kuchingitsolution.betterpepperboard.message;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
import java.util.List;
import java.util.Map;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Session;

public class ChatActivity extends AppCompatActivity implements ChatAdapter.onUserClickCallBack{

    List<ChatModel> chatModels = new ArrayList<>();
    ChatAdapter chatAdapter;
    RecyclerView recyclerView;
    TextView no_content;
    Button send;
    EditText message;
    Session session;
    DB_Offline db_offline;
    private String chat_room_id, name, messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new Session(ChatActivity.this);
        db_offline = new DB_Offline(ChatActivity.this);

        recyclerView = findViewById(R.id.reyclerview_message_list);
        chatAdapter = new ChatAdapter(ChatActivity.this, chatModels, session.getUserID());
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        no_content = findViewById(R.id.no_content);
        message = findViewById(R.id.edittext_chatbox);

        if(getIntent() != null) {
            chat_room_id = getIntent().getStringExtra("chat_room_id");
            name = getIntent().getStringExtra("name");
            get_message();
        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(name);
            getSupportActionBar().show();
        }
    }

//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "message", Toast.LENGTH_SHORT).show();
//            Log.d("onesignal_data", intent.getStringExtra("data"));
//            append_received_msg(intent.getStringExtra("data"));
//        }
//    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
//        LocalBroadcastManager.getInstance(ChatActivity.this).registerReceiver(broadcastReceiver,
//                new IntentFilter(Config.CHAT_NOTIFICATION));
    }

    @Override
    public void onPause() {
        // LocalBroadcastManager.getInstance(ChatActivity.this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    private void get_message(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_FETCH_CHAT_ROOM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Status: ", response);
                        if(!response.equals("empty"))
                            process_chat_room(response);
                        else no_content.setVisibility(View.VISIBLE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("Error", error.toString());

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("user_id",session.getUserID());
                map.put("chat_room_id", chat_room_id);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ChatActivity.this);
        requestQueue.add(stringRequest);
    }

    private void process_chat_room(String result){

        try {
            JSONArray jsonArray = new JSONArray(result);
            int length = jsonArray.length();

            for (int i = 0; i < length ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject user = jsonObject.getJSONObject("user");
                ChatModel chatModel = new ChatModel(" ", user.getString("name"),
                        jsonObject.getString("message"), jsonObject.getString("created_at"),
                        jsonObject.getString("id"), user.getString("id"));
                chatModels.add(chatModel);
                Log.d("message_content", user.getString("name"));
            }
            chatAdapter.notifyDataSetChanged();
            recyclerView.getLayoutManager().scrollToPosition(chatAdapter.getItemCount()-1);
            no_content.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void validate(View view) {
        messages = message.getText().toString().trim();
        if(messages.length() == 0 || messages.isEmpty())
            Toast.makeText(ChatActivity.this, "No message. Please enter message", Toast.LENGTH_SHORT).show();
        else {
            sendMsg();
            message.setText("");
        }
    }

    private void sendMsg(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_ADD_MSG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Status: ", response);
                        if(!response.equals("empty")) {
                            append_msg(response);
                        }

                        /*{"message":{"updated_at":"2017-09-09 22:32:52","user_id":6,"chat_room_id":1,"created_at":"2017-09-09 22:32:52",
                        "id":11,"message":"hahahahahahahahhahahahahahahahahahahahhahahahahahahahahahahahhahahaha",
                        "user":{"updated_at":"2017-09-05 23:29:45","role_id":3,"name":"user_demo_2","created_at":"2017-09-05 23:29:45",
                        "avatar_link":null,"id":6,"email":"demo@demo.com"}},"chat_room":[{"room_id":1,"updated_at":"2017-09-09 00:00:38",
                        "user_id":6,"chatroom":{"updated_at":null,"name":"Test Room","created_at":"2017-09-09 00:00:59",
                        "last_message":{"updated_at":"2017-09-09 22:32:52","user_id":6,"chat_room_id":1,"created_at":"2017-09-09 22:32:52",
                        "id":11,"message":"hahahahahahahahhahahahahahahahahahahahhahahahahahahahahahahahhahahaha"},"id":1,"member_count":2},
                        "created_at":"2017-09-09 00:00:38","id":2}]}*/

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("Error", error.toString());

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("user_id",session.getUserID());
                map.put("room_id", chat_room_id);
                map.put("message", messages);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ChatActivity.this);
        requestQueue.add(stringRequest);

    }

    private void append_msg(String result){

        try {
            JSONObject data = new JSONObject(result);
            JSONObject info = data.getJSONObject("info");
            JSONObject message = info.getJSONObject("message");
            JSONObject user = message.getJSONObject("user");
            ChatModel chatModel = new ChatModel(" ", user.getString("name"),
                    message.getString("message"), message.getString("created_at"),
                    message.getString("id"), user.getString("id"));
            chatModels.add(chatModel);
            chatAdapter.notifyDataSetChanged();
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, chatAdapter.getItemCount()-1);
            no_content.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void append_received_msg(String result){
        /*
        * {"state":"chat","message":{"updated_at":"2017-09-10 15:09:44","user_id":7,"chat_room_id":2,"created_at":"2017-09-10 15:09:44",
        * "id":26,"message":"hahahahahahahahhahahaha","user":{"updated_at":"2017-09-05 23:30:08","role_id":3,"name":"user_demo_3",
        * "created_at":"2017-09-05 23:30:08","avatar_link":null,"id":7,"email":"demo@demo.com"},"room":{"updated_at":"2017-09-09 00:44:46"
        * ,"name":"Test Room 2","created_at":"2017-09-09 00:44:46","id":2,"member_count":2}},"chat_room":[{"room_id":2,
        * "updated_at":"2017-09-09 00:44:24","user_id":7,"chatroom":{"updated_at":"2017-09-09 00:44:46","name":"Test Room 2",
        * "created_at":"2017-09-09 00:44:46","last_message":{"updated_at":"2017-09-10 15:09:44","user_id":7,"chat_room_id":2,
        * "created_at":"2017-09-10 15:09:44","id":26,"message":"hahahahahahahahhahahaha"},"id":2,"member_count":2},"created_at":"2017-09-09 00:44:24",
        * "id":4}]}
        * */
        try {
            JSONObject data = new JSONObject(result);
            JSONObject message = data.optJSONObject("message");
            JSONObject user = message.optJSONObject("user");
            ChatModel chatModel = new ChatModel(" ", user.getString("name"),
                    message.getString("message"), message.getString("created_at"),
                    message.getString("id"), user.getString("id"));
            chatModels.add(chatModel);
            chatAdapter.notifyDataSetChanged();
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, chatAdapter.getItemCount()-1);
            no_content.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDeleteMessage(final String msg_id, final int current_position) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_DELETE_MSG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Status: ", response);
                        if(response.equals("success")){
                            chatModels.remove(current_position);
                            chatAdapter.notifyDataSetChanged();
                        } else
                            Toast.makeText(ChatActivity.this, "Internet error occur", Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("Error", error.toString());

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("msg_id", msg_id);
                map.put("user_id", session.getUserID());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ChatActivity.this);
        requestQueue.add(stringRequest);
    }
}
