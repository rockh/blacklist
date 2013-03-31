package me.caketalk.blacklist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import me.caketalk.blacklist.dao.BlacklistDao;
import me.caketalk.blacklist.model.Blacklist;
import me.caketalk.blacklist.service.BlacklistService;
import me.caketalk.blacklist.util.ServiceUtil;

/**
 * @author Rock Created at 03:32 26/02/13
 * @version 0.2 31/03/13
 */
public class SettingsActivity extends SherlockActivity {

    public final static int THEME = R.style.Theme_Sherlock;

    private EditText etPhoneNumber;
    private Button btnAdd;
    private Button btnRemove;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(THEME);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.setting);

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

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    startService(new Intent(SettingsActivity.this, BlacklistService.class));
                    Toast.makeText(SettingsActivity.this, "Blacklist service has been enabled.", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder b = new AlertDialog.Builder(SettingsActivity.this);
                    b.setTitle("Warning");
                    b.setMessage("The Blacklist service will be disabled, are you sure?");
                    b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            stopService(new Intent(SettingsActivity.this, BlacklistService.class));
                            Toast.makeText(SettingsActivity.this, "Blacklist service has been disabled.", Toast.LENGTH_LONG).show();
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
        btnAdd.setOnClickListener(new View.OnClickListener() {
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
                        Log.d(this.getClass().getName(), "Blocked phone number: " + phoneNumber);
                        Toast.makeText(SettingsActivity.this, String.format("The " +
                                "number %s has been added into blacklist, " +
                                "you will not receive the phone call.",
                                phoneNumber), Toast.LENGTH_LONG).show();

                        // Notify BlacklistActivity that ListView has been changed
                        BlacklistActivity.changed = true;

                        clearInputPhoneNumber();
                    } else {
                        Toast.makeText(SettingsActivity.this, "This number has " +
                                "been in the blacklist.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.w(this.getClass().getName(), e);
                    Toast.makeText(SettingsActivity.this, e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        // Removes a phone number from blacklist.
        btnRemove.setOnClickListener(new View.OnClickListener() {
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
                        Toast.makeText(SettingsActivity.this, msgRm, Toast.LENGTH_LONG).show();

                        // Notify BlacklistActivity that ListView has been changed
                        BlacklistActivity.changed = true;

                        clearInputPhoneNumber();
                    } else {
                        Toast.makeText(SettingsActivity.this, "This number has not" +
                                " in the blacklist yet.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.w(this.getClass().getName(), e);
                    Toast.makeText(SettingsActivity.this, e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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
