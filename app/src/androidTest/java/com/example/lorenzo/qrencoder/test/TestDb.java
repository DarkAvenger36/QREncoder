package com.example.lorenzo.qrencoder.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.lorenzo.qrencoder.data.EncodedDbHelper;
import com.example.lorenzo.qrencoder.data.EncodedContract.StringEntry;

/**
 * Created by lorenzo on 28/07/14.
 */
public class TestDb extends AndroidTestCase {

    private static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable{
        mContext.deleteDatabase(EncodedDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new EncodedDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
    }

    public void testInsertReadDb(){
        String fileName = "pippo.bmp";
        String message = "safgnksldngkljsdfng";

        EncodedDbHelper dbHelper = new EncodedDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(StringEntry.COLUMN_FILE_NAME, fileName);
        values.put(StringEntry.COLUMN_ENCODED_STRING, message);

        long locationRowId = db.insert(StringEntry.TABLE_NAME, null, values);

        assertTrue(locationRowId != -1);

        Log.d(LOG_TAG, "New row = " + locationRowId);

        String[] columns = {
                StringEntry.COLUMN_FILE_NAME,
                StringEntry.COLUMN_ENCODED_STRING
        };

        Cursor cursor = db.query(
                StringEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        if(cursor.moveToFirst()){

            int fileNameIndex = cursor.getColumnIndex(StringEntry.COLUMN_FILE_NAME);
            String file = cursor.getString(fileNameIndex);

            int encodedStringIndex = cursor.getColumnIndex(StringEntry.COLUMN_ENCODED_STRING);
            String encodedString = cursor.getString(encodedStringIndex);

            assertEquals(fileName, file);
            assertEquals(message, encodedString);
        }else{
            fail("No value returned! ");
        }
    }
}
