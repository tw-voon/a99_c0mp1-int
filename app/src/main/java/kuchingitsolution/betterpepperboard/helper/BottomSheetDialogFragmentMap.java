package kuchingitsolution.betterpepperboard.helper;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.map.MapsModel;

public class BottomSheetDialogFragmentMap extends BottomSheetDialogFragment{
    private ArrayList<MapsModel> mapsModels = new ArrayList<>();
    private String location_id;
    private DB_Offline db_offline;

    public void setData(String location_id){
        this.location_id = location_id;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_map, null);
        db_offline = new DB_Offline(getActivity());
        RecyclerView recyclerView = contentView.findViewById(R.id.map_list);
        final TextView location_name = contentView.findViewById(R.id.location_name);
        final MapsAdapter mapsAdapter = new MapsAdapter(mapsModels, getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(mapsAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        final String result = db_offline.get_map(location_id);
        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                try {

                    JSONArray jsonArray = new JSONArray(result);
                    int length = jsonArray.length();
                    for (int i = 0 ; i < length ; i ++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        MapsModel news = new MapsModel(jsonObject);
                        location_name.setText(jsonObject.getString("name"));
                        mapsModels.add(news);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mapsAdapter.notifyDataSetChanged();
            }
        };

        handler.post(r);
        dialog.setContentView(contentView);
//        dialog.setContentView(contentView);
//        TextView tvTitle = (TextView) contentView.findViewById(R.id.post_title);
//        TextView tvDesc = (TextView) contentView.findViewById(R.id.post_description);
//        TextView readMore = (TextView) contentView.findViewById(R.id.readmore);
//        ImageView imagePreview = (ImageView) contentView.findViewById(R.id.imgPreview);
//        loading = contentView.findViewById(R.id.loading_img);
//        Log.d("imagelink", imageLink);
//
//        Glide.with(getContext())
//                .load(imageLink)
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        loading.setVisibility(View.GONE);
//                        return false;
//                    }
//                })
//                .skipMemoryCache(false)
//                .into(imagePreview);
//        tvTitle.setText(title);
//        tvDesc.setText(desc);
//
//        contentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), SingleReportActivity.class);
//                intent.putExtra("report_id", String.valueOf(report_id));
//                startActivity(intent);
//            }
//        });
    }
}
