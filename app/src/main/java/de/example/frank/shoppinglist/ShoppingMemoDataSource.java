package de.example.frank.shoppinglist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Administrator on 06.09.2016.
 */
public class ShoppingMemoDataSource {
    private static final String LOG_TAG = ShoppingMemoDataSource.class.getSimpleName();
    private SQLiteDatabase db;
    private ShoppingMemoDbHelper helper;

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
}
