package kuchingitsolution.betterpepperboard.helper;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import kuchingitsolution.betterpepperboard.R;

public class BottomSheetDialogFragmentMap extends BottomSheetDialogFragment{
    private String title, desc, imageLink;
    private int report_id;

    public void setData(String title, String desc, int report_id, String imageLink){
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
        Picasso.with(getContext()).load(imageLink).into(imagePreview);
        tvTitle.setText(title);
        tvDesc.setText(desc);

        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "report id" + String.valueOf(report_id), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getActivity(), SinglePost.class);
//                intent.putExtra("report_id", String.valueOf(report_id));
//                startActivity(intent);
            }
        });
    }
}
