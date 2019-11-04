package kuchingitsolution.betterpepperboard.complaint;

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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import kuchingitsolution.betterpepperboard.R;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private List<CommentModel> comments;
    Context context;
    private String url;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username, message, time;
        public CircleImageView profile;
        public ImageView comment_img;
        public ProgressBar loading;

        public MyViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            message = itemView.findViewById(R.id.commentmsg);
            time = itemView.findViewById(R.id.time);
            profile = itemView.findViewById(R.id.profile_image);
            comment_img = itemView.findViewById(R.id.comment_img);
            loading = itemView.findViewById(R.id.loading_img);
        }

    }

    CommentAdapter(Context context, List<CommentModel> comments) {
        this.comments = comments;
        this.context = context;
    }

    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_comments, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CommentAdapter.MyViewHolder holder, int position) {

        CommentModel comment = comments.get(position);
        holder.profile.setImageResource(R.drawable.profile_sample);
        holder.username.setText(comment.getUsername());
        holder.message.setText(comment.getCommentmsg());
        holder.time.setText(comment.getTimeago());
        if (comment.getImage_link().length() > 4) {
            holder.loading.setVisibility(View.VISIBLE);
            url = comment.getImage_link();
            Picasso.with(context).load(comment.getImage_link()).into(holder.comment_img, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    holder.loading.setVisibility(View.GONE);
                }

                @Override
                public void onError() {

                }
            });
            /*Glide.with(context)
                    .load(comment.getImage_link())
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
                    .into(holder.comment_img);*/

            holder.comment_img.setVisibility(View.VISIBLE);
            holder.comment_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context.getApplicationContext(), ImageFullscreenActivity.class);
                    intent.putExtra("image", url);
                    context.startActivity(intent);
                }
            });
        }
//        else Glide.with(context).clear(holder.comment_img);

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


}
