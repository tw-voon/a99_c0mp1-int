package kuchingitsolution.betterpepperboard.auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import kuchingitsolution.betterpepperboard.PepperApps;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText email;
    private Button submit;
    private ProgressBar loading;
    private String email_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.edtEmail);
        submit = findViewById(R.id.form_send);
        loading = findViewById(R.id.loading);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email_data = email.getText().toString().trim();

                if(TextUtils.isEmpty(email.getText().toString()))
                    Toast.makeText(ForgotPasswordActivity.this, "Please input your email address", Toast.LENGTH_SHORT).show();
                else
                    SendPasswordResetLink();
            }
        });
    }

    private void SendPasswordResetLink(){
        submit.setEnabled(false); /* disable button click */
        loading.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_RESET_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                        showMessage(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.setVisibility(View.GONE);
                        submit.setEnabled(true);
                        Toast.makeText(ForgotPasswordActivity.this, "Line109: " + error.toString() + error.getCause(),Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map = new HashMap<>();
                map.put("email",email_data);
                return map;
            }
        };
        PepperApps.getInstance(ForgotPasswordActivity.this).addToRequestQueue(stringRequest, "TAG");
    }

    private void showMessage(final String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
        builder.setCancelable(false);
        builder.setTitle(message);

        if(message.equals("We have e-mailed your password reset link!"))
            builder.setMessage("Please go your email to reset the password");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(message.equals("We have e-mailed your password reset link!")) {
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    dialogInterface.dismiss();
                    loading.setVisibility(View.GONE);
                    submit.setEnabled(true);
                }
            }
        });
        builder.show();
    }
}
