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

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import me.theofrancisco.android.bookstore.data.DataContract.DataEntry;
import static me.theofrancisco.android.bookstore.data.MyDbHelper.LOG_TAG;



public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Content URI for the existing pet (null if it's a new pet)
    private Uri currentItemUri;
    // Identifier for the item data loader
    private static final int EXISTING_ITEM_LOADER = 0;

    //EditText field to enter the item description
    private EditText etName;
    private EditText etPrice;
    private EditText etQuantity;
    private EditText etSupplier;
    private EditText etSupplierPH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Log.i("MyApp","[EditorActivity.onCreate] start");
        //Use getIntent() and getData() to get the associated URI

        //Set the title of EditorActivity on with situation we have
        //if the EditorActivity was opened using the ListView item, then we will
        //have uri of item so change app bar to say "Edit Mode"
        //Otherwise if this is a new item, uri is null so change app bar to
        //say "Add New Data"
        Intent intent = getIntent();
        currentItemUri = intent.getData();
        //if the intent DOES NOT contain a pet URI, then we know that we are
        //creating a new pet
        if (currentItemUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_item));
        } else {
            //Otherwise this is an existing pet, so change app bar to say "Edit Pet"
            setTitle(getString(R.string.editor_activity_title_edit_item));
            // Initialize a loader to read the item data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        etName = findViewById(R.id.edit_name);
        etPrice = findViewById(R.id.edit_price);
        etQuantity = findViewById(R.id.edit_quantity);
        etSupplier = findViewById(R.id.edit_supplier);
        etSupplierPH = findViewById(R.id.edit_supplier_ph);

        Log.i("MyApp","[EditorActivity.onCreate] finish");
    }

    /**
     * Get user input from editor and save new pet into database.
     *
     * @link https://developer.android.com/reference/android/content/ContentValues
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

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(DataEntry.COLUMN_DATA_NAME, name);
        values.put(DataEntry.COLUMN_DATA_PRICE, price);
        values.put(DataEntry.COLUMN_DATA_QUANTITY, quantity);
        values.put(DataEntry.COLUMN_DATA_SUPPLIER, supplier);
        values.put(DataEntry.COLUMN_DATA_SUPPLIER_PH, supplierPH);

        if (currentItemUri==null){
            saveItem(values);     //new pet
        }
        // Otherwise this is an EXISTING item, so update the pet with content URI: currentItemUri
        // and pass in the new ContentValues. Pass in null for the selection and selection args
        // because currentPetUri will already identify the correct row in the database that
        // we want to modify.
        int rowsAffected = getContentResolver().update(currentItemUri, values, null, null);
        // Show a toast message depending on whether or not the update was successful.
        if (rowsAffected == 0) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, getString(R.string.editor_update_item_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the update was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_update_item_successful),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void saveItem(ContentValues values){
        Uri uri = null;
        try {
            // Insert a new row in the database, returning the content uri of that new row.
            uri = getContentResolver().insert(DataEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (uri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage() + "/n" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, getString(R.string.failed_to_insert_row));
            if (uri != null) Log.e(LOG_TAG, uri.toString());
            else
                Log.e(LOG_TAG, getString(R.string.uri_is_null));
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

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        //Projection. specifies the columns from the table we care about
        String[] projection = {
                DataEntry._ID,
                DataEntry.COLUMN_DATA_NAME,
                DataEntry.COLUMN_DATA_PRICE,
                DataEntry.COLUMN_DATA_QUANTITY,
                DataEntry.COLUMN_DATA_SUPPLIER,
                DataEntry.COLUMN_DATA_SUPPLIER_PH};
        return new CursorLoader(this,    //Parent activity context
                DataEntry.CONTENT_URI,          //Provider content URI to query
                projection,                     //Columns to include in the resulting cursor
                null,                   //No selection clause
                null,                //No selection arguments
                null);                  //Default sort order
    }

    /*
    When the data from the pet is loaded into a cursor, onLoadFinished() is called.
     Here, I’ll first most the cursor to it’s first item position.
     Even though it only has one item, it starts at position -1.
     */
    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_PRICE);
            int supplierColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_SUPPLIER);
            int supplier_phColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_SUPPLIER_PH);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(nameColumnIndex);
            float quantity = cursor.getFloat(quantityColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String supplier_ph = cursor.getString(supplier_phColumnIndex);

            etName.setText(title);
            etPrice.setText(Float.toString(price));
            etQuantity.setText(Float.toString(quantity));
            etSupplier.setText(supplier);
            etSupplierPH.setText(supplier_ph);
        }

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        etName.setText("");
        etPrice.setText("");
        etQuantity.setText("");
        etSupplier.setText("");
        etSupplierPH.setText("");
    }

    //------------------implements LoaderManager.LoaderCallbacks<Cursor>--------------------


}