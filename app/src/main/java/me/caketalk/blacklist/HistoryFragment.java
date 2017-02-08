package me.caketalk.blacklist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import me.caketalk.MainActivity;
import me.caketalk.R;
import me.caketalk.blacklist.manager.BlacklistManager;
import me.caketalk.blacklist.manager.IBlacklistManager;
import me.caketalk.blacklist.model.History;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Rock Created at 21:56 09/05/13
 */
public class HistoryFragment extends Fragment {

    private IBlacklistManager manager;
    private ListView listView;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> historyData;
    private String checkedPhoneNumber; // The number that is selected from BlacklistFragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_list, container, false);
        listView = (ListView) view.findViewById(R.id.listHistory);
        listView.setOnItemClickListener(new HistoryItemClickListener());
        listView.setOnItemLongClickListener(new HistoryItemLongClickListner());
        return view;
    }

    @Override
    public void onResume() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }

        populateHistoryRecordsList(bundle);

        // Gives prompt if history data is empty
        if (historyData.size() == 0) {
            Toast.makeText(getActivity(), "No history for this phone number", Toast.LENGTH_LONG).show();
        }

        super.onResume();
    }

    private void populateHistoryRecordsList(Bundle bundle) {
        manager = new BlacklistManager(this.getActivity());

        HashMap phoneItem = (HashMap) bundle.getSerializable("phoneItem");
        checkedPhoneNumber = phoneItem.get("phone").toString();

        historyData = manager.getHistoryRecords(checkedPhoneNumber);
        setHistoryActionMode();
        String[] from = new String[] {History.F_ACTION, History.F_CREATED_DATE, History.F_DETAIL};
        int[] to = new int[] {R.id.phoneAction, R.id.actionDateTime, R.id.smsText};
        adapter = new SimpleAdapter(getActivity(), historyData, R.layout.history_list_item, from , to);

        listView.setAdapter(adapter);
    }

    private void refreshList() {
        historyData.clear();
        historyData.addAll(manager.getHistoryRecords(checkedPhoneNumber));
        adapter.notifyDataSetChanged();
    }

    private void setHistoryActionMode() {
        ActionMode actionMode = ((MainActivity)getActivity()).getActMode();
        actionMode.setSubtitle(checkedPhoneNumber);

        if (historyData.size() == 0) {
        // Don't need to add menu item if no history records
            return;
        }

        // Adds actions on history screen actionbar
        Menu menu = actionMode.getMenu();
        menu.clear(); // to prevent repeating
        actionMode.getMenuInflater().inflate(R.menu.history_clear, menu);
        menu.findItem(R.id.clear_call_history).setOnMenuItemClickListener(new OnClearCallHistory());
        menu.findItem(R.id.clear_sms_history).setOnMenuItemClickListener(new OnClearSmsHistory());
        menu.findItem(R.id.clear_all_history).setOnMenuItemClickListener(new OnClearBothHistory());
    }

    private class OnClearCallHistory implements MenuItem.OnMenuItemClickListener {
        public boolean onMenuItemClick(MenuItem item) {
            int count = manager.removeCallHistory(checkedPhoneNumber);
            refreshList();
            Toast.makeText(getActivity(), "Cleared " + count + " call history", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private class OnClearSmsHistory implements MenuItem.OnMenuItemClickListener {
        public boolean onMenuItemClick(MenuItem item) {
            int count = manager.removeSmsHistory(checkedPhoneNumber);
            refreshList();
            Toast.makeText(getActivity(), "Cleared " + count + " SMS history", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private class OnClearBothHistory implements MenuItem.OnMenuItemClickListener {
        public boolean onMenuItemClick(MenuItem item) {
            manager.removeAllHistory(checkedPhoneNumber);
            refreshList();
            Toast.makeText(getActivity(), "Cleared all history", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    // On clicking a history record with SMS detail
    private class HistoryItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Map listItem = (Map) adapterView.getItemAtPosition(i);
            String smsText = listItem.get(History.F_DETAIL).toString();
            if (!smsText.equals("")) {
                new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.sms_dialog_title)
                    .setMessage(smsText)
                    .setPositiveButton(R.string.sms_dialog_button, null)
                    .show();
            }
        }
    }

    // On long clicking a history record
    private class HistoryItemLongClickListner implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, final View view, final int position, long l) {
            final Map listItem = (Map) adapterView.getItemAtPosition(position);
            new AlertDialog.Builder(getActivity())
                    .setTitle("Warning")
                    .setMessage("Are you sure to remove this record?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int id = (Integer) listItem.get("_id");
                            if (manager.removeRecord(id)) {
                                historyData.remove(position);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "Removed selected record.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            }).show();
            return true;
        }
    }

}
