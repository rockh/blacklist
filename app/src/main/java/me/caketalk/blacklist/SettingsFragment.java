package me.caketalk.blacklist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;
import me.caketalk.R;

/**
 * @author Rock created at 12:24 04/04/13
 */
public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setting, container, false);
        final FragmentActivity activity = getActivity();

        // setting radio button status if Blacklist manager is running
        final ToggleButton tglBlacklist = (ToggleButton) v.findViewById(R.id.tglBlacklistService);
        if (AndrServiceHelper.isServiceRunning(activity, CallReceiverService.class.getName())) {
            tglBlacklist.setChecked(true);
        }

        tglBlacklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((ToggleButton) view).isChecked()) {
                    activity.startService(new Intent(activity, CallReceiverService.class));
                    Toast.makeText(activity, "Blacklist service has been enabled.", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder b = new AlertDialog.Builder(activity);
                    b.setTitle("Warning");
                    b.setMessage("The Blacklist service will be disabled, are you sure?");
                    b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.stopService(new Intent(activity, CallReceiverService.class));
                            Toast.makeText(activity, "Blacklist service has been disabled.", Toast.LENGTH_LONG).show();
                        }
                    });
                    b.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            tglBlacklist.setChecked(true);
                        }
                    });
                    b.show();
                }
            }
        });

        return v;
    }

}
