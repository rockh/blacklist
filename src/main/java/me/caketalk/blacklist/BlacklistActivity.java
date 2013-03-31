package me.caketalk.blacklist;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View.OnCreateContextMenuListener;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import me.caketalk.blacklist.dao.BlacklistDao;
import java.util.List;
import java.util.Map;

/**
 * @author Rock created at 02:05 26/02/13
 */
public class BlacklistActivity extends SherlockListActivity {

    // A refresh may be needed if the Blacklist ListView is changed by other Activity.
    public static boolean changed;

    private String TAG = BlacklistActivity.class.getName();
    private BlacklistDao dao;
    private SimpleAdapter adapter;
    private List<Map<String, String>> blockedList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(SettingsActivity.THEME);

        setContentView(R.layout.blacklist);

        // Creates a data access object
        dao = new BlacklistDao(this);

        // Fills data to ListView
        populateListView();

        // Popups context menu on clicking ListView item.
        this.getListView().setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                String blockedNumber = ((TextView) info.targetView.findViewById(R.id.blockedNumber)).getText().toString();
                menu.setHeaderTitle(blockedNumber);
//                menu.add(0, 0, 0, "Edit Phone Number");
                menu.add(0, 1, 0, "Remove Phone Number");
            }
        });


        // loading Google Admod
        //adView.loadAd(new AdRequest());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {

        switch(item.getItemId()) {
            case R.id.abs__home:
                finish();
                return true;
            case R.id.settings:
                Intent intent = new Intent(BlacklistActivity.this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.About:
                Log.d(this.getLocalClassName(), "Clicked 'About' menu item.");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int index = (int) info.id; // info.id corresponding with _id value in database
        String _id = ((Map) adapter.getItem(index)).get("_id").toString();


        switch (item.getItemId()) {
            case 0:  // Edit
                Log.d(TAG, String.format("Clicking Edit, Index => %s, _id => %s", index, _id));
                break;
            case 1:  // Remove
                int affectedRow = dao.remove(Integer.parseInt(_id));
                if (affectedRow > 0) {
                    blockedList.remove(index);
                    Toast.makeText(BlacklistActivity.this, "The phone number has removed. ", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, String.format("Clicking Remove Phone Number, Index => %s, _id => %s", index, _id));
                } else {
                    Toast.makeText(BlacklistActivity.this, "Nothing has changed.", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void populateListView() {
        blockedList = dao.getAllBlacklist();
        String[] from = new String[] { "phone" };
        int[] to = new int[] { R.id.blockedNumber };
        adapter = new SimpleAdapter(this, blockedList, R.layout.blacklist_item, from, to);
        this.setListAdapter(adapter);
    }

}
