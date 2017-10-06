package kuchingitsolution.betterpepperboard.home;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.widget.ImageView;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Session;
import kuchingitsolution.betterpepperboard.hotline.HotlineActivity;
import kuchingitsolution.betterpepperboard.info.InfoActivity;
import kuchingitsolution.betterpepperboard.map.MapsActivity;
import kuchingitsolution.betterpepperboard.new_complaint.NewComplaintActivity;

public class HomeFragment extends Fragment {

    View importPanel;
    ImageView hotline, newComplaint, viewMap, viewInfo;
    Handler handler = new Handler();
    Session session;
    ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getView() != null){

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    importPanel = ((ViewStub) getView().findViewById(R.id.menu_lists)).inflate();
                    hotline = (ImageView) getView().findViewById(R.id.hot_line);
                    newComplaint = (ImageView) getView().findViewById(R.id.newComplaint);
                    viewMap = (ImageView) getView().findViewById(R.id.viewMap);
                    viewInfo = (ImageView) getView().findViewById(R.id.viewInfo);
                }
            });

            session = new Session(getContext());
            hotline.setVisibility(View.VISIBLE);
            viewMap.setVisibility(View.VISIBLE);
            viewInfo.setVisibility(View.VISIBLE);

            Log.d("user_idddd", session.getUserID());

            if(session.getPosition().equals("3")){
                newComplaint.setVisibility(View.VISIBLE);
            }

            hotline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), HotlineActivity.class);
                    getActivity().startActivity(intent);
                }
            });

            newComplaint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), NewComplaintActivity.class);
                    getActivity().startActivity(intent);
                }
            });

            viewMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    getActivity().startActivity(intent);
                }
            });

            viewInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), InfoActivity.class);
                    getActivity().startActivity(intent);
                }
            });
        }
    }
}
