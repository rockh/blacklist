package me.caketalk.blacklist;

import android.content.ContentValues;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import me.caketalk.R;
import me.caketalk.blacklist.dao.BlacklistDao;
import me.caketalk.blacklist.model.Blacklist;

import java.util.HashMap;

/**
 * @author Rock created at 18:03 03/04/13
 */
public class AddFragment extends SherlockFragment {

    private EditText etPhoneNumber;
    private Button btnAdd;
    private Button btnRemove;
    private Spinner spnBlockOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.add, container, false);

        final BlacklistDao dao = new BlacklistDao(getActivity());

        // initializing controls
        etPhoneNumber = (EditText) v.findViewById(R.id.etPhone);
        btnAdd = (Button) v.findViewById(R.id.btnAdd);
        btnRemove = (Button) v.findViewById(R.id.btnRemove);
        spnBlockOptions = (Spinner) v.findViewById(R.id.spnBlockOptions);


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
                        Toast.makeText(getActivity(), String.format("The " +
                                "number %s has been added into blacklist, " +
                                "you will not receive the phone call.",
                                phoneNumber), Toast.LENGTH_LONG).show();

                        // Notify BlacklistFragment that ListView has been changed
                        BlacklistFragment.changed = true;

                        clearInputPhoneNumber();
                    } else {
                        Toast.makeText(getActivity(), "This number has " +
                                "been in the blacklist.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.w(this.getClass().getName(), e);
                    Toast.makeText(getActivity(), e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        // Removes a phone number from blacklist.
        btnRemove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String phoneNumber = getInputPhoneNumber();

                try {
                    boolean exist = dao.isExist(phoneNumber);
                    // if the phone number exists then remove it.
                    if (exist) {
                        dao.remove(phoneNumber);
                        String msgRm = String.format("The number %s has been" +
                                " removed from the blacklist", phoneNumber);
                        Log.d(this.getClass().getName(), msgRm);
                        Toast.makeText(getActivity(), msgRm, Toast.LENGTH_LONG).show();

                        // Notify BlacklistFragment that ListView has been changed
                        BlacklistFragment.changed = true;

                        clearInputPhoneNumber();
                    } else {
                        Toast.makeText(getActivity(), "This number has not" +
                                " in the blacklist yet.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.w(this.getClass().getName(), e);
                    Toast.makeText(getActivity(), e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            final HashMap dataItem = (HashMap) bundle.getSerializable("dataItem");
            final String phoneNumber = dataItem.get("phone").toString();
            etPhoneNumber.setText(phoneNumber);

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
                        Toast.makeText(getActivity(), noChange, Toast.LENGTH_LONG).show();
                        Log.d(AddFragment.class.getName(), noChange);
                    } else {
                        new BlacklistDao(getActivity()).update(v, phoneNumber);
                        String updatedMsg = "Updated an existing number in Blacklist.";
                        Toast.makeText(getActivity(), updatedMsg, Toast.LENGTH_LONG).show();
                        Log.d(AddFragment.class.getName(), updatedMsg);
                        BlacklistFragment.changed = true;
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
            Toast.makeText(getActivity(), "Sorry, you should put '+' at the beginning of phone number!", Toast.LENGTH_LONG).show();
        }
    }

}
