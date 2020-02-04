package com.mario.myapplication.ui.badges.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.mario.myapplication.R;
import com.mario.myapplication.materialx.utils.Tools;
import com.mario.myapplication.util.Constantes;

public class BadgeDetailActivity extends AppCompatActivity implements BadgeDetailListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_detail);

        Bundle extras = getIntent().getExtras();
        String id = extras.getString(Constantes.EXTRA_BADGE_ID);
        boolean isEarned = extras.getBoolean(Constantes.EXTRA_BADGE_EARNED);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedor, new BadgeDetailFragment(id, isEarned))
                .commit();

        initToolbar();


    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.badge));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }
}
