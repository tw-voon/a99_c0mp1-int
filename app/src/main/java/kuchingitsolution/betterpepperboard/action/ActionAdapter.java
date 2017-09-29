package kuchingitsolution.betterpepperboard.action;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;

import java.util.List;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.complaint.ImageFullscreenActivity;

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.MyViewHolder> {

    private List<ActionModel> actionModels;
    private Context context;
    int width, height;

    public ActionAdapter(Context context, List<ActionModel> actionModels){
        this.actionModels = actionModels;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_action, parent, false);
        return new ActionAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ActionModel actionModel = actionModels.get(position);
        RelativeLayout.LayoutParams parms;

        if(position == 0) {
            holder.timeline_dots.setBackgroundResource(R.drawable.current_act_);
            holder.action_taken.setTextColor(Color.BLACK);
            holder.action_time.setTextColor(Color.BLACK);
            holder.vertical_line.setBackgroundResource(R.color.colorPrimary);
        }
        else holder.timeline_dots.setBackgroundResource(R.drawable.previous_act_);

        Log.d("link", " link " + actionModel.getLink());

        if(actionModel.getMedia_type() != 0){
            holder.loading.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(actionModel.getLink())
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
                    .into(holder.image_attach);

            holder.image_attach.setVisibility(View.VISIBLE);
            Log.d("link", " id " + actionModel.getAction_taken());
            final String url = actionModel.getLink();
            holder.image_attach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context.getApplicationContext(), ImageFullscreenActivity.class);
                    intent.putExtra("image", url);
                    context.startActivity(intent);
                }
            });
        } else Glide.clear(holder.image_attach);

        holder.action_taken.setText(actionModel.getAction_taken());
        holder.action_time.setText(actionModel.getCreated_at());

//        int no_line = holder.action_taken.getMeasuredHeight();

//        holder.itemView.measure(View..UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        holder.itemView.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY);
//        width = holder.itemView.getMeasuredWidth();
//        height = holder.itemView.getMeasuredHeight();
//
//        Log.d("view height", " " + width + "  " + height);
//
//        int newheight = Math.round((width / height) / 10);
//
//        if(newheight == 1)
//            parms = new RelativeLayout.LayoutParams(3,newheight  * 180);
//        else if(newheight == 0)
//            parms = new RelativeLayout.LayoutParams(3, 180);
//        else
//            parms = new RelativeLayout.LayoutParams(3,newheight  * 90);
//
//        parms.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//        holder.vertical_line.setLayoutParams(parms);

//        Log.d("view height", " " + holder.content.getHeight() + " line: " + no_line + "  height " + Math.round((width / height) / 10)  * 80);

    }

    @Override
    public int getItemCount() {
        return actionModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View timeline_dots, vertical_line;
        TextView action_taken, action_time;
        ImageView image_attach;
        ProgressBar loading;
//        LinearLayout content;
        public MyViewHolder(View itemView) {
            super(itemView);
            timeline_dots = itemView.findViewById(R.id.timeline_dots);
            vertical_line = itemView.findViewById(R.id.vertical_line);
            action_taken = (TextView) itemView.findViewById(R.id.action_taken);
            action_time = (TextView) itemView.findViewById(R.id.action_time);
            image_attach = (ImageView) itemView.findViewById(R.id.image_attached);
            loading = itemView.findViewById(R.id.loading_img);
//            content = (LinearLayout) itemView.findViewById(R.id.content);

        }
    }
}
