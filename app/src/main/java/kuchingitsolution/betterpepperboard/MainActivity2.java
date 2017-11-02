package kuchingitsolution.betterpepperboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import kuchingitsolution.betterpepperboard.complaint.ComplaintFragment;
import kuchingitsolution.betterpepperboard.helper.BottomNavigationViewHelper;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Session;
import kuchingitsolution.betterpepperboard.home.HomeFragment;
import kuchingitsolution.betterpepperboard.message.MessageFragment;
import kuchingitsolution.betterpepperboard.notification.NotificationFragment;
import kuchingitsolution.betterpepperboard.personal.PersonalFragment;
import kuchingitsolution.betterpepperboard.search.SearchActivity;
import kuchingitsolution.betterpepperboard.search.SearchUserActivity;
import kuchingitsolution.betterpepperboard.service.onesignal_service;
import kuchingitsolution.betterpepperboard.setting.SettingActivity;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class MainActivity2 extends AppCompatActivity {

    DB_Offline db_offline;
    Toolbar toolbar;
    FrameLayout content;
    Session session;
    BottomNavigationViewEx navigation;
    QBadgeView notification, message, complaint;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setShowHideAnimationEnabled(false);
        }

        navigation = findViewById(R.id.navigation);
        navigation.enableItemShiftingMode(false);
        navigation.enableShiftingMode(false);
        navigation.enableAnimation(false);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        content = findViewById(R.id.contents);
        db_offline = new DB_Offline(this);
        session = new Session(this);
        startService(new Intent(MainActivity2.this, onesignal_service.class));
        notification = new QBadgeView(this);
        message = new QBadgeView(this);
        complaint = new QBadgeView(this);
        pushFragment(new HomeFragment(), R.string.title_home);
    }

    public void addBadgeAt(int position, int number) {

        if(position == 3){
//            if(notification.getVisibility() == View.INVISIBLE)
//                notification.setVisibility(View.VISIBLE);
            notification.setGravityOffset(8, 3, true)
                    .setBadgeNumber(number)
                    .bindTarget(navigation.getBottomNavigationItemView(position));
        } else if(position == 2){
//            if(message.getVisibility() == View.INVISIBLE)
//                message.setVisibility(View.VISIBLE);
            message.setGravityOffset(8, 3, true)
                    .setBadgeNumber(number)
                    .bindTarget(navigation.getBottomNavigationItemView(position));
        } else if(position == 1){
            complaint.setGravityOffset(8, 3, true)
                    .setBadgeNumber(number)
                    .bindTarget(navigation.getBottomNavigationItemView(position));
        }
        Log.d("BadgeView", String.valueOf(number));

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "message", Toast.LENGTH_SHORT).show();
            Log.d("onesignal_data", intent.getStringExtra("data"));
            if(intent.getStringExtra("data") != null){
                switch (intent.getStringExtra("data")){
                    case Config.MESSAGE:
                        addBadgeAt(2, 1);
                        break;
                    case Config.COMPLAINT:
                        addBadgeAt(1, 1);
                        break;
                    case Config.NOTI:
                        addBadgeAt(3, session.getNotiBadge());
                        break;
                }
            }
        }
    };

    public void removeBadgeAt(int position){

        if(position == 3){
            notification.setBadgeNumber(0);
//            notification.hide(true);
        } else if(position == 2){
            message.setBadgeNumber(0);
            message.hide(true);
        } else if(position == 1){
            complaint.setBadgeNumber(0);
            complaint.hide(true);
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.activity_news);
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
                    removeBadgeAt(3);
                    session.resetBadge(3);
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
        PepperApps.activityPaused();// On Pause notify the Application
    }

    @Override
    protected void onResume() {
        super.onResume();
        PepperApps.activityResumed();// On Resume notify the Application
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("notification_received"));
        setup_tab();
    }
}
