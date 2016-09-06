package de.example.frank.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 06.09.2016.
 */
public class ShoppingMemoDataSource {
    private static final String LOG_TAG = ShoppingMemoDataSource.class.getSimpleName();
    private SQLiteDatabase db;
    private ShoppingMemoDbHelper helper;

    private String[] columns= {
            ShoppingMemoDbHelper.COLUMN_ID,
            ShoppingMemoDbHelper.COLUMN_PRODUCT,
            ShoppingMemoDbHelper.COLUMN_QUANTITY
    };


    public ShoppingMemoDataSource(Context con){
        Log.d(LOG_TAG, " Der Helper wird erzeugt.");
        helper = new ShoppingMemoDbHelper(con);
    }

    public void open(){
        Log.d(LOG_TAG,"Eine Referenz wird angefragt");
        db = helper.getWritableDatabase();
        Log.d(LOG_TAG,"Pfad zur DB " + db.getPath());
    }

    public void close(){
        helper.close();
        Log.d(LOG_TAG,"Datenbank wieder geschlossen");
    }

    public ShoppingMemo createShoppingMemo(String product, int quantity){
        ContentValues values = new ContentValues();
        values.put(ShoppingMemoDbHelper.COLUMN_PRODUCT,product);
        values.put(ShoppingMemoDbHelper.COLUMN_QUANTITY, quantity);

        long insertId = db.insert(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST, null, values);

        Cursor cursor = db.query(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST,
                columns, ShoppingMemoDbHelper.COLUMN_ID + "=" + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        ShoppingMemo shoppingMemo = cursorToShoppingMemo(cursor);
        cursor.close();
        return shoppingMemo;
    }
    private ShoppingMemo cursorToShoppingMemo(Cursor cursor) {

        int idIndex = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_ID);
        int idProduct = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_PRODUCT);
        int idQuantity = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_QUANTITY);

        String product = cursor.getString(idProduct);
        int quantity = cursor.getInt(idQuantity);
        long id = cursor.getLong(idIndex);

        ShoppingMemo shoppingMemo = new ShoppingMemo(product, quantity, id);
        return shoppingMemo;
    }

    public List<ShoppingMemo> getAllShoppingMemos(){
        List<ShoppingMemo> shoppingMemoList = new ArrayList<>();
        Cursor c = db.query(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST,columns,null,null,null,null,null);
        c.moveToFirst();
        ShoppingMemo memo;
        while(!c.isAfterLast()){
            memo = cursorToShoppingMemo(c);
            shoppingMemoList.add(memo);
            c.moveToNext();
        }
        c.close();
        return shoppingMemoList;
    }


}
