package com.example.lorenzo.qrencoder;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    View rootView;
    ImageView imgView;
    EditText textArea;
    TextView encodedTxt;
    Spinner eccSpinner;
    Spinner modeSpinner;
    int min=0;
    String ecc = "L";
    private int width,height;
    private Bitmap bmp;

    public CodeFragment() {
    }

    public void onCreate(Bundle savedIstanceState){
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        width=dm.widthPixels;
        height=dm.heightPixels;
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
            saveImage();
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


    private void generateHCC(String msg,String ecc){
        HCCQRcodeWriter writer = new HCCQRcodeWriter();

        try {
            //HCCQR
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.valueOf(ecc));
            BitVectorMatrix matrix = writer.encode(msg, BarcodeFormat.QR_CODE,min,min,hintMap);
            bmp = toBitmap(matrix);
            imgView.setImageBitmap(bmp);
            encodedTxt.setText(msg);
        }catch (WriterException we){
            we.printStackTrace();
        }
    }

    private void generateAll(String msg,String ecc,String mode){
        QRCodeWriter writer = new QRCodeWriter();

        try {
            //QR
            Hashtable<EncodeHintType,  main.java.com.google.zxing.qrcode.decoder.ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType,  main.java.com.google.zxing.qrcode.decoder.ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, main.java.com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.valueOf(ecc));
            BitMatrix matrix = writer.encode(msg, BarcodeFormat.valueOf(mode.toUpperCase()),min,min,hintMap);
            bmp = toBitmapBW(matrix);
            imgView.setImageBitmap(bmp);
            encodedTxt.setText(msg);

        }catch (WriterException we){
            we.printStackTrace();
        }
    }

    public void generateQR(){


        String message = textArea.getText().toString();
        if (!message.isEmpty()){
            Log.d(LOG_TAG, "message = " + message);
            String ecc = eccSpinner.getSelectedItem().toString();
            String mode = modeSpinner.getSelectedItem().toString();

            if(mode.equals("HCC-QR")){
                generateHCC(message,ecc);
            }else{
                generateAll(message,ecc,mode);
            }
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

    private void saveImage(){
        if (bmp != null) {
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/QRCODE";
            File dir = new File(file_path);
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(dir, "QR.png");
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(file);
                //Log.d(LOG_TAG,file_path);
                bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                Toast.makeText(getActivity(),R.string.image_saved,Toast.LENGTH_SHORT).show();
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
        }else{
            Toast.makeText(getActivity(),R.string.empty_string,Toast.LENGTH_SHORT).show();
        }



    }


}