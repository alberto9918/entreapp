package com.mario.myapplication.ui.pois.qrScanner;

import com.mario.myapplication.responses.PoiResponse;
import com.mario.myapplication.responses.VisitPoiResponse;

public interface IQrScanListener {
    void onQrScan(VisitPoiResponse poi);
}
