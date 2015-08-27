package airwatchcoffee.airwatch.com.coffeeapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by josephben on 8/18/2015.
 */
public class AccountDetails extends AppCompatActivity {
    private TextView mUsername;
    private TextView mWalletBalance;
    private EditText mRechargeValue;
    private Button mRechargeButton;
    private static final String TAG=AccountDetails.class.getSimpleName();
    public static final String WALLET_BALANCE="wallet_balance";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        mUsername = (TextView) findViewById(R.id.username);
        mWalletBalance = (TextView) findViewById(R.id.wallet_balance);
        mRechargeButton = (Button) findViewById(R.id.recharge_button);
        mRechargeValue = (EditText) findViewById(R.id.recharge_amt_et);

        setData();
        registerOrderReceiver();

        mRechargeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int recharge_amt;
                try {
                    recharge_amt = Integer.parseInt(mRechargeValue.getText().toString());
                    if (recharge_amt <= 0) {
                        Toast.makeText(AccountDetails.this, R.string.enter_valid_amt, Toast.LENGTH_SHORT).show();
                    } else {
                        Context context = AccountDetails.this;
                        OrderIntentService.startActionRecharge(context, recharge_amt);
                        mRechargeValue.getText().clear();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(AccountDetails.this,R.string.enter_number, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerOrderReceiver();
    }

    // TODO: Read about hoe u tag
    private void setData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String username = prefs.getString(LoginActivity.USERNAME, "");
        Log.d(TAG, username);
        mUsername.setText(username);
        int balance = prefs.getInt(WALLET_BALANCE, 0);
        String wallet_balance = String.valueOf(balance);
        Log.d(TAG,"Balance"+ wallet_balance);
        mWalletBalance.setText(wallet_balance);
    }

    private void registerOrderReceiver() {
        IntentFilter orderFilter = new IntentFilter();
        orderFilter.addAction(OrderIntentService.BROADCAST_RECHARGE_DONE);
        registerReceiver(receiver, orderFilter);
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(OrderIntentService.BROADCAST_RECHARGE_DONE)) {
                Toast.makeText(AccountDetails.this, R.string.recharge_success, Toast.LENGTH_SHORT).show();
                setData();
            }
        }
    };

    // TODO: Read up on activity life cycles and why we need to do stuffs in pairs
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }
}
