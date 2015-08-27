package airwatchcoffee.airwatch.com.coffeeapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


// TODO: Don't do a complete UI change. Create a new Activity.
public class MainActivity extends AppCompatActivity {

    private static final String NO_OF_ITEMS = "no_of_items";
    private static final String ITEM_NAME = "item_name";
    private static final String ITEM_SPINNER = "item_spinner";
    private static final String CREAM_SELECTED = "cream_selected";
    private static final String CHOCOLATE_SELECTED = "chocolate_selected";
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mCustomerNameField;
    private static final String ORDER_VISIBILITY ="order_visibilty";
    private static final String SUMMARY_VISIBILITY ="summary_visibilty";
    private static final String THANKS_VISIBILITY ="thanks_visibilty";
    private static final String TOTAL_PRICE ="total_price";
    private TextView mNoOfItemsTextView;
    private TextView mSummaryText;
    private int mNoOfItem = 2;
    private Spinner mMenuItemsSpinner;
    private String mItemName;
    private ProgressDialog mProgDiag;
    private ViewGroup mSummary;
    private ViewGroup mOrder;
    private ViewGroup mThanks;
    private CheckBox mCream;
    private CheckBox mChocolate;
    private OrderHistoryDbUtility mDbHelper;
    private int mTotalPrice;
// TODO: Read API docs. Pass an extra from LoginActivity and log it here.

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button OrderNowButton;
        Button PlusButton;
        Button MinusButton;
        Button AddToCart;
        Button EditOrder;
        Button NewOrder;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDbHelper = new OrderHistoryDbUtility(this);
        mCustomerNameField = (TextView) findViewById(R.id.customer_name);
        OrderNowButton = (Button) findViewById(R.id.order_now_button);
        PlusButton = (Button) findViewById((R.id.plus_button));
        MinusButton = (Button) findViewById(R.id.minus_button);
        mNoOfItemsTextView = (TextView) findViewById(R.id.no_of_item_input);
        mSummaryText = (TextView) findViewById(R.id.summary);
        mMenuItemsSpinner = (Spinner) findViewById(R.id.menuitem_spinner);
        mSummary = (ViewGroup) findViewById(R.id.summary_view_group);
        AddToCart = (Button) findViewById(R.id.add_to_cart);
        mOrder = (ViewGroup) findViewById(R.id.order_menu);
        mThanks = (ViewGroup) findViewById(R.id.thank_you_view_group);
        EditOrder = (Button) findViewById(R.id.edit_order_button);
        NewOrder = (Button) findViewById(R.id.new_order_button);
        mCream = (CheckBox) findViewById(R.id.check_box_1);
        mChocolate = (CheckBox) findViewById(R.id.check_box_2);

        //setting up the initial view
        mCustomerNameField.setText(String.format(getResources().getString(R.string.salutation), getUserName()));
        mSummary.setVisibility(View.GONE);
        mThanks.setVisibility(View.GONE);
        updateItemsCounter();

        //for retaining data on rotation

        // TODO: Don't use spinners.
        if (savedInstanceState != null) {
            mNoOfItem = savedInstanceState.getInt(NO_OF_ITEMS);
            mMenuItemsSpinner.setSelection(savedInstanceState.getInt(ITEM_SPINNER));
            int n=savedInstanceState.getInt(ORDER_VISIBILITY,View.VISIBLE);
            mTotalPrice=savedInstanceState.getInt(TOTAL_PRICE);
            mItemName=savedInstanceState.getString(ITEM_NAME, "failed");
            if(n==View.VISIBLE) {
                mOrder.setVisibility(View.VISIBLE);
            }else{
                mOrder.setVisibility(View.GONE);
            }
            n=savedInstanceState.getInt(SUMMARY_VISIBILITY,View.GONE);
            if(n==View.VISIBLE) {
                mSummary.setVisibility(View.VISIBLE);
            }else{
                mSummary.setVisibility(View.GONE);
            }
            n=savedInstanceState.getInt(THANKS_VISIBILITY,View.GONE);
            if(n==View.VISIBLE) {
                mThanks.setVisibility(View.VISIBLE);
            }else{
                mThanks.setVisibility(View.GONE);
            }
            mCream.setChecked(savedInstanceState.getBoolean(CREAM_SELECTED));
            mChocolate.setChecked(savedInstanceState.getBoolean(CHOCOLATE_SELECTED));
            updateItemsCounter();
            setSummary();
        }

        //spinner stuff
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.menuitem_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMenuItemsSpinner.setAdapter(adapter);
        mMenuItemsSpinner.setOnItemSelectedListener(new SpinnerItems());

        mItemName=mMenuItemsSpinner.getItemAtPosition(0).toString();
        //Listeners setup

        PlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementQuantity();
            }

        });

        MinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity();
            }

        });

        AddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTotalPrice=getTotalPrice();
                setSummary();
                mSummary.setVisibility(View.VISIBLE);
                mOrder.setVisibility(View.GONE);
            }

        });

        EditOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSummary.setVisibility(View.GONE);
                mOrder.setVisibility(View.VISIBLE);
            }

        });

        OrderNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                orderNowInBackground(mItemName, mNoOfItem);

            }
        });

        NewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSummary.setVisibility(View.GONE);
                mThanks.setVisibility(View.GONE);
                resetOrderForm();
                mOrder.setVisibility(View.VISIBLE);
            }

        });


    } //end of oncreate

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(NO_OF_ITEMS, mNoOfItem);
        savedInstanceState.putString(ITEM_NAME, mItemName);
        Log.v(TAG,"item name is"+mItemName);
        savedInstanceState.putInt(TOTAL_PRICE,mTotalPrice);
        savedInstanceState.putInt(ITEM_SPINNER, mMenuItemsSpinner.getSelectedItemPosition());
        savedInstanceState.putBoolean(CHOCOLATE_SELECTED, mChocolate.isChecked());
        savedInstanceState.putBoolean(CREAM_SELECTED, mCream.isChecked());
        savedInstanceState.putInt(ORDER_VISIBILITY, mOrder.getVisibility());
        savedInstanceState.putInt(SUMMARY_VISIBILITY, mSummary.getVisibility());
        savedInstanceState.putInt(THANKS_VISIBILITY, mThanks.getVisibility());
        // TODO: Do you need to save the value user has typed in EditText?
        super.onSaveInstanceState(savedInstanceState);
    }

    private void resetOrderForm(){
        mMenuItemsSpinner.setSelection(0);
        mNoOfItem=2;
        mChocolate.setChecked(false);
        mCream.setChecked(false);
        updateItemsCounter();
    }

    private void incrementQuantity() {
            mNoOfItem++;
            updateItemsCounter();
    }

    private void decreaseQuantity() {
        if (mNoOfItem == 2) {
            Toast.makeText(MainActivity.this, R.string.minimum_coffee, Toast.LENGTH_LONG).show();
        } else {
            mNoOfItem--;
            updateItemsCounter();
        }
    }

    private void updateItemsCounter() {
        mNoOfItemsTextView.setText(String.valueOf(mNoOfItem));
    }

    private int getTotalPrice() {
        int itemPrice = 0;
        int cream = 5;
        int chocolate = 10;
        switch (mItemName) {
            case "Coffee":
                itemPrice = 10;
                break;
            case "Cappuccino":
                itemPrice = 15;
                break;
            case "Latte":
                itemPrice = 20;
                break;
            case "Espresso":
                itemPrice = 7;
                break;
            default:
                itemPrice = 0;
        }
        if (mChocolate.isChecked() && mCream.isChecked()) {
            return ((chocolate + cream + itemPrice) * mNoOfItem);
        } else if (mChocolate.isChecked()) {
            return ((chocolate + itemPrice) * mNoOfItem);
        } else if (mCream.isChecked()) {
            return ((cream + itemPrice) * mNoOfItem);
        } else {
            return itemPrice * mNoOfItem;
        }

    }
//TODO: use string format here properly
    private void setSummary() {
        String summary = String.format(getResources().getString(R.string.summary), mNoOfItem, mItemName);
        if (mChocolate.isChecked() && mCream.isChecked()) {
            summary += " with whipped cream and chocolate";
        } else if (mChocolate.isChecked()) {
            summary += " with chocolate";
        } else if (mCream.isChecked()) {
            summary += " with whipped cream";
        }
        summary += "\n Total price is Rs. ";
        summary += String.valueOf(mTotalPrice);
        mSummaryText.setText(summary);
    }

    private void dismissOrderProgress() {
        mProgDiag.dismiss();
    }

    private void registerOrderReceiver() {
        IntentFilter orderFilter = new IntentFilter();
        orderFilter.addAction(OrderIntentService.BROADCAST_ORDER_PLACED);
        registerReceiver(receiver, orderFilter);
    }

    private void unregisterOrderReceiver() {
        unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(OrderIntentService.BROADCAST_ORDER_PLACED)) {
                Log.d(TAG,"Progressbar:Dismissed as action completed");
                dismissOrderProgress();
                Toast.makeText(MainActivity.this, R.string.order_success, Toast.LENGTH_SHORT).show();
                mSummary.setVisibility(View.GONE);
                mThanks.setVisibility(View.VISIBLE);
                unregisterOrderReceiver();
            }
        }
    };
// TODO: 8/26/2015 cant replace string
    private void showProgressDialog() {
        mProgDiag = ProgressDialog.show(MainActivity.this, "Please wait ...", "Order is being placed ...", true);
        mProgDiag.setCancelable(true);


    }

    private void orderNowInBackground(String item, int no_of_items) {
        registerOrderReceiver();
        Context context = MainActivity.this;
        OrderIntentService.startActionOrder(context, item, no_of_items);
        writeOrderToDb();

    }

    private String getUserName() {
        String userName = null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        userName = prefs.getString(LoginActivity.USERNAME, "");
        return userName;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    // User switch cases.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_order_history:Intent orderHistoryIntent = new Intent(this, OrderHistory.class);
//                startActivity(orderHistoryIntent);
//                break;
                return true;

            case R.id.action_account_details:Intent accountDetailsIntent = new Intent(this, AccountDetails.class);
                startActivity(accountDetailsIntent);
                break;
            default:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    // Spinner class for implementing items spinner
    private class SpinnerItems implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mItemName = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            mItemName = parent.getItemAtPosition(0).toString();

        }
    }


    private void writeOrderToDb() {
        int cream = 0;
        int chocolate = 0;
        if (mCream.isChecked()){
            cream = 1;
        }
        if (mChocolate.isChecked()){
            chocolate = 1;
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        mDbHelper.insertData(db, mItemName, mNoOfItem, cream, chocolate);

    }
}
