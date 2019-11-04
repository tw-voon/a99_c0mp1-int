package kuchingitsolution.betterpepperboard.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.complaint.SingleReportActivity;
import kuchingitsolution.betterpepperboard.map.MapsModel;

public class MapsAdapter extends RecyclerView.Adapter<MapsAdapter.MyViewHolder>{

    private ArrayList<MapsModel> newslist = new ArrayList<>();
    private Context context;

    public MapsAdapter(ArrayList<MapsModel> newslist, Context context){
        this.context = context;
        this.newslist = newslist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final MapsModel mapsModel = newslist.get(position);
        holder.tvTitle.setText(mapsModel.getReportTitle());
        holder.tvDesc.setText(mapsModel.getReportDescription());

        Picasso.with(context).load(mapsModel.getImgLink()).into(holder.imagePreview, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                holder.loading.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

            }
        });
        /*Glide.with(context)
                .load(mapsModel.getImgLink())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.loading.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imagePreview);*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SingleReportActivity.class);
                intent.putExtra("report_id", mapsModel.getReportID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newslist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, readMore;
        ImageView imagePreview;
        ProgressBar loading;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle =  itemView.findViewById(R.id.post_title);
            tvDesc =  itemView.findViewById(R.id.post_description);
            readMore =  itemView.findViewById(R.id.readmore);
            imagePreview =  itemView.findViewById(R.id.imgPreview);
            loading = itemView.findViewById(R.id.loading_img);
        }
    }
}
