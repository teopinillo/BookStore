package me.theofrancisco.android.bookstore;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.theofrancisco.android.bookstore.data.DataContract.DataEntry;

//import android.widget.CursorAdapter;

/**
 Loaders Documentation
 @link https://developer.android.com/guide/components/loaders
 @link https://stuff.mit.edu/afs/sipb/project/android/docs/training/load-data-background/setup-loader.html
 @link https://developer.android.com/guide/topics/ui/layout/listview?utm_source=udacity&utm_medium=course&utm_campaign=android_basics
 @link https://drive.google.com/file/d/1kpDWF-hu-rsN6b20wUrqIRGu377iRrJS/view?usp=sharing
 */
public class DataCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link DataCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    DataCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }
    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
    }
    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView tvName = view.findViewById(R.id.textview_01);
        TextView tvQuantity = view.findViewById(R.id.textview_02);
        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_QUANTITY);
        // Read the pet attributes from the Cursor for the current pet
        String bookName = cursor.getString(nameColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);
        // Update the TextViews with the attributes for the current pet
        tvName.setText(bookName);
        tvQuantity.setText(quantity);
    }

}
