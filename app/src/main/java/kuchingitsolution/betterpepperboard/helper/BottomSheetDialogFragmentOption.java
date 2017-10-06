package kuchingitsolution.betterpepperboard.helper;

import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import kuchingitsolution.betterpepperboard.R;

public class BottomSheetDialogFragmentOption extends BottomSheetDialogFragment{
    private String progress, action;
    private OptionBottomSheetCallback optionBottomSheetCallback;

    public void setData(String status, String action){
        this.progress = status;
        this.action = action;
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        Session session = new Session(getContext());
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_option, null);
        dialog.setContentView(contentView);
        final LinearLayout status = (LinearLayout) dialog.findViewById(R.id.btnStatus);
        LinearLayout officer = (LinearLayout) dialog.findViewById(R.id.btnOfficer);
        LinearLayout updateAction = (LinearLayout) dialog.findViewById(R.id.btnUpdateAction);
        this.optionBottomSheetCallback = ((OptionBottomSheetCallback) getContext());
        status.setVisibility(View.GONE);

        if(session.getPosition().equals("1"))
            updateAction.setVisibility(View.GONE);
        else if(session.getPosition().equals("2"))
            officer.setVisibility(View.GONE);

        updateAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionBottomSheetCallback.assignOfficer();
            }
        });

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "status clicked", Toast.LENGTH_SHORT).show();
                optionBottomSheetCallback.onUserClick(progress, action);
            }
        });

        officer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "officer clicked", Toast.LENGTH_SHORT).show();
                optionBottomSheetCallback.assignOfficer();
            }
        });

    }

    public static interface OptionBottomSheetCallback {
        void onUserClick(String status, String action_taken);
        void assignOfficer();
    }
}
