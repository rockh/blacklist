package me.caketalk.blacklist;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.Toast;
import me.caketalk.R;
import me.caketalk.blacklist.dao.BlacklistDao;
import me.caketalk.blacklist.service.BlacklistService;
import me.caketalk.blacklist.util.ServiceUtil;

import java.util.List;

/**
 * @author Rock Huang
 * @version 0.1
 */
public class MainActivity extends Activity {

    private final static int OP_REGISTER = 100;
    private final static int OP_CANCEL = 200;

    //占线时转移，这里13800000000是空号，所以会提示所拨的号码为空号
    private final static String ENABLE_SERVICE = "tel:**67#13800000000#";

    //占线时转移
    private final static String DISABLE_SERVICE = "tel:##67#";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        CheckBox checkBox = (CheckBox) findViewById(R.id.chkEnableBlacklist);

        // setting radio button status if Blacklist service is running
        if (ServiceUtil.isServiceRunning(this, BlacklistService.class.getName())) {
            checkBox.setChecked(true);
        }

        checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    startService(new Intent(MainActivity.this, BlacklistService.class));
                    Toast.makeText(MainActivity.this, "Blacklist service has been enabled.", Toast.LENGTH_LONG).show();

                    // selects blacklist from DB & caches blacklist
                    BlacklistDao dao = new BlacklistDao(MainActivity.this);
                    CallReceiver.cachedBlacklist = dao.getAllBlacklist();

                } else {
                    stopService(new Intent(MainActivity.this, BlacklistService.class));
                    Toast.makeText(MainActivity.this, "Blacklist service has been disabled.", Toast.LENGTH_LONG).show();
                }
            }
        });


        findViewById(R.id.enableCallTransfer).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
//                //setting call forwarding
//                Message message = mHandler.obtainMessage();
//                message.what = OP_REGISTER;
//                mHandler.dispatchMessage(message);
                // todo: to persist a phone number to db and refresh blacklist cache (CallReceiver will read cached blacklist)

                Log.d("MainActivity", "Blocked phone number: " + CallReceiver.cachedBlacklist);
            }
        });

        findViewById(R.id.disableCallTransfer).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
//                //cancel call forwarding
//                Message message = mHandler.obtainMessage();
//                message.what = OP_CANCEL;
//                mHandler.dispatchMessage(message);
                // todo: to remove a phone number from dao and refresh blacklist cache
                CallReceiver.cachedBlacklist = null;
                Log.d("MainActivity", "Unblocked phone number: " + CallReceiver.cachedBlacklist);
            }
        });
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message response) {
            int what = response.what;
            switch (what) {
                case OP_REGISTER: {
                    Intent i = new Intent(Intent.ACTION_CALL);
                    i.setData(Uri.parse(ENABLE_SERVICE));
                    startActivity(i);
                    break;
                }
                case OP_CANCEL: {
                    Intent i = new Intent(Intent.ACTION_CALL);
                    i.setData(Uri.parse(DISABLE_SERVICE));
                    startActivity(i);
                    break;
                }
            }
        }
    };

}