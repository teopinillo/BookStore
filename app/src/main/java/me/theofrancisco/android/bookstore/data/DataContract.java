package me.theofrancisco.android.bookstore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the BookStore app.
 */

public final class DataContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.

         this value has to be the same as in the manifest for provider
            /*
            <provider
            android:name=".data.MyProvider"
            android:authorities="me.theofrancisco.android.bookstore"
            android:exported="false" />
             */

    static final String CONTENT_AUTHORITY = "me.theofrancisco.android.bookstore";
    /*
    URI Example pattern
    content://com.android.contacts/contacts/
    content://com.android.contacts/contacts/#   <-replace any integer
    content://com.android.contacts/contacts/lookup/* <-replace with any string
    "content://com.android.contacts/contacts/lookup/asterisk/#
     */
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    static final String PATH_BOOKS = "books";
    /*
    BASE_CONTENT_URI
      Next, we concatenate the CONTENT_AUTHORITY constant with the scheme
       “content://” we will create the BASE_CONTENT_URI which will be shared
        by every URI associated with DataContract.
        To make this a usable URI, we use the parse method which takes in a URI
        string and returns a Uri.
        - Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
          the content provider.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private DataContract() {
    }

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single item.
     */
    public static final class DataEntry implements BaseColumns {
        /*
        Complete CONTENT_URI
        Full URI for the class as a constant called CONTENT_URI.
         The Uri.withAppendedPath() method appends the BASE_CONTENT_URI
        (which contains the scheme and the content authority) to the path segment.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public final static String COLUMN_DATA_NAME = "name";        //TEXT
        public final static String COLUMN_DATA_QUANTITY = "quantity"; //INTEGER
        public final static String COLUMN_DATA_PRICE = "buy_price"; //REAL
        //Name of database table
        final static String TABLE_NAME = "books";
        public final static String COLUMN_DATA_SUPPLIER = "supplier"; //TEXT
        public final static String COLUMN_DATA_SUPPLIER_PH = "supplier_ph"; //TEXT
    }

}

