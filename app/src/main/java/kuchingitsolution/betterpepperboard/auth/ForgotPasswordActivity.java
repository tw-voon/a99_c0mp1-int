package kuchingitsolution.betterpepperboard.auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import kuchingitsolution.betterpepperboard.PepperApps;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText email;
    private CircularProgressButton submit;
    private String email_data;
    private int ENABLE = 1, DISABLE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.edtEmail);
        submit = findViewById(R.id.form_send);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email_data = email.getText().toString().trim();

                if(TextUtils.isEmpty(email.getText().toString()))
                    Toast.makeText(ForgotPasswordActivity.this, "Please input your email address", Toast.LENGTH_SHORT).show();
                else {
                    submit.startAnimation();
                    disable_interaction(DISABLE);
                    SendPasswordResetLink();
                }
            }
        });
    }

    private void disable_interaction(int status){

        switch (status){
            case 1:
                email.setEnabled(true);
                break;
            case 2:
                email.setEnabled(false);
                break;
        }

    }


    private void SendPasswordResetLink(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_RESET_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                        showMessage(response);
                        disable_interaction(ENABLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        disable_interaction(ENABLE);
                        revert_();
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

        if(message.equals("We have e-mailed your password reset link!")) {
            submit.doneLoadingAnimation(R.color.colorBg, BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));
            builder.setMessage("Please go your email to reset the password. The email may take some time to reach your email. Thank you.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            revert_();
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(message.equals("We have e-mailed your password reset link!")) {

                } else {
                    dialogInterface.dismiss();
                    submit.setEnabled(true);
                }
            }
        });
        builder.show();
    }

    private void revert_(){
        submit.doneLoadingAnimation(R.color.mt_red, BitmapFactory.decodeResource(getResources(), R.drawable.ic_close_white_24dp));
        submit.revertAnimation();
    }
}
