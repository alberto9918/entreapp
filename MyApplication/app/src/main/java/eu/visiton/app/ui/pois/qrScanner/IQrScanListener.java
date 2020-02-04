package eu.visiton.app.ui.pois.qrScanner;

import eu.visiton.app.responses.VisitPoiResponse;

public interface IQrScanListener {
    void onQrScan(VisitPoiResponse poi);
}
