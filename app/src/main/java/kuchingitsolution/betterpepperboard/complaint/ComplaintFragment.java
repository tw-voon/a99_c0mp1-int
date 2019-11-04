package kuchingitsolution.betterpepperboard.complaint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import kuchingitsolution.betterpepperboard.MainActivity2;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Session;

public class ComplaintFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DB_Offline db_offline;
    private Session session;
    private GetComplaint getComplaint;
    private boolean reload = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaint, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getView() != null) {
            viewPager =  getView().findViewById(R.id.viewpager);
            tabLayout =  getView().findViewById(R.id.tabs);
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
            session = new Session(getActivity());
            db_offline = new DB_Offline(getActivity());
            getComplaint = new GetComplaint(getContext());
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(true);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setOffscreenPageLimit(2);
        adapter.addFragment(new UnresolveFragment(), "Unsolved");
        adapter.addFragment(new ResolveFragment(), "Solved");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        getComplaint.load_data(1, Config.URL_GET_UNSOLVE);
        getComplaint.load_data(1, Config.URL_GET_SOLVE);
        super.onResume();
//        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onPause() {
        reload = true;
        ((MainActivity2)getActivity()).removeBadgeAt(1);
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
//        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
