package camilog.adminapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


/**
 * Created by diego on 19-01-16.
 */
public class ShowQRActivity extends Activity{
    private String candidateList;
    private String publicKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_qr_layout);
        addOnClickListeners();

        candidateList = getIntent().getStringExtra("candidates");
        publicKey = getIntent().getStringExtra("publicKey");
        ImageView qrImageView = (ImageView) findViewById(R.id.qr_imageView);

        try {
            qrImageView.setImageBitmap(generateQRCodeBitmap(candidateList));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public Bitmap generateQRCodeBitmap(String data) throws WriterException {
        BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, 600, 600);

        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++)
        {
            int offset = y * width;
            for (int x = 0; x < width; x++)
                pixels[offset + x] = bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
        }

        Bitmap bitmap = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        return bitmap;
    }

    public void generateSecondQR(){
        ImageView firstQR = (ImageView) findViewById(R.id.qr_imageView);
        firstQR.setVisibility(View.GONE);
        ImageView secondQR = (ImageView) findViewById(R.id.second_qr_imageView);
        secondQR.setVisibility(View.VISIBLE);

        try {
            TextView candidateText = (TextView) findViewById(R.id.user_info_candidate);
            candidateText.setVisibility(View.GONE);
            TextView publicKeyText = (TextView) findViewById(R.id.user_info_key);
            publicKeyText.setVisibility(View.VISIBLE);
            secondQR.setImageBitmap(generateQRCodeBitmap(publicKey));
            Button nextQR = (Button) findViewById(R.id.continue_to_other_qr);
            nextQR.setVisibility(View.GONE);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void addOnClickListeners(){
        Button nextQR = (Button) findViewById(R.id.continue_to_other_qr);
        nextQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateSecondQR();
            }
        });
    }
}
