package com.example.businesscard;

import static com.example.businesscard.R.string.incorrectlink;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


public class Cabinet extends AppCompatActivity {
    private ImageView qrCodeImageView;
    private Button turnOnNFC;
    public static String link = "http://u147316.test-handyhost.ru/";

    @Override
    protected void onCreate(Bundle savedInstanceState) { //при запуске активити лепится qr, пока что со статичной ссылкой, ибо на динамику нет нитиво)))
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cabinet);
        qrCodeImageView = findViewById(R.id.qrimg);
        turnOnNFC = findViewById(R.id.nfc);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(link, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        turnOnNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(Cabinet.this);
                if (nfcAdapter == null) {
                    Toast.makeText(Cabinet.this, "NFC is not supported on this device", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Проверяем, включена ли NFC на устройстве
                if (!nfcAdapter.isEnabled()) {
                    Toast.makeText(Cabinet.this, "Please enable NFC and try again", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                    return;
                }

                // Создаем объект с передаваемыми данными
                NdefMessage ndefMessage = new NdefMessage(
                        new NdefRecord[] {
                                NdefRecord.createUri(link)
                        });

                // Устанавливаем колбэк для обработки событий NFC
                nfcAdapter.setNdefPushMessage(ndefMessage, Cabinet.this);

                // Выводим сообщение о готовности передачи
                Toast.makeText(Cabinet.this, "Ready to send link via NFC", Toast.LENGTH_SHORT).show();
            }
        });

    }

}