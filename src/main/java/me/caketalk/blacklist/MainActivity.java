package me.caketalk.blacklist;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import me.caketalk.R;

/**
 * @author Rock created at 16:34 03/04/13
 */
public class MainActivity extends SherlockFragmentActivity {

    private ActionMode actMode;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Sherlock);
        setContentView(R.layout.main);

        // loading Google Admod
        AdView adView = (AdView) findViewById(R.id.adView);
        adView.loadAd(new AdRequest());


        if (findViewById(R.id.fragment_container) != null) {
            // if we are being restored from a previous state, then we dont need to do anything and should
            // return or else we could end up with overlapping fragments.
            if(savedInstanceState != null) {
                return;
            }

            // create an instance of BlacklistFragment
            BlacklistFragment blacklistFragment = new BlacklistFragment();

            // add fragment to the fragment container layout
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, blacklistFragment).commit();
        }
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
            case android.R.id.home:
                finish();
                return true;
            case R.id.add :
                return replaceFragment(new AddFragment(), R.string.lbl_add);
            case R.id.settings:
                return replaceFragment(new SettingsFragment(), R.string.lbl_settings);
            case R.id.About:
                return replaceFragment(new AboutFragment(), R.string.lbl_about);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean replaceFragment(SherlockFragment fragment, int resId) {
        actMode = startActionMode(new MyActionMode(resId));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        String tag = getResources().getString(resId);
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
        return true;
    }

    public ActionMode getActMode() {
        return this.actMode;
    }

    private final class MyActionMode implements ActionMode.Callback {
        private int resourceId;
        public MyActionMode(int resourceId) {
            this.resourceId = resourceId;
        }
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(resourceId);
            mode.setSubtitle(null);
            return true;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return true;
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            onBackPressed(); // trig back button to previous fragment.
        }
    }

}