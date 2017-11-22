package kuchingitsolution.betterpepperboard.auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import kuchingitsolution.betterpepperboard.MainActivity;
import kuchingitsolution.betterpepperboard.MainActivity2;
import kuchingitsolution.betterpepperboard.PepperApps;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.Session;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtTxtname, edtEmail, edtPhone, password, passwordAgain;
    private String username, pswd, pswd_again, email, phone;
    private Session session;
    private CircularProgressButton register;
    private Handler handler;
    private boolean can_back = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtTxtname =  findViewById(R.id.edtUsername);
        password = findViewById(R.id.edtPassword);
        passwordAgain = findViewById(R.id.edtPswdAgain);
        edtEmail = findViewById(R.id.edtEmail);
        edtEmail.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        edtPhone = findViewById(R.id.edtPhone);
        edtPhone.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_CLASS_PHONE);
        session = new Session(this);
        register = findViewById(R.id.form_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInfo();
            }
        });
        handler = new Handler();
    }

    public void toLogin(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showMessage(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Register fail");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void validateInfo() {

        username = edtTxtname.getText().toString();
        pswd = password.getText().toString();
        pswd_again = passwordAgain.getText().toString();
        email = edtEmail.getText().toString().trim();
        phone = edtPhone.getText().toString().trim();

        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(pswd) || TextUtils.isEmpty(pswd_again) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Please input all field before proceed", Toast.LENGTH_SHORT).show();
            return;
        }

        if(pswd.equals(pswd_again)){
            register.startAnimation();
            disable_interaction(Config.DISABLE);
            can_back = false;
            register();
        } else
            Toast.makeText(RegisterActivity.this, "Please make sure the password is same.", Toast.LENGTH_SHORT).show();
    }

    private void register(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                        processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        button_revert();
                        disable_interaction(Config.ENABLE);
                        can_back = true;
                        Toast.makeText(RegisterActivity.this, "Line109: " + error.toString() + error.getCause(),Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map = new HashMap<String,String>();
                map.put("name",username);
                map.put("pass",pswd);
                map.put("email", email);
                map.put("phone", phone);
                return map;
            }
        };
        PepperApps.getInstance(RegisterActivity.this).addToRequestQueue(stringRequest, "TAG");
    }

    private void button_revert(){
        register.doneLoadingAnimation(R.color.mt_red, BitmapFactory.decodeResource(getResources(), R.drawable.ic_close_white_24dp));
        register.revertAnimation();
    }

    final Runnable r = new Runnable() {
        public void run() {
            Intent intent = new Intent(RegisterActivity.this, MainActivity2.class);
            startActivity(intent);
            finish();
        }
    };

    private void processResponse(String result){

        try {
            JSONObject jObject = new JSONObject(result);

            if (jObject.getString("status").equals("success")) {
                JSONObject dataObject = new JSONObject(jObject.getString("data"));
                register.doneLoadingAnimation(R.color.colorBg, BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));
                session.setLoggedin(true);
                session.putData(dataObject.getString("id"), dataObject.getString("name"), dataObject.getString("avatar_link"), dataObject.getString("role_id"));
                session.putUserAvatar(dataObject.getString("avatar_link"));
                handler.postDelayed(r, 1000);
            } else {
                Toast.makeText(RegisterActivity.this, jObject.getString("data"), Toast.LENGTH_SHORT).show();
                disable_interaction(Config.ENABLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            showMessage(result);
            button_revert();
            disable_interaction(Config.ENABLE);
        }

        can_back = true;
    }

    private void disable_interaction(int status){
        switch (status){
            case 1:
                edtTxtname.setEnabled(true);
                edtEmail.setEnabled(true);
                edtPhone.setEnabled(true);
                password.setEnabled(true);
                passwordAgain.setEnabled(true);
                break;
            case 2:
                edtTxtname.setEnabled(false);
                edtEmail.setEnabled(false);
                edtPhone.setEnabled(false);
                password.setEnabled(false);
                passwordAgain.setEnabled(false);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(can_back)
            super.onBackPressed();
        else
            Toast.makeText(RegisterActivity.this, "Registration is running. Please wait...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        register.dispose();
    }
}
