package kuchingitsolution.betterpepperboard.complaint;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import kuchingitsolution.betterpepperboard.R;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{

    private List<CommentModel> comments;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username, message, time;
        public CircleImageView profile;
        public MyViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            message = (TextView) itemView.findViewById(R.id.commentmsg);
            time = (TextView) itemView.findViewById(R.id.time);
            profile = (CircleImageView) itemView.findViewById(R.id.profile_image);
        }

    }

    CommentAdapter(Context context, List<CommentModel> comments){
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
    public void onBindViewHolder(CommentAdapter.MyViewHolder holder, int position) {

        CommentModel comment = comments.get(position);
//        if(comment.getAvatar_link().equals("null"))
        holder.profile.setImageResource(R.drawable.profile_sample);
//        else
//            Picasso.with(context).load(comment.getAvatar_link()).into(holder.profile);
        holder.username.setText(comment.getUsername());
        holder.message.setText(comment.getCommentmsg());
        holder.time.setText(comment.getTimeago());

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


}
