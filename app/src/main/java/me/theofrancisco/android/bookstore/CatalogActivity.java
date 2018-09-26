package me.theofrancisco.android.bookstore;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import me.theofrancisco.android.bookstore.data.DataContract.DataEntry;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity (FloatingActionButton)
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        String state = "";
        TextView displayView = findViewById(R.id.text_view_pet);
        StringBuilder records = new StringBuilder();
        //Projection
        String[] projection = {
                DataEntry._ID,
                DataEntry.COLUMN_DATA_NAME,
                DataEntry.COLUMN_DATA_PRICE,
                DataEntry.COLUMN_DATA_QUANTITY,
                DataEntry.COLUMN_DATA_SUPPLIER,
                DataEntry.COLUMN_DATA_SUPPLIER_PH};

        try (Cursor cursor = getContentResolver().query(DataEntry.CONTENT_URI,
                projection,
                null,
                null,
                null)) {

            if (cursor == null) {
                displayView.setText("Error: cursor is null for : " + DataEntry.CONTENT_URI);
                return;
            }

            // Figure out the index of each column
            //int idColumnIndex = cursor.getColumnIndex(DataEntry._ID);
            state = "/n1: " + DataEntry.COLUMN_DATA_NAME + " /n";
            int nameColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_NAME);
            state = "/n2/n";
            int priceColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_SUPPLIER);
            int supplier_phColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_SUPPLIER_PH);

            records.append("items count: ").append(cursor.getCount()).append("\n");
            records.append("id Item Description Unit \tQuantity \tbuy \tsell\n\n");
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    //int id = cursor.getInt(idColumnIndex);
                    String name = cursor.getString(nameColumnIndex);
                    float price = cursor.getInt(priceColumnIndex);
                    float quantity = cursor.getFloat(quantityColumnIndex);
                    String supplier = cursor.getString(supplierColumnIndex);
                    String supplier_ph = cursor.getString(supplier_phColumnIndex);

                    records.append(name).append(" _ ");
                    records.append(price).append(" _ ");
                    records.append(quantity).append(" _ ");
                    records.append(supplier).append(" _ ");
                    records.append(supplier_ph).append("\n");
                    cursor.moveToNext();
                }
            } else {
                records.append("no records found!");
            }
        } catch (Exception e) {
            records.append("Error: " + state).append(e.getMessage());
        }
        // Display the number of rows in the Cursor (which reflects the number of rows in the
        // pets table in the database).

        //displayView.setText("Number of rows in pets database table: " + c.getCount());
        displayView.setText(records.toString());
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertDummyData() {
        ContentValues values = new ContentValues();
        values.put(DataEntry.COLUMN_DATA_NAME, "The Engineer's Guide to Fashion");
        values.put(DataEntry.COLUMN_DATA_PRICE, 10);
        values.put(DataEntry.COLUMN_DATA_QUANTITY, 2);
        values.put(DataEntry.COLUMN_DATA_SUPPLIER, "Fazlur Rahman Khan");
        values.put(DataEntry.COLUMN_DATA_SUPPLIER_PH, "(605) 475 6959");
        // Use the {@link DataEntry#CONTENT_URI} to indicate that we want to insert
        // into the database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(DataEntry.CONTENT_URI, values);

        values = new ContentValues();
        values.put(DataEntry.COLUMN_DATA_NAME, "Everything Men Know About Women");
        values.put(DataEntry.COLUMN_DATA_PRICE, 15);
        values.put(DataEntry.COLUMN_DATA_QUANTITY, 8);
        values.put(DataEntry.COLUMN_DATA_SUPPLIER, "Ricky Martin");
        values.put(DataEntry.COLUMN_DATA_SUPPLIER_PH, "(951) 262 3062");
        newUri = getContentResolver().insert(DataEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyData();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}