package kuchingitsolution.betterpepperboard.complaint;

import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import kuchingitsolution.betterpepperboard.R;

public class loadingAdapter extends RecyclerView.ViewHolder {
    public ProgressBar loading;
    public loadingAdapter(View itemView) {
        super(itemView);
        loading = (ProgressBar) itemView.findViewById(R.id.progressBar1);
    }

}
