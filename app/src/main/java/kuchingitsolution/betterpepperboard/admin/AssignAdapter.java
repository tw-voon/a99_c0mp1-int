package kuchingitsolution.betterpepperboard.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.complaint.DetailsComplaintActivity;

public class AssignAdapter extends RecyclerView.Adapter<AssignAdapter.MyViewHolder>{

    private List<AssignModel> assignModelList;
    private AssignOption assignOption;
    Context context;

    @Override
    public AssignAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_officer, parent, false);
        this.assignOption = ((AssignOption) context);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AssignAdapter.MyViewHolder holder, int position) {

        final AssignModel officerModel = assignModelList.get(position);

        holder.username.setText(officerModel.getUsername());
        holder.position.setText("Officer");
        holder.imgProfile.setImageResource(R.drawable.profile_sample);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "User id: " + officerModel.getUsername(), Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Are you sure you want to assign " +  officerModel.getUsername() + " to handle this?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent(context, DetailsComplaintActivity.class);
                                context.startActivity(intent);
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                holder.checkBox.setChecked(true);
//                alertDialog.show();
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch (compoundButton.getId()){
                    case R.id.selected:
                        holder.checkBox.setChecked(b);
                        assignOption.onOfficerClick(officerModel.getUser_id(), officerModel.getUsername(), b);
                        break;
                }
            }
        });

    }

    AssignAdapter(Context context, List<AssignModel> assignModelList){
        this.context = context;
        this.assignModelList = assignModelList;
    }

    @Override
    public int getItemCount() {
        return assignModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username, position;
        public ImageView imgProfile;
        public CheckBox checkBox;
        public MyViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            position = (TextView) itemView.findViewById(R.id.position);
            imgProfile = (CircleImageView) itemView.findViewById(R.id.profile_image);
            checkBox = (CheckBox) itemView.findViewById(R.id.selected);
        }
    }

    public static interface AssignOption {
        void onOfficerClick(String officer_id, String name, Boolean checked);
    }
}
