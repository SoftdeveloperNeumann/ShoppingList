package de.example.frank.shoppinglist;

/**
 * Created by Administrator on 06.09.2016.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ShoppingMemoDataSource dataSource;
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShoppingMemo testMemo = new ShoppingMemo("Golf", 5,105);
        Log.d(LOG_TAG, "Inhalt Main" + testMemo.toString());
        dataSource = new ShoppingMemoDataSource(this);
        Log.d(LOG_TAG,"Quelle wird geoeffnet");
        dataSource.open();
        ShoppingMemo memo = dataSource.createShoppingMemo("BMW",5);
        showAllListEntries();
        Log.d(LOG_TAG,"Quelle wird geschlossen");
        dataSource.close();
    }
    private void showAllListEntries(){
        List<ShoppingMemo> list = dataSource.getAllShoppingMemos();
        ArrayAdapter<ShoppingMemo> adapter= new ArrayAdapter<ShoppingMemo>(this,android.R.layout.simple_list_item_multiple_choice,list);
        ListView listView = (ListView)findViewById(R.id.listview_shopping_memos);
        listView.setAdapter(adapter);
    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;

    }


    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}
