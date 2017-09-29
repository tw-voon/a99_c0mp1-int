package kuchingitsolution.betterpepperboard.hotline;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kuchingitsolution.betterpepperboard.R;

public class HotlineAdapter extends RecyclerView.Adapter<HotlineAdapter.MyViewHolder>{

    ArrayList<HotlineModel> hotlines = new ArrayList<>();
    public Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, contact_no, desc;
        public final ImageButton callButton;
        public TextView readmore;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            contact_no = (TextView) itemView.findViewById(R.id.contact);
            desc = (TextView) itemView.findViewById(R.id.desc);
            callButton = (ImageButton) itemView.findViewById(R.id.call);
            readmore = (TextView) itemView.findViewById(R.id.readmore);
        }
    }
    HotlineAdapter (ArrayList<HotlineModel> contactList)
    {
        this.hotlines = contactList;
    }

    @Override
    public HotlineAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_hotline, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    public HotlineAdapter(Context context, ArrayList<HotlineModel> hotlines){
        this.hotlines = hotlines;
    }

    @Override
    public void onBindViewHolder(final HotlineAdapter.MyViewHolder holder,int position) {
        final HotlineModel contact = hotlines.get(position);
        holder.title.setText(contact.getTitle());
        holder.contact_no.setText(contact.getContact_no());
        holder.desc.setText(contact.getDesc());
        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HotlineModel contact = hotlines.get(holder.getAdapterPosition());
                Intent i = new Intent(Intent.ACTION_DIAL);
                String phone = "tel:" + contact.getContact_no();
                i.setData(Uri.parse(phone));
                view.getContext().startActivity(i);
                Toast.makeText(view.getContext(), "Call to " + contact.getContact_no(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.readmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.desc.getVisibility() == View.GONE)
                    holder.desc.setVisibility(View.VISIBLE);
                else if(holder.desc.getVisibility() == View.VISIBLE)
                    holder.desc.setVisibility(View.GONE);
            }
        });

        ObjectAnimator animation = ObjectAnimator.ofInt(holder.desc, "maxLines", holder.desc.getMaxLines());
        animation.setDuration(200).start();
    }

    @Override
    public int getItemCount() {
        return hotlines.size();
    }

    public void filter(ArrayList<HotlineModel> filterList)
    {
        hotlines = new ArrayList<>();
        hotlines.addAll(filterList);
        notifyDataSetChanged();
    }

}
