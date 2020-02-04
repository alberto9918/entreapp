package eu.visiton.app.ui.pois.list;

public interface PoiListListener {
    void goPoiDetails(String id);
    void showPoiMap();
    void showPoiList();
    void showQrScanner();
    void showGoogleMaps(String coords);
}
