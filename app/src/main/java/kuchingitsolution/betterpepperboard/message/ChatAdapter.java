package kuchingitsolution.betterpepperboard.message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder>{

    private List<ChatModel> chatModels = new ArrayList<>();
    private Context context;
    private String user_id, msg_id;
    private int current_position;
    private int SELF = 100;
    private static String today;
    private onUserClickCallBack onUserClickCallBack;

    public ChatAdapter(Context context, List<ChatModel> chatModels, String user_id){
        this.context = context;
        this.chatModels = chatModels;
        this.user_id = user_id;
        this.onUserClickCallBack = ((onUserClickCallBack) context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_self, parent, false);
        } else if(viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_received_continue, parent, false);
        } else {
            // others message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ChatModel chatModel = chatModels.get(position);

        if(getItemViewType(position) == 1 || getItemViewType(position) == 100){
            holder.message.setText(chatModel.getMessage());
            holder.timestamp.setText(getTimeStamp(chatModel.getTimestamp()));
        } else {
            holder.username.setText(chatModel.getUsername());
            holder.message.setText(chatModel.getMessage());
            holder.timestamp.setText(getTimeStamp(chatModel.getTimestamp()));
        }

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                MenuItem DeleteMsg = contextMenu.add(Menu.NONE, 1, 1, "Delete Message");
                msg_id = chatModel.getMessage_id();
                current_position = position;
                DeleteMsg.setOnMenuItemClickListener(onEditMenu);
            }
        });
    }

    private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {
                case 1:
                    onUserClickCallBack.onDeleteMessage(msg_id, current_position);
                    break;
            }

            return true;
        }
    };

    @Override
    public int getItemCount() {
        return chatModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatModel chatModel = chatModels.get(position);
        ChatModel previousModel = chatModels.get(position != 0 ? position-1 : 0);

        if(position == 0 && !chatModel.getUser_id().equals(user_id))
            return 0;

        if(chatModel.getUser_id().equals(user_id))
            return SELF;
        else if(previousModel.getUser_id().equals(chatModel.getUser_id()) && !chatModel.getUser_id().equals(user_id))
            return 1;
        else
            return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profile_image;
        public TextView username, message, timestamp;
        public MyViewHolder(View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.image_message_profile);
            username = itemView.findViewById(R.id.text_message_name);
            message = itemView.findViewById(R.id.text_message_body);
            timestamp = itemView.findViewById(R.id.text_message_time);
        }
    }

    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat(
                "EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());
        String timestamp = "";

        try {
            Date date = format.parse(dateStr);
            timestamp = dateFormat2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    public static interface onUserClickCallBack {
        void onDeleteMessage(String msg_id, int current_position);
    }
}
