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

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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

    //flag if the user changes any data
    private boolean editDataHasChanged = false;

    //I found a test case where the dialog box was not appearing when the user added details for a new pet but then clicking the back button before saving the details
    //from {@link https://github.com/udacity/ud845-Pets/commit/bea7d9080f06d447892c634f6271cb83eef9762b
    private View.OnKeyListener mKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            editDataHasChanged = true;
            return false;
        }
    };

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            editDataHasChanged = true;
            return false;
        }
    };

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
            Log.i("MyApp", "[EditorActivity.OnCreate] uri: " + currentItemUri);
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        etName = findViewById(R.id.edit_name);
        etPrice = findViewById(R.id.edit_price);
        etQuantity = findViewById(R.id.edit_quantity);
        etSupplier = findViewById(R.id.edit_supplier);
        etSupplierPH = findViewById(R.id.edit_supplier_ph);

        etName.setOnTouchListener(mTouchListener);
        etPrice.setOnTouchListener(mTouchListener);
        etQuantity.setOnTouchListener(mTouchListener);
        etSupplier.setOnTouchListener(mTouchListener);
        etSupplierPH.setOnTouchListener(mTouchListener);

        etName.setOnKeyListener(mKeyListener);
        etPrice.setOnKeyListener(mKeyListener);
        etQuantity.setOnKeyListener(mKeyListener);
        etSupplier.setOnKeyListener(mKeyListener);
        etSupplierPH.setOnKeyListener(mKeyListener);

        Log.i("MyApp","[EditorActivity.onCreate] finish");
    }

    private void saveItem(ContentValues values) {
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
                // If the item hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!editDataHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        if (TextUtils.isEmpty(name)) return;
        String priceStr = etPrice.getText().toString().trim();
        if (TextUtils.isEmpty(priceStr)) return;
        String quantityStr = etQuantity.getText().toString().trim();
        if (TextUtils.isEmpty(quantityStr)) return;
        String supplier = etSupplier.getText().toString().trim();
        if (TextUtils.isEmpty(supplier)) return;
        String supplierPH = etSupplierPH.getText().toString().trim();
        if (TextUtils.isEmpty(supplierPH)) return;
        int quantity;
        float price;
        try {
            quantity = Integer.valueOf(quantityStr);
            price = Float.valueOf(priceStr);
        } catch (NumberFormatException e) {
            quantity = 0;
            price = 0;
        }

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

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i("MyApp", "[EditorActivity.onCreateLoader] start");
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
        Loader<Cursor> cLoader =
                new CursorLoader(this,    //Parent activity context
                        currentItemUri,          //Provider content URI to query
                        projection,                     //Columns to include in the resulting cursor
                        null,                   //No selection clause
                        null,                //No selection arguments
                        null);                  //Default sort order
        Log.i("MyApp", "[EditorActivity.onCreateLoader] end.");
        return cLoader;
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

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mPetHasChanged boolean to true.

    /*
    When the data from the pet is loaded into a cursor, onLoadFinished() is called.
     Here, I’ll first most the cursor to it’s first item position.
     Even though it only has one item, it starts at position -1.
     */
    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor _cursor) {
        String step = "0";
        try {
            Log.i("MyApp", "[EditorActivity.onLoadFinished] start");
            // Proceed with moving to the first row of the cursor and reading data from it
            // (This should be the only row in the cursor)
            // Bail early if the cursor is null or there is less than 1 row in the cursor
            if (_cursor == null || _cursor.getCount() < 1) {
                return;
            }

            // Proceed with moving to the first row of the cursor and reading data from it
            // (This should be the only row in the cursor)
            if (_cursor.moveToFirst()) {
                // Find the columns of pet attributes that we're interested in
                step = "1";
                int nameColumnIndex = _cursor.getColumnIndex(DataEntry.COLUMN_DATA_NAME);
                step = "2";
                int quantityColumnIndex = _cursor.getColumnIndex(DataEntry.COLUMN_DATA_QUANTITY);
                int priceColumnIndex = _cursor.getColumnIndex(DataEntry.COLUMN_DATA_PRICE);
                int supplierColumnIndex = _cursor.getColumnIndex(DataEntry.COLUMN_DATA_SUPPLIER);
                int supplier_phColumnIndex = _cursor.getColumnIndex(DataEntry.COLUMN_DATA_SUPPLIER_PH);

                // Extract out the value from the Cursor for the given column index
                step = "8 (title) ";
                String title = _cursor.getString(nameColumnIndex);
                Log.i("MyApp", "[EditorActivity.onLoadFinished] title: " + title);
                etName.setText(title);

                int quantity = _cursor.getInt(quantityColumnIndex);
                Log.i("MyApp", "[EditorActivity.onLoadFinished] quantity: " + quantity);
                etQuantity.setText(Integer.toString(quantity));

                float price = _cursor.getFloat(priceColumnIndex);
                Log.i("MyApp", "[EditorActivity.onLoadFinished] price: " + price);
                etPrice.setText(String.format("%.2f", price));

                String supplier = _cursor.getString(supplierColumnIndex);
                etSupplier.setText(supplier);

                //http://www.java67.com/2014/06/how-to-format-float-or-double-number-java-example.html
                etPrice.setText(String.format("%.2f", price));

                String supplier_ph = _cursor.getString(supplier_phColumnIndex);
                etSupplierPH.setText(supplier_ph);

            }
            Log.i("MyApp", "[EditorActivity.onLoadFinished] end");
        } catch (Exception e) {
            Log.i("MyApp", "[EditorActivity.onLoadFinished] error at: " + step + " " + e.getLocalizedMessage());
        }
    }

    //This method will create a dialog if the user exit the activity without saving
    // OnClickListener for the discard button. We do this because the behavior for clicking
    // back or up is a little bit different.
    //This method will create a dialog if the user exit the activity without saving
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    //Hook up the back button
    //Here is the code for the back button. You need to override the activity's normal
    // “back button”. If the pet has changed, you make a discarded click listener that closes
    // the current activity.
    // Then you pass this listener to the showUnsavedChangesDialog method you just created.
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!editDataHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    //Helpful links:

    //To add behavior to when the back button is clicked, see this StackOverflow post.
    //@link https://stackoverflow.com/questions/18337536/android-overriding-onbackpressed
    //To add behavior when the “Up” button is clicked, see this article.
    //@link https://developer.android.com/training/implementing-navigation/ancestral?utm_source=udacity&utm_medium=course&utm_campaign=android_basics#NavigateUp
    // You’ll need to add code to the case when the android.R.id.home button is clicked.
    //@link https://developer.android.com/guide/topics/ui/dialogs?utm_source=udacity&utm_medium=course&utm_campaign=android_basics#AlertDialog

}