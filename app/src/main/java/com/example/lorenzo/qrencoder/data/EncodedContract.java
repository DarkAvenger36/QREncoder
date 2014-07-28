package com.example.lorenzo.qrencoder.data;

import android.provider.BaseColumns;

/**
 * Created by lorenzo on 28/07/14.
 */
public class EncodedContract {

    public static final class StringEntry implements BaseColumns{
        public static final String TABLE_NAME = "encoded_strings";

        //colonna contenente il nome dell'immagine codificata
        public static final String COLUMN_FILE_NAME = "file_name";
        //colonna contenente il messaggio codificato
        public static final String COLUMN_ENCODED_STRING = "encoded_string";
    }
}
