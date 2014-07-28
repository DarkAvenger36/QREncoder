package com.example.lorenzo.qrencoder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.lorenzo.qrencoder.data.EncodedContract.StringEntry;

/**
 * Created by lorenzo on 28/07/14.
 */
public class EncodedDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "encodedText.db";

    public EncodedDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_ENCODED_TEXT_TABLE = "CREATE TABLE " + StringEntry.TABLE_NAME + " (" +
                StringEntry.COLUMN_FILE_NAME + " TEXT PRIMARY KEY, " +
                StringEntry.COLUMN_ENCODED_STRING + " TEXT NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_ENCODED_TEXT_TABLE);
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2){

    }
}
