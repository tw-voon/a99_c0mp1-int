package kuchingitsolution.betterpepperboard.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Session;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.MyViewHolder>{

    private ArrayList<SearchUserModel> newUsers = new ArrayList<>();
    public Context context;
    Session session;
    private AdapterCallback mAdapterCallback;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        CircleImageView userProfile;
        CheckBox selected;
        public MyViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.chatUserName);
            userProfile = (CircleImageView) itemView.findViewById(R.id.profile_image);
            selected = (CheckBox) itemView.findViewById(R.id.selected);
        }
    }

    SearchUserAdapter(Context context,  ArrayList<SearchUserModel> newUsers){
        this.context = context;
        this.newUsers = newUsers;
        try {
            this.mAdapterCallback = ((AdapterCallback) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    @Override
    public SearchUserAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final int current = position;
        final SearchUserModel user = newUsers.get(position);
        holder.username.setText(user.getName());
        holder.selected.setOnCheckedChangeListener(null);
        holder.selected.setChecked(user.getSelected());

        holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean is_check) {
                user.setSelected(is_check);
                mAdapterCallback.onUserClick(user.getId(), user.getName(), current, user.getSelected());
            }
        });

        if(!user.getAvatarLink().equals("null"))
            Picasso.with(context).load(user.getAvatarLink()).fit().into(holder.userProfile);
        else
            holder.userProfile.setImageResource(R.drawable.profile_sample);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!user.getSelected())
                    user.setSelected(true);
                else
                    user.setSelected(false);

                holder.selected.setChecked(user.getSelected());
                mAdapterCallback.onUserClick(user.getId(), user.getName(), current, user.getSelected());
            }
        });
    }

    @Override
    public int getItemCount() {
        return newUsers.size();
    }

    public static interface AdapterCallback {
        void onUserClick(String target_user_id, String name, int position, Boolean is_checked);
    }

}
