package com.example.lorenzo.qrencoder;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lorenzo.qrencoder.data.EncodedContract.StringEntry;
import com.example.lorenzo.qrencoder.data.EncodedDbHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListActivity extends Activity {

    private final String LOG_TAG = ListActivity.class.getSimpleName();

    private boolean doubleBackToExitPressedOnce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ListFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ListFragment extends Fragment {

        private final String LOG_TAG = ListFragment.class.getSimpleName();
        private ArrayAdapter<String> mListAdapter;
        private EncodedDbHelper encodedDbHelper;
        private  ListView listView;

        private SimpleCursorAdapter updateList(){

            String[] projection = {
                    StringEntry._ID,
                    StringEntry.COLUMN_FILE_NAME,
                    StringEntry.COLUMN_ENCODED_STRING
            };

            SQLiteDatabase db = encodedDbHelper.getReadableDatabase();

            Cursor cursor = db.query(
                    StringEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            int[] to = new int[]{
                    R.id._id,
                    R.id.list_item_file_name,
                    R.id.list_item_encoded_strings_textview
            };


            /*cursor.moveToFirst();

            while(!cursor.isLast()){
                sampleData.add(cursor.getString(0));
                Log.d(LOG_TAG, "STO ITERANDO SULL'ELEMENTO: "+ cursor.getString(0) +" messaggio = "+cursor.getString(1) );
                cursor.moveToNext();
            }*/


            final SimpleCursorAdapter adapter1 = new SimpleCursorAdapter(
                    getActivity(),
                    R.layout.list_item_encoded_strings,
                    cursor,
                    projection,
                    to,
                    0
            );
            return adapter1;
        }


        public ListFragment() {
            setHasOptionsMenu(true);
        }

        public void onCreate(Bundle savedInstance){
            setHasOptionsMenu(true);
            encodedDbHelper = new EncodedDbHelper(getActivity());
            super.onCreate(savedInstance);
        }

        public boolean onOptionsItemSelected(MenuItem item){
            int id = item.getItemId();
            //Log.d(LOG_TAG,"Voglio confrontare: "+id);
            if (id == R.id.add_qr){
                Log.d(LOG_TAG,"add qr pressed from menu");
                Intent openGenerateQRIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(openGenerateQRIntent);
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_list, container, false);

            String[] sampleDataArray = {
                    "encodedString",
                    "g",
                    "QR",
                    "Tracchete"
            };

            List<String> sampleData = new ArrayList<String>(
                    Arrays.asList(sampleDataArray)
            );


            listView = (ListView) rootView.findViewById(R.id.list_encoded_strings);
            listView.setAdapter(updateList());

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //String selectedItem = mListAdapter.getItem(i);
                    String selectedItem =( (TextView) view.findViewById(R.id.list_item_file_name)).getText().toString();
                    String encodedText = ( (TextView) view.findViewById(R.id.list_item_encoded_strings_textview)).getText().toString();
                    //Log.d(LOG_TAG, "ITEM i = " + selectedItem);
                    TextView a = (TextView) adapterView.findViewById(R.id.list_item_file_name);
                    Bundle bundle = new Bundle();
                    bundle.putString("SELECTED_ITEM", selectedItem);
                    bundle.putString("ENCODED_TEXT", encodedText);
                    Intent openDetailIntent = new Intent(getActivity(), DetailActivity.class)
                            .putExtras(bundle);
                    startActivity(openDetailIntent);
                }

            });

            return rootView;
        }


        public void onResume(){
            super.onResume();
            //Log.d(LOG_TAG, "SONO ON RESUME");
            listView.setAdapter(updateList());
        }
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.toast_exit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }




}
