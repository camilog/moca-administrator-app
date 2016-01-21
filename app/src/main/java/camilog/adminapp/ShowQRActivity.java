package camilog.adminapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


/**
 * Created by diego on 19-01-16.
 */
public class ShowQRActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_qr_layout);

        String data = getIntent().getStringExtra("candidatesAndKey");
        ImageView qrImageView = (ImageView) findViewById(R.id.qr_imageView);

        try {
            qrImageView.setImageBitmap(generateQRCodeBitmap(data));
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

}
