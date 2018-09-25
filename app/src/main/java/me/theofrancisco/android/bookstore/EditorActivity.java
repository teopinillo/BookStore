/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.theofrancisco.android.bookstore;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import me.theofrancisco.android.bookstore.data.DataContract.DataEntry;
import me.theofrancisco.android.bookstore.data.MyDbHelper;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    /** EditText field to enter the item description */
    private EditText etName;
    private EditText etPrice;
    private EditText etQuantity;
    private EditText etSupplier;
    private EditText etSupplierPH;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        etName = findViewById(R.id.edit_name);
        etPrice = findViewById(R.id.edit_price);
        etQuantity = findViewById(R.id.edit_quantity);
        etSupplier = findViewById(R.id.edit_supplier);
        etSupplierPH = findViewById(R.id.edit_supplier_ph);
    }

    /**
     * Get user input from editor and save new pet into database.
     */
    private void insertItem() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String supplier = etSupplier.getText().toString().trim();
        String supplierPH = etSupplierPH.getText().toString().trim();

        int quantity = Integer.valueOf(quantityStr);
        float price = Float.valueOf(priceStr);

        // Create database helper
        MyDbHelper mDbHelper = new MyDbHelper(this);

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(DataEntry.COLUMN_DATA_NAME, name);
        values.put(DataEntry.COLUMN_DATA_PRICE,price);
        values.put(DataEntry.COLUMN_DATA_QUANTITY,quantity);
        values.put(DataEntry.COLUMN_DATA_SUPPLIER, supplier);
        values.put(DataEntry.COLUMN_DATA_SUPPLIER_PH,supplierPH);

        // Insert a new row for pet in the database, returning the ID of that new row.
        long newRowId = db.insert(DataEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving item", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Item saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                insertItem();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}