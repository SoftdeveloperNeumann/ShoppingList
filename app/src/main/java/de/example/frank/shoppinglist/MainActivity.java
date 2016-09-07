package de.example.frank.shoppinglist;

/**
 * Created by Administrator on 06.09.2016.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
//        Log.d(LOG_TAG,"Quelle wird geoeffnet");
//        dataSource.open();
        activateAddButton();
        initializeContextualActionBar();
//        Log.d(LOG_TAG,"Quelle wird geschlossen");
//        dataSource.close();
    }

    private void initializeContextualActionBar() {
        final ListView shoppingMemoListView = (ListView) findViewById(R.id.listview_shopping_memos);
        shoppingMemoListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        shoppingMemoListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener(){

            /**
             * Called when action mode is first created. The menu supplied will be used to
             * generate action buttons for the action mode.
             *
             * @param mode ActionMode being created
             * @param menu Menu used to populate action buttons
             * @return true if the action mode should be created, false if entering this
             * mode should be aborted.
             */
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar,menu);
                return true;
            }

            /**
             * Called to refresh an action mode's action menu whenever it is invalidated.
             *
             * @param mode ActionMode being prepared
             * @param menu Menu used to populate action buttons
             * @return true if the menu or action mode was updated, false otherwise.
             */
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            /**
             * Called to report a user click on an action button.
             *
             * @param mode The current ActionMode
             * @param item The item that was clicked
             * @return true if this callback handled the event, false if the standard MenuItem
             * invocation should continue.
             */
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete:
                        SparseBooleanArray touchedMemoPosition = shoppingMemoListView.getCheckedItemPositions();
                        for(int i=0;i<touchedMemoPosition.size();i++){
                            boolean isCheckd = touchedMemoPosition.valueAt(i);
                            if(isCheckd) {
                                int posInListView = touchedMemoPosition.keyAt(i);
                                ShoppingMemo memo = (ShoppingMemo) shoppingMemoListView.getItemAtPosition(posInListView);
                                dataSource.deleteShoppingMemo(memo);
                            }
                        }
                        showAllListEntries();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }


            }

            /**
             * Called when an action mode is about to be exited and destroyed.
             *
             * @param mode The current ActionMode being destroyed
             */
            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

            /**
             * Called when an item is checked or unchecked during selection mode.
             *
             * @param mode     The {@link ActionMode} providing the selection mode
             * @param position Adapter position of the item that was checked or unchecked
             * @param id       Adapter ID of the item that was checked or unchecked
             * @param checked  <code>true</code> if the item is now checked, <code>false</code>
             */
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        dataSource.open();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dataSource.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
    }

    private void activateAddButton(){
        Button button = (Button) findViewById(R.id.button_add_product);
        final EditText editQuantity = (EditText) findViewById(R.id.editText_quantity);
        final EditText editProduct = (EditText) findViewById(R.id.editText_product);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String quantity = editQuantity.getText().toString();
                String product = editProduct.getText().toString();
                if(TextUtils.isEmpty(quantity)){
                    editQuantity.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                if(TextUtils.isEmpty(product)){
                    editProduct.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                int quant = Integer.parseInt(quantity);
                editProduct.setText("");
                editQuantity.setText("");
                dataSource.createShoppingMemo(product, quant);

                InputMethodManager inputMethodManager;
                inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                if(getCurrentFocus()!= null){
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
                }
                showAllListEntries();
//                dataSource.close();
            }
        });
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
