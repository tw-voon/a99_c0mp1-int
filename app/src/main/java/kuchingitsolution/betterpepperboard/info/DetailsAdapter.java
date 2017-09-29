package kuchingitsolution.betterpepperboard.info;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kuchingitsolution.betterpepperboard.R;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.MyViewHolder>{

    List<DetailsModel> details = new ArrayList<>();
    Context context;

    DetailsAdapter(Context context, List<DetailsModel> details){
        this.context = context;
        this.details = details;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_details, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        DetailsModel detail = details.get(position);
        holder.tip_name.setText(detail.getTips_name());
        holder.tip_desc.setText(detail.getTips_desc());

    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tip_name, tip_desc;
        public MyViewHolder(View itemView) {
            super(itemView);
            tip_name = (TextView) itemView.findViewById(R.id.tips_name);
            tip_desc = (TextView) itemView.findViewById(R.id.tips_desc);
        }
    }
}
