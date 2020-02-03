package com.mario.myapplication.ui.pois.list;

import android.view.View;

public interface PoiListListener {
    void goPoiDetails(String id);
    void showPoiMap();
    void showPoiList();
    void showQrScanner();
}
