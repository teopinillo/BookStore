package me.theofrancisco.android.bookstore.data;

/**
 * {@link https://developer.android.com/guide/topics/providers/content-provider-creating#ContentURI}
 */
//Is this class where all data changes for the database are triggered.

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import static me.theofrancisco.android.bookstore.data.DataContract.DataEntry;
import static me.theofrancisco.android.bookstore.data.MyDbHelper.LOG_TAG;
    /*
    USE URI MATCHER IN CONTENT PROVIDER

    1. Setup the UriMatcher with the URI patterns your COntentProvider will accept and
       assign each pattern an integer coder.
        sUriMatcher.addURI (ContactsContract.AUTHORITY,"contacts",CONTACTS);
        sUriMatcher.addURI (ContactsContract.AUTHORITY,"contacts/#",CONTACTS_ID);
    2. Call UriMatcher.match(Uri) and pass in a Uri, which will return the corresponding
       integer code (if it matched a valid pattern) or will tell you there's no match.
           int match = sUriMatcher(uri);
           switch (match) {
                case CONTACTS: doSomething();
                case CONTACTS_ID: doSomethingElse();
                default: noMatchFound();
           }
     */

/**
 * {@link ContentProvider} for BookStore app.
 */
public class MyProvider extends ContentProvider {
    //Creates a UriMatcher Object
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int BOOKS = 100;
    private static final int BOOK_ID = 101;

    //static code to setup
    static {
            /*
             * The calls to addURI() go here, for all of the content URI patterns that the provider
             * should recognize.
             * example
             * Sets the integer value for multiple rows in table 3 to 1. Notice that no wildcard is used
             * in the path
             * uriMatcher.addURI("com.example.app.provider", "table3", 1);
             * /*
             * Sets the code for a single row to 2. In this case, the "#" wildcard is
             * used. "content://com.example.app.provider/table3/3" matches, but
             * "content://com.example.app.provider/table3 doesn't.
                uriMatcher.addURI("com.example.app.provider", "table3/#", 2);
             */
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_BOOKS, BOOKS);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    private MyDbHelper myDbHelper;

    // Tag for the log messages
    //public static final String LOG_TAG = MyProvider.class.getSimpleName();
    //public static final String LOG_TAG = "MyApp";

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        myDbHelper = new MyDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     * 1-Get the Database Object
     * 2-URIMatcher
     * 3-Pets case | PET _ID case
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase sqLiteDatabase = myDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                //Perform database query on pets table
                cursor = sqLiteDatabase.query(DataContract.DataEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = DataContract.DataEntry._ID + "=?";
                //array of strings to be substituted wherever there was question mark
                //up in the selection String
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(DataContract.DataEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null, sortOrder);
                //Example imputs to query() method
                //URI: content://com.example.android.pets/pets/5
                //Projection: {"_id","name}
                //== SELECT id, name FROM pets WHERE _id=5
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        //Set the notification URI on the cursor,
        //so we know what content URI the Cursor was created for.
        //If the data at this URI changes, then we know we need to update the Cursor
        //tutorial:https://www.grokkingandroid.com/android-tutorial-writing-your-own-content-provider/
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     * Example inputs to insert() method
     * URI: content://me.theofrancisco.android.database/table
     * ContentValues: name is Tommy, breed is Pomeranian, gender is 1, weight is 4
     * <p>
     * Within the insert() method
     * SQLite statement: INSERT INTO table (name, breed, gender,weight) VALUES
     * ("Tommy","Pomeranian",1,4)
     * <p>
     * Result
     * ID os newly inserted row (such as 6 for example)
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertData(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);

        }
    }

    //this method is a helper method for insert
    private Uri insertData(Uri uri, ContentValues values) {
        SQLiteDatabase sqliteDatabase = myDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = sqliteDatabase.insert(DataContract.DataEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = DataContract.DataEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update an item in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more items).
     * Return the number of rows that were successfully updated.
     */
    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link DataEntry#COLUMN_DATA_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(DataContract.DataEntry.COLUMN_DATA_NAME)) {
            String name = values.getAsString(DataContract.DataEntry.COLUMN_DATA_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = myDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        int rowsUpdated = database.update(DataContract.DataEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated>0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        // Track the number of rows that were deleted
        int rowsDeleted;
        SQLiteDatabase database = myDbHelper.getWritableDatabase();
        switch (match) {
            case BOOKS:
                //Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(DataContract.DataEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                //Delete a single row given the ID in the URI
                selection = DataEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(DataEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     *
     * @link https://classroom.udacity.com/nanodegrees/nd803/parts/0b4a1e27-4535-464f-bf74-4908a6a17897/modules/5250e120-df7d-43eb-b4a8-beff2fe1e2f7/lessons/d3e97af7-7e35-40c2-8016-7dab121a3e39/concepts/c77228ff-2cf1-41da-b12d-c15e95b9dbf7
     * @link https://stackoverflow.com/questions/7157129/what-is-the-mimetype-attribute-in-data-used-for
     */
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return DataEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return DataEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}


