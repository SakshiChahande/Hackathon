package com.example.hackathon;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRGenerator extends AppCompatActivity {
    ImageView i;
    String text,text1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenerator);

        i = (ImageView) findViewById(R.id.imageViewQr);


         text = getIntent().getStringExtra("item_code");

        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

        //text1="abcde";
        if(text!=null && !text.isEmpty())
        {
            try{
                MultiFormatWriter multiFormatWriter= new MultiFormatWriter();
                BitMatrix bitMatrix= multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,460,460);
                BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
                Bitmap bitmap=barcodeEncoder.createBitmap(bitMatrix);
                i.setImageBitmap(bitmap);
            }
            catch(WriterException e)
            {e.printStackTrace();}

        }

    }

}

