package kuchingitsolution.betterpepperboard.auth;

import android.content.DialogInterface;
import android.content.Intent;
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

import kuchingitsolution.betterpepperboard.MainActivity;
import kuchingitsolution.betterpepperboard.MainActivity2;
import kuchingitsolution.betterpepperboard.PepperApps;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.Session;

public class RegisterActivity extends AppCompatActivity {

    EditText edtTxtname, edtEmail, edtPhone, password, passwordAgain;
    String username, pswd, pswd_again, email, phone;
    Session session;
    private ProgressBar loading;

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
        loading = findViewById(R.id.loading);
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

    public void validateInfo(View view) {

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
//            Toast.makeText(RegisterActivity.this, "Password same", Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.VISIBLE);
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
//                        Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
                        processResponse(response);
//                        loading.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.setVisibility(View.GONE);
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

    private void processResponse(String result){

        try {
            JSONObject jObject = new JSONObject(result);

            if (jObject.getString("status").equals("success")) {
                JSONObject dataObject = new JSONObject(jObject.getString("data"));
//                Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                session.setLoggedin(true);
                session.putData(dataObject.getString("id"), dataObject.getString("name"), dataObject.getString("avatar_link"), dataObject.getString("role_id"));
//                registerKey(dataObject.getString("id"));
                session.putUserAvatar(dataObject.getString("avatar_link"));
                startActivity(new Intent(RegisterActivity.this, MainActivity2.class));
                finish();
            } else
                Toast.makeText(RegisterActivity.this, jObject.getString("data"), Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
            showMessage(result);
        }
        loading.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
