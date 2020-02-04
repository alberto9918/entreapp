package eu.visiton.app.ui.routes;

import android.view.View;

import eu.visiton.app.responses.RouteResponse;

public interface RouteListener {
    void onRouteClick(View v, RouteResponse r);
}
