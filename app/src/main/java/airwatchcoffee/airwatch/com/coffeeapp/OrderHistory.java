package airwatchcoffee.airwatch.com.coffeeapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by josephben on 8/26/2015.
 */
public class OrderHistory extends AppCompatActivity {

    Cursor cursor;
    private OrderHistoryDbUtility dbHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderhistory);
        db=dbHelper.getReadableDatabase();
        cursor =dbHelper.readData(db);

    }


    public void printData(){
        cursor.moveToFirst();
        long orderId = cursor.getLong(
                cursor.getColumnIndexOrThrow(OrderHistoryDbUtility.OrderEntry._ID));
        String itemName=cursor.getString(cursor.getColumnIndexOrThrow(OrderHistoryDbUtility.OrderEntry.TABLE_NAME));
    }


}
