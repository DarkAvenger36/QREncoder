package com.example.lorenzo.qrencoder;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;

import main.java.com.google.zxing.BarcodeFormat;
import main.java.com.google.zxing.EncodeHintType;
import main.java.com.google.zxing.WriterException;
import main.java.com.google.zxing.common.BitMatrix;
import main.java.com.google.zxing.common.BitVectorMatrix;
import main.java.com.google.zxing.hccqrcode.HCCQRcodeWriter;
import main.java.com.google.zxing.hccqrcode.decoder.ErrorCorrectionLevel;
import main.java.com.google.zxing.hccqrcode.encoder.HCCQRcode;
import main.java.com.google.zxing.qrcode.QRCodeWriter;


public class MainActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new CodeFragment())
                    .commit();
        }



        //Log.d("MainActivity","width ="+width);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

}
