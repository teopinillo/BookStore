package me.theofrancisco.android.bookstore.data;

/**
 * {@link https://developer.android.com/guide/topics/providers/content-provider-creating#ContentURI}
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

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
                //TODO: Perform database query on pets table
                cursor = sqLiteDatabase.query(DataContract.DataEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = DataContract.DataEntry._ID + "=?";
                //array of strings to be substituted wherever there was question mark
                //up in the selection String
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(DataContract.DataEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                //Example imputs to query() method
                //URI: content://com.example.android.pets/pets/5
                //Projection: {"_id","name}
                //== SELECT id, name FROM pets WHERE _id=5
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
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

    //this methos is a helper method for insert
    private Uri insertData(Uri uri, ContentValues values) {
        SQLiteDatabase sqliteDatabase = myDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = sqliteDatabase.insert(DataContract.DataEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}

