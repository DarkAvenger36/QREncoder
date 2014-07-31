package com.example.lorenzo.qrencoder;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lorenzo.qrencoder.data.EncodedContract;
import com.example.lorenzo.qrencoder.data.EncodedDbHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Hashtable;

import main.java.com.google.zxing.BarcodeFormat;
import main.java.com.google.zxing.EncodeHintType;
import main.java.com.google.zxing.WriterException;
import main.java.com.google.zxing.common.BitMatrix;
import main.java.com.google.zxing.common.BitVectorMatrix;
import main.java.com.google.zxing.hccqrcode.HCCQRcodeWriter;
import main.java.com.google.zxing.hccqrcode.decoder.ErrorCorrectionLevel;
import main.java.com.google.zxing.qrcode.QRCodeWriter;

/**
 * Created by lorenzo on 23/07/14.
 */
public class CodeFragment extends Fragment {

    private final String LOG_TAG = CodeFragment.class.getSimpleName();
    public static final String FORMAT = ".png";

    private View rootView;
    private ImageView imgView;
    private EditText textArea;
    private TextView encodedTxt;
    private Spinner eccSpinner;
    private Spinner modeSpinner;
    private int min=0;
    private String ecc = "L";
    private int width, height;
    private Bitmap bmp;
    private String message = null;
    private File dir;

    private EncodedDbHelper encodedDbHelper;

    public CodeFragment() {
    }

    public void onCreate(Bundle savedIstanceState){
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        width=dm.widthPixels;
        height=dm.heightPixels;

        encodedDbHelper = new EncodedDbHelper(getActivity());

        super.onCreate(savedIstanceState);
        setHasOptionsMenu(true);

    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        //Log.d(LOG_TAG,"Voglio confrontare: "+id);
        if(id == R.id.clear){
            //clear all
            clearPage();
            Log.d(LOG_TAG,"Clear!");
            return true;
        }else if (id == R.id.saveFile){
            //saveImage();
            showDialog();
            Log.d(LOG_TAG,"Save!");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        imgView = (ImageView)rootView.findViewById(R.id.imageViewer);
        textArea = (EditText)rootView.findViewById(R.id.textArea);
        encodedTxt = (TextView)rootView.findViewById(R.id.encodedText);
        eccSpinner = (Spinner) rootView.findViewById(R.id.eccList);
        modeSpinner = (Spinner) rootView.findViewById(R.id.modeList);

        encodedTxt.setMovementMethod(new ScrollingMovementMethod());

        if(height<width){
            min=height;
        }else{
            min=width;
        }

        Button generate = (Button)rootView.findViewById(R.id.generateQR);

        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(getActivity(),R.array.eccArray,android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eccSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.modeArray, android.R.layout.simple_spinner_item);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(modeAdapter);



        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateQR();
            }
        });

        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/QRCODE";
        dir = new File(file_path);
        if (!dir.exists())
            dir.mkdirs();

        dir.setReadOnly();

        return rootView;
    }

    public static Bitmap toBitmap(BitVectorMatrix matrix){
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        int red = Color.rgb(255, 15, 0);
        int green = Color.rgb(45, 255, 60);


        Bitmap bmp= Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);
        for(int x=0; x<width;x++){
            for(int y=0; y<height; y++){
                boolean value[] = matrix.get(x,y);

                if (value[0] && value[1]){
                    bmp.setPixel(x,y,Color.BLACK);
                }else if(value[0] && !value[1]){
                    bmp.setPixel(x,y,red);
                }else if(!value[0] && value[1]){
                    bmp.setPixel(x,y,green);
                }else{
                    bmp.setPixel(x,y,Color.WHITE);
                }
            }
        }

        return  bmp;
    }

    public static Bitmap toBitmapBW(BitMatrix matrix){
        int height = matrix.getHeight();
        int width = matrix.getWidth();


        Bitmap bmp= Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);
        for(int x=0; x<width;x++){
            for(int y=0; y<height; y++){
                boolean value= matrix.get(x,y);

                if (value){
                    bmp.setPixel(x,y,Color.BLACK);
                }else{
                    bmp.setPixel(x,y,Color.WHITE);
                }
            }
        }

        return  bmp;
    }


    private Bitmap generateHCC(String msg,String ecc){
        HCCQRcodeWriter writer = new HCCQRcodeWriter();

        try {
            //HCCQR
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.valueOf(ecc));
            BitVectorMatrix matrix = writer.encode(msg, BarcodeFormat.QR_CODE,min,min,hintMap);
            bmp = toBitmap(matrix);
            //imgView.setImageBitmap(bmp);

            return bmp;
        }catch (WriterException we){
            we.printStackTrace();
        }
        return null;
    }

    private Bitmap generateAll(String msg,String ecc,String mode){
        QRCodeWriter writer = new QRCodeWriter();

        try {
            //QR
            Hashtable<EncodeHintType,  main.java.com.google.zxing.qrcode.decoder.ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType,  main.java.com.google.zxing.qrcode.decoder.ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, main.java.com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.valueOf(ecc));
            BitMatrix matrix = writer.encode(msg, BarcodeFormat.valueOf(mode.toUpperCase()),min,min,hintMap);
            bmp = toBitmapBW(matrix);
            //imgView.setImageBitmap(bmp);
            //encodedTxt.setText(msg);
            return bmp;

        }catch (WriterException we){
            we.printStackTrace();
        }
        return null;
    }

    public void generateQR(){

        message = textArea.getText().toString();
        if (!message.isEmpty()){
            Log.d(LOG_TAG, "User input = " + message);
            ecc = eccSpinner.getSelectedItem().toString();
            String mode = modeSpinner.getSelectedItem().toString();
            new GenerateQRClass().execute(message, mode);
            encodedTxt.setText(message);
        }else{
            Toast.makeText(getActivity(),R.string.empty_string,Toast.LENGTH_SHORT).show();
            clearPage();
        }

    }

    private void clearPage(){
        textArea.setText("");
        bmp = null;
        imgView.setImageBitmap(null);
        encodedTxt.setText("");
    }

    private void saveImage(String fileName){

        if (fileName.isEmpty()) {
            //do not save
            Log.w(LOG_TAG,"empty file name");
            Toast.makeText(getActivity(), R.string.image_not_saved, Toast.LENGTH_SHORT).show();

        } else {

            String selectedName = fileName + this.FORMAT;

            SQLiteDatabase db = encodedDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(EncodedContract.StringEntry.COLUMN_FILE_NAME, fileName);
            values.put(EncodedContract.StringEntry.COLUMN_ENCODED_STRING, message);

            long newRowId;

            newRowId = db.insert(
                    EncodedContract.StringEntry.TABLE_NAME,
                    null,
                    values);


            //Log.d(LOG_TAG,"element insert in database with id = " + newRowId);
            File file = new File(dir, selectedName);
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(file);
                //Log.d(LOG_TAG,file_path);
                bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                Toast.makeText(getActivity(), R.string.image_saved, Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException fnf) {
                fnf.printStackTrace();
            } finally {
                try {
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showDialog(){
        if (bmp != null) {

            final EditText input = new EditText(getActivity());

            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.alert_save_title)
                    .setMessage(R.string.alert_save_label)
                    .setView(input)
                    .setPositiveButton(R.string.alert_save_positive, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String fileName = input.getText().toString();
                            saveImage(fileName);
                        }
                    })
                    .setNegativeButton(R.string.alert_save_negative, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    }).show();

        }else{
            Toast.makeText(getActivity(), R.string.empty_string, Toast.LENGTH_SHORT).show();
        }
    }

    private class GenerateQRClass extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... a) {
            String message = a[0];
            String mode = a[1];


            if(mode.equals("HCC-QR")){
                return generateHCC(message,ecc);
            }else{
                return generateAll(message,ecc,mode);
            }
        }

        protected void onPostExecute(Bitmap bmp){
            if (bmp != null) {
                imgView.setImageBitmap(bmp);
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (bmp != null) {
            imgView.setImageBitmap(bmp);
            encodedTxt.setText(message);
        }
    }

}