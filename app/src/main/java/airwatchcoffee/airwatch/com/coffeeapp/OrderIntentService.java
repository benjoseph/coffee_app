package airwatchcoffee.airwatch.com.coffeeapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class OrderIntentService extends IntentService {


    private static final String ACTION_ORDER = "order.coffee";
    private static final String ACTION_RECHARGE = "recharge.wallet";
    private static final String EXTRA_PARAM_ITEM_NAME = "itemName";
    private static final String EXTRA_PARAM_ITEM_QUANTITY = "itemQuantity";
    private static final String EXTRA_PARAM_RECHARGE_AMOUNT = "rechargeAmount";
    private static final String TAG=OrderIntentService.class.getSimpleName();
    public static final String BROADCAST_ORDER_PLACED="com.airwatch.coffeeapp.ORDER_PLACED";
    public static final String BROADCAST_RECHARGE_DONE="com.airwatch.coffeeapp.RECHARGED";

    public static void startActionOrder(Context context, String item, int quantity) {
        Intent intent = new Intent(context, OrderIntentService.class);
        intent.setAction(ACTION_ORDER);
        intent.putExtra(EXTRA_PARAM_ITEM_NAME, item);
        intent.putExtra(EXTRA_PARAM_ITEM_QUANTITY, quantity);
        context.startService(intent);
    }

    public static void startActionRecharge(Context context, int amt) {
        Intent intent = new Intent(context, OrderIntentService.class);
        intent.setAction(ACTION_RECHARGE);
        intent.putExtra(EXTRA_PARAM_RECHARGE_AMOUNT, amt);
        context.startService(intent);
    }

    public OrderIntentService() {
        super("OrderIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ORDER.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM_ITEM_NAME);
                final int param2 = intent.getIntExtra(EXTRA_PARAM_ITEM_QUANTITY, 0);
                handleActionOrder(param1, param2);
            } else if (ACTION_RECHARGE.equals(action)) {
                final int param3 = intent.getIntExtra(EXTRA_PARAM_RECHARGE_AMOUNT, 0);
                handleActionRecharge(param3);
            }
        }
    }

    private void handleActionOrder(String itemName, int itemQuantity) {

        Log.d(TAG,"Order:"+itemQuantity+" cups of "+itemName);
        try {
            Thread.sleep(2*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"Order Placed Successfully");

        sendBroadcast(new Intent(BROADCAST_ORDER_PLACED));

    }


    private void handleActionRecharge(int param1) {
        int currentBalance;
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor=prefs.edit();
        currentBalance=prefs.getInt(AccountDetails.WALLET_BALANCE,0);
        currentBalance+=param1;
        editor.putInt(AccountDetails.WALLET_BALANCE,currentBalance);
        editor.commit();
        Log.d(TAG, "Recharge done. Added " + param1 + ". New bal= " + currentBalance);
        sendBroadcast(new Intent(BROADCAST_RECHARGE_DONE)); // com.airwatch.intent.action.RECHARGED
    }

}
