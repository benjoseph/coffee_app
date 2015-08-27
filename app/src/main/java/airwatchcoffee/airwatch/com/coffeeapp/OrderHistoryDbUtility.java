package airwatchcoffee.airwatch.com.coffeeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by josephben on 8/26/2015.
 */
public class OrderHistoryDbUtility extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "OrderHistory.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    public abstract class OrderEntry implements BaseColumns {
        public static final String TABLE_NAME = "orders";
        public static final String COLUMN_NAME_ORDER_ID = "order_id";
        public static final String COLUMN_NAME_ITEM = "item_name";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_CREAM = "cream";
        public static final String COLUMN_NAME_CHOCOLATE = "chocolate";
        public static final String COLUMN_NULLABLE = "";
    }
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " +OrderEntry.TABLE_NAME + " (" +
                    OrderEntry._ID + " INTEGER PRIMARY KEY," +
                    OrderEntry.COLUMN_NAME_ITEM + TEXT_TYPE + COMMA_SEP +
                    OrderEntry.COLUMN_NAME_QUANTITY + TEXT_TYPE + COMMA_SEP +
                    OrderEntry.COLUMN_NAME_CREAM + INT_TYPE + COMMA_SEP +
                    OrderEntry.COLUMN_NAME_CHOCOLATE + INT_TYPE +
                    " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + OrderEntry.TABLE_NAME;


    public OrderHistoryDbUtility(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database's  upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void insertData(SQLiteDatabase db,String itemName, int noOfItems,int cream,int chocolate){
        ContentValues values = new ContentValues();
        values.put(OrderEntry.COLUMN_NAME_ITEM, itemName);
        values.put(OrderEntry.COLUMN_NAME_QUANTITY, noOfItems);
        values.put(OrderEntry.COLUMN_NAME_CREAM, cream);
        values.put(OrderEntry.COLUMN_NAME_CHOCOLATE, chocolate);
        db.insert(OrderEntry.TABLE_NAME, OrderEntry.COLUMN_NULLABLE, values);
    }

    public Cursor readData(SQLiteDatabase db){
        String[] projection = {
                OrderEntry._ID,
                OrderEntry.COLUMN_NAME_ITEM,
                OrderEntry.COLUMN_NAME_QUANTITY,
                OrderEntry.COLUMN_NAME_CREAM,
                OrderEntry.COLUMN_NAME_CHOCOLATE
        };
        Cursor cursor=db.query(OrderEntry.TABLE_NAME, projection,null,null,null,null,null);
        return cursor;
    }
}
