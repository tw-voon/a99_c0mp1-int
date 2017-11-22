package kuchingitsolution.betterpepperboard.personal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.complaint.SingleReportActivity;
import kuchingitsolution.betterpepperboard.helper.Session;

public class FollowedAdapter extends RecyclerView.Adapter<FollowedAdapter.MyViewHolder>{

    Context context;
    ArrayList<FollowedModel> followedModels = new ArrayList<>();
    Session session;

    public FollowedAdapter(Context context, ArrayList<FollowedModel> followedModels){
        this.context = context;
        this.followedModels = followedModels;
        session = new Session(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_report, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final FollowedModel model = followedModels.get(position);

        holder.profile_image.setImageResource(R.drawable.profile_sample);

        holder.timestamp.setText(model.getTimestamp());
        holder.title.setText(model.getTitle());
        holder.username.setText(model.getUsername());

        Glide.with(holder.itemView.getContext())
                .load(model.getPreview())
                .thumbnail(Glide.with(holder.itemView.getContext()).load(R.drawable.load))
                .skipMemoryCache(false)
                .into(holder.preview);

        if(model.getStatus().equals("1")){
            holder.imgStatus.setImageResource(R.drawable.status_approve);
        } else if(model.getStatus().equals("2")) {
            holder.imgStatus.setImageResource(R.drawable.status_pending);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), SingleReportActivity.class);
                intent.putExtra("report_id", model.getReport_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return followedModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView username, timestamp, title;
        public ImageView imgStatus, preview;
        public CircleImageView profile_image;

        public MyViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            timestamp = (TextView)itemView.findViewById(R.id.timestamp);
            title = (TextView) itemView.findViewById(R.id.title);
            imgStatus = (ImageView) itemView.findViewById(R.id.status);
            profile_image = (CircleImageView) itemView.findViewById(R.id.profile_image);
            preview = (ImageView) itemView.findViewById(R.id.preview);
        }
    }
}
