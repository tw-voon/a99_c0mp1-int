package kuchingitsolution.betterpepperboard.notification;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.complaint.SingleReportActivity;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<NotificationModel> notificationModels;

    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_notification, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final NotificationModel model = notificationModels.get(position);
        holder.action_name.setText(Html.fromHtml(model.getAction()));
        holder.timestamp.setText(model.getTimestamp());
        holder.profile_image.setImageResource(R.drawable.profile_sample);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleReportActivity.class);
                intent.putExtra("report_id", model.getReport_id());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationModels.size();
    }

    public NotificationAdapter(Context context, ArrayList<NotificationModel> notificationModels){
        this.context = context;
        this.notificationModels = notificationModels;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView action_name, timestamp;
        CircleImageView profile_image;
        public MyViewHolder(View itemView) {
            super(itemView);
            profile_image = (CircleImageView) itemView.findViewById(R.id.profile_image);
            action_name = (TextView) itemView.findViewById(R.id.action_name);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
        }
    }
}
