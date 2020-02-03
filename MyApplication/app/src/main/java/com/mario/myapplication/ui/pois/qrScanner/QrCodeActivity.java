package com.mario.myapplication.ui.pois.qrScanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.mario.myapplication.R;
import com.mario.myapplication.responses.PoiResponse;
import com.mario.myapplication.ui.pois.details.DetallePoiActivity;

public class QrCodeActivity extends AppCompatActivity implements IQrScanListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
    }

    @Override
    public void onQrScan(PoiResponse poi) {
        Intent i = new Intent(this, DetallePoiActivity.class);
        i.putExtra("id", poi.getId());
        startActivity(i);
    }
}
