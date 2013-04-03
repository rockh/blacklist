package me.caketalk.blacklist;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Rock created at 07:35 03/04/13
 */
public class AboutActivity extends SherlockActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(SettingsActivity.THEME);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.about);

        TextView txtAbout = (TextView) findViewById(R.id.txtAbout);
        txtAbout.setText(Html.fromHtml(getString(R.string.about_content)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, BlacklistActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}