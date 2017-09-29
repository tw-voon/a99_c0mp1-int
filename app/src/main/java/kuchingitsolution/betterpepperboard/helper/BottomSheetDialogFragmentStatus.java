package kuchingitsolution.betterpepperboard.helper;

import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import kuchingitsolution.betterpepperboard.R;

public class BottomSheetDialogFragmentStatus extends BottomSheetDialogFragment {
    private String status, reason, action, imgLink;

    public void setData(String status, String reason, String action, String imgLink){
        this.status = status;
        this.reason = reason;
        this.action = action;
        this.imgLink = imgLink;
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_status, null);
        dialog.setContentView(contentView);
        TextView tvStatus = (TextView) contentView.findViewById(R.id.tvStatus);
        TextView tvActionTaken = (TextView) contentView.findViewById(R.id.tvActionTaken);
        TextView tvReason = (TextView) contentView.findViewById(R.id.tvReason);
        TextView labelImg = (TextView) contentView.findViewById(R.id.labelImg);
        ImageView imgStatus = (ImageView) contentView.findViewById(R.id.status_img);
        tvStatus.setText(status);
        tvReason.setText(reason);
        tvActionTaken.setText(action);
        Log.d("imglink", imgLink);
        if(!imgLink.equals("null"))
            Picasso.with(getContext()).load(imgLink).into(imgStatus);
        else {
            imgStatus.setVisibility(View.GONE);
            labelImg.setVisibility(View.GONE);
        }
    }
}
