package kuchingitsolution.betterpepperboard.action;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
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
            Picasso.with(context).load(actionModel.getLink()).into(holder.image_attach, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    holder.loading.setVisibility(View.GONE);
                }

                @Override
                public void onError() {

                }
            });
            /*Glide.with(context)
                    .load(actionModel.getLink())
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
                    .into(holder.image_attach);*/

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
        }
//        else Glide.with(context).clear(holder.image_attach);

        holder.action_taken.setText(actionModel.getAction_taken());
        holder.action_time.setText(actionModel.getCreated_at());

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
