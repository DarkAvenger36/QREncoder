package com.example.lorenzo.qrencoder;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListActivity extends Activity {

    private final String LOG_TAG = ListActivity.class.getSimpleName();


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

        public ListFragment() {
        }

        public void onCreate(Bundle savedInstance){
            setHasOptionsMenu(true);
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
                    "encodedString.png",
                    "fdsafa",
                    "g.png",
                    "QR.png",
                    "gfe",
                    "sdfgd",
                    "hgfdhgfd"};

            List<String> sampleData = new ArrayList<String>(
                    Arrays.asList(sampleDataArray)
                    );

            mListAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.list_item_encoded_strings,
                    R.id.list_item_encoded_strings_textview,
                    sampleData
            );

            ListView listView = (ListView) rootView.findViewById(R.id.list_encoded_strings);
            listView.setAdapter(mListAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedItem = mListAdapter.getItem(i).toString();
                    Intent openDetailIntent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, selectedItem);
                    startActivity(openDetailIntent);
                }

            });

            return rootView;
        }
    }
}
