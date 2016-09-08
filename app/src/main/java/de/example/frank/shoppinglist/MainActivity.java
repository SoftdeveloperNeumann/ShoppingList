package de.example.frank.shoppinglist;

/**
 * Created by Administrator on 06.09.2016.
 */

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ShoppingMemoDataSource dataSource;
    private ListView mShoppingMemosListView;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataSource = new ShoppingMemoDataSource(this);

        intializeShoppingMemosListView();
        activateAddButton();
        initializeContextualActionBar();

    }

    private void intializeShoppingMemosListView() {
        List<ShoppingMemo> emptyListForInitialization = new ArrayList<>();
        mShoppingMemosListView =(ListView) findViewById(R.id.listview_shopping_memos);

        ArrayAdapter<ShoppingMemo> arrayAdapter = new ArrayAdapter<ShoppingMemo>(this,android.R.layout.simple_list_item_multiple_choice,emptyListForInitialization){
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position,convertView,parent);
                TextView textView = (TextView) view;
                ShoppingMemo memo = (ShoppingMemo) mShoppingMemosListView.getItemAtPosition(position);
                if(memo.isChecked()){
                    textView.setPaintFlags(textView.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                    textView.setTextColor(Color.rgb(175,175,175));
                }else{
                    textView.setPaintFlags(textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    textView.setTextColor(Color.DKGRAY);
                }
                return view;
            }

        };
        mShoppingMemosListView.setAdapter(arrayAdapter);
        mShoppingMemosListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShoppingMemo memo = (ShoppingMemo) parent.getItemAtPosition(position);
                ShoppingMemo updateMemo = dataSource.updateShoppingMemo(memo.getId(),memo.getProduct(),memo.getQuantity(),!memo.isChecked());
                showAllListEntries();
            }
        });
    }

    private void initializeContextualActionBar() {
        final ListView shoppingMemoListView = (ListView) findViewById(R.id.listview_shopping_memos);
        shoppingMemoListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        shoppingMemoListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            int selCount = 0;

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
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar, menu);
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
                MenuItem item = menu.findItem(R.id.change);
                if (selCount == 1) {
                    item.setVisible(true);
                } else {
                    item.setVisible(false);
                }
                return true;
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
                SparseBooleanArray touchedMemoPosition = shoppingMemoListView.getCheckedItemPositions();
                switch (item.getItemId()) {
                    case R.id.delete:

                        for (int i = 0; i < touchedMemoPosition.size(); i++) {
                            boolean isCheckd = touchedMemoPosition.valueAt(i);
                            if (isCheckd) {
                                int posInListView = touchedMemoPosition.keyAt(i);
                                ShoppingMemo memo = (ShoppingMemo) shoppingMemoListView.getItemAtPosition(posInListView);
                                dataSource.deleteShoppingMemo(memo);
                            }
                        }
                        showAllListEntries();
                        break;
                    case R.id.change:
                        for (int i = 0; i < touchedMemoPosition.size(); i++) {
                            boolean isCheckd = touchedMemoPosition.valueAt(i);
                            if (isCheckd) {
                                int posInListView = touchedMemoPosition.keyAt(i);
                                ShoppingMemo memo = (ShoppingMemo) shoppingMemoListView.getItemAtPosition(posInListView);
                                AlertDialog editShoppingMemoDialog = createEditShoppingMemoDialog(memo);
                                editShoppingMemoDialog.show();
                            }
                        }

                        break;
                    default:
                        return false;
                }

                mode.finish();
                return true;


            }

            /**
             * Called when an action mode is about to be exited and destroyed.
             *
             * @param mode The current ActionMode being destroyed
             */
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selCount = 0;
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
                if (checked) {
                    selCount++;
                } else {
                    selCount--;
                }
                String capTitle = selCount + " " + getString(R.string.checked);
                mode.setTitle(capTitle);
                mode.invalidate();
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
        showAllListEntries();
    }

    private void activateAddButton() {
        Button button = (Button) findViewById(R.id.button_add_product);
        final EditText editQuantity = (EditText) findViewById(R.id.editText_quantity);
        final EditText editProduct = (EditText) findViewById(R.id.editText_product);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = editQuantity.getText().toString();
                String product = editProduct.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    editQuantity.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                if (TextUtils.isEmpty(product)) {
                    editProduct.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                int quant = Integer.parseInt(quantity);
                editProduct.setText("");
                editQuantity.setText("");
                dataSource.createShoppingMemo(product, quant);

                hideKeyboard();
                showAllListEntries();
//                dataSource.close();
            }
        });
    }

    private void showAllListEntries() {
        List<ShoppingMemo> list = dataSource.getAllShoppingMemos();
        ArrayAdapter<ShoppingMemo> adapter = (ArrayAdapter<ShoppingMemo>) mShoppingMemosListView.getAdapter();
        adapter.clear();
        adapter.addAll(list);
        adapter.notifyDataSetChanged();
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

    private AlertDialog createEditShoppingMemoDialog(final ShoppingMemo memo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogsView = inflater.inflate(R.layout.dialog_edit_shopping_memo, null);
        final EditText editTextQuantity = (EditText) dialogsView.findViewById(R.id.editText_new_quantity);
        editTextQuantity.setText(String.valueOf(memo.getQuantity()));

        final EditText editTextProduct = (EditText) dialogsView.findViewById(R.id.editText_new_product);
        editTextProduct.setText(String.valueOf(memo.getProduct()));

        builder.setView(dialogsView).setTitle(R.string.dialog_title).setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {

            /**
             * This method will be invoked when a button in the dialog is clicked.
             *
             * @param dialog The dialog that received the click.
             * @param which  The button that was clicked (e.g.
             *               {@link DialogInterface#BUTTON1}) or the position
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String quantityString = editTextQuantity.getText().toString();
                String product = editTextProduct.getText().toString();
                if (TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(product)) {
                    return;
                }


                int quantity = Integer.parseInt(quantityString);
                ShoppingMemo sMemo = dataSource.updateShoppingMemo(memo.getId(), product, quantity, memo.isChecked());
                showAllListEntries();

                dialog.dismiss();
                hideKeyboard();

            }
        }).setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder.create();
    }

    private void hideKeyboard() {

        InputMethodManager inputMethodManager;
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(inputMethodManager.HIDE_IMPLICIT_ONLY,inputMethodManager.HIDE_NOT_ALWAYS);

    }
}
