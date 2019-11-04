package kuchingitsolution.betterpepperboard.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import java.util.List;
import java.util.Map;

import kuchingitsolution.betterpepperboard.MainActivity2;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Session;

public class MessageFragment extends Fragment implements UserListAdapter.onUserClickCallBack{

    private List<UserListModel> userListModels = new ArrayList<>();
    private RecyclerView userListView;
    private UserListAdapter userListAdapter;
    private TextView no_content;
    private String Id;
    private int position;
    DB_Offline db_offline;
    Session session;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getView() != null){
            session = new Session(getActivity());
            db_offline = new DB_Offline(getActivity());
            userListView = getView().findViewById(R.id.message_recycler_view);
            userListAdapter = new UserListAdapter(getContext(), userListModels, this);
            userListView.setAdapter(userListAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            userListView.setLayoutManager(linearLayoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(userListView.getContext(),
                    linearLayoutManager.getOrientation());
            userListView.addItemDecoration(dividerItemDecoration);
            no_content = getView().findViewById(R.id.no_content);
            load_view(db_offline.get_room());
            get_user_list();
        }
    }

    private void get_user_list(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_USERLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Status: ", response);
                        process_chat_room(response);

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
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void process_chat_room(String result){

        if(result.equals("empty")){
            no_content.setVisibility(View.VISIBLE);
        } else {

            try {

                JSONArray jsonArray = new JSONArray(result);
                int length = jsonArray.length();
                db_offline.deleteRoom();

                for (int i = 0; i < length ; i++){
                    JSONObject data = jsonArray.getJSONObject(i);
                    JSONObject chatroom = data.getJSONObject("chatroom");
                    JSONObject last = chatroom.optJSONObject("last_message");
                    UserListModel userListModel;

                    if(last == null) {
                        userListModel = new UserListModel(
                                chatroom.optString("name"), chatroom.optString("created_at"), " ",
                                "No message yet", data.getString("updated_at"), data.getString("room_id"));
                    } else {
                        userListModel = new UserListModel(
                                chatroom.optString("name"), last.optString("created_at"), " ",
                                last.getString("message"), data.getString("updated_at"), data.getString("room_id"));
                    }
                    db_offline.insertRoom(userListModel);
                }
                load_view(db_offline.get_room());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void load_view(Cursor cursor){

        if(userListModels.size() > 0)
            userListModels.clear();

        if(cursor.moveToFirst()){
            Log.d("Result complaint", DatabaseUtils.dumpCursorToString(cursor));

            do {
                UserListModel jsonObject = new UserListModel(
                        cursor.getString(1), cursor.getString(3), cursor.getString(2),
                        cursor.getString(4), cursor.getString(5), cursor.getString(0)
                );
                userListModels.add(jsonObject);

            } while (cursor.moveToNext());
            no_content.setVisibility(View.GONE);
        } else { Log.d("Result == ", "NO"); no_content.setVisibility(View.VISIBLE); }

        userListAdapter.notifyDataSetChanged();
        cursor.close();
        db_offline.close();

    }

    private void update_new_list(String result){

        /*{"message":{"updated_at":"2017-09-09 22:32:52","user_id":6,"chat_room_id":1,"created_at":"2017-09-09 22:32:52",
                        "id":11,"message":"hahahahahahahahhahahahahahahahahahahahhahahahahahahahahahahahhahahaha",
                        "user":{"updated_at":"2017-09-05 23:29:45","role_id":3,"name":"user_demo_2","created_at":"2017-09-05 23:29:45",
                        "avatar_link":null,"id":6,"email":"demo@demo.com"}},"chat_room":[{"room_id":1,"updated_at":"2017-09-09 00:00:38",
                        "user_id":6,"chatroom":{"updated_at":null,"name":"Test Room","created_at":"2017-09-09 00:00:59",
                        "last_message":{"updated_at":"2017-09-09 22:32:52","user_id":6,"chat_room_id":1,"created_at":"2017-09-09 22:32:52",
                        "id":11,"message":"hahahahahahahahhahahahahahahahahahahahhahahahahahahahahahahahhahahaha"},"id":1,"member_count":2},
                        "created_at":"2017-09-09 00:00:38","id":2}]}*/

        try {
            JSONObject data = new JSONObject(result);
            JSONArray chat_room = data.optJSONArray("chat_room");
            int length = chat_room.length();

            for (int i =0; i < length; i++){
                JSONObject room = chat_room.optJSONObject(i);
                JSONObject chatroom = room.optJSONObject("chatroom");
                JSONObject message = chatroom.optJSONObject("last_message");
                UserListModel userListModel = new UserListModel(
                        chatroom.optString("name"), chatroom.optString("created_at"), " ",
                        message.getString("message"), data.getString("updated_at"), data.getString("room_id"));
                db_offline.insertRoom(userListModel);
            }
            load_view(db_offline.get_room());
//            userListModels = db_offline.get_room();
//            userListAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "message", Toast.LENGTH_SHORT).show();
            get_user_list();
        }
    };

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_add);
        item.setVisible(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(Config.CHAT_NOTIFICATION));
        get_user_list();
        ((MainActivity2)getActivity()).removeBadgeAt(2);
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void OnChangeName(String id, int position) {
        this.Id = id;
        this.position = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View CustomView = inflater.inflate(R.layout.dialog_change_name, null);
        final EditText room_name = CustomView.findViewById(R.id.room_name);
        final TextView valid = CustomView.findViewById(R.id.valid_name);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(CustomView)
                // Add action buttons
            .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    if(room_name.getText().toString().isEmpty())
                        valid.setVisibility(View.VISIBLE);
                    else{
                        valid.setVisibility(View.GONE);
                        ChangeGroupName(Id, room_name.getText().toString());
                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.setTitle("New Name");
        builder.show();
    }

    private void ChangeGroupName(final String id, final String name){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_CHANGE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Status: ", response);
                        if(response.equals("success")){
                            db_offline.changeRoomName(name, id);
                            load_view(db_offline.get_room());
                        } else
                            Toast.makeText(getContext(), "Internet error occur", Toast.LENGTH_SHORT).show();

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
                map.put("room_id", id);
                map.put("name", name);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void DeleteGroup(final String room_id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_DELETE_ROOM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Status: ", response);
                        if(response.equals("success")){
                            db_offline.deleteRoom(room_id);
                            load_view(db_offline.get_room());
                        } else
                            Toast.makeText(getContext(), "Internet error occur", Toast.LENGTH_SHORT).show();

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
                map.put("room_id", room_id);
                map.put("user_id", session.getUserID());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    @Override
    public void OnDeleteChat(final String id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete this chat room");
        builder.setMessage("Are you sure want to delete this chat room ? Once it is deleted, it cannot be undone.");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DeleteGroup(id);
            }
        }). setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(true);
        builder.show();

    }
}
