package kuchingitsolution.betterpepperboard.personal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.complaint.DetailsComplaintActivity;
import kuchingitsolution.betterpepperboard.helper.Session;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<ReportModel> reportModels = new ArrayList<>();
    private Session session;

    public ReportAdapter(Context context, ArrayList<ReportModel> reportModels){
        this.context = context;
        this.reportModels = reportModels;
        session = new Session(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_report, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final ReportModel model = reportModels.get(position);
        holder.loading.setVisibility(View.VISIBLE);

//        if(session.getUserAvatar() == null || session.getUserAvatar().equals("null"))
        holder.profile_image.setImageResource(R.drawable.profile_sample);
//        else
//            Picasso.with(context).load(session.getUserAvatar()).into(holder.profile_image);

        holder.timestamp.setText(model.getTimestamp());
        holder.title.setText(model.getTitle());
        holder.username.setText(model.getUsername());
//        Picasso.with(context).load(model.getPreview()).into(holder.preview);

        Glide.with(holder.itemView.getContext())
                .load(model.getPreview())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.loading.setVisibility(View.GONE);
                        return false;
                    }
                })
                .skipMemoryCache(false)
                .into(holder.preview);

        if(model.getStatus().equals("1")){
            holder.imgStatus.setImageResource(R.drawable.status_approve);
        } else if(model.getStatus().equals("2")){
            holder.imgStatus.setImageResource(R.drawable.status_pending);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), DetailsComplaintActivity.class);
                intent.putExtra("report_id", model.getReport_id());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return reportModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView username, timestamp, title;
        public ImageView imgStatus, preview;
        public CircleImageView profile_image;
        public ProgressBar loading;

        public MyViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            timestamp = (TextView)itemView.findViewById(R.id.timestamp);
            title = (TextView) itemView.findViewById(R.id.title);
            imgStatus = (ImageView) itemView.findViewById(R.id.status);
            profile_image = (CircleImageView) itemView.findViewById(R.id.profile_image);
            preview = (ImageView) itemView.findViewById(R.id.preview);
            loading = itemView.findViewById(R.id.loading_img);
        }
    }
}
