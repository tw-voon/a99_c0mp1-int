package kuchingitsolution.betterpepperboard.complaint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Session;
import kuchingitsolution.betterpepperboard.notification.NotificationAdapter;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.MyViewHolder> {

    private ArrayList<ComplaintModel> newslist = new ArrayList<>();
    public Context context;
    private DB_Offline db_offline;
    private Session session;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView username, report_title, report_desc, timestamp, location_name, supported, affected, status, officer_incharge;
        ImageView report_image;
        CircleImageView profile_image;
        Button comment, support, affect;
        ProgressBar loading;

        MyViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            report_title =  itemView.findViewById(R.id.report_title);
            report_desc =  itemView.findViewById(R.id.description);
            report_image =  itemView.findViewById(R.id.imageView);
            timestamp =  itemView.findViewById(R.id.timestamp);
            location_name =  itemView.findViewById(R.id.location);
            supported =  itemView.findViewById(R.id.supported);
            affected =  itemView.findViewById(R.id.affected);
            status =  itemView.findViewById(R.id.status);
            officer_incharge =  itemView.findViewById(R.id.officer_incharge);
            profile_image =  itemView.findViewById(R.id.profile_image);
            comment =  itemView.findViewById(R.id.comment);
            support =  itemView.findViewById(R.id.support);
            affect =  itemView.findViewById(R.id.affect);
            loading = itemView.findViewById(R.id.loading_img);
        }
    }

    public ComplaintAdapter(Context context, ArrayList<ComplaintModel> newsList){
        this.context = context;
        this.newslist = newsList;
    }


    @Override
    public ComplaintAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_complaints, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(final ComplaintAdapter.MyViewHolder holder, int position) {

        db_offline = new DB_Offline(this.context);
        session = new Session(this.context);
        final ComplaintModel news = newslist.get(position);
        String officer_name;
        holder.loading.setVisibility(View.VISIBLE);

        holder.username.setText(news.getUsername());
        Log.d("Image Url ---", news.getLink());

        Glide.with(holder.itemView.getContext())
                .load(news.getLink())
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
                .priority(position < 5 ? Priority.HIGH : Priority.NORMAL)
                .into(holder.report_image);

        holder.profile_image.setImageResource(R.drawable.ic_person_outline_black_24dp);
        holder.report_desc.setText(news.getNewsDescription());
        holder.report_title.setText(news.getReportTitle());
        holder.timestamp.setText(get_time(news.getCreated_at()));
        String location = String.format("at - <b>%s</b>", news.getLocation_name());
        holder.location_name.setText(Html.fromHtml(location));
//        if(news.getStatus_id() == 1){
        String tvStatus = news.getStatus_name();
        holder.status.setText(tvStatus);
//        } else if (news.getStatus_id() == 2){
//            String tvStatus = "Unsolve";
//            holder.status.setText(tvStatus);
//        }

        if(!news.getOfficer_name().equals("NULL")) {
            officer_name = String.format("Assigned to - %S", news.getOfficer_name());
            holder.officer_incharge.setText(officer_name);
        } else holder.officer_incharge.setVisibility(View.GONE);

        String support = String.format("%S Supported", news.getSupported());
        String affect = String.format("%S Affected", news.getAffected());
        holder.supported.setText(support);
        holder.affected.setText(affect);
        JSONObject response = db_offline.ifResponse(news.getId());

        if(response.length() != 0){
            try {

                Log.d("response", response.getString("support"));
                Log.d("response", response.getString("affected"));

                if(response.getString("support").equals("1")) {
                    holder.support.setTextColor(Color.BLUE);
                } else holder.support.setTextColor(Color.BLACK);

                if(response.getString("affected").equals("1"))
                    holder.affect.setTextColor(Color.BLUE);
                else holder.affect.setTextColor(Color.BLACK);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        holder.support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.support.getCurrentTextColor() == Color.BLUE) {
                    holder.support.setTextColor(Color.BLACK);
                    news.noSupport();
                    holder.supported.setText(news.getSupport() + " Supported");
                    updateResponse("support", 0, news.getId(), news.getSupport());
                }
                else {
                    holder.support.setTextColor(Color.BLUE);
                    news.support();
                    holder.supported.setText(news.getSupport() + " Supported");
                    updateResponse("support", 1, news.getId(), news.getSupport());
                }
            }
        });

        holder.affect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status;
                if(holder.affect.getCurrentTextColor() == Color.BLUE){
                    holder.affect.setTextColor(Color.BLACK);
                    news.noAffected();
                    status = String.format("%s Affected", news.getAffected());
                    holder.affected.setText(status);
                    updateResponse("affected", 0, news.getId(), news.getAffected());
                } else{
                    holder.affect.setTextColor(Color.BLUE);
                    news.affected();
                    status = String.format("%s Affected", news.getAffected());
                    holder.affected.setText(status);
                    updateResponse("affected", 1, news.getId(), news.getAffected());
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Under constructing comment feature for"+ news.getReportTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context.getApplicationContext(), DetailsComplaintActivity.class);
                intent.putExtra("report_id", news.getId());
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Under constructing comment feature for"+ news.getReportTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DetailsComplaintActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("report_id", news.getId());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    /*
    ADD DATA TO ADAPTER
     */
    public void add(ComplaintModel s) {
        newslist.add(s);
        notifyDataSetChanged();
    }
    /*
    CLEAR DATA FROM ADAPTER
     */
    public void clear() {
        newslist.clear();
        notifyDataSetChanged();
    }

    private void updateResponse(final String type,final int value, final String report_id, final int initial){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.UPDATE_RESPONSE,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("result", response);
                        if(response.equals("success")){
                            db_offline.updateResponse(type, value, session.getUserID(), report_id, initial);
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("user_id",session.getUserID());
                map.put("report_id", report_id);
                map.put("type", type);
                map.put("value", String.valueOf(value));
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private String get_time(String time){

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        SimpleDateFormat dateFormat2 = new SimpleDateFormat(
                "EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());

        try {
            Date date = dateFormat.parse(time);
            Date now = dateFormat.parse(getDateTime());
            Log.d("date", date.toString() + " " + now.toString());
            long diff = now.getTime() - date.getTime();

            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            Log.d("start_min", " " + diffMinutes);
            Log.d("start_hour", " " + diffHours);
            Log.d("start_day", " " + diffDays);

            if(diffDays>0) {
                Log.d("Time", diffDays + " days ago");
                return String.valueOf(dateFormat2.format(date));
            } else if(diffHours>=1) {
                Log.d("Time", diffHours + " hours ago");
                return String.format("%s hour ago", diffHours);
            } else if(diffMinutes<60) {
                Log.d("Time", diffMinutes + " minutes ago");
                return String.format("%s minute ago",diffMinutes);
            } else {
                Log.d("Time", String.valueOf("Now"));
                return String.valueOf(0);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;

    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public int getItemCount() {
        return newslist == null ? 0 : newslist.size();
    }

    public void filter(ArrayList<ComplaintModel> filterList)
    {
        newslist = new ArrayList<>();
        newslist.addAll(filterList);
        notifyDataSetChanged();
    }
}
