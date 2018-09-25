package me.theofrancisco.android.bookstore.data;

import android.provider.BaseColumns;

/**
 * API Contract for the Pets app.
 */
public final class DataContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private DataContract() {}

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class DataEntry implements BaseColumns {

        /** Name of database table for bar log */
        public final static String TABLE_NAME = "books";
        public final static String _ID = BaseColumns._ID; //INTEGER
        public final static String COLUMN_DATA_NAME ="name";        //TEXT
        public final static String COLUMN_DATA_PRICE = "buy_price"; //REAL
        public final static String COLUMN_DATA_QUANTITY="quantity"; //INTEGER
        public final static String COLUMN_DATA_SUPPLIER = "supplier"; //TEXT
        public final static String COLUMN_DATA_SUPPLIER_PH = "supplier_ph"; //TEXT
    }

}

