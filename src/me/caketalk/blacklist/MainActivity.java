package me.caketalk.blacklist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import me.caketalk.R;
import me.caketalk.blacklist.dao.BlacklistDao;
import me.caketalk.blacklist.model.Blacklist;
import me.caketalk.blacklist.service.BlacklistService;
import me.caketalk.blacklist.util.ServiceUtil;

/**
 * @author Rock Huang
 * @version 0.2
 */
public class MainActivity extends Activity {

    private EditText etPhoneNumber;
    private Button btnAdd;
    private Button btnRemove;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Tab navigation bar

        TabHost tabs = (TabHost) findViewById(R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec1 = tabs.newTabSpec("tab1");
        spec1.setContent(R.id.tabBlacklist);
        spec1.setIndicator("Blacklist");
        tabs.addTab(spec1);

        TabHost.TabSpec spec2 = tabs.newTabSpec("tab2");
        spec2.setContent(R.id.tabSetting);
        spec2.setIndicator("Setting");
        tabs.addTab(spec2);



        final BlacklistDao dao = new BlacklistDao(this);

        etPhoneNumber = (EditText) findViewById(R.id.etPhone);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnRemove = (Button) findViewById(R.id.btnRemove);

        setButtonStatus(!getInputPhoneNumber().equals(""));

        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setButtonStatus(!getInputPhoneNumber().equals(""));
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        // setting radio button status if Blacklist service is running
        final CheckBox checkBox = (CheckBox) findViewById(R.id.chkEnableBlacklist);
        if (ServiceUtil.isServiceRunning(this, BlacklistService.class.getName())) {
            checkBox.setChecked(true);
        }

        checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    startService(new Intent(MainActivity.this, BlacklistService.class));
                    Toast.makeText(MainActivity.this, "Blacklist service has been enabled.", Toast.LENGTH_LONG).show();

                    // selects blacklist from DB & caches blacklist when starting the blacklist service
                    //CallReceiver.cachedBlacklist = dao.getAllBlacklist();

                } else {
                    AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                    b.setTitle("Warning");
                    b.setMessage("The Blacklist service will be disabled, are you sure?");
                    b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            stopService(new Intent(MainActivity.this, BlacklistService.class));
                            Toast.makeText(MainActivity.this, "Blacklist service has been disabled.", Toast.LENGTH_LONG).show();
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


        // Adds a phone number into black list
        btnAdd.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
//                //setting call forwarding
//                Message message = mHandler.obtainMessage();
//                message.what = OP_REGISTER;
//                mHandler.dispatchMessage(message);

                String phoneNumber = getInputPhoneNumber();

                // Prepares blacklist content.
                Blacklist blacklist = new Blacklist();
                blacklist.setPhone(phoneNumber);
                blacklist.setComment("Harassing phone call");

                try {
                    boolean exist = dao.isExist(phoneNumber);
                    // If the number has not in the blacklist then put it in.
                    if (!exist) {
                        dao.add(blacklist);
                        Log.d("MainActivity", "Blocked phone number: " + phoneNumber);
                        Toast.makeText(MainActivity.this, String.format("The " +
                                "number %s has been added into blacklist, " +
                                "you will not receive the phone call.",
                                phoneNumber), Toast.LENGTH_LONG).show();

                        // Refreshes cached blacklist
                        //CallReceiver.cachedBlacklist.clear();
                        //CallReceiver.cachedBlacklist = dao.getAllBlacklist();

                        clearInputPhoneNumber();
                    } else {
                        Toast.makeText(MainActivity.this, "This number has " +
                                "been in the blacklist.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.w(this.getClass().getName(), e);
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        // Removes a phone number from blacklist.
        btnRemove.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
//                //cancel call forwarding
//                Message message = mHandler.obtainMessage();
//                message.what = OP_CANCEL;
//                mHandler.dispatchMessage(message);

                String phoneNumber = getInputPhoneNumber();

                try {
                    boolean exist = dao.isExist(phoneNumber);
                    // if the phone number exists then remove it.
                    if (exist) {
                        dao.remove(phoneNumber);
                        String msgRm = String.format("The number %s has been" +
                                " removed from the blacklist", phoneNumber);
                        Log.d(this.getClass().getName(), msgRm);
                        Toast.makeText(MainActivity.this, msgRm, Toast.LENGTH_LONG).show();

                        // Refreshes cached blacklist
                        //CallReceiver.cachedBlacklist.clear();
                        //CallReceiver.cachedBlacklist = dao.getAllBlacklist();

                        clearInputPhoneNumber();
                    } else {
                        Toast.makeText(MainActivity.this, "This number has not" +
                                " in the blacklist yet.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.w(this.getClass().getName(), e);
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        // Look up the AdView as a resource and load a request.
        AdView adView = (AdView)this.findViewById(R.id.adview);
        adView.loadAd(new AdRequest());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //respond to menu item selection
        return false;
    }

    private void setButtonStatus(boolean enabled) {
        btnAdd.setEnabled(enabled);
        btnRemove.setEnabled(enabled);
    }

    private void clearInputPhoneNumber() {
        etPhoneNumber.getEditableText().clear();
        etPhoneNumber.clearFocus();
    }

    private String getInputPhoneNumber() {
        return etPhoneNumber.getText().toString();
    }


}