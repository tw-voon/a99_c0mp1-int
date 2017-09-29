package kuchingitsolution.betterpepperboard.complaint;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import kuchingitsolution.betterpepperboard.R;

public class loadingAdapter extends RecyclerView.ViewHolder {
    public ProgressBar loading;
    public loadingAdapter(View itemView) {
        super(itemView);
        loading = (ProgressBar) itemView.findViewById(R.id.progressBar1);
    }

}
