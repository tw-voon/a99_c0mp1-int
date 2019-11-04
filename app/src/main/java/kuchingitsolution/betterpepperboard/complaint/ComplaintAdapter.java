package kuchingitsolution.betterpepperboard.complaint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Session;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.MyViewHolder> {

    private ArrayList<ComplaintModel> newslist = new ArrayList<>();
    public Context context;
    private DB_Offline db_offline;
    private Session session;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView day, month, complaint_title, category, timestamp, officer_name, person, location;
        TextView like_no, like, follow_no, follow;
        ImageView like_logo, follow_logo, status_color, attachment;
        LinearLayout like_region, follow_region;

        MyViewHolder(View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.day);
            month = itemView.findViewById(R.id.month);
            complaint_title = itemView.findViewById(R.id.complaint_title);
            category = itemView.findViewById(R.id.category);
            timestamp = itemView.findViewById(R.id.timestamp);
            officer_name = itemView.findViewById(R.id.officer_name);
            person = itemView.findViewById(R.id.person);
            location = itemView.findViewById(R.id.location);
            like = itemView.findViewById(R.id.like);
            like_no = itemView.findViewById(R.id.like_no);
            follow = itemView.findViewById(R.id.follow);
            follow_no = itemView.findViewById(R.id.follow_no);
            like_logo = itemView.findViewById(R.id.like_logo);
            follow_logo = itemView.findViewById(R.id.follow_logo);
            status_color = itemView.findViewById(R.id.status_color);
            like_region = itemView.findViewById(R.id.like_region);
            follow_region = itemView.findViewById(R.id.follow_region);
            attachment = itemView.findViewById(R.id.attachment);
        }
    }

    public ComplaintAdapter(Context context, ArrayList<ComplaintModel> newsList) {
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
    public void onBindViewHolder(final ComplaintAdapter.MyViewHolder holder, final int position) {

        db_offline = new DB_Offline(this.context);
        session = new Session(this.context);
        final ComplaintModel news = newslist.get(position);

        holder.person.setText(news.getUsername());
        holder.complaint_title.setText(news.getReportTitle());
        holder.timestamp.setText(get_time(news.getCreated_at()));
        String location = String.format("at - <b>%s</b>", news.getLocation_name());
        holder.location.setText(Html.fromHtml(location));
        holder.day.setText(get_date("day", news.getCreated_at()));
        holder.month.setText(get_date("month", news.getCreated_at()));
        holder.category.setText(news.getType_name());

        if (news.getStatus_id() == 1) {
            holder.status_color.setBackgroundResource(R.color.colorPrimary);
        } else {
            holder.status_color.setBackgroundResource(R.color.mt_red);
        }

        String officer_name;

        if (!news.getOfficer_name().equals("NULL")) {
            officer_name = news.getOfficer_name();
        } else {
            officer_name = "Waiting admin assign officer";
        }

        holder.officer_name.setText(officer_name);
        final String support = String.format("(%S)", db_offline.get_support(news.getId()));
        String affect = String.format("(%S)", db_offline.get_affect(news.getId()));
        holder.like_no.setText(support);
        holder.follow_no.setText(affect);
        JSONObject response = db_offline.ifResponse(news.getId());
//        Log.d("responssss", "Report id : " + news.getId() + " Response : " + response.toString());

        if (response.length() != 0) {
            try {

//                Log.d("response", response.getString("support"));
//                Log.d("response", response.getString("affected"));

                if (response.getString("support").equals("1")) {
                    holder.like.setTextColor(Color.BLUE);
                    holder.like_no.setTextColor(Color.BLUE);
                    holder.like_logo.setImageResource(R.drawable.ic_favorite_black_24dp);
                } else {
                    holder.like.setTextColor(Color.BLACK);
                    holder.like_no.setTextColor(Color.BLACK);
                    holder.like_logo.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }

                if (response.getString("affected").equals("1")) {
                    holder.follow.setTextColor(Color.BLUE);
                    holder.follow_no.setTextColor(Color.BLUE);
                    holder.follow_logo.setImageResource(R.drawable.ic_star_black_24dp);
                } else {
                    holder.follow.setTextColor(Color.BLACK);
                    holder.follow_no.setTextColor(Color.BLACK);
                    holder.follow_logo.setImageResource(R.drawable.ic_star_border_black_24dp);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            holder.follow.setTextColor(Color.BLACK);
            holder.follow_no.setTextColor(Color.BLACK);
            holder.follow_logo.setImageResource(R.drawable.ic_star_border_black_24dp);

            holder.like.setTextColor(Color.BLACK);
            holder.like_no.setTextColor(Color.BLACK);
            holder.like_logo.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }

        holder.like_region.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.like.getCurrentTextColor() == Color.BLUE) {

                    holder.like.setTextColor(Color.BLACK);
                    holder.like_no.setTextColor(Color.BLACK);
                    holder.like_logo.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    news.noSupport();
                    String support = String.format("(%S)", news.getSupport());
                    holder.like_no.setText(support);
                    updateResponse("support", 0, news.getId(), news.getSupport());

                } else {

                    holder.like.setTextColor(Color.BLUE);
                    holder.like_no.setTextColor(Color.BLUE);
                    holder.like_logo.setImageResource(R.drawable.ic_favorite_black_24dp);
                    news.support();
                    String support = String.format("(%S)", news.getSupport());
                    holder.like_no.setText(support);
                    updateResponse("support", 1, news.getId(), news.getSupport());

                }
            }
        });

        holder.follow_region.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.follow.getCurrentTextColor() == Color.BLUE) {

                    holder.follow.setTextColor(Color.BLACK);
                    holder.follow_no.setTextColor(Color.BLACK);
                    holder.follow_logo.setImageResource(R.drawable.ic_star_border_black_24dp);
                    news.noAffected();
                    String affect = String.format("(%S)", news.getSupport());
                    holder.follow_no.setText(affect);
                    updateResponse("affected", 0, news.getId(), news.getAffected());

                } else {

                    holder.follow.setTextColor(Color.BLUE);
                    holder.follow_no.setTextColor(Color.BLUE);
                    holder.follow_logo.setImageResource(R.drawable.ic_star_black_24dp);
                    news.affected();
                    String affect = String.format("(%S)", news.getAffected());
                    holder.follow_no.setText(affect);
                    updateResponse("affected", 1, news.getId(), news.getAffected());

                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleReportActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("report_id", news.getId());
                intent.putExtras(bundle);
                session.setCurrentId(news.getId());
                session.setCurrentPosition(String.valueOf(position));
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

    private void updateResponse(final String type, final int value, final String report_id, final int initial) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.UPDATE_RESPONSE,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.d("result", response);
                        if (response.equals("success")) {
                            db_offline.updateResponse(type, value, session.getUserID(), report_id, initial);
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", session.getUserID());
                map.put("report_id", report_id);
                map.put("type", type);
                map.put("value", String.valueOf(value));
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private String get_date(String type, String time) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        SimpleDateFormat dateFormat2 = new SimpleDateFormat(
                "dd", Locale.getDefault());

        SimpleDateFormat dateFormat3 = new SimpleDateFormat(
                "MMM", Locale.getDefault());

        String day = null;

        switch (type) {
            case "day":
                try {
                    Date date = dateFormat.parse(time);
                    day = dateFormat2.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            case "month":
                try {
                    Date date = dateFormat.parse(time);
                    day = dateFormat3.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }

        return day;
    }

    private String get_time(String time) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        SimpleDateFormat dateFormat2 = new SimpleDateFormat(
                "EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());

        try {
            Date date = dateFormat.parse(time);
            Date now = dateFormat.parse(getDateTime());
            long diff = now.getTime() - date.getTime();

            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);


            if (diffDays > 0) {
                return String.valueOf(dateFormat2.format(date));
            } else if (diffHours >= 1) {
                return String.format("%s hour ago", diffHours);
            } else if (diffMinutes < 60) {
                return String.format("%s minute ago", diffMinutes);
            } else {
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

    public void filter(ArrayList<ComplaintModel> filterList) {
        newslist = new ArrayList<>();
        newslist.addAll(filterList);
        notifyDataSetChanged();
    }
}
