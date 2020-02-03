package com.mario.myapplication.ui.pois.qrScanner;

import com.mario.myapplication.responses.PoiResponse;

public interface IQrScanListener {
    void onQrScan(PoiResponse poi);
}
