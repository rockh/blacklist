package me.caketalk.blacklist;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockListFragment;
import me.caketalk.R;
import me.caketalk.blacklist.dao.BlacklistDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rock created at 16:06 03/04/13
 */
public class BlacklistFragment extends SherlockListFragment {

    // A refresh may be needed if the Blacklist ListView is changed by other Activity.
    public static boolean changed;

    private BlacklistDao dao;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> blockedList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.blacklist, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Creates a data access object
        dao = new BlacklistDao(getActivity());

        // Fills data to ListView
        populateListView();

        // Popups context menu on clicking ListView item.
        getListView().setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                String blockedNumber = ((TextView) info.targetView.findViewById(R.id.blockedNumber)).getText().toString();
                menu.setHeaderTitle(blockedNumber);
                menu.add(0, 0, 0, "Edit Phone Number");
                menu.add(0, 1, 1, "Remove Phone Number");
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int index = (int) info.id;
        HashMap dataItem = (HashMap) adapter.getItem(index);
        String id = dataItem.get("_id").toString();

        switch (item.getItemId()) {
            case 0:  // Edit
                Log.d(BlacklistFragment.class.getName(), String.format("Clicking Edit, Index => %s, _id => %s", index, id));
                AddFragment editFragment = new AddFragment();
                Bundle args = new Bundle();
                args.putSerializable("dataItem", dataItem);
                editFragment.setArguments(args);
                ((MainActivity) getActivity()).replaceFragment(editFragment, R.string.lbl_edit);
                break;
            case 1:  // Remove
                int affectedRow = dao.remove(Integer.parseInt(id));
                if (affectedRow > 0) {
                    blockedList.remove(index);
                    Toast.makeText(getActivity(), "The phone number has removed. ", Toast.LENGTH_SHORT).show();
                    Log.d(BlacklistFragment.class.getName(), String.format("Clicking Remove Phone Number, Index => %s, _id => %s", index, id));
                } else {
                    Toast.makeText(getActivity(), "Nothing has changed.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        adapter.notifyDataSetChanged();
        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        if (changed) {
            populateListView();
            changed = false;
        }
        super.onResume();
    }

    /************************* Private Method ***************************/

    private void populateListView() {
        blockedList = dao.getAllBlacklist();
        String[] from = new String[] { "phone" };
        int[] to = new int[] { R.id.blockedNumber };
        adapter = new SimpleAdapter(getActivity(), blockedList, R.layout.blacklist_item, from, to);
        this.setListAdapter(adapter);
    }
}
