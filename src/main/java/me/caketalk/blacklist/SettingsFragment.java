package me.caketalk.blacklist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import me.caketalk.blacklist.service.BlacklistService;
import me.caketalk.blacklist.util.ServiceUtil;

/**
 * @author Rock created at 12:24 04/04/13
 */
public class SettingsFragment extends SherlockFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setting, container, false);

        // setting radio button status if Blacklist service is running
        final CheckBox checkBox = (CheckBox) v.findViewById(R.id.chkEnableBlacklist);
        if (ServiceUtil.isServiceRunning(getActivity(), BlacklistService.class.getName())) {
            checkBox.setChecked(true);
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    getActivity().startService(new Intent(getActivity(), BlacklistService.class));
                    Toast.makeText(getActivity(), "Blacklist service has been enabled.", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                    b.setTitle("Warning");
                    b.setMessage("The Blacklist service will be disabled, are you sure?");
                    b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().stopService(new Intent(getActivity(), BlacklistService.class));
                            Toast.makeText(getActivity(), "Blacklist service has been disabled.", Toast.LENGTH_LONG).show();
                        }
                    });
                    b.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkBox.setChecked(true);
                        }
                    });
                    b.show();
                }
            }
        });

        return v;
    }

}
