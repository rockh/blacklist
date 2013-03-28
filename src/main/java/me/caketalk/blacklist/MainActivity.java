package me.caketalk.blacklist;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.googlecode.androidannotations.annotations.*;

/**
 * @author Rock Huang
 * @version 0.2
 */
@NoTitle
@Fullscreen
@EActivity(R.layout.main)
public class MainActivity extends TabActivity {

    @ViewById
    AdView adView;

    @AfterViews
    void initTab() {
        TabHost tabHost = getTabHost();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("tab1");
        spec1.setIndicator("Setting");
        spec1.setContent(new Intent(this, SettingActivity.class));
        tabHost.addTab(spec1);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("tab2");
        spec2.setIndicator("Blacklist");
        spec2.setContent(new Intent(this, BlacklistActivity.class));
        tabHost.addTab(spec2);
    }

    @AfterViews
    void requestAd() {
        adView.loadAd(new AdRequest());
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}