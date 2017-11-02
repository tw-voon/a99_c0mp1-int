package kuchingitsolution.betterpepperboard.helper;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialogFragment;
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

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.complaint.DetailsComplaintActivity;
import kuchingitsolution.betterpepperboard.complaint.SingleReportActivity;

public class BottomSheetDialogFragmentMap extends BottomSheetDialogFragment{
    private String title, desc, imageLink;
    private String report_id;
    private ProgressBar loading;

    public void setData(String title, String desc, String report_id, String imageLink){
        this.title = title;
        this.desc = desc;
        this.report_id = report_id;
        this.imageLink = imageLink;
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_map, null);
        dialog.setContentView(contentView);
        TextView tvTitle = (TextView) contentView.findViewById(R.id.post_title);
        TextView tvDesc = (TextView) contentView.findViewById(R.id.post_description);
        TextView readMore = (TextView) contentView.findViewById(R.id.readmore);
        ImageView imagePreview = (ImageView) contentView.findViewById(R.id.imgPreview);
        loading = contentView.findViewById(R.id.loading_img);
//        Picasso.with(getContext()).load(imageLink).into(imagePreview);
        Log.d("imagelink", imageLink);

        Glide.with(getContext())
                .load(imageLink)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        loading.setVisibility(View.GONE);
                        return false;
                    }
                })
                .skipMemoryCache(false)
                .into(imagePreview);
        tvTitle.setText(title);
        tvDesc.setText(desc);

        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "report id" + String.valueOf(report_id), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), SingleReportActivity.class);
                intent.putExtra("report_id", String.valueOf(report_id));
                startActivity(intent);
            }
        });
    }
}
