package com.mario.myapplication.ui.badges.list;

import android.view.View;

import com.mario.myapplication.responses.BadgeResponse;

public interface BadgeListener {
    void onBadgeClick(View v, BadgeResponse b);
}
