package kuchingitsolution.betterpepperboard.message;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import kuchingitsolution.betterpepperboard.R;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder>{

    List<UserListModel> userlists = new ArrayList<>();
    Context context;

    public UserListAdapter(Context context, List<UserListModel> userlists){
        this.context = context;
        this.userlists = userlists;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_chat_room, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final UserListModel userListModel = userlists.get(position);
        holder.profile_image.setImageResource(R.drawable.profile_sample);
        holder.timestamp.setText(userListModel.getTimestamp());
        holder.username.setText(userListModel.getUsername());

        if(!check_seen_msg(userListModel.getLast_seen(), userListModel.getTimestamp())){
            holder.previewContentMessage.setTypeface(holder.previewContentMessage.getTypeface(), Typeface.BOLD);
        } else holder.previewContentMessage.setTypeface(Typeface.DEFAULT);

        holder.previewContentMessage.setText(userListModel.getLast_msg());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), ChatActivity.class);
                intent.putExtra("chat_room_id", userListModel.getRoom_id());
                intent.putExtra("name", userListModel.getUsername());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userlists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profile_image;
        public TextView username, timestamp, count, previewContentMessage;
        public MyViewHolder(View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.chatUserName);
            timestamp = itemView.findViewById(R.id.timestamp);
            count = itemView.findViewById(R.id.count);
            previewContentMessage = itemView.findViewById(R.id.previewContentMessage);
        }
    }

    private boolean check_seen_msg(String last_seen, String last_msg_time){

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {
            Date seen_time = dateFormat.parse(last_seen);
            Date msg_time = dateFormat.parse(last_msg_time);
            long diff = seen_time.getTime() - msg_time.getTime();

            return diff >= 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }
}