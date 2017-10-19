package kuchingitsolution.betterpepperboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
//        BottomNavigationViewHelper.disableShiftMode(navigation);
//        bottomNavigationMenuView = (BottomNavigationMenuView)navigation.getChildAt(0);
//        bottomNavigationMenuView2 = (BottomNavigationMenuView)navigation.getChildAt(0);
//        icon_noti = bottomNavigationMenuView.getChildAt(3);
//        icon_message = bottomNavigationMenuView2.getChildAt(2);
//        notifcation_view = (BottomNavigationItemView)icon_noti;
//        message_view = (BottomNavigationItemView)icon_message;
//
//        View badge = LayoutInflater.from(this)
//                .inflate(R.layout.notification_badge, bottomNavigationMenuView, false);
//        notifcation_view.addView(badge);
//        message_view.addView(badge);
        content = findViewById(R.id.contents);
        db_offline = new DB_Offline(this);
        session = new Session(this);
        startService(new Intent(MainActivity2.this, onesignal_service.class));

        pushFragment(new HomeFragment(), R.string.title_home);
        addBadgeAt(2,1);
    }

    private Badge addBadgeAt(int position, int number) {
        // add badge setGravityOffset(12, 2, true)
        return new QBadgeView(this)
                .setBadgeNumber(number)
                .setGravityOffset(8, 3, true)
                .bindTarget(navigation.getBottomNavigationItemView(position))
                .setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
                    @Override
                    public void onDragStateChanged(int dragState, Badge badge, View targetView) {
                        if (Badge.OnDragStateChangedListener.STATE_SUCCEED == dragState)
                            Toast.makeText(MainActivity2.this, "Count 1", Toast.LENGTH_SHORT).show();
                    }
                });
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
                    if(currentFragment instanceof ComplaintFragment)
                        return false;
                    else{
                        pushFragment(new ComplaintFragment(), R.string.title_complaint);
                        return true;
                    }
                case R.id.navigation_notifications:
                    pushFragment(new NotificationFragment(), R.string.title_notifications);
                    return true;
                case R.id.navigation_message:
                    pushFragment(new MessageFragment(), R.string.action_chat);
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

        FragmentManager fragmentManager = getSupportFragmentManager();
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
        super.onPause();
        PepperApps.activityPaused();// On Pause notify the Application
    }

    @Override
    protected void onResume() {

        super.onResume();
        PepperApps.activityResumed();// On Resume notify the Application
    }
}
