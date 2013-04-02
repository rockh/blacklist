package me.caketalk.blacklist;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import me.caketalk.blacklist.dao.BlacklistDao;
import me.caketalk.blacklist.model.Blacklist;

import java.util.HashMap;

/**
 * @author Rock created at 07:47 02/04/13
 */
public class AddActivity extends SherlockActivity {

    private ActionBar actionBar;
    private EditText etPhoneNumber;
    private Button btnAdd;
    private Button btnRemove;
    private Spinner spnBlockOptions;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(SettingsActivity.THEME);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.add);

        final BlacklistDao dao = new BlacklistDao(this);

        // initializing controls
        etPhoneNumber = (EditText) findViewById(R.id.etPhone);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnRemove = (Button) findViewById(R.id.btnRemove);
        spnBlockOptions = (Spinner) findViewById(R.id.spnBlockOptions);


        // block options drop-down list
//        spnBlockOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                Toast.makeText(parent.getContext(), String.format("Id: %s, Position: %s, Text: %s", id, pos, parent.getItemAtPosition(pos).toString()), Toast.LENGTH_LONG).show();
//
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });

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
                checkPlusPostion();
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
                int blockOptId = (int) spnBlockOptions.getSelectedItemId();

                // Prepares blacklist content.
                Blacklist blacklist = new Blacklist();
                blacklist.setPhone(phoneNumber);
                blacklist.setBlockOptId(blockOptId);
                blacklist.setComment("Harassing phone call");

                try {
                    boolean exist = dao.isExist(phoneNumber);
                    // If the number has not in the blacklist then put it in.
                    if (!exist) {
                        dao.add(blacklist);
                        Log.d(this.getClass().getName(), "Added new phone number to Blacklist: " + phoneNumber);
                        Toast.makeText(AddActivity.this, String.format("The " +
                                "number %s has been added into blacklist, " +
                                "you will not receive the phone call.",
                                phoneNumber), Toast.LENGTH_LONG).show();

                        // Notify BlacklistActivity that ListView has been changed
                        BlacklistActivity.changed = true;

                        clearInputPhoneNumber();
                    } else {
                        Toast.makeText(AddActivity.this, "This number has " +
                                "been in the blacklist.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.w(this.getClass().getName(), e);
                    Toast.makeText(AddActivity.this, e.getLocalizedMessage(),
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
                        Toast.makeText(AddActivity.this, msgRm, Toast.LENGTH_LONG).show();

                        // Notify BlacklistActivity that ListView has been changed
                        BlacklistActivity.changed = true;

                        clearInputPhoneNumber();
                    } else {
                        Toast.makeText(AddActivity.this, "This number has not" +
                                " in the blacklist yet.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.w(this.getClass().getName(), e);
                    Toast.makeText(AddActivity.this, e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
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

    @Override
    public void onResume() {
        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null) {
            final HashMap dataItem = (HashMap) bundle.getSerializable("dataItem");
            final String phoneNumber = dataItem.get("phone").toString();
            etPhoneNumber.setText(phoneNumber);

            actionBar.setTitle("Edit number");
            btnAdd.setText("Save");
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues v = new ContentValues();

                    String updatedPhone = etPhoneNumber.getText().toString();
                    if (!updatedPhone.equals(phoneNumber)) {
                        v.put("phone", updatedPhone);
                    }

                    int oldBlockOptId = (Integer) dataItem.get("block_opt_id");
                    int newBlockOptId = (int) spnBlockOptions.getSelectedItemId();
                    if (oldBlockOptId != newBlockOptId) {
                        v.put("block_opt_id", newBlockOptId);
                    }

                    if (v.size() == 0) {
                        String noChange = "Nothing changed!";
                        Toast.makeText(AddActivity.this, noChange, Toast.LENGTH_LONG).show();
                        Log.d(AddActivity.class.getName(), noChange);
                    } else {
                        new BlacklistDao(AddActivity.this).update(v, phoneNumber);
                        String updatedMsg = "Updated an existing number in Blacklist.";
                        Toast.makeText(AddActivity.this, updatedMsg, Toast.LENGTH_LONG).show();
                        Log.d(AddActivity.class.getName(), updatedMsg);
                        BlacklistActivity.changed = true;
                    }
                }
            });

            // Sets drop-down list according to selected item value
            // NOTE: saved id is 'spnBlockOptions.getSelectedItemId()',
            // but here the param of 'setSelection(int)' is position id.
            // They should keep consistent and need to consider this further.
            int blockOptId = (Integer) dataItem.get("block_opt_id");
            spnBlockOptions.setSelection(blockOptId);
        }

        super.onResume();
    }


    /********************** private methods **********************/

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

    private void checkPlusPostion() {
        String pn = getInputPhoneNumber();
        if (pn != null && pn.length() > 0 && !pn.equals("+") && pn.endsWith("+")) {
            etPhoneNumber.setText(pn.substring(0, pn.length()-1));
            Toast.makeText(AddActivity.this, "Sorry, you should put '+' at the beginning of phone number!", Toast.LENGTH_LONG).show();
        }
    }
}