package kuchingitsolution.betterpepperboard.auth;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
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

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import kuchingitsolution.betterpepperboard.MainActivity2;
import kuchingitsolution.betterpepperboard.PepperApps;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Session;

public class LoginActivity extends AppCompatActivity {

    DB_Offline db_offline;
    Session session;
    Handler handler;
    private EditText edtUsername, edtPassword;
    private CircularProgressButton login;
    private int ENABLE = 1, DISABLE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db_offline = new DB_Offline(this);
        session = new Session(this);
        edtUsername = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        login = findViewById(R.id.form_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInfo();
            }
        });
        handler = new Handler();
//        loading = findViewById(R.id.loading);
    }

    private void modified_edt_field(int status){

        switch (status){
            case 1:
                edtUsername.setEnabled(true);
                edtPassword.setEnabled(true);
                break;

            case 2:
                edtUsername.setEnabled(false);
                edtPassword.setEnabled(false);
        }

    }

    public void toRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void validateInfo() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if(username.length() != 0 && password.length() != 0){
            modified_edt_field(DISABLE);
            login.startAnimation();
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
                        revert_();
                        Log.d("Error", error.toString());
                        Toast.makeText(LoginActivity.this, "No Internet access", Toast.LENGTH_SHORT).show();
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
            revert_();
            showMessage();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getString("status").equals("success")){
                JSONObject user = jsonObject.getJSONObject("data");
                login.doneLoadingAnimation(R.color.colorBg, BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));
                session.setLoggedin(true);
                session.putData(user.getString("id"), user.getString("name"), user.optString("avatar_link", "null"), user.getString("role_id"));
                handler.postDelayed(r, 1000);
            } else {
                Toast.makeText(LoginActivity.this, "Fail to login. Please check your login details", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "Server error.", Toast.LENGTH_SHORT).show();
            revert_();
        }
//        loading.setVisibility(View.GONE);
    }

    private void revert_(){
        login.doneLoadingAnimation(R.color.mt_red, BitmapFactory.decodeResource(getResources(), R.drawable.ic_close_white_24dp));
        login.revertAnimation();
        modified_edt_field(ENABLE);
    }


    final Runnable r = new Runnable() {
        public void run() {
            Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
            startActivity(intent);
            finish();
        }
    };

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
    protected void onDestroy() {
        super.onDestroy();
        login.dispose();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PepperApps.activityResumed();// On Resume notify the Application
    }

    public void forgot_password(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
}
