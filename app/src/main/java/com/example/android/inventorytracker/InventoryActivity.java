package com.example.android.inventorytracker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventorytracker.Data.InventoryContract;
import com.example.android.inventorytracker.Data.InventoryDbHelper;

public class InventoryActivity extends AppCompatActivity {

    //Adapter for the list of items
    InventoryCursorAdapter cursorAdapter;

    //Cursor that iterates through table
    private Cursor cursor;

    private InventoryDbHelper mDbhelper;

    //ListView for the list of items
    private ListView itemListView;

    //Empty TextView if nothing to show
    TextView emptyTextView;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_list);

        mDbhelper = new InventoryDbHelper(this);

        itemListView = (ListView) findViewById(R.id.inventory_list);

        //Set the TextView with id empty to an empty view.
        emptyTextView = (TextView) findViewById(R.id.empty);
        itemListView.setEmptyView(emptyTextView);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(onClickListener);

        //Setup onClickListener for when a row is clicked
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Intent editIntent = new Intent(InventoryActivity.this, EditorActivity.class);
//                Toast.makeText(InventoryActivity.this, String.valueOf(l), Toast.LENGTH_SHORT).show();
                editIntent.putExtra("item_id", l);
                startActivity(editIntent);
                }

        });
        displayDatabaseInfo();
    }

    // Setup onClickListener for buttons.
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.sale_button:
                    saleButton(itemListView.getPositionForView(view));
                    break;
                case R.id.fab:
                    Intent newItemIntent = new Intent(InventoryActivity.this, NewItemActivity.class);
                    startActivity(newItemIntent);
                    break;
            }
        }
    };

    private void displayDatabaseInfo() {
        InventoryDbHelper mDbHelper = new InventoryDbHelper(this);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String[] project = {
            InventoryContract.InventoryEntry._ID,
            InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME,
            InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE,
            InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY
        };

        cursor = db.query(
                InventoryContract.InventoryEntry.TABLE_NAME,
                project,
                null,
                null,
                null,
                null,
                null);

        itemListView = (ListView) findViewById(R.id.inventory_list);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                cursorAdapter = new InventoryCursorAdapter(
                        InventoryActivity.this,
                        cursor);
                itemListView.setAdapter(cursorAdapter);
            }
        });
    }

    private void saleButton(long l) {
        mDbhelper = new InventoryDbHelper(this);
        SQLiteDatabase db = mDbhelper.getWritableDatabase();
        String filter = "_ID=" + l;

        ContentValues updateValues = new ContentValues();
        updateValues.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, "quantity = quantity - 1");

        db.update(InventoryContract.InventoryEntry.TABLE_NAME, updateValues, filter, null);
        db.close();
        displayDatabaseInfo();
    }
}
