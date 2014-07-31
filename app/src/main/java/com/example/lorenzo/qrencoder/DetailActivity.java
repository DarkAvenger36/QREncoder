package com.example.lorenzo.qrencoder;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lorenzo.qrencoder.data.EncodedContract;
import com.example.lorenzo.qrencoder.data.EncodedDbHelper;

import java.io.File;

public class DetailActivity extends Activity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            delete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void delete(){
        String selection = EncodedContract.StringEntry.COLUMN_FILE_NAME + "=?";
        //Log.d(LOG_TAG, "filename = " + DetailFragment.fileName);
        String[] selectionArgs = {DetailFragment.fileName};
        EncodedDbHelper encodedDbHelper = new EncodedDbHelper(this);
        SQLiteDatabase db = encodedDbHelper.getWritableDatabase();
        db.delete(EncodedContract.StringEntry.TABLE_NAME,selection,selectionArgs);
        DetailFragment.deleteFile();
        Toast.makeText(this,R.string.toast_deleted,Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +"/QRCODE/";

        private String completeFileName;

        public static String fileName;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        private Intent createShareSocialIntent(){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("image/jpeg");
             //shareIntent.setType("text/plain");//dovrà diventare "image/jpeg"
            //shareIntent.putExtra(Intent.EXTRA_TEXT, "Sto condividendo dall'app!!");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + FILE_PATH + completeFileName));
            //dovrà diventare putExtra((Intent.EXTRA_STREAM, Uri.parse("file:///...."));
            return shareIntent;
        }

        public static void deleteFile(){
            File file = new File(FILE_PATH + fileName + CodeFragment.FORMAT);
            file.delete();
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();
            TextView textView = (TextView) rootView.findViewById(R.id.text_label);
            ImageView image = (ImageView) rootView.findViewById(R.id.show_image);

            if (intent != null && intent.getExtras() != null){
                Bundle extras = intent.getExtras();
                String selectedItem = extras.getString("SELECTED_ITEM");
                fileName = selectedItem;
                String encodedText = extras.getString("ENCODED_TEXT");
                Log.d(LOG_TAG, "ricevuto = " + selectedItem + " prova = " + encodedText);
                textView.setText(encodedText);
                completeFileName = selectedItem + CodeFragment.FORMAT;
                try {
                    Bitmap bmp = BitmapFactory.decodeFile(FILE_PATH + completeFileName);
                    image.setImageBitmap(bmp);
                }catch (Exception e){
                    Log.e(LOG_TAG, "ERROR: unable to decode image. Image exist?");
                }
            }
            return rootView;
        }

        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
            inflater.inflate(R.menu.detailfragment, menu);

            MenuItem menuItem = menu.findItem(R.id.action_share);

            ShareActionProvider mShareActionProvider = (ShareActionProvider) menuItem.getActionProvider();

            if (mShareActionProvider != null){
                mShareActionProvider.setShareIntent(createShareSocialIntent());
            }else{
                Log.w(LOG_TAG, "ERROR: Share action provider is null");
            }
        }
    }
}
