package kuchingitsolution.betterpepperboard.message;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    private List<UserListModel> userlists = new ArrayList<>();
    private Context context;
    private String ids;
    private int current_position;
    private onUserClickCallBack onUserClickCallBack;

    public UserListAdapter(Context context, List<UserListModel> userlists, onUserClickCallBack onUserClickCallBack){
        this.context = context;
        this.userlists = userlists;
        this.onUserClickCallBack = onUserClickCallBack;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_chat_room, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

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

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                MenuItem Edit = contextMenu.add(Menu.NONE, 1, 1, "Change Group Name");
                MenuItem Delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");
                ids = userListModel.getRoom_id();
                current_position = position;
                Edit.setOnMenuItemClickListener(onEditMenu);
                Delete.setOnMenuItemClickListener(onEditMenu);
            }
        });

    }

    private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {
                case 1:
                    Log.d("contextmenu", "helloe edit " +  ids);
                    onUserClickCallBack.OnChangeName(ids, current_position);
                    break;

                case 2:
                    Log.d("contextmenu", "helloe delete");
                    onUserClickCallBack.OnDeleteChat(ids);
                    break;
            }
            return true;
        }
    };

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

    public interface onUserClickCallBack{
        void OnChangeName(String id, int position);
        void OnDeleteChat(String id);
    }
}
