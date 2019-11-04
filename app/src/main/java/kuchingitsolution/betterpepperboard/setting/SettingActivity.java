package kuchingitsolution.betterpepperboard.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.auth.LoginActivity;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Session;

public class SettingActivity extends AppCompatActivity {

    private TextView logout;
    private Session session;
    private DB_Offline db_offline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        logout = (TextView) findViewById(R.id.logout);
        session = new Session(this);
        db_offline = new DB_Offline(this);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("Logout", "Are you sure want to log out?");
            }
        });
    }

    private void showMessage(String title, String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingActivity.this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                session.setLoggedin(false);
                session.clearPreference();
                db_offline.clearTable();
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        alertDialogBuilder.show();
    }
}
