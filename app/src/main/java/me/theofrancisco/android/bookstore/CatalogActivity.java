package me.theofrancisco.android.bookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import me.theofrancisco.android.bookstore.data.DataContract.DataEntry;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Constant that will identified my loader. Could be any value.
    private static final int DATA_LOADER = 1900;
    private DataCursorAdapter adapter;

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

        //Find the ListView which will populated with the data
        ListView listView = findViewById(R.id.list);

        //Find and ser empty view on the ListView, so that it only shows when the
        //list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        //Setup an Adapter to create a list for each row of pet data in the cursor
        //There is no data yet (until the loader finished) so pass in null for the cursor
        adapter = new DataCursorAdapter(this, null);
        listView.setAdapter(adapter);

        //Setup item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("MyApp", "[CatalogActivity.onItemClick] started.");
                Log.i("MyApp", "position id: " + id);
                //Create a new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);

                //From the content URI that represents the specific item that was clicked on,
                //by appending the "id" (passed as input to this method) onto the
                //{@link DataEntry#CONTENT_URI}.
                //For example, the URI would be:
                //"content://me.theofrancisco.android.books/books/2"
                //if the item with ID 2 was clicked on
                Uri currentItemUri = ContentUris.withAppendedId(DataEntry.CONTENT_URI, id);
                Log.i("MyApp", "[CatalogActivity.onItemClick] uri: " + currentItemUri);
                //set the URI on the data field of the intent
                intent.setData(currentItemUri);
                //Launch the {@link EditorActivity} to display the data for the current item
                Log.i("MyApp", "[CatalogActivity.onItemClick] end.");
                startActivity(intent);
            }
        });
        //Kick off the loader
        getLoaderManager().initLoader(DATA_LOADER, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertDummyData() {
        ContentValues values = new ContentValues();
        values.put(DataEntry.COLUMN_DATA_NAME, "The Engineer's Guide to Fashion");
        values.put(DataEntry.COLUMN_DATA_PRICE, 10.40);
        values.put(DataEntry.COLUMN_DATA_QUANTITY, 11);
        values.put(DataEntry.COLUMN_DATA_SUPPLIER, "Fazlur Rahman Khan");
        values.put(DataEntry.COLUMN_DATA_SUPPLIER_PH, "6054756959");
        // Use the {@link DataEntry#CONTENT_URI} to indicate that we want to insert
        // into the database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        getContentResolver().insert(DataEntry.CONTENT_URI, values);

        values = new ContentValues();
        values.put(DataEntry.COLUMN_DATA_NAME, "Everything Men Know About Women");
        values.put(DataEntry.COLUMN_DATA_PRICE, 15.60);
        values.put(DataEntry.COLUMN_DATA_QUANTITY, 22);
        values.put(DataEntry.COLUMN_DATA_SUPPLIER, "Ricky Martin");
        values.put(DataEntry.COLUMN_DATA_SUPPLIER_PH, "9512623062");
        getContentResolver().insert(DataEntry.CONTENT_URI, values);
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
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //----------LoadManager.LoaderCallbacks<Cursor> methods implementation---------
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Update {@link DataCursorAdapter} with this new cursor containing updated data
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Callback called when the data needs to be deleted
        adapter.swapCursor(null);
    }

    //---------END LoadManager.LoaderCallbacks<Cursor> methods implementation--------

}