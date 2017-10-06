package kuchingitsolution.betterpepperboard.auth;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kuchingitsolution.betterpepperboard.MainActivity2;
import kuchingitsolution.betterpepperboard.PepperApps;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Session;

public class LoginActivity extends AppCompatActivity {

    DB_Offline db_offline;
    Session session;
    private ProgressBar loading;
    private EditText edtUsername, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db_offline = new DB_Offline(this);
        session = new Session(this);
        edtUsername = (EditText) findViewById(R.id.username);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        loading = findViewById(R.id.loading);
    }

    public void toRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void validateInfo(View view) {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if(username.length() != 0 && password.length() != 0){
            loading.setVisibility(View.VISIBLE);
            _login(username, password);
        } else {
            Toast.makeText(LoginActivity.this, "Please input all the field.", Toast.LENGTH_LONG ).show();
        }
    }

    private void _login(final String username, final String password){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
//                        Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                        process_login(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Line109: " + error.toString() + error.getCause(),Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map = new HashMap<String,String>();
                map.put("name",username);
                map.put("pass",password);
                return map;
            }
        };
        PepperApps.getInstance(LoginActivity.this).addToRequestQueue(stringRequest, "TAG");
    }

    private void process_login(String result){

        if(result.equals("fail")){
            loading.setVisibility(View.GONE);
            showMessage();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getString("status").equals("success")){
                JSONObject user = jsonObject.getJSONObject("data");
                session.setLoggedin(true);
                session.putData(user.getString("id"), user.getString("name"), user.optString("avatar_link", "null"), user.getString("role_id"));
                Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Fail to login. Please check your login details", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "Server error.", Toast.LENGTH_SHORT).show();
        }
        loading.setVisibility(View.GONE);
    }

    private void showMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Credential fail");
        builder.setMessage("Wrong email or password");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onPause() {

        super.onPause();
        PepperApps.activityPaused();// On Pause notify the Application
    }

    @Override
    protected void onResume() {
        super.onResume();
        PepperApps.activityResumed();// On Resume notify the Application
    }
}
