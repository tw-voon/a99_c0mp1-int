package kuchingitsolution.betterpepperboard.helper;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import kuchingitsolution.betterpepperboard.R;

public class BottomSheetDialogUpload extends BottomSheetDialogFragment {

    public ProgressBar uploading;
    public TextView uploading_progress;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_upload, null);
        dialog.setContentView(contentView);
        uploading = contentView.findViewById(R.id.uploading);
        uploading_progress = contentView.findViewById(R.id.uploading_progress);
    }

    public void setUploading_progress(int progress){
        uploading.setProgress(progress);
        uploading_progress.setText(String.valueOf(progress));
    }

}
