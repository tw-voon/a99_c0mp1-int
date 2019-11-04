package kuchingitsolution.betterpepperboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import kuchingitsolution.betterpepperboard.complaint.ComplaintFragment;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Session;
import kuchingitsolution.betterpepperboard.home.HomeFragment;
import kuchingitsolution.betterpepperboard.message.MessageFragment;
import kuchingitsolution.betterpepperboard.notification.NotificationFragment;
import kuchingitsolution.betterpepperboard.personal.PersonalFragment;
import kuchingitsolution.betterpepperboard.search.SearchActivity;
import kuchingitsolution.betterpepperboard.search.SearchUserActivity;
import kuchingitsolution.betterpepperboard.setting.SettingActivity;
import q.rorbin.badgeview.QBadgeView;

public class MainActivity2 extends AppCompatActivity {

    DB_Offline db_offline;
    Toolbar toolbar;
    FrameLayout content;
    Session session;
    BottomNavigationViewEx navigation;
    QBadgeView notification, message, complaint;
    FragmentManager fragmentManager;
    private String user_roles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigation = findViewById(R.id.navigation);
        session = new Session(this);
        user_roles = session.getPosition();

        if(user_roles.equals("3")) /* temporary hide the message features from normal users*/
            hide_message();

        navigation.enableItemShiftingMode(false);
        navigation.enableShiftingMode(false);
        navigation.enableAnimation(false);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        content = findViewById(R.id.contents);
        db_offline = new DB_Offline(this);
        // startService(new Intent(MainActivity2.this, onesignal_service.class));
        notification = new QBadgeView(this);
        message = new QBadgeView(this);
        complaint = new QBadgeView(this);

        pushFragment(new HomeFragment(), R.string.title_home);

        /*
        * i) verify user type
        * ii) if user is normal user then disable the message function
        * iii)
        * */
    }

    private void hide_message(){

        navigation.getMenu().removeItem(R.id.navigation_message);

    }

    public void addBadgeAt(int position, int number) {

        if(user_roles.equals("3")){

            if(position == 2 || position == 3){
                notification.setGravityOffset(8, 3, true)
                        .setBadgeNumber(number)
                        .bindTarget(navigation.getBottomNavigationItemView(2));
            } else if(position == 1){
                complaint.setGravityOffset(8, 3, true)
                        .setBadgeNumber(number)
                        .bindTarget(navigation.getBottomNavigationItemView(position));
            }

        } else {

            if(position == 3){
                notification.setGravityOffset(8, 3, true)
                        .setBadgeNumber(number)
                        .bindTarget(navigation.getBottomNavigationItemView(position));
            } else if(position == 2){
                message.setGravityOffset(8, 3, true)
                        .setBadgeNumber(number)
                        .bindTarget(navigation.getBottomNavigationItemView(position));
            } else if(position == 1){
                complaint.setGravityOffset(8, 3, true)
                        .setBadgeNumber(number)
                        .bindTarget(navigation.getBottomNavigationItemView(position));
            }

        }
        Log.d("BadgeView", "Position: " + String.valueOf(position) + " -- Number: " + String.valueOf(number));

    }

//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "message", Toast.LENGTH_SHORT).show();
//            Log.d("onesignal_data", intent.getStringExtra("data"));
//            if(intent.getStringExtra("data") != null){
//                switch (intent.getStringExtra("data")){
//                    case Config.MESSAGE:
//                        addBadgeAt(2, 1);
//                        break;
//                    case Config.COMPLAINT:
//                        addBadgeAt(1, 1);
//                        break;
//                    case Config.NOTI:
//                        addBadgeAt(3, session.getNotiBadge());
//                        break;
//                }
//            }
//        }
//    };

    public void removeBadgeAt(int position){

        switch (user_roles){
            case "3":
                if(position == 2){
                    notification.setBadgeNumber(0);
                } else if(position == 1){
                    complaint.setBadgeNumber(0);
                }
                break;
            default:
                if(position == 3){
                    notification.setBadgeNumber(0);
                } else if(position == 2){
                    message.setBadgeNumber(0);
                } else if(position == 1){
                    complaint.setBadgeNumber(0);
                }
                break;
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    pushFragment(new HomeFragment(), R.string.title_home);
                    return true;
                case R.id.navigation_complaint:
                    pushFragment(new ComplaintFragment(), R.string.title_complaint);
                    removeBadgeAt(1);
                    session.resetBadge(1);
                    return true;
                case R.id.navigation_notifications:
                    pushFragment(new NotificationFragment(), R.string.title_notifications);
                    if(user_roles.equals("3")){
                        session.resetBadge(3);
                        removeBadgeAt(2);
                    } else {
                        session.resetBadge(3);
                        removeBadgeAt(3);
                    }
                    return true;
                case R.id.navigation_message:
                    pushFragment(new MessageFragment(), R.string.action_chat);
                    removeBadgeAt(2);
                    session.resetBadge(2);
                    return true;
                case R.id.navigation_personal:
                    pushFragment(new PersonalFragment(), R.string.action_personal);
                    return true;
            }
            return true;
        }
    };

    protected void pushFragment(Fragment fragment, int title_resource) {
        if (fragment == null)
            return;

        fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.contents, fragment);
                ft.commit();
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(title_resource);
                 }
             }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent(MainActivity2.this, SettingActivity.class);
                startActivity(intent);
                break;

            case R.id.action_search:
                Intent intent2 = new Intent(MainActivity2.this, SearchActivity.class);
                startActivity(intent2);
                break;

            case R.id.action_add:
                Intent intent3 = new Intent(MainActivity2.this, SearchUserActivity.class);
                startActivity(intent3);
                break;

            default:
                break;
        }
        return true;
    }

    private void setup_tab(){

        int notibadge = session.getNotiBadge();
        int msgBadge = session.getMessageBadge();
        int complaintBadge = session.getComplaintBadge();

        if(complaintBadge > 0){
            addBadgeAt(1, complaintBadge);
        }

        if(msgBadge > 0){
            addBadgeAt(2, msgBadge);
        }

        if(notibadge > 0){
            addBadgeAt(3, notibadge);
        }
    }

    @Override
    public void onBackPressed() {

        if(getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
        else {
            Log.d("close", "close " + getFragmentManager().getBackStackEntryCount());
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        // LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
        PepperApps.activityPaused();// On Pause notify the Application
    }

    @Override
    protected void onResume() {
        super.onResume();
        PepperApps.activityResumed();// On Resume notify the Application
        //LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
        //        new IntentFilter("notification_received"));
        setup_tab();
    }
}
