package me.caketalk.blacklist;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import me.caketalk.R;

/**
 * @author Rock created at 12:40 04/04/13
 */
public class AboutFragment extends SherlockFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.about, container, false);

        TextView txtAbout = (TextView) v.findViewById(R.id.txtAbout);
        txtAbout.setText(Html.fromHtml(getString(R.string.about_content)));

        return v;
    }
}
