package kuchingitsolution.betterpepperboard.personal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import kuchingitsolution.betterpepperboard.complaint.SingleReportActivity;
import kuchingitsolution.betterpepperboard.helper.Session;
import kuchingitsolution.betterpepperboard.helper.Utility;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<ReportModel> reportModels = new ArrayList<>();
    private Session session;
    private Utility utility = new Utility();

    public ReportAdapter(Context context, ArrayList<ReportModel> reportModels){
        this.context = context;
        this.reportModels = reportModels;
        session = new Session(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_test, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final ReportModel model = reportModels.get(position);
        String day = utility.get_date("day", model.getTimestamp());
        String month = utility.get_date("month", model.getTimestamp());
        holder.complaint_title.setText(model.getTitle());
        holder.person.setText(model.getUsername());
        holder.timestamp.setText(utility.get_time(model.getTimestamp()));
        holder.day.setText(day);
        holder.month.setText(month);
        holder.category.setText(model.getCategory());
        String location = String.format("at - %S", model.getLocation());
        holder.location.setText(location);

        if(model.getStatus().equals("1")){
            holder.status_color.setBackgroundResource(R.color.colorPrimary);
        } else if(model.getStatus().equals("2")){
            holder.status_color.setBackgroundResource(R.color.mt_red);
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
        return reportModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView day, month, complaint_title, category, timestamp, officer_name, person, location;
        ImageView status_color, attachment;

        public MyViewHolder(View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.day);
            month = itemView.findViewById(R.id.month);
            complaint_title = itemView.findViewById(R.id.complaint_title);
            category = itemView.findViewById(R.id.category);
            timestamp = itemView.findViewById(R.id.timestamp);
            officer_name = itemView.findViewById(R.id.officer_name);
            person = itemView.findViewById(R.id.person);
            location = itemView.findViewById(R.id.location);
            status_color = itemView.findViewById(R.id.status_color);
            attachment = itemView.findViewById(R.id.attachment);
        }
    }
}
