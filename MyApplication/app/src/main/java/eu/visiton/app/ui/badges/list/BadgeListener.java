package eu.visiton.app.ui.badges.list;

import android.view.View;

import eu.visiton.app.responses.BadgeResponse;

public interface BadgeListener {
    void onBadgeClick(View v, BadgeResponse b);
}
