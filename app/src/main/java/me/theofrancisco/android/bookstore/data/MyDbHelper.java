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
package me.theofrancisco.android.bookstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.theofrancisco.android.bookstore.data.DataContract.DataEntry;

/**
 * Database helper for Pets app. Manages database creation and version management.
 */
public class MyDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = "myApp";

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "bstore.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 2;

    /**
     * Constructs a new instance of {@link MyDbHelper}.
     *
     * @param context of the app
     */
    MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " + DataEntry.TABLE_NAME + " ("
                + DataContract.DataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DataContract.DataEntry.COLUMN_DATA_NAME + " TEXT NOT NULL, "
                + DataContract.DataEntry.COLUMN_DATA_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + DataContract.DataEntry.COLUMN_DATA_PRICE + " REAL NOT NULL DEFAULT 0, "
                + DataContract.DataEntry.COLUMN_DATA_SUPPLIER + " TEXT NOT NULL,"
                + DataContract.DataEntry.COLUMN_DATA_SUPPLIER_PH + " TEXT NOT NULL )";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_ITEM_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}